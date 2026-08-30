package cl.sanrucho.pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pagos.model.entity.MetodoPago;
import cl.sanrucho.pagos.model.entity.MetodoPago.NombreMetodoPago;

import java.util.List;


public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer>{

    Optional<MetodoPago> findById(Integer id);

    List<MetodoPago> findByNombre(NombreMetodoPago nombre);

}
