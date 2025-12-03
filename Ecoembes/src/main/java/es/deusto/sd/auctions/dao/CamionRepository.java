package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {
    //Verificar si existe un camión con este id
    boolean existsById(Long id);
}
