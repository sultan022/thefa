package com.thefa.audit.controller;

import com.thefa.audit.dao.service.datastore.PlayerSportsCodeDatastoreService;
import com.thefa.audit.model.dto.tectac.TecTacAggregatedDTO;
import com.thefa.audit.model.dto.tectac.TecTacDetailDTO;
import com.thefa.audit.model.shared.TeamType;
import com.thefa.audit.model.shared.TecTac;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.exception.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tectac")
@Api(value = "Tec Tac", description = "Tec Tac operations")
public class TecTacController {

    private final PlayerSportsCodeDatastoreService playerSportsCodeDatastoreService;

    @Autowired
    public TecTacController(PlayerSportsCodeDatastoreService playerSportsCodeDatastoreService) {
        this.playerSportsCodeDatastoreService = playerSportsCodeDatastoreService;
    }

    @ApiOperation(value = "Get TecTac Aggregated Details")
    @GetMapping("/{playerId}")
    public List<TecTacAggregatedDTO> getTecTac(
            @PathVariable() String playerId,
            @RequestParam(value = "season") String season,
            @RequestParam(value = "position") Integer position,
            @RequestParam(value = "matchType", required = false) TeamType teamType
    ) {
        return playerSportsCodeDatastoreService.getAggregatedTecTacs(playerId, season, position, teamType);
    }

    @ApiOperation(value = "Get TecTac Details")
    @GetMapping("/{playerId}/videos")
    public PageResponse<TecTacDetailDTO> getTecTacDetail(
            @PathVariable() String playerId,
            @RequestParam(value = "tectac") String tecTac,
            @RequestParam(value = "season") String season,
            @RequestParam(value = "position") Integer position,
            @RequestParam(value = "matchType", required = false) TeamType teamType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        TecTac tecTacEnum = TecTac.fromFriendlyName(tecTac);
        if (tecTacEnum == null) {
            throw new BadRequestException("Invalid TecTac value");
        }

        return playerSportsCodeDatastoreService.getTecTacDetails(
                playerId, tecTacEnum, season, position, teamType, page, size
        );
    }

}
