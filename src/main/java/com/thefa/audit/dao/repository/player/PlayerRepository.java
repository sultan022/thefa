package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.audit.model.shared.SquadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Stream<Player> findAllByFanIdIn(List<Long> fanIds);

    @Query("FROM PlayerForeignMapping WHERE source = :source AND foreignPlayerId IS NOT NULL")
    Set<PlayerForeignMapping> findPlayers(@Param("source") DataSourceType source);

    @Query("SELECT COUNT(p) FROM Player p LEFT JOIN p.playerSquads s WHERE p.fanId IN :fanIds AND s.squad = :squad")
    long countByFanIdInAndPlayerSquadsSquadTypeContaining(@Param("fanIds") Set<Long> fanId, @Param("squad") SquadType squadType);

    long countByFanIdIn(Set<Long> fanId);

}
