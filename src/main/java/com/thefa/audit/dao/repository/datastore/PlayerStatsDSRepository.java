package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerStatsKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatsDSRepository extends DatastoreRepository<PlayerStatsKind, String> {

}
