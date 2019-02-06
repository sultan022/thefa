package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerForeignMappingRepository;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
import com.thefa.audit.model.shared.DataSourceType;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@CommonsLog
@Transactional
public class PlayerForeignMappingService {

    private PlayerForeignMappingRepository playerForeignMappingRepository;

    public PlayerForeignMappingService(PlayerForeignMappingRepository playerForeignMappingRepository) {
        this.playerForeignMappingRepository = playerForeignMappingRepository;
    }

    public Set<PlayerForeignMapping> findPmaExternalPlayers() {
        return playerForeignMappingRepository.findPlayers(DataSourceType.PMA_EXTERNAL);
    }
}
