package cl.sanrucho.catalogo.repository;

import com.tienda.mscatalogo.entity.EspecificacionProducto;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;

public interface EspecificacionProductoRepository extends JpaRepository<EspecificacionProducto, Long> {

    List<EspecificacionProducto> findByProductoId(Long productoId);
 
    void deleteByProductoId(Long productoId);

}
