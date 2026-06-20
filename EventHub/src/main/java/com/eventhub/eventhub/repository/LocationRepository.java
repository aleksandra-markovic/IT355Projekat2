package com.eventhub.eventhub.repository;

import java.util.List;

import com.eventhub.eventhub.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCity(String city);
    
}
