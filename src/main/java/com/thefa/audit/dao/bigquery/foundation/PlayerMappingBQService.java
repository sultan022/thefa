package com.thefa.audit.dao.bigquery.foundation;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import com.thefa.audit.model.table.foundation.MappingPlayerIdTable;
import com.thefa.common.bigquery.AbstractBigQueryService;
import com.thefa.common.helper.BQHelper;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@CommonsLog
public class PlayerMappingBQService extends AbstractBigQueryService {

    private final String bigQueryProject;

    @Autowired
    public PlayerMappingBQService(@Value("${bigquery.project.id}") String bigQueryProject,
                               BigQuery bigQuery) {
        super(bigQuery);
        this.bigQueryProject = bigQueryProject;
    }

    public CompletableFuture<List<MappingPlayerIdTable>> addPlayerMappings(@NonNull List<MappingPlayerIdTable> fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            List<InsertAllRequest.RowToInsert> rowsToInsert = StreamEx.of(fndPlayersTable)
                    .map(BQHelper::toMap)
                    .map(InsertAllRequest.RowToInsert::of)
                    .toList();

            insertAll(TableId.of(bigQueryProject, "fa_foundation", "mapping_playerid"), rowsToInsert);

            return fndPlayersTable;
        });
    }
}
