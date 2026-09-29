package cl.sanrucho.catalogo.enums;


public enum EstadoProducto {
    ACTIVO("activo"),
    INACTIVO("inactivo"),
    DESCONTINUADO("descontinuado");
 
    private final String valor;
 
    EstadoProducto(String valor) {
        this.valor = valor;
    }
 
    public String getValor() {
        return valor;
    }
 
    public static EstadoProducto fromValor(String valor) {
        for (EstadoProducto estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de producto no válido: " + valor);
    }

}
