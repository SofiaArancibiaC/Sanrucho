package cl.sanrucho.catalogo.enums;

public enum TipoEvento {

    PRODUCTO_CREADO("producto_creado"),
    PRODUCTO_ACTUALIZADO("producto_actualizado"),
    PRECIO_CAMBIADO("precio_cambiado"),
    STOCK_CAMBIADO("stock_cambiado"),
    PRODUCTO_DESCONTINUADO("producto_descontinuado");
 
    private final String valor;
 
    TipoEvento(String valor) {
        this.valor = valor;
    }
 
    public String getValor() {
        return valor;
    }
 
    public static TipoEvento fromValor(String valor) {
        for (TipoEvento tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de evento no válido: " + valor);
    }
}
