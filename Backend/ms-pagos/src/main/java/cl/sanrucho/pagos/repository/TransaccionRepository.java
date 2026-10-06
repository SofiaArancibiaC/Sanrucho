package cl.sanrucho.pagos.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pagos.model.entity.Transaccion;
import java.util.List;


public interface TransaccionRepository extends JpaRepository<Transaccion, Integer>{

    List<Transaccion> findByPedidoId(Integer pedidoId);

}
