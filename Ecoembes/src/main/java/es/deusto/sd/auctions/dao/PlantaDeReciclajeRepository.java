package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.PlantaDeReciclaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaDeReciclajeRepository extends JpaRepository<PlantaDeReciclaje, Long> {

    PlantaDeReciclaje findById(long idPlanta);
}