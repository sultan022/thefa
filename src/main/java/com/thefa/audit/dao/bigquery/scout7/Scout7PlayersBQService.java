package com.thefa.audit.dao.bigquery.scout7;

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
public class Scout7PlayersBQService extends AbstractBigQueryService {

    private final static String PLAYER_SEARCH_QUERY = "SELECT PlayerId, FirstName, LastName, DateOfBirth, ClubName, Nationality " +
                                                        "FROM `fa_scout7.scout7_players`";

    private final static String DATASET = "fa_scout7";
    private final static String TABLE = "scout7_players";

    @Autowired
    public Scout7PlayersBQService(BigQuery bigQuery) {
        super(bigQuery);
    }

    public CompletableFuture<List<ForeignPlayerDTO>> findPlayers(@NonNull ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        Pair<String, List<QueryParameterValue>> query = buildPlayerSearchQuery(foreignPlayerLookupDTO);

        return runBigQueryJob(query.getFirst(), query.getSecond())
                .thenApplyAsync(tableResult -> StreamEx.of(tableResult.iterateAll().iterator())
                        .map(row -> new ForeignPlayerDTO(
                                getString(row.get("PlayerId")),
                                getString(row.get("FirstName")),
                                getString(row.get("LastName")),
                                null,
                                null,
                                getLocalDate(row.get("DateOfBirth")),
                                getString(row.get("ClubName")),
                                getString(row.get("Nationality")),
                                DataSourceType.SCOUT7,
                                null
                        ))
                        .toList());
    }

    private Pair<String, List<QueryParameterValue>> buildPlayerSearchQuery(ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        StringBuilder builder = new StringBuilder(PLAYER_SEARCH_QUERY);

        List<String> conditions = new ArrayList<>();
        List<QueryParameterValue> params = new ArrayList<>();

        Optional.ofNullable(foreignPlayerLookupDTO.getFirstName())
                .ifPresent(firstName -> {
                    conditions.add("UPPER(FirstName) = ?");
                    params.add(QueryParameterValue.string(firstName.toUpperCase()));
                });

        Optional.ofNullable(foreignPlayerLookupDTO.getLastName())
                .ifPresent(lastName -> {
                    conditions.add("UPPER(LastName) = ?");
                    params.add(QueryParameterValue.string(lastName.toUpperCase()));
                });

        Optional.ofNullable(foreignPlayerLookupDTO.getDateOfBirth())
                .ifPresent(dateOfBirth -> {
                    conditions.add("DateOfBirth = ?");
                    params.add(QueryParameterValue.date(dateOfBirth.toString()));
                });

        String query = builder.append(" WHERE ")
                .append(StreamEx.of(conditions).joining(" AND ", " ", " "))
                .append("LIMIT 100").toString();

        return Pair.of(query, params);

    }
}
