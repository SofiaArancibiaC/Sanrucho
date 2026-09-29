package cl.sanrucho.pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.sanrucho.pedidos.model.entity.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import feign.Param;


public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    Page<Pedido> findByUsuarioId(Integer usuarioId, Pageable pageable);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByEstado(EstadoPedido estado);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.numeroPedido LIKE CONCAT('ORD-', :anio, '-%')")
    long countByAnio(@Param("anio") String anio);
}
