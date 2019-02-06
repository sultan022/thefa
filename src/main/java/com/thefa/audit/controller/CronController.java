package com.thefa.audit.controller;

import com.thefa.audit.dao.service.PlayerForeignMappingService;
import com.thefa.audit.model.dto.pubsub.InjRecordUpdateMsgDTO;
import com.thefa.audit.pubsub.publisher.InjPlayerUpdatedPublisher;
import com.thefa.common.dto.shared.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonsLog
@RestController
@RequestMapping("/cron/data")
@Api(value = "Cron Data", description = "Cron operations")
public class CronController {

    private final PlayerForeignMappingService playerForeignMappingService;
    private final InjPlayerUpdatedPublisher injPlayerUpdatedPublisher;


    @Autowired
    public CronController(PlayerForeignMappingService playerForeignMappingService,
                          InjPlayerUpdatedPublisher injPlayerUpdatedPublisher) {
        this.playerForeignMappingService = playerForeignMappingService;
        this.injPlayerUpdatedPublisher = injPlayerUpdatedPublisher;
    }

    @ApiOperation(value = "Trigger PMA Injury Status Update - (Internal Use Only)")
    @GetMapping("/pma/injury")
    public ApiResponse<String> findPmaExternalPlayers() {

        StreamEx.of(playerForeignMappingService.findPmaExternalPlayers())
                .map(foreignPlayer -> new InjRecordUpdateMsgDTO(foreignPlayer.getPlayerId(), foreignPlayer.getForeignPlayerId()))
                .forEach(injPlayerUpdatedPublisher::publish);

        return ApiResponse.success("OK");
    }
}
