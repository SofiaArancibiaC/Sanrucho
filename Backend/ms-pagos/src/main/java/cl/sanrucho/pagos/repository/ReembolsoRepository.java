package cl.sanrucho.pagos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pagos.model.entity.Reembolso;


public interface ReembolsoRepository extends JpaRepository<Reembolso, Integer>{

    Optional<Reembolso> findById(Integer id);

    List<Reembolso> findByTransaccionId(Integer transaccionId);

}
