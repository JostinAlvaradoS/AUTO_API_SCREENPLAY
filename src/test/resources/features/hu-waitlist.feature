# language: es
@HU-Waitlist @ListaEspera
Característica: Sistema de Lista de Espera Inteligente
  Como usuario interesado en un evento agotado
  Quiero registrarme en la lista de espera
  Para recibir una asignación de asiento cuando uno esté disponible

  @RegistroExitoso
  Escenario: Registro exitoso en lista de espera
    Dado que el evento "Concierto Rock 2026" tiene stock igual a cero
    Cuando el usuario "jostin@example.com" se registra en la lista de espera del evento
    Entonces el sistema lo registra correctamente

  @TicketsDisponibles
  Escenario: Intento de registro con tickets disponibles
    Dado que el evento "Concierto Rock 2026" tiene tickets disponibles
    Cuando el usuario "jostin@example.com" intenta unirse a la lista de espera del evento
    Entonces el sistema indica que aún hay tickets disponibles

  @RegistroDuplicado
  Escenario: Registro duplicado en la misma lista
    Dado que "jostin@example.com" ya está registrado en la lista del evento "Concierto Rock 2026"
    Cuando el mismo correo intenta registrarse nuevamente para el mismo evento
    Entonces el sistema indica que ya está en la lista de espera

  @AsignacionAutomatica
  Escenario: Asignación automática al expirar una reserva
    Dado que "jostin@example.com" es el primero en la lista de espera del evento "Concierto Rock 2026"
    Cuando el tiempo de pago inicial caduca
    Entonces el sistema crea una orden automática para "jostin@example.com"
    Y actualiza el estado de la entrada a Asignado
    Y envía un correo con el enlace de pago con validez de 30 minutos

  @LiberacionConSiguiente
  Escenario: Liberación por inacción con siguiente en cola
    Dado que "jostin@example.com" fue asignado y no pagó en 30 minutos
    Y "segundo@example.com" es el siguiente en la lista de espera
    Cuando el sistema detecta la inacción
    Entonces el sistema marca la entrada de "jostin@example.com" como Expirado
    Y reasigna el asiento directamente a "segundo@example.com" sin liberarlo al pool general
    Y envía correo de pago a "segundo@example.com" con validez de 30 minutos

  @LiberacionSinCola
  Escenario: Liberación por inacción con cola vacía
    Dado que "jostin@example.com" fue asignado y no pagó en 30 minutos
    Y no hay más usuarios en la lista de espera del evento
    Cuando el sistema detecta la inacción
    Entonces el sistema cancela la orden y libera el asiento al pool general
