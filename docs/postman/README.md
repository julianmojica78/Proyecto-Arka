# Postman collections

Archivos listos para importar en Postman:

- `Proyecto-Ark.local.postman_environment.json`
- `auth-service.postman_collection.json`
- `inventory-service.postman_collection.json`
- `order-service.postman_collection.json`
- `notification-service.postman_collection.json`
- `report-service.postman_collection.json`

Orden recomendado:

1. Importa el environment `Proyecto Ark - Local`.
2. Importa las colecciones.
3. Selecciona el environment en Postman.
4. Ejecuta `Login` en la coleccion de auth para guardar `token`.
5. Usa las demas colecciones.

Variables utiles:

- `baseUrl`: por defecto `http://localhost:8080`
- `token`: se llena automaticamente al hacer login
- `productId`, `orderId`, `cartId`: ids de ejemplo para editar o consultar
- `startDate`, `endDate`, `threshold`: parametros para reportes y consultas
