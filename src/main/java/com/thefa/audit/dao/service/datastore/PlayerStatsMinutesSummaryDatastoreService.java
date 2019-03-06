package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerStatsMinutesSummaryDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatsMinutesSummaryDatastoreService {

    private final PlayerStatsMinutesSummaryDSRepository playerStatsMinutesSummaryDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerStatsMinutesSummaryDatastoreService(PlayerStatsMinutesSummaryDSRepository playerStatsMinutesSummaryDSRepository,
                                                     ModelMapper modelMapper) {
        this.playerStatsMinutesSummaryDSRepository = playerStatsMinutesSummaryDSRepository;
        this.modelMapper = modelMapper;
    }
}
