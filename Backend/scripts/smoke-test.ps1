# ============================================================
# smoke-test.ps1
# Prueba funcional end-to-end contra el API Gateway (puerto 9000).
#
# Requisitos:
#   - PostgreSQL con los scripts de init-multi-db cargados
#   - eureka, ms-catalogo, ms-pedidos y api-gateway en ejecucion
#
# Uso:
#   powershell -ExecutionPolicy Bypass -File .\smoke-test.ps1
# ============================================================

$ErrorActionPreference = 'Stop'
$base = 'http://localhost:9000'
$scriptDir = $PSScriptRoot
$resultados = New-Object System.Collections.Generic.List[object]

function Registrar([string]$nombre, [bool]$ok, [string]$detalle) {
    $resultados.Add([pscustomobject]@{ Prueba = $nombre; OK = $ok; Detalle = $detalle })
    $color = if ($ok) { 'Green' } else { 'Red' }
    Write-Host ("[{0}] {1} - {2}" -f $(if ($ok) { 'PASS' } else { 'FAIL' }), $nombre, $detalle) -ForegroundColor $color
}

function Nuevo-Token([string]$email, [string]$rol) {
    $script = Join-Path $scriptDir 'generar-token.ps1'
    $token = & $script -Email $email -Rol $rol | Select-Object -Last 1
    return $token.ToString().Trim()
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [string]$Token,
        $Body
    )

    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }

    $params = @{
        Uri             = "$base$Path"
        Method          = $Method
        Headers         = $headers
        UseBasicParsing = $true
        TimeoutSec      = 30
    }

    if ($null -ne $Body) {
        $params['ContentType'] = 'application/json'
        $params['Body'] = ($Body | ConvertTo-Json -Depth 6)
    }

    try {
        $response = Invoke-WebRequest @params
        return @{ Status = [int]$response.StatusCode; Content = $response.Content }
    } catch {
        $webResponse = $_.Exception.Response
        if ($null -eq $webResponse) { throw }

        $stream = $webResponse.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $content = $reader.ReadToEnd()
        return @{ Status = [int]$webResponse.StatusCode; Content = $content }
    }
}

function Get-Stock([int]$idProducto, [string]$token) {
    $r = Invoke-Api -Method 'GET' -Path "/api/v1/catalogo/productos/$idProducto" -Token $token
    return (ConvertFrom-Json $r.Content).stock
}

Write-Host ''
Write-Host '=== Generando tokens de prueba ===' -ForegroundColor Cyan
$admin = Nuevo-Token 'admin@gmail.com' 'Admin'
$cliente = Nuevo-Token 'camila@gmail.com' 'Cliente'
Write-Host "Token Admin   : $($admin.Substring(0, 25))..." -ForegroundColor DarkGray
Write-Host "Token Cliente : $($cliente.Substring(0, 25))..." -ForegroundColor DarkGray

# ─── 1. Seguridad ────────────────────────────────────────────────────────────
$r = Invoke-Api -Method 'GET' -Path '/api/v1/catalogo/productos' -Token $null
Registrar 'GET catalogo sin token -> 401' ($r.Status -eq 401) "status=$($r.Status)"

$r = Invoke-Api -Method 'GET' -Path '/api/v1/catalogo/productos' -Token $cliente
Registrar 'GET catalogo con token Cliente -> 200' ($r.Status -eq 200) "status=$($r.Status)"

$payload = @{
    codigo      = 'SMOKE-PROD-001'
    idCategoria = 2
    nombre      = 'Producto Smoke Test'
    precio      = 1000
    stock       = 10
    stockCritico = 1
    estado      = 'Activo'
}
$r = Invoke-Api -Method 'POST' -Path '/api/v1/catalogo/productos' -Token $cliente -Body $payload
Registrar 'POST producto con Cliente -> 403' ($r.Status -eq 403) "status=$($r.Status)"

# ─── 2. CRUD catalogo ────────────────────────────────────────────────────────
$r = Invoke-Api -Method 'POST' -Path '/api/v1/catalogo/productos' -Token $admin -Body $payload
$productoSmoke = if ($r.Status -eq 201) { ConvertFrom-Json $r.Content } else { $null }
Registrar 'POST producto con Admin -> 201' ($r.Status -eq 201) "status=$($r.Status), id=$($productoSmoke.id)"

$r = Invoke-Api -Method 'POST' -Path '/api/v1/catalogo/productos' -Token $admin -Body $payload
Registrar 'POST producto duplicado -> 409' ($r.Status -eq 409) "status=$($r.Status)"

