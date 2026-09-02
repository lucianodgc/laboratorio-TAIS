curl -i \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@test.com",
    "direccion": "Calle 123",
    "telefono": "099123456",
    "items": [
      {
        "productoId": "6a987af03aecdaffe236d635", 
        "cantidad": 999
      }
    ]
  }' \
  http://localhost:8080/api/ordenes

 