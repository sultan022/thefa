package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerSocialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlayerSocialService {

    private final PlayerSocialRepository playerSocialRepository;

    public PlayerSocialService(PlayerSocialRepository playerSocialRepository) {
        this.playerSocialRepository = playerSocialRepository;
    }

    public boolean doAllSocialsExistAndBelongToPlayerId(List<Long> socialIds, String playerId) {
        return socialIds.size() == 0 || playerSocialRepository.countByPlayerIdAndIdIn(playerId, socialIds) == socialIds.size();
    }
}
