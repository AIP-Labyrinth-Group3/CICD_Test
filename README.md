# Labyrinth Client Prototype

![CI Status](https://github.com/AIP-Labyrinth-Group3/labyrinth-client-prototype/workflows/Client%20Prototype%20CI/badge.svg)

JavaFX Prototyp für "Das verrückte Labyrinth".

## Technologie

- Java 22
- JavaFX 21
- Maven

## Build & Run
```bash
# Kompilieren
mvn clean compile

# Ausführen
mvn javafx:run

# JAR erstellen
mvn package
```

## CI/CD

- CI: Automatischer Build bei Push/PR
- CD: Artifact-Upload bei Push auf main
- Artifacts: [GitHub Actions](https://github.com/AIP-Labyrinth-Group3/labyrinth-client-prototype/actions)
