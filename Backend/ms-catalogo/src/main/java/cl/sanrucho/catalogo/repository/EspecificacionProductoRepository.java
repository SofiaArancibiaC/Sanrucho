package cl.sanrucho.catalogo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import cl.sanrucho.catalogo.model.entity.EspecificacionProducto;

public interface EspecificacionProductoRepository extends JpaRepository<EspecificacionProducto, Long> {

    List<EspecificacionProducto> findByProductoId(Long productoId);

    void deleteByProductoId(Long productoId);

}
