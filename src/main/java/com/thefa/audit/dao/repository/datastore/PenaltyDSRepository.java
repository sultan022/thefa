package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PenaltyKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyDSRepository extends DatastoreRepository<PenaltyKind,String> {
}
