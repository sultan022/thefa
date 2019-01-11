package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Stream<Player> findAllByFanIdIn(List<Long> fanIds);

    @Query("SELECT fanId FROM PlayerForeignMapping WHERE source = 'INTERNAL' AND foreignPlayerId = :foreignPlayerId")
    Long getFanIdByPlayerIdForInternalSource(String foreignPlayerId);

}