if ($productoSmoke) {
    $r = Invoke-Api -Method 'PATCH' -Path "/api/v1/catalogo/productos/$($productoSmoke.id)/stock" `
        -Token $admin -Body @{ cantidad = -999 }
    Registrar 'PATCH stock que deja negativo -> 400' ($r.Status -eq 400) "status=$($r.Status)"
}

# ─── 3. Pedido: creacion, Feign y stock ──────────────────────────────────────
$stockAntes = Get-Stock 1 $admin
$pedidoPayload = @{
    idUsuario = 3
    items     = @(
        @{ idProducto = 1; cantidad = 2 },
        @{ idProducto = 5; cantidad = 1 }
    )
}
$r = Invoke-Api -Method 'POST' -Path '/api/v1/pedidos' -Token $cliente -Body $pedidoPayload
$pedido = if ($r.Status -eq 201) { ConvertFrom-Json $r.Content } else { $null }
$totalEsperado = (2 * 19990) + 5990
Registrar 'POST pedido con Cliente -> 201' ($r.Status -eq 201) "status=$($r.Status), id=$($pedido.id)"
if ($pedido) {
    Registrar 'Total del pedido calculado en servidor' ([decimal]$pedido.total -eq [decimal]$totalEsperado) "total=$($pedido.total), esperado=$totalEsperado"
    Registrar 'Pedido en estado Pendiente' ($pedido.estado -eq 'Pendiente') "estado=$($pedido.estado)"
}

$stockDespues = Get-Stock 1 $admin
Registrar 'Stock descontado en ms-catalogo' (($stockAntes - $stockDespues) -eq 2) "antes=$stockAntes, despues=$stockDespues"

$r = Invoke-Api -Method 'GET' -Path "/api/v1/pedidos/$($pedido.id)" -Token $cliente
$detalle = if ($r.Status -eq 200) { ConvertFrom-Json $r.Content } else { $null }
$nombreFeign = if ($detalle) { $detalle.detalles[0].nombreProducto } else { '' }
Registrar 'GET pedido resuelve nombre via Feign con JWT' ($r.Status -eq 200 -and $nombreFeign -like '*Hello Kitty*') "status=$($r.Status), producto='$nombreFeign'"

# ─── 4. Transiciones de estado ──────────────────────────────────────────────
$pedidoB = @{
    idUsuario = 3
    items     = @(@{ idProducto = 1; cantidad = 1 })
}
$stockB = Get-Stock 1 $admin
$r = Invoke-Api -Method 'POST' -Path '/api/v1/pedidos' -Token $cliente -Body $pedidoB
$pedido2 = if ($r.Status -eq 201) { ConvertFrom-Json $r.Content } else { $null }

$r = Invoke-Api -Method 'PATCH' -Path "/api/v1/pedidos/$($pedido2.id)/estado" -Token $cliente -Body @{ estado = 'Entregado' }
Registrar 'Transicion Pendiente -> Entregado -> 400' ($r.Status -eq 400) "status=$($r.Status)"

$r = Invoke-Api -Method 'PATCH' -Path "/api/v1/pedidos/$($pedido2.id)/estado" -Token $cliente -Body @{ estado = 'Pagado' }
Registrar 'Transicion Pendiente -> Pagado -> 200' ($r.Status -eq 200) "status=$($r.Status)"

$r = Invoke-Api -Method 'PATCH' -Path "/api/v1/pedidos/$($pedido2.id)/estado" -Token $cliente -Body @{ estado = 'Cancelado' }
Registrar 'Transicion Pagado -> Cancelado -> 200' ($r.Status -eq 200) "status=$($r.Status)"

$stockRepuesto = Get-Stock 1 $admin
Registrar 'Cancelacion repone el stock' ($stockRepuesto -eq $stockB) "antes=$stockB, despues=$stockRepuesto"

# ─── 5. Borrado de pedido ───────────────────────────────────────────────────
$r = Invoke-Api -Method 'DELETE' -Path "/api/v1/pedidos/$($pedido.id)" -Token $cliente
Registrar 'DELETE pedido con Cliente -> 403' ($r.Status -eq 403) "status=$($r.Status)"

$stockAntesDelete = Get-Stock 1 $admin
$r = Invoke-Api -Method 'DELETE' -Path "/api/v1/pedidos/$($pedido.id)" -Token $admin
Registrar 'DELETE pedido Pendiente con Admin -> 204' ($r.Status -eq 204) "status=$($r.Status)"
$stockTrasDelete = Get-Stock 1 $admin
Registrar 'Borrado de pedido Pendiente repone stock' (($stockTrasDelete - $stockAntesDelete) -eq 2) "antes=$stockAntesDelete, despues=$stockTrasDelete"

# ─── 6. Documentacion y gateway ────────────────────────────────────────────
$r = Invoke-Api -Method 'GET' -Path '/catalogo/v3/api-docs' -Token $admin
Registrar 'OpenAPI de catalogo via gateway -> 200' ($r.Status -eq 200) "status=$($r.Status)"

$r = Invoke-Api -Method 'GET' -Path '/pedidos/v3/api-docs' -Token $admin
Registrar 'OpenAPI de pedidos via gateway -> 200' ($r.Status -eq 200) "status=$($r.Status)"

# ─── 7. Limpieza ────────────────────────────────────────────────────────────
if ($productoSmoke) {
    $r = Invoke-Api -Method 'DELETE' -Path "/api/v1/catalogo/productos/$($productoSmoke.id)" -Token $admin
    Write-Host "Limpieza: producto smoke eliminado (status=$($r.Status))" -ForegroundColor DarkGray
}

# ─── Resumen ────────────────────────────────────────────────────────────────
$total = $resultados.Count
$ok = ($resultados | Where-Object { $_.OK }).Count
$fallos = $total - $ok

Write-Host ''
Write-Host "RESULTADO: $ok/$total pruebas OK, $fallos fallos" -ForegroundColor $(if ($fallos -eq 0) { 'Green' } else { 'Red' })
Write-Host ''

if ($fallos -gt 0) {
    $resultados | Where-Object { -not $_.OK } | Format-Table -AutoSize
    exit 1
}
exit 0
