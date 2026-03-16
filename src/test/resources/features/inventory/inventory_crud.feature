#language: es

@InventoryCRUD
Característica: Gestión de Reservas en el Inventario
  Como un cliente de la API de Ticketing
  Quiero realizar el ciclo de vida completo de una reserva
  Para asegurar la disponibilidad y persistencia de los asientos

  Escenario: Flujo completo de creación y consulta de reserva de asiento
    Dado que el "Sistema" tiene acceso a la API de Inventario
    Cuando realiza una reserva para:
      | eventId                              | seatId                               | customerId                           |
      | 550e8400-e29b-41d4-a716-446655440000 | 660e8400-e29b-41d4-a716-446655440001 | 770e8400-e29b-41d4-a716-446655440002 |
    Entonces el código de respuesta debe ser 201
    Y la respuesta debe contener un "reservationId" válido
    Cuando consulta la reserva por su ID
    Entonces el código de respuesta debe ser 200
    Y la reserva debe tener el estado "active"
