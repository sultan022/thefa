package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerSquadHistoryRepository extends PagingAndSortingRepository<PlayerSquadHistory, Long> {

    Page<PlayerSquadHistory> findByPlayerId(String playerId, Pageable pageable);

}
