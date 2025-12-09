package es.deusto.sd.auctions.dao;

import es.deusto.sd.auctions.entity.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {
    
    Optional<Personal> findByEmail(String email);
    boolean existsByEmail(String email);
}