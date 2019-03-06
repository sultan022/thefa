package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerStatsDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatsDatastoreService {

    private final PlayerStatsDSRepository playerStatsDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerStatsDatastoreService(PlayerStatsDSRepository playerStatsDSRepository,
                                       ModelMapper modelMapper) {
        this.playerStatsDSRepository = playerStatsDSRepository;
        this.modelMapper = modelMapper;
    }
}
