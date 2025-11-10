# Script para iniciar el frontend de MotorPlus
# Este script configura la política de ejecución y inicia el servidor de desarrollo

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Iniciando MotorPlus Frontend   " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Configurar política de ejecución para el proceso actual
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

# Verificar si node_modules existe
if (!(Test-Path "node_modules")) {
    Write-Host "Instalando dependencias..." -ForegroundColor Yellow
    npm install
}

Write-Host ""
Write-Host "Iniciando servidor de desarrollo..." -ForegroundColor Green
Write-Host "El frontend estará disponible en: http://localhost:5173" -ForegroundColor Green
Write-Host ""

# Iniciar el servidor de desarrollo
npm run dev

