package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.PlayerSquad;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface PlayerSquadRepository extends PagingAndSortingRepository<PlayerSquad, String> {

    Stream<PlayerSquad> findAllByPlayerIdIn(Iterable<String> playerIds);
}
