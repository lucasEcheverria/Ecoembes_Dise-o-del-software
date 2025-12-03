package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.PlantaDeReciclaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantaDeReciclajeRepository extends JpaRepository<PlantaDeReciclaje, Long> {

    PlantaDeReciclaje findById(long idPlanta);
}