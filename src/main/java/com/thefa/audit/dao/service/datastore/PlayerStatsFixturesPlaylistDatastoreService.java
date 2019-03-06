package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerStatsFixturesPlaylistDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatsFixturesPlaylistDatastoreService {

    private final PlayerStatsFixturesPlaylistDSRepository playerStatsFixturesPlaylistDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerStatsFixturesPlaylistDatastoreService(PlayerStatsFixturesPlaylistDSRepository playerStatsFixturesPlaylistDSRepository,
                                                       ModelMapper modelMapper) {
        this.playerStatsFixturesPlaylistDSRepository = playerStatsFixturesPlaylistDSRepository;
        this.modelMapper = modelMapper;
    }
}
