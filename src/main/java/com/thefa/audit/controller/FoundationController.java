package com.thefa.audit.controller;

import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.dao.service.datastore.LiveObservationDatastoreService;
import com.thefa.audit.model.dto.datastore.PlayerObservationDTO;
import com.thefa.common.exception.RecordNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/foundation")
@CommonsLog
@Api(value = "Foundation", description = "Foundation Controller")
public class FoundationController {

    private final LiveObservationDatastoreService observationDatastoreService;
    private final PlayerService playerService;

    @Autowired
    public FoundationController(
            LiveObservationDatastoreService observationDatastoreService,
            PlayerService playerService) {

        this.observationDatastoreService = observationDatastoreService;
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}/observations")
    @ApiOperation("Retrieve Player's Observations.")
    public List<PlayerObservationDTO> playerObservations(@PathVariable() String playerId) {

        if (!playerService.playerExists(playerId)) {
            throw new RecordNotFoundException();
        }

        return observationDatastoreService.getPlayerObservations(playerId);

    }


}
