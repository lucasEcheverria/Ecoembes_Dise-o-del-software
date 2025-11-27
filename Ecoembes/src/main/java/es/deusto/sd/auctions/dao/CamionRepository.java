package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {
    //Verificar si existe un camión con este id
    boolean existsById(Long id);
}
