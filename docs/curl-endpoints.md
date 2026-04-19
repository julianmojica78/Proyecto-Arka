# Curl de endpoints

Los ejemplos de este archivo usan el `api-gateway` en `http://localhost:8080`.

En PowerShell conviene usar `curl.exe` para evitar el alias de `curl`.

## 1. Login y health

### Login

```powershell
curl.exe -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"1234\"}"
```

Usuario semilla disponible en `docker/postgres/auth/script-auth.sql`:

- `admin / 1234`
- `client / 1234`

### Health

```powershell
curl.exe "http://localhost:8080/auth/health"
```

## 2. Inventory service

### Crear producto

```powershell
curl.exe -X POST "http://localhost:8080/products" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"name\":\"Teclado mecanico\",\"description\":\"Switch blue\",\"price\":199900,\"stock\":25,\"category\":\"perifericos\"}"
```

### Listar productos

```powershell
curl.exe "http://localhost:8080/products" -H "Authorization: Bearer <TOKEN>"
```

### Actualizar stock

```powershell
curl.exe -X PATCH "http://localhost:8080/products/1/stock" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"stock\":18,\"reason\":\"venta manual\"}"
```

## 3. Order service

### Crear orden

```powershell
curl.exe -X POST "http://localhost:8080/orders" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"customerId\":\"client\",\"items\":[{\"productId\":1,\"quantity\":2},{\"productId\":2,\"quantity\":1}]}"
```

### Modificar orden

Nota: este endpoint solo modifica ordenes en estado `PENDING`.

```powershell
curl.exe -X PUT "http://localhost:8080/orders/1" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"customerId\":\"client\",\"items\":[{\"productId\":1,\"quantity\":1},{\"productId\":3,\"quantity\":2}]}"
```

### Listar ordenes confirmadas por rango

Los parametros `start` y `end` deben ir en formato ISO-8601, por ejemplo `2026-04-01T00:00:00Z`.

```powershell
curl.exe "http://localhost:8080/orders/confirmed?start=2026-04-01T00:00:00Z&end=2026-04-15T23:59:59Z" -H "Authorization: Bearer <TOKEN>"
```

### Guardar carrito

Estados permitidos: `ACTIVE`, `ABANDONED`, `CONVERTED`.

```powershell
curl.exe -X POST "http://localhost:8080/carts" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"customerId\":\"client\",\"status\":\"ABANDONED\",\"items\":[{\"productId\":1,\"quantity\":1},{\"productId\":2,\"quantity\":3}]}"
```

### Listar carritos abandonados

```powershell
curl.exe "http://localhost:8080/carts/abandoned" -H "Authorization: Bearer <TOKEN>"
```

### Enviar recordatorio de carrito

```powershell
curl.exe -X POST "http://localhost:8080/carts/1/reminder" -H "Authorization: Bearer <TOKEN>"
```

## 4. Notification service

### Consultar notificaciones por orden

```powershell
curl.exe "http://localhost:8080/notifications/orders/1" -H "Authorization: Bearer <TOKEN>"
```

### Crear recordatorio de carrito

```powershell
curl.exe -X POST "http://localhost:8080/notifications/cart-reminders" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"cartId\":1,\"customerId\":\"client\",\"items\":[{\"productId\":1,\"quantity\":2},{\"productId\":2,\"quantity\":1}]}"
```

## 5. Report service

### Reporte CSV de productos con bajo stock

Si no envias `threshold`, el servicio usa `10`.

```powershell
curl.exe "http://localhost:8080/reports/products/low-stock?threshold=5" -H "Authorization: Bearer <TOKEN>"
```

### Reporte CSV de ventas semanales

```powershell
curl.exe "http://localhost:8080/reports/sales/weekly" -H "Authorization: Bearer <TOKEN>"
```

## 6. Puertos directos por servicio

Si quieres pegarle directo a cada microservicio en vez del gateway:

- `auth-service`: `http://localhost:8081`
- `inventory-service`: `http://localhost:8082`
- `order-service`: `http://localhost:8083`
- `notification-service`: `http://localhost:8084`
- `report-service`: `http://localhost:8085`

Las rutas son las mismas que aparecen arriba.
