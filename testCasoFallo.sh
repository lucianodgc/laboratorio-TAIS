#!/bin/bash

PRODUCTOS_URL="http://localhost:8080/api/productos"
ORDENES_URL="http://localhost:8080/api/ordenes"

STOCK=50
CANTIDAD=999

PASS=0
FAIL=0

pass() {
    echo "✓ PASS - $1"
    ((PASS++))
}

fail() {
    echo "✗ FAIL - $1"
    ((FAIL++))
}

echo "======================================"
echo "   PRUEBA ORDEN SIN STOCK SUFICIENTE"
echo "======================================"

# ======================================
# 1. CREAR PRODUCTO
# ======================================

echo ""
echo "1. Creando producto con stock = $STOCK"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{
    \"nombre\": \"Producto-Stock-Test\",
    \"descripcion\": \"Producto para prueba de stock insuficiente\",
    \"precio\": 1000.0,
    \"stock\": $STOCK,
    \"imagenes\": []
  }" \
  "$PRODUCTOS_URL")

echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

# ======================================
# 2. OBTENER ID DEL PRODUCTO
# ======================================

producto_id=$(jq -r '.id' "$response_file")

rm "$response_file"

if [ -z "$producto_id" ] || [ "$producto_id" = "null" ]; then
    fail "No se pudo obtener el ID del producto"
    exit 1
fi

pass "Producto creado"

echo "ID: $producto_id"

# ======================================
# 3. CREAR ORDEN SUPERANDO EL STOCK
# ======================================

echo ""
echo "2. Creando orden"

echo "Stock disponible: $STOCK"
echo "Cantidad solicitada: $CANTIDAD"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"cliente@test.com\",
    \"direccion\": \"Calle 123\",
    \"telefono\": \"099123456\",
    \"items\": [
      {
        \"productoId\": \"$producto_id\",
        \"cantidad\": $CANTIDAD
      }
    ]
  }" \
  "$ORDENES_URL")

echo ""
echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

# ======================================
# 4. OBTENER ID DE LA ORDEN
# ======================================

orden_id=$(jq -r '.id' "$response_file")

rm "$response_file"

if [ -z "$orden_id" ] || [ "$orden_id" = "null" ]; then
    fail "No se pudo obtener el ID de la orden"
    exit 1
fi

pass "Orden creada"

echo "ID de orden: $orden_id"

# ======================================
# 5. ESPERAR PROCESAMIENTO DE RABBITMQ
# ======================================

echo ""
echo "3. Esperando procesamiento de RabbitMQ..."

sleep 2

# ======================================
# 6. CONSULTAR ORDEN
# ======================================

echo ""
echo "4. Consultando estado de la orden"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  "$ORDENES_URL/$orden_id")

echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

estado=$(jq -r '.estado' "$response_file")

rm "$response_file"

echo "Estado recibido: $estado"

if [ "$estado" = "NO_STOCK" ]; then
    pass "La orden pasó a NO_STOCK"
else
    fail "Esperado NO_STOCK, recibido $estado"
fi

# ======================================
# 7. CONSULTAR PRODUCTO
# ======================================

echo ""
echo "5. Verificando inventario"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  "$PRODUCTOS_URL/$producto_id")

echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

stock_final=$(jq -r '.stock' "$response_file")

rm "$response_file"

echo ""
echo "Stock inicial: $STOCK"
echo "Stock final:   $stock_final"

# ======================================
# 8. COMPROBAR QUE EL STOCK NO CAMBIÓ
# ======================================

if [ "$stock_final" = "$STOCK" ]; then
    pass "El inventario no fue modificado"
else
    fail "El inventario fue modificado"
    echo "  Esperado: $STOCK"
    echo "  Recibido: $stock_final"
fi

# ======================================
# RESULTADO FINAL
# ======================================

echo ""
echo "======================================"
echo "              RESULTADO"
echo "======================================"

echo "PASS: $PASS"
echo "FAIL: $FAIL"

if [ "$FAIL" -eq 0 ]; then
    echo ""
    echo "✓ TODAS LAS PRUEBAS PASARON"
    exit 0
else
    echo ""
    echo "✗ HAY PRUEBAS FALLIDAS"
    exit 1
fi