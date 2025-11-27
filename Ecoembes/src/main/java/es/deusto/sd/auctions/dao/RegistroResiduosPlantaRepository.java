package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.Estado;
import es.deusto.sd.auctions.entity.PlantaDeReciclaje;
import es.deusto.sd.auctions.entity.RegistroResiduosPlanta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroResiduosPlantaRepository extends JpaRepository<Estado,Long>{
    Optional<PlantaDeReciclaje> findByIdAndFecha(Long aLong, Date date);
}
