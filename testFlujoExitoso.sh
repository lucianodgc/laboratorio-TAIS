#!/bin/bash

PRODUCTOS_URL="http://localhost:8080/api/productos"
ORDENES_URL="http://localhost:8080/api/ordenes"

STOCK=50
CANTIDAD=10
PRODUCTO_NOMBRE="Producto-Stock-Test-$(date +%s)"

echo "======================================"
echo "       PRUEBA FLUJO EXITOSO"
echo "======================================"

echo ""
echo "1. Creando producto"
echo "Stock inicial: $STOCK"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{
    \"nombre\": \"$PRODUCTO_NOMBRE\",
    \"descripcion\": \"Producto para prueba de stock\",
    \"precio\": 1000.0,
    \"stock\": $STOCK,
    \"imagenes\": []
  }" \
  "$PRODUCTOS_URL")

echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

producto_id=$(jq -r '.id' "$response_file")

rm "$response_file"

echo ""
echo "ID del producto: $producto_id"

if [ -z "$producto_id" ] || [ "$producto_id" = "null" ]; then
    echo "✗ No se pudo crear el producto"
    exit 1
fi

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

orden_id=$(jq -r '.id' "$response_file")

rm "$response_file"

echo ""
echo "ID de la orden: $orden_id"

if [ -z "$orden_id" ] || [ "$orden_id" = "null" ]; then
    echo "✗ No se pudo crear la orden"
    exit 1
fi

echo ""
echo "3. Esperando procesamiento de RabbitMQ..."

sleep 2

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

echo ""
echo "Estado recibido: $estado"

if [ "$estado" = "READY_TO_DELIVERY" ]; then
    echo "✓ PASS - La orden fue procesada correctamente"
else
    echo "✗ FAIL - Estado inesperado: $estado"
    exit 1
fi

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

stock_esperado=$((STOCK - CANTIDAD))

echo ""
echo "Stock inicial:   $STOCK"
echo "Cantidad vendida: $CANTIDAD"
echo "Stock esperado:  $stock_esperado"
echo "Stock final:     $stock_final"

if [ "$stock_final" -eq "$stock_esperado" ]; then
    echo "✓ PASS - El stock fue descontado correctamente"
else
    echo "✗ FAIL - El stock no coincide"
    exit 1
fi

echo ""
echo "6. Consultando factura generada"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  "$ORDENES_URL/$orden_id/factura")

echo "HTTP $status"
echo "Respuesta:"
jq . "$response_file"

if [ "$status" -ne 200 ]; then
    echo "✗ FAIL - No se pudo consultar la factura"
    rm "$response_file"
    exit 1
fi

monto_total=$(jq -r '.montoTotal' "$response_file")

monto_esperado=$(echo "1000 * $CANTIDAD" | bc)

echo ""
echo "Precio unitario: 1000"
echo "Cantidad:        $CANTIDAD"
echo "Monto esperado:  $monto_esperado"
echo "Monto factura:   $monto_total"

if [ "$(echo "$monto_total == $monto_esperado" | bc)" -eq 1 ]; then
    echo "✓ PASS - El monto total de la factura es correcto"
else
    echo "✗ FAIL - El monto total de la factura es incorrecto"
    echo "  Esperado: $monto_esperado"
    echo "  Recibido: $monto_total"
    rm "$response_file"
    exit 1
fi

subtotal=$(jq -r '.items[0].subtotal' "$response_file")

if [ "$(echo "$subtotal == $monto_esperado" | bc)" -eq 1 ]; then
    echo "✓ PASS - El subtotal es correcto"
else
    echo "✗ FAIL - El subtotal es incorrecto"
    echo "  Esperado: $monto_esperado"
    echo "  Recibido: $subtotal"
    rm "$response_file"
    exit 1
fi

rm "$response_file"



echo ""
echo "======================================"
echo "       ✓ PRUEBA EXITOSA"
echo "======================================"