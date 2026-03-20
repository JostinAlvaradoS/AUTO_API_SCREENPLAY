# language: es

Característica: Gestión de eventos - HU05
  Escenario CRUD Completo para Taller Automatización API

@CRUD @Mastery
Escenario: Ciclo de vida completo de un evento musical mediante API (CRUD)
  Dado que el Administrador tiene acceso al servicio de Catálogo
  Cuando crea un nuevo evento con el nombre "Rock Fest 2026" y precio 150.0
  Entonces el evento debe ser creado exitosamente con un ID válido
  Y al consultar el evento por su ID el nombre debe ser "Rock Fest 2026"
  Y al actualizar el nombre del evento a "Rock Fest 2026 - Sold Out"
  Entonces el cambio debe persistirse correctamente en el sistema
  Y al desactivar el evento mediante el proceso de borrado lógico
  Entonces el evento ya no debe figurar como activo para la venta
