package com.thefa.audit.dao.service;


import com.thefa.audit.dao.repository.player.PlayerForeignMappingRepository;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
import com.thefa.audit.model.shared.DataSourceType;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@CommonsLog
public class PlayerForeignMappingService {

    private PlayerForeignMappingRepository playerForeignMappingRepository;

    @Autowired
    public void setPlayerForeignMappingRepository(PlayerForeignMappingRepository playerForeignMappingRepository) {
        this.playerForeignMappingRepository = playerForeignMappingRepository;
    }

    public Map<String, Long> findPlayersFanId(DataSourceType dataSourceType, Set<String> playerIds) {
       return StreamEx.of(playerForeignMappingRepository.findPlayersMapping(dataSourceType ,playerIds))
               .collect(Collectors.toMap(PlayerForeignMapping::getForeignPlayerId, PlayerForeignMapping::getFanId));
    }
}
