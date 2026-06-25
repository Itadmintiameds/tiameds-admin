param(
    [string]$EnvFile = ".env"
)

if (Test-Path $EnvFile) {
    Write-Host "Loading environment variables from $EnvFile" -ForegroundColor Cyan
    Get-Content $EnvFile | Where-Object { $_ -match '^([^#=]+)=(.*)$' } | ForEach-Object {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
} else {
    Write-Host "Warning: $EnvFile not found. Running without it." -ForegroundColor Yellow
}

Write-Host "Starting Spring Boot application with dev profile..." -ForegroundColor Green
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
