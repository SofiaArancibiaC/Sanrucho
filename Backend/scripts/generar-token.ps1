param(
    [string]$Email = "admin@gmail.com",
    [ValidateSet("Dueño", "Admin", "Cliente")]
    [string]$Rol = "Admin",
    [string]$Nombre = "Admin Sanrucho",
    [int]$Minutos = 60
)

# ============================================================
# Genera un JWT HS256 valido para probar ms-catalogo y ms-pedidos.
#
# El secreto debe ser IDENTICO al de los application.yml:
#   jwt.secret = c2FucnVjaG8...
#
# Ejemplos:
#   .\generar-token.ps1
#   .\generar-token.ps1 -Email cliente@gmail.com -Rol Cliente -Nombre "Camila"
#   .\generar-token.ps1 -Email dueno@gmail.com -Rol Dueno -Minutos 120
#
# Si PowerShell bloquea el script:
#   powershell -ExecutionPolicy Bypass -File .\generar-token.ps1
# ============================================================

$SecretBase64 = "c2FucnVjaG8tdGllbmRhLXNlY3JldC1rZXktMjAyNi1qd3Qtc2VjdXJpdHktaHMyNTY="
$Key = [System.Convert]::FromBase64String($SecretBase64)

function ConvertTo-Base64Url([byte[]]$Bytes) {
    return [System.Convert]::ToBase64String($Bytes).TrimEnd('=').Replace('+', '-').Replace('/', '_')
}

$Now = [System.DateTimeOffset]::UtcNow

$HeaderJson = '{"alg":"HS256","typ":"JWT"}'

$PayloadObject = [ordered]@{
    sub    = $Email
    rol    = $Rol
    nombre = $Nombre
    iat    = [int]$Now.ToUnixTimeSeconds()
    exp    = [int]$Now.AddMinutes($Minutos).ToUnixTimeSeconds()
}
$PayloadJson = $PayloadObject | ConvertTo-Json -Compress

$HeaderB64 = ConvertTo-Base64Url ([System.Text.Encoding]::UTF8.GetBytes($HeaderJson))
$PayloadB64 = ConvertTo-Base64Url ([System.Text.Encoding]::UTF8.GetBytes($PayloadJson))
$Data = "$HeaderB64.$PayloadB64"

$Hmac = [System.Security.Cryptography.HMACSHA256]::new($Key)
$Signature = ConvertTo-Base64Url ($Hmac.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($Data)))
$Hmac.Dispose()

$Token = "$Data.$Signature"

Write-Host ""
Write-Host "Token JWT generado" -ForegroundColor Green
Write-Host "  Email : $Email"
Write-Host "  Rol   : $Rol"
Write-Host "  Expira: $($Now.AddMinutes($Minutos).ToLocalTime())"
Write-Host ""
Write-Output $Token
Write-Host ""
Write-Host "Header Authorization para Swagger/curl:" -ForegroundColor Cyan
Write-Host "  Bearer $Token"
