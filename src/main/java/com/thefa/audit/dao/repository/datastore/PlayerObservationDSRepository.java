package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerObservationKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PlayerObservationDSRepository extends DatastoreRepository<PlayerObservationKind, String> {

    List<PlayerObservationKind> findByPlayerId(String playerId);

}
