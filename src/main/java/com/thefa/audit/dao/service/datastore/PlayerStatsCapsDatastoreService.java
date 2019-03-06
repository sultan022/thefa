package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerStatsCapsDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatsCapsDatastoreService {

    private final PlayerStatsCapsDSRepository playerStatsCapsDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerStatsCapsDatastoreService(PlayerStatsCapsDSRepository playerStatsCapsDSRepository,
                                           ModelMapper modelMapper) {
        this.playerStatsCapsDSRepository = playerStatsCapsDSRepository;
        this.modelMapper = modelMapper;
    }
}
