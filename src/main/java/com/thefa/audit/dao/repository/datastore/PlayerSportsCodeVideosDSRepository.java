package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerSportsCodeVideoKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerSportsCodeVideosDSRepository extends DatastoreRepository<PlayerSportsCodeVideoKind, String> {

}
