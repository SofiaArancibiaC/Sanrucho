package cl.sanrucho.pedidos.model.enums;

/**
 * Estados posibles de un pedido y sus transiciones validas:
 *
 * Pendiente -> Pagado | Cancelado
 * Pagado    -> Enviado | Cancelado
 * Enviado   -> Entregado
 * Entregado -> (final)
 * Cancelado -> (final)
 */
public enum EstadoPedido {
    Pendiente,
    Pagado,
    Enviado,
    Entregado,
    Cancelado
}
