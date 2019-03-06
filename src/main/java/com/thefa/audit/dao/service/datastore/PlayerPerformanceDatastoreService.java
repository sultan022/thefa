package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerPerformanceDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerPerformanceDatastoreService {

    private final PlayerPerformanceDSRepository playerPerformanceDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerPerformanceDatastoreService(PlayerPerformanceDSRepository playerPerformanceDSRepository,
                                             ModelMapper modelMapper) {
        this.playerPerformanceDSRepository = playerPerformanceDSRepository;
        this.modelMapper = modelMapper;

    }
}
