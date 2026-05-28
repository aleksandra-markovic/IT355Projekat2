package com.eventhub.eventhub.repository;

import java.util.List;

import com.eventhub.eventhub.model.Location;

public interface LocationRepository {

    List<Location> findByCity(String city);
    
}
