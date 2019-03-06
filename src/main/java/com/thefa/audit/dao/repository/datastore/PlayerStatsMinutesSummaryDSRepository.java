package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerStatsMinutesSummaryKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatsMinutesSummaryDSRepository extends DatastoreRepository<PlayerStatsMinutesSummaryKind, String> {

}
