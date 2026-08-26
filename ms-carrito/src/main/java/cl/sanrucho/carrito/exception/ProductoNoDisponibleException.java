package cl.sanrucho.carrito.exception;

public class ProductoNoDisponibleException extends RuntimeException {
    public ProductoNoDisponibleException(Long productoId) {
        super("El producto con id: " + productoId + " no está disponible en catálogo");
    }
}
