# Constitución de Automatización API - AUTO_API_SCREENPLAY

Este documento define las leyes y estándares de ingeniería específicos para la automatización de la capa de servicios REST.

## 1. Misión de la Capa API
Validar la integridad, confiabilidad y contratos de los servicios del Catálogo mediante un flujo de ciclo de vida completo (CRUD), asegurando que el negocio pueda confiar en sus datos.

## 2. Principios del Actor de API
- **Identidad:** El actor principal se denomina "The System" (El Sistema), representando al consumidor de servicios técnicos.
- **Habilidades:** El actor posee la habilidad `CallAnApi` utilizando Serenity Rest.
- **Estado:** Se debe gestionar el estado del actor para encadenar las peticiones (ej: persistir el ID del evento creado para el GET/PUT/DELETE posterior).

## 3. Estándares REST & Screenplay
- **Verbos Obligatorios:** El flujo debe ejecutar secuencialmente: `POST` (Crear), `GET` (Leer), `PUT` (Actualizar) y `DELETE` (Eliminar/Desactivar).
- **Validaciones (Questions):**
  - Cada petición debe validar su **Código de Estado HTTP** (201, 200, 204).
  - Se debe validar el **Esquema JSON** de la respuesta para detectar cambios en el contrato.
  - Se debe validar que los datos retornados coincidan con los enviados (Integridad).

## 4. Arquitectura de Paquetes
- `tasks`: Clases que representan acciones REST (ej: `CrearEvento.conDetalles(...)`).
- `questions`: Clases para validar estados (ej: `ElCodigoDeRespuesta.es(201)`).
- `models`: POJOs/Records para la serialización de datos JSON.
- `utils`: Constantes de endpoints y headers.

## 5. Leyes de Clean Code (Taller)
- **Zero Comments:** El código debe ser tan descriptivo que los comentarios sean redundantes.
- **Semantic Names:** Métodos y variables deben usar términos del dominio de negocio (Catalog, Event, Price).
- **Encadenamiento:** El flujo Cucumber debe ser declarativo, ocultando la complejidad técnica de las URLs y Headers dentro de las Tasks.

---
**Ambiente de Ejecución:** http://localhost:50001
