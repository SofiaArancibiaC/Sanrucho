package cl.sanrucho.inventario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.inventario.dto.MovimientoInventarioResponse;
import cl.sanrucho.inventario.mapper.MovimientoInventarioMapper;
import cl.sanrucho.inventario.model.MovimientoInventario;
import cl.sanrucho.inventario.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de consulta del historial de movimientos de inventario.
 *
 * Este servicio es de solo lectura a proposito. Los movimientos son la
 * trazabilidad del stock: cada uno guarda la cantidad anterior y la nueva, y
 * borrarlos dejaria el inventario sin forma de reconstruir de donde salio cada
 * unidad. Para corregir un movimiento erroneo existe el ajuste, que agrega un
 * movimiento nuevo en vez de destruir el registro anterior.
 *
 * La escritura de movimientos tampoco vive aqui. Todas las operaciones que
 * mueven stock (entrada, salida, ajuste, reserva y liberacion) las aplica
 * InventarioService, porque son las que actualizan el stock, dejan el registro
 * del movimiento y generan el evento, todo en la misma transaccion. Exponer un
 * alta de movimiento suelta permitiria crear historial sin tocar el stock, y el
 * inventario quedaria desalineado.
 */
@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private static final String ENTIDAD = "Movimientos de inventario";

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final MovimientoInventarioMapper movimientoInventarioMapper;

    public List<MovimientoInventarioResponse> findAll() {
        return movimientoInventarioMapper.toResponseList(movimientoInventarioRepository.findAll());
    }

    public MovimientoInventarioResponse findById(int id) {
        return movimientoInventarioMapper.toResponse(getMovimientoById(id));
    }

    public List<MovimientoInventarioResponse> findByInventarioId(int inventarioId) {
        return movimientoInventarioMapper.toResponseList(
                movimientoInventarioRepository.findByInventarioId(inventarioId));
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private MovimientoInventario getMovimientoById(int id) {
        return movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "ID", id));
    }
}
