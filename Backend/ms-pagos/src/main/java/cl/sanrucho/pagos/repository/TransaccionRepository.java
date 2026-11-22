package cl.sanrucho.pagos.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pagos.model.entity.Transaccion;
import cl.sanrucho.pagos.model.enums.EstadoTransaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    Page<Transaccion> findByIdPedido(Integer idPedido, Pageable pageable);

    Page<Transaccion> findByEstado(EstadoTransaccion estado, Pageable pageable);

    Page<Transaccion> findByIdPedidoAndEstado(
            Integer idPedido, EstadoTransaccion estado, Pageable pageable);

    List<Transaccion> findByIdPedidoOrderByFechaTransaccionDesc(Integer idPedido);

    boolean existsByIdPedidoAndEstado(Integer idPedido, EstadoTransaccion estado);
}
