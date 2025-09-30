@echo off
echo Démarrage du backend Spring Boot...
cd /d "%~dp0"

REM Essayer avec gradlew.bat d'abord
if exist gradlew.bat (
    echo Utilisation de gradlew.bat...
    call gradlew.bat bootRun
) else if exist mvnw.cmd (
    echo Utilisation de mvnw.cmd...
    call mvnw.cmd spring-boot:run
) else (
    echo Aucun wrapper trouvé, tentative avec Gradle/Maven globaux...
    if command -v gradle 2>nul (
        gradle bootRun
    ) else if command -v mvn 2>nul (
        mvn spring-boot:run
    ) else (
        echo Erreur: Ni Gradle ni Maven ne sont disponibles
        pause
    )
)

pause