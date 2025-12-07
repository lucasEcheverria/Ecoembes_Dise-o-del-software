package es.deusto.sd.auctions.dao;
import es.deusto.sd.auctions.entity.Contenedor;
import es.deusto.sd.auctions.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    // Spring genera automáticamente la query basándose en el nombre
    List<Estado> findByContenedorAndFechaBetween(
            Contenedor contenedor,
            Date fechaInicio,
            Date fechaFin
    );
}