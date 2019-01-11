package com.thefa.audit.controller;

import com.thefa.audit.dao.bigquery.foundation.FoundationPlayersBQService;
import com.thefa.audit.dao.bigquery.opta.OptaPlayersBQService;
import com.thefa.audit.dao.bigquery.pma.PmaPlayersBQService;
import com.thefa.audit.dao.bigquery.scout7.Scout7PlayersBQService;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.foreign.FanIdDTO;
import com.thefa.audit.model.dto.foreign.ForeignPlayerDTO;
import com.thefa.audit.model.dto.foreign.ForeignPlayerLookupDTO;
import com.thefa.audit.model.dto.player.small.PlayerShortDTO;
import com.thefa.audit.rest.FanIdService;
import com.thefa.common.exception.BadRequestException;
import com.thefa.common.helper.DeferredResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommonsLog
@RestController
@RequestMapping("/foreign")
@Api(value = "Foreign Player", description = "Foreign player mapping operations")
public class ForeignController {

    private final OptaPlayersBQService optaPlayersBQService;
    private final Scout7PlayersBQService scout7PlayersBQService;
    private final PmaPlayersBQService pmaPlayersBQService;
    private final FoundationPlayersBQService foundationPlayersBQService;

    private final PlayerService playerService;
    private final FanIdService fanIdService;
    private final ModelMapper modelMapper;

    @Autowired
    public ForeignController(OptaPlayersBQService optaPlayersBQService,
                             Scout7PlayersBQService scout7PlayersBQService,
                             PmaPlayersBQService pmaPlayersBQService,
                             FoundationPlayersBQService foundationPlayersBQService,
                             PlayerService playerService,
                             FanIdService fanIdService,
                             ModelMapper modelMapper) {
        this.optaPlayersBQService = optaPlayersBQService;
        this.scout7PlayersBQService = scout7PlayersBQService;
        this.pmaPlayersBQService = pmaPlayersBQService;
        this.foundationPlayersBQService = foundationPlayersBQService;

        this.playerService = playerService;
        this.fanIdService = fanIdService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "Search for Fan Id")
    @PostMapping("/fanIdSearch")
    public DeferredResult<List<FanIdDTO>> fanIdLookup(
            @RequestBody ForeignPlayerLookupDTO foreignPlayerLookupDTO
    ) {
        if (foreignPlayerLookupDTO.isEmpty()) {
            throw new BadRequestException();
        }

        return DeferredResults.from(fanIdService.findPlayers(foreignPlayerLookupDTO)
                .thenApplyAsync(fanServicePlayerDTOS -> {

                    List<FanIdDTO> players = StreamEx.of(fanServicePlayerDTOS).map(fans -> modelMapper.map(fans, FanIdDTO.class)).toList();

                    List<Long> fanIds = StreamEx.of(players).map(FanIdDTO::getFanId).toList();

                    List<PlayerShortDTO> existingPlayers = playerService.searchPlayersWithFanIds(fanIds);

                    StreamEx.of(players)
                            .filter(player -> StreamEx.of(existingPlayers).findFirst(entity -> entity.getFanId().equals(player.getFanId())).isPresent())
                            .forEach(player -> player.setExistsPPS(true));

                    return players;
                }));
    }

    @ApiOperation(value = "Search for foreign players")
    @PostMapping("/playersSearch")
    public DeferredResult<List<ForeignPlayerDTO>> searchForeignPlayers (
            @RequestBody ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        if (foreignPlayerLookupDTO.isEmpty()) {
            throw new BadRequestException();
        }

        return DeferredResults.from(StreamEx.of(optaPlayersBQService.findPlayers(foreignPlayerLookupDTO),
                    scout7PlayersBQService.findPlayers(foreignPlayerLookupDTO),
                    pmaPlayersBQService.findPlayers(foreignPlayerLookupDTO),
                    foundationPlayersBQService.findPlayers(foreignPlayerLookupDTO))
                .map(CompletableFuture::join)
                .flatCollection(each -> each)
                .toListAndThen(CompletableFuture::completedFuture));

    }
}
