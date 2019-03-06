package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerObservationDSRepository;
import com.thefa.audit.model.dto.datastore.PlayerObservationDTO;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveObservationDatastoreService {

    private final PlayerObservationDSRepository observationRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public LiveObservationDatastoreService(
            PlayerObservationDSRepository observationRepository,
            ModelMapper modelMapper) {

        this.observationRepository = observationRepository;
        this.modelMapper = modelMapper;

    }

    public List<PlayerObservationDTO> getPlayerObservations(String playerId) {

        return StreamEx.of(observationRepository.findByPlayerId(playerId))
                .map(kind -> modelMapper.map(kind, PlayerObservationDTO.class)).toList();
    }


}
