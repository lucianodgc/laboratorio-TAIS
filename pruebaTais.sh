#!/bin/bash

BASE_URL="http://localhost:8080/api/productos"

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

NOMBRE="Laptop-Test-$TIMESTAMP"
NOMBRE_UPDATE="Laptop-Actualizada-Test-$TIMESTAMP"

# ======================================
# ENCABEZADO
# ======================================

echo "======================================"
echo "       PRUEBAS API PRODUCTOS"
echo "======================================"

# ======================================
# 1. GET ALL
# ======================================

echo ""
echo "1. GET /api/productos"

status=$(curl -s \
    -o /dev/null \
    -w "%{http_code}" \
    "$BASE_URL")

if [ "$status" -eq 200 ]; then
    pass "HTTP $status"
else
    fail "Esperado 200, recibido $status"
fi

# ======================================
# 2. POST
# ======================================

echo ""
echo "2. POST /api/productos"

response_file=$(mktemp)

status=$(curl -s \
    -o "$response_file" \
    -w "%{http_code}" \
    -X POST \
    -H "Content-Type: application/json" \
    -d "{
        \"nombre\": \"$NOMBRE\",
        \"descripcion\": \"Laptop para pruebas automatizadas\",
        \"precio\": 1500.0,
        \"stock\": 10,
        \"imagenes\": []
    }" \
    "$BASE_URL")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 201 ]; then

    id=$(jq -r '.id' "$response_file")

    if [ "$id" != "null" ] && [ -n "$id" ]; then
        pass "Producto creado"
        echo "  ID = $id"
    else
        fail "El servidor devolvió 201 pero no se pudo obtener el ID"
    fi

else
    fail "Esperado 201, recibido $status"
fi

rm "$response_file"

# Si no conseguimos ID, no podemos continuar
if [ -z "$id" ] || [ "$id" = "null" ]; then

    echo ""
    echo "No se puede continuar con GET, PUT y PATCH."

    echo ""
    echo "======================================"
    echo "              RESULTADO"
    echo "======================================"

    echo "PASS: $PASS"
    echo "FAIL: $FAIL"

    exit 1
fi

# ======================================
# 3. GET BY ID
# ======================================

echo ""
echo "3. GET /api/productos/$id"

response_file=$(mktemp)

status=$(curl -s \
    -o "$response_file" \
    -w "%{http_code}" \
    "$BASE_URL/$id")

if [ "$status" -eq 200 ]; then
    pass "HTTP $status"
else
    fail "Esperado 200, recibido $status"
fi

rm "$response_file"

# ======================================
# 4. PUT
# ======================================

echo ""
echo "4. PUT /api/productos/$id"

response_file=$(mktemp)

status=$(curl -s \
    -o "$response_file" \
    -w "%{http_code}" \
    -X PUT \
    -H "Content-Type: application/json" \
    -d "{
        \"nombre\": \"$NOMBRE_UPDATE\",
        \"descripcion\": \"Descripcion actualizada\",
        \"precio\": 1800.0,
        \"stock\": 20,
        \"imagenes\": []
    }" \
    "$BASE_URL/$id")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 200 ]; then

    nombre=$(jq -r '.nombre' "$response_file")
    descripcion=$(jq -r '.descripcion' "$response_file")
    precio=$(jq -r '.precio' "$response_file")
    stock=$(jq -r '.stock' "$response_file")

    if [ "$nombre" = "$NOMBRE_UPDATE" ] &&
       [ "$descripcion" = "Descripcion actualizada" ] &&
       [ "$precio" = "1800.0" ] &&
       [ "$stock" = "20" ]; then

        pass "Producto actualizado correctamente"

    else
        fail "HTTP 200 pero los datos no fueron actualizados correctamente"
    fi

else
    fail "Esperado 200, recibido $status"
fi

rm "$response_file"

# ======================================
# 5. PATCH
# ======================================

echo ""
echo "5. PATCH /api/productos/$id"

response_file=$(mktemp)

status=$(curl -s \
    -o "$response_file" \
    -w "%{http_code}" \
    -X PATCH \
    -H "Content-Type: application/json" \
    -d '{
        "precio": 2000.0
    }' \
    "$BASE_URL/$id")

echo "Respuesta:"
jq . "$response_file"

if [ "$status" -eq 200 ]; then

    precio=$(jq -r '.precio' "$response_file")

    if [ "$precio" = "2000.0" ]; then
        pass "Precio actualizado correctamente"
    else
        fail "HTTP 200 pero el precio no fue actualizado"
    fi

else
    fail "Esperado 200, recibido $status"
fi

rm "$response_file"