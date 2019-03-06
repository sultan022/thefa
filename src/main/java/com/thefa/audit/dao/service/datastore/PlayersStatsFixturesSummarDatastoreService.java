package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayersStatsFixturesSummaryDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayersStatsFixturesSummarDatastoreService {

    private final PlayersStatsFixturesSummaryDSRepository playersStatsFixturesSummaryDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayersStatsFixturesSummarDatastoreService(PlayersStatsFixturesSummaryDSRepository playersStatsFixturesSummaryDSRepository,
                                                      ModelMapper modelMapper) {
        this.playersStatsFixturesSummaryDSRepository = playersStatsFixturesSummaryDSRepository;
        this.modelMapper = modelMapper;
    }
}
