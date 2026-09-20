#!/bin/bash

STOCK=50
CANTIDAD=10

PRODUCTOS_URL="http://localhost:8080/api/productos"
ORDENES_URL="http://localhost:8080/api/ordenes"

echo ""
echo "1. Creando producto con stock = $STOCK"

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{
    \"nombre\": \"Producto-idempotencia-Test\",
    \"descripcion\": \"Producto para prueba de idempotencia\",
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

echo "ID del producto: $producto_id"

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
fecha_orden=$(jq -r '.fechaCreacion' "$response_file")

echo "ID de la orden: $orden_id"
echo "Fecha de creación: $fecha_orden"

rm "$response_file"

echo ""
echo "3. Esperando procesamiento de RabbitMQ..."
sleep 2

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  "$PRODUCTOS_URL/$producto_id")

echo ""
echo "HTTP $status"
echo "Stock después del primer procesamiento:"
jq '.stock' "$response_file"

stock_primero=$(jq -r '.stock' "$response_file")

rm "$response_file"

echo "Stock: $stock_primero"

echo ""
echo "4. Reenviando el mismo evento a RabbitMQ..."

rabbit_response=$(curl -s \
  -u guest:guest \
  -H "Content-Type: application/json" \
  -X POST \
  -d "{
    \"properties\": {},
    \"routing_key\": \"ordenes.routingkey\",
    \"payload\": \"{\\\"id\\\":\\\"$orden_id\\\",\\\"estado\\\":\\\"CREATED\\\",\\\"fechaCreacion\\\":\\\"$fecha_orden\\\"}\",
    \"payload_encoding\": \"string\"
  }" \
  "http://localhost:15672/api/exchanges/%2F/ordenes.exchange/publish")

echo "Respuesta RabbitMQ:"
echo "$rabbit_response"

echo ""
echo "5. Esperando procesamiento del mensaje duplicado..."
sleep 2

response_file=$(mktemp)

status=$(curl -s \
  -o "$response_file" \
  -w "%{http_code}" \
  "$PRODUCTOS_URL/$producto_id")

echo ""
echo "Stock después del mensaje duplicado:"
jq '.stock' "$response_file"

stock_final=$(jq -r '.stock' "$response_file")

rm "$response_file"

if [ "$stock_primero" -eq 40 ]; then
    echo "✓ PASS - Primer procesamiento descontó correctamente 10 unidades"
else
    echo "✗ FAIL - Stock inesperado después del primer procesamiento"
fi

if [ "$stock_final" -eq 40 ]; then
    echo "✓ PASS - Mensaje duplicado no volvió a descontar stock"
else
    echo "✗ FAIL - El mensaje duplicado modificó el stock"
fi





