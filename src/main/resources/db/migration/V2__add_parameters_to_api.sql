-- Agregar columnas para path parameters y query parameters en la tabla api
ALTER TABLE api ADD COLUMN IF NOT EXISTS path_parameters TEXT;
ALTER TABLE api ADD COLUMN IF NOT EXISTS query_parameters TEXT;

-- Comentarios para documentar las columnas
COMMENT ON COLUMN api.path_parameters IS 'Lista de path parameters separados por comas. Ejemplo: id,orderId para /users/{id}/orders/{orderId}';
COMMENT ON COLUMN api.query_parameters IS 'Lista de query parameters separados por comas. Ejemplo: name,age,status para /users?name=John&age=25&status=active';

