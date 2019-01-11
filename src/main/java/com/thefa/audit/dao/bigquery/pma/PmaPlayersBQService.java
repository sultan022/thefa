package com.thefa.audit.dao.bigquery.pma;

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
public class PmaPlayersBQService extends AbstractBigQueryService {

    private final static String PLAYER_SEARCH_QUERY = "SELECT playerName, dateOfBirth, gender, homeClub, playersClub " +
                                                        "FROM `fa_pma.pma_players`";

    private final static String DATASET = "fa_pma";
    private final static String TABLE = "pma_players";

    @Autowired
    public PmaPlayersBQService(BigQuery bigQuery) {
        super(bigQuery);
    }

    public CompletableFuture<List<ForeignPlayerDTO>> findPlayers(@NonNull ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        Pair<String, List<QueryParameterValue>> query = buildPlayerSearchQuery(foreignPlayerLookupDTO);

        return runBigQueryJob(query.getFirst(), query.getSecond())
                .thenApplyAsync(tableResult -> StreamEx.of(tableResult.iterateAll().iterator())
                        .map(row -> {

                            String playerName = getString(row.get("playerName"));

                            String[] names = Optional.ofNullable(playerName)
                                    .map(str -> str.split(" "))
                                    .orElse(new String[]{});

                            return new ForeignPlayerDTO(
                                    playerName,
                                    names.length >= 1 ? names[0] : null,
                                    names.length >= 2 ? names[1] : null,
                                    getString(row.get("gender")),
                                    getLocalDate(row.get("dateOfBirth")),
                                    getString(row.get("playersClub")),
                                    null,
                                    DataSourceType.PMA
                            );
                        })
                        .toList());
    }

    private Pair<String, List<QueryParameterValue>> buildPlayerSearchQuery(ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        StringBuilder builder = new StringBuilder(PLAYER_SEARCH_QUERY);

        List<String> conditions = new ArrayList<>();
        List<QueryParameterValue> params = new ArrayList<>();

        if (foreignPlayerLookupDTO.getFirstName() != null && foreignPlayerLookupDTO.getLastName() != null) {

            conditions.add("UPPER(playerName) = ?");
            params.add(QueryParameterValue.string(foreignPlayerLookupDTO.getFirstName().toUpperCase() + " " + foreignPlayerLookupDTO.getLastName().toUpperCase()));

        } else if (foreignPlayerLookupDTO.getFirstName() != null) {

            conditions.add("UPPER(playerName) LIKE CONCAT(?, ' %')");
            params.add(QueryParameterValue.string(foreignPlayerLookupDTO.getFirstName().toUpperCase()));

        } else if (foreignPlayerLookupDTO.getLastName() != null) {

            conditions.add("UPPER(playerName) LIKE CONCAT('% ', ?)");
            params.add(QueryParameterValue.string(foreignPlayerLookupDTO.getLastName().toUpperCase()));
        }

        Optional.ofNullable(foreignPlayerLookupDTO.getDateOfBirth())
                .ifPresent(dateOfBirth -> {
                    conditions.add("dateOfBirth = ?");
                    params.add(QueryParameterValue.date(dateOfBirth.toString()));
                });

        String query = builder.append(" WHERE ")
                .append(StreamEx.of(conditions).joining(" AND ", " ", " "))
                .append("LIMIT 100").toString();

        return Pair.of(query, params);

    }
}
