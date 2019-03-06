package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PenaltyPlayerDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PenaltyPlayerDatastoreService {

    private final PenaltyPlayerDSRepository penaltyPlayerDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PenaltyPlayerDatastoreService(PenaltyPlayerDSRepository penaltyPlayerDSRepository,
                                         ModelMapper modelMapper) {

        this.penaltyPlayerDSRepository = penaltyPlayerDSRepository;
        this.modelMapper = modelMapper;
    }
}
