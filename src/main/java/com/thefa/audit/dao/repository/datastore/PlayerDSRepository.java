package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerDSRepository extends DatastoreRepository<PlayerKind, String> {

    List<PlayerKind> findAllByPlayerId(String playerId);

}
