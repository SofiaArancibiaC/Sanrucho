docker exec -i postgres-db psql -U postgres -d postgres < 01-init.sql
docker exec -i postgres-db psql -U postgres -d postgres < 02_usuarios.sql
docker exec -i postgres-db psql -U postgres -d postgres < 03_catalogo.sql
docker exec -i postgres-db psql -U postgres -d postgres < 04_pedidos.sql
docker exec -i postgres-db psql -U postgres -d postgres < 05_pagos.sql