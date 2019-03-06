package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.PlayerSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerSocialRepository extends JpaRepository<PlayerSocial, Long> {

    long countByPlayerIdAndIdIn(String playerId, List<Long> ids);
}
