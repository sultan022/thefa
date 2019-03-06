package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerSummaryKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerSummaryDSRepository extends DatastoreRepository<PlayerSummaryKind, String> {

    List<PlayerSummaryKind> findAllByPlayerId(String playerId);

}
