package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.TeamDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamDatastoreService {

    private final TeamDSRepository teamDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamDatastoreService(TeamDSRepository teamDSRepository,
                                ModelMapper modelMapper) {
        this.teamDSRepository = teamDSRepository;
        this.modelMapper = modelMapper;
    }
}
