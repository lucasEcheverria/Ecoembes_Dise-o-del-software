package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.Estado;
import es.deusto.sd.auctions.entity.PlantaDeReciclaje;
import es.deusto.sd.auctions.entity.RegistroResiduosPlanta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface RegistroResiduosPlantaRepository extends JpaRepository<Estado,Long>{
    Optional<PlantaDeReciclaje> findByIdAndFecha(Long aLong, Date date);
}
