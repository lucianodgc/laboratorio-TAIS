curl -i \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@test.com",
    "direccion": "Calle 123",
    "telefono": "099123456",
    "items": [
      {
        "productoId": "ID_REAL_DEL_PRODUCTO", 
        "cantidad": 999
      }
    ]
  }' \
  http://localhost:8080/api/ordenes

  # En producto productoId ponemos el id real del producto que queremos probar