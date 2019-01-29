package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.history.PlayerInjuryStatusHistory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerInjuryStatusHistoryRepository extends PagingAndSortingRepository<PlayerInjuryStatusHistory, Long> {

}
