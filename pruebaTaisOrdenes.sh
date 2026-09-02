#!/bin/bash

BASE_URL="http://localhost:8080/api"
PRODUCTOS_URL="$BASE_URL/productos"
ORDENES_URL="$BASE_URL/ordenes"

PASS=0
FAIL=0

# ======================================

# FUNCIONES AUXILIARES

# ======================================

pass() {
echo "✓ PASS - $1"
((PASS++))
}

fail() {
echo "✗ FAIL - $1"
((FAIL++))
}

# ======================================

# DATOS DE PRUEBA

# ======================================

TIMESTAMP=$(date +%s)

NOMBRE_PRODUCTO="Producto-Test-$TIMESTAMP"

# ======================================

# ENCABEZADO

# ======================================

echo "======================================"
echo "         PRUEBAS API ORDENES"
echo "======================================"

# ======================================

# 0. CREAR PRODUCTO DE PRUEBA

# ======================================

echo ""
echo "0. Creando producto para la prueba"

response_file=$(mktemp)

status=$(curl -s 
-o "$response_file" 
-w "%{http_code}" 
-X POST 
-H "Content-Type: application/json" 
-d "{
"nombre": "$NOMBRE_PRODUCTO",
"descripcion": "Producto para pruebas automatizadas",
"precio": 1000.0,
"stock": 50,
"imagenes": []
}" 
"$PRODUCTOS_URL")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 201 ]; then

```
producto_id=$(jq -r '.id' "$response_file")

if [ "$producto_id" != "null" ] && [ -n "$producto_id" ]; then
    pass "Producto de prueba creado"
    echo "  Producto ID = $producto_id"
else
    fail "El servidor devolvió 201 pero no se pudo obtener el ID del producto"
fi
```

else
fail "No se pudo crear el producto de prueba. HTTP $status"
fi

rm "$response_file"

# ======================================

# VERIFICAR PRODUCTO ID

# ======================================

if [ -z "$producto_id" ] || [ "$producto_id" = "null" ]; then

```
echo ""
echo "No se puede crear la orden porque no tenemos un producto válido."

echo ""
echo "======================================"
echo "              RESULTADO"
echo "======================================"

echo "PASS: $PASS"
echo "FAIL: $FAIL"

exit 1
```

fi

# ======================================

# 1. GET ALL ORDENES

# ======================================

echo ""
echo "1. GET /api/ordenes"

status=$(curl -s 
-o /dev/null 
-w "%{http_code}" 
"$ORDENES_URL")

if [ "$status" -eq 200 ]; then
pass "HTTP $status"
else
fail "Esperado 200, recibido $status"
fi

# ======================================

# 2. POST ORDEN

# ======================================

echo ""
echo "2. POST /api/ordenes"

response_file=$(mktemp)

status=$(curl -s 
-o "$response_file" 
-w "%{http_code}" 
-X POST 
-H "Content-Type: application/json" 
-d "{
"email": "cliente-$[TIMESTAMP@test.com](mailto:TIMESTAMP@test.com)",
"direccion": "Av. Italia 1234",
"telefono": "099123456",
"items": [
{
"productoId": "$producto_id",
"cantidad": 2
}
]
}" 
"$ORDENES_URL")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 201 ]; then

```
orden_id=$(jq -r '.id' "$response_file")

if [ "$orden_id" != "null" ] && [ -n "$orden_id" ]; then
    pass "Orden creada"
    echo "  Orden ID = $orden_id"
else
    fail "El servidor devolvió 201 pero no se pudo obtener el ID de la orden"
fi
```

else
fail "Esperado 201, recibido $status"
fi

rm "$response_file"

# ======================================

# VERIFICAR ORDEN ID

# ======================================

if [ -z "$orden_id" ] || [ "$orden_id" = "null" ]; then

```
echo ""
echo "No se puede continuar con GET y DETALLE."

echo ""
echo "======================================"
echo "              RESULTADO"
echo "======================================"

echo "PASS: $PASS"
echo "FAIL: $FAIL"

exit 1
```

fi

# ======================================

# 3. GET ORDEN BY ID

# ======================================

echo ""
echo "3. GET /api/ordenes/$orden_id"

response_file=$(mktemp)

status=$(curl -s 
-o "$response_file" 
-w "%{http_code}" 
"$ORDENES_URL/$orden_id")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 200 ]; then

```
id=$(jq -r '.id' "$response_file")
email=$(jq -r '.email' "$response_file")

if [ "$id" = "$orden_id" ] &&
   [ "$email" = "cliente-$TIMESTAMP@test.com" ]; then

    pass "Orden recuperada correctamente"

else
    fail "HTTP 200 pero los datos de la orden no coinciden"

fi
```

else
fail "Esperado 200, recibido $status"
fi

rm "$response_file"

# ======================================

# 4. GET DETALLE

# ======================================

echo ""
echo "4. GET /api/ordenes/$orden_id/detalle"

response_file=$(mktemp)

status=$(curl -s 
-o "$response_file" 
-w "%{http_code}" 
"$ORDENES_URL/$orden_id/detalle")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 200 ]; then
pass "Detalle de orden recuperado correctamente"
else
fail "Esperado 200, recibido $status"
fi

rm "$response_file"

