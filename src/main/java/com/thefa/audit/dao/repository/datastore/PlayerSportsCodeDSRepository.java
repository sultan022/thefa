package com.thefa.audit.dao.repository.datastore;

import com.thefa.audit.model.kind.PlayerSportsCodeKind;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerSportsCodeDSRepository extends DatastoreRepository<PlayerSportsCodeKind, String> {

    List<PlayerSportsCodeKind> findAllByPlayerIdAndSeasonIdAndPositionAndTeamTypeOrderByDateDesc(String playerId, String seasonId, Integer position, String teamType);

    List<PlayerSportsCodeKind> findAllByPlayerIdAndSeasonIdAndPositionOrderByDateDesc(String playerId, String seasonId, Integer position);

}
