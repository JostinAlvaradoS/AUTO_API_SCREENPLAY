# AUTO_API_SCREENPLAY - Automatización de Pruebas de API (Screenplay Pattern)

Este proyecto contiene una suite de pruebas automatizadas para la gestión de eventos, implementada utilizando **Serenity BDD**, **Cucumber** y el patrón de diseño **Screenplay**.

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes componentes:

*   **Java JDK 17** (Recomendado: Temurin 17)
*   **Docker** y **Docker Compose**
*   **Node.js 18+** (Para el frontend)
*   **pnpm** (Opcional, se puede usar npm)
*   **Git**

## 🚀 Guía de Configuración y Ejecución

Sigue estos pasos en orden para preparar el entorno y ejecutar las pruebas:

### 1. Clonar el Repositorio y Submódulos

El proyecto utiliza submódulos para las especificaciones compartidas. Clona el repositorio e inicializa los submódulos de la siguiente manera:

```bash
git clone https://github.com/JostinAlvaradoS/AUTO_API_SCREENPLAY.git
cd AUTO_API_SCREENPLAY
git submodule update --init --recursive
```

### 2. Levantar la Infraestructura (Docker)

Entra en la carpeta de infraestructura y levanta los servicios necesarios (Bases de datos, Kafka, etc.):

```bash
cd shared-specs/infra
docker-compose up -d
cd ../..
```

### 3. Levantar el Frontend

Navega a la carpeta del frontend, instala las dependencias y ejecútalo:

```bash
cd shared-specs/frontend
pnpm install  # o npm install
pnpm dev      # o npm run dev
cd ../..
```

### 4. Construir y Ejecutar las Pruebas

Ahora puedes compilar el proyecto de automatización y ejecutar los tests de API:

```bash
# Limpiar y construir el proyecto
gradle build

# Ejecutar los tests de Serenity/Cucumber
gradle test
```

### 5. Generar y Ver Reportes

Después de ejecutar las pruebas, genera el reporte agregado de Serenity:

```bash
gradle aggregate
```

El reporte detallado estará disponible en:
`target/site/serenity/index.html`

## 🛠 Estructura del Proyecto

*   `src/test/java`: Contiene toda la lógica de automatización (Tasks, Questions, Models, Runners, Step Definitions).
*   `src/test/resources/features`: Archivos `.feature` (Gherkin).
*   `shared-specs/`: Submódulo con la definición de infraestructura y frontend.

---
**Nota:** Toda la lógica de negocio ha sido movida a la carpeta `src/test/java` para mantener `src/main/java` libre de código de prueba, siguiendo los requerimientos del proyecto.
