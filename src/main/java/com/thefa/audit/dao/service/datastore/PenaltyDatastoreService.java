package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PenaltyDSRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PenaltyDatastoreService {

    private final PenaltyDSRepository penaltyDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PenaltyDatastoreService(PenaltyDSRepository penaltyDSRepository,
                                   ModelMapper modelMapper) {
        this.penaltyDSRepository = penaltyDSRepository;
        this.modelMapper = modelMapper;

    }
}
