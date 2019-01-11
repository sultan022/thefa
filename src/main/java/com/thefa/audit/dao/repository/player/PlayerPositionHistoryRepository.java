package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerPositionHistoryRepository extends PagingAndSortingRepository<PlayerPositionHistory, Long> {
}
