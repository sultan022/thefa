package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerFutureFixturesDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerFutureFixturesDatastoreService {

    private final PlayerFutureFixturesDSRepository playerFutureFixturesDSRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public PlayerFutureFixturesDatastoreService(PlayerFutureFixturesDSRepository playerFutureFixturesDSRepository,
                                                ModelMapper modelMapper) {
        this.playerFutureFixturesDSRepository = playerFutureFixturesDSRepository;
        this.modelMapper = modelMapper;
    }
}
