package com.thefa.audit.dao.bigquery.foundation;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryParameterValue;
import com.thefa.audit.model.dto.foreign.ForeignPlayerDTO;
import com.thefa.audit.model.dto.foreign.ForeignPlayerLookupDTO;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.common.bigquery.AbstractBigQueryService;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@CommonsLog
public class FoundationPlayersBQService extends AbstractBigQueryService {

    private final static String PLAYER_SEARCH_QUERY = "SELECT playerId, firstName, lastName, gender, dateOfBirth, currentClubName, nationality " +
            "FROM `fa_foundation.players` ";

    @Autowired
    public FoundationPlayersBQService(BigQuery bigQuery) {
        super(bigQuery);
    }

    public CompletableFuture<List<ForeignPlayerDTO>> findPlayers(@NonNull ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        Pair<String, List<QueryParameterValue>> queryPair = buildPlayerSearchQuery(foreignPlayerLookupDTO);

        return runBigQueryJob(queryPair.getFirst(), queryPair.getSecond())
                .thenApplyAsync(tableResult -> StreamEx.of(tableResult.iterateAll().iterator())
                        .map(row -> new ForeignPlayerDTO(
                                getString(row.get("playerId")),
                                getString(row.get("firstName")),
                                getString(row.get("lastName")),
                                getString(row.get("gender")),
                                getLocalDate(row.get("dateOfBirth")),
                                getString(row.get("currentClubName")),
                                getString(row.get("nationality")),
                                DataSourceType.INTERNAL
                        ))
                        .toList());
    }

    @SuppressWarnings("Duplicates")
    private Pair<String, List<QueryParameterValue>> buildPlayerSearchQuery(ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        StringBuilder builder = new StringBuilder(PLAYER_SEARCH_QUERY);

        List<String> conditions = new ArrayList<>();

        List<QueryParameterValue> parameters = new ArrayList<>();

        Optional.ofNullable(foreignPlayerLookupDTO.getFirstName())
                .ifPresent(firstName -> {
                    conditions.add("UPPER(firstName) = ?");
                    parameters.add(QueryParameterValue.string(firstName.toUpperCase()));
                });

        Optional.ofNullable(foreignPlayerLookupDTO.getLastName())
                .ifPresent(lastName -> {
                    conditions.add("UPPER(lastName) = ?");
                    parameters.add(QueryParameterValue.string(lastName.toUpperCase()));
                });

        Optional.ofNullable(foreignPlayerLookupDTO.getDateOfBirth())
                .ifPresent(dateOfBirth -> {
                    conditions.add("dateOfBirth = ?");
                    parameters.add(QueryParameterValue.date(dateOfBirth.toString()));
                });

        String query = builder.append(" WHERE ")
                .append(StreamEx.of(conditions).joining(" AND ", " ", " "))
                .append("LIMIT 100").toString();

        return Pair.of(query, parameters);
    }
}
