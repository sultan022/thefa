package com.thefa.audit.dao.bigquery.faraw;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerPositionTable;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerPpsDataTable;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerSquadTable;
import com.thefa.audit.model.table.the_fa_raw.FndPlayersTable;
import com.thefa.common.bigquery.AbstractBigQueryService;
import com.thefa.common.helper.BQHelper;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@CommonsLog
public class FndPlayersBQService extends AbstractBigQueryService {

    private final String bigQueryProject;

    @Autowired
    public FndPlayersBQService(@Value("${bigquery.project.id}") String bigQueryProject,
                               BigQuery bigQuery) {
        super(bigQuery);
        this.bigQueryProject = bigQueryProject;
    }

    public CompletableFuture<FndPlayersTable> addFndRecord(@NonNull FndPlayersTable fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            Map<String, Object> objectMap = BQHelper.toMap(fndPlayersTable);

            InsertAllRequest.RowToInsert rowToInsert = InsertAllRequest.RowToInsert.of(fndPlayersTable.getPlayerId(), objectMap);

            insertAll(TableId.of(bigQueryProject, "the_fa_raw", "fnd_players"), Collections.singleton(rowToInsert));

            return fndPlayersTable;
        });
    }

    public CompletableFuture<List<FndPlayerPositionTable>> addPlayerPositions(@NonNull List<FndPlayerPositionTable> fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            List<InsertAllRequest.RowToInsert> rowsToInsert = StreamEx.of(fndPlayersTable)
                    .map(BQHelper::toMap)
                    .map(InsertAllRequest.RowToInsert::of)
                    .toList();

            insertAll(TableId.of(bigQueryProject, "the_fa_raw", "fnd_player_position"), rowsToInsert);

            return fndPlayersTable;
        });
    }

    public CompletableFuture<List<FndPlayerSquadTable>> addPlayerSquads(@NonNull List<FndPlayerSquadTable> fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            List<InsertAllRequest.RowToInsert> rowsToInsert = StreamEx.of(fndPlayersTable)
                    .map(BQHelper::toMap)
                    .map(InsertAllRequest.RowToInsert::of)
                    .toList();

            insertAll(TableId.of(bigQueryProject, "the_fa_raw", "fnd_player_squad"), rowsToInsert);

            return fndPlayersTable;
        });
    }

    public CompletableFuture<List<FndPlayerPpsDataTable>> addPlayerPpsData(@NonNull List<FndPlayerPpsDataTable> fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            List<InsertAllRequest.RowToInsert> rowsToInsert = StreamEx.of(fndPlayersTable)
                    .map(BQHelper::toMap)
                    .map(InsertAllRequest.RowToInsert::of)
                    .toList();

            insertAll(TableId.of(bigQueryProject, "the_fa_raw", "fnd_player_pps_data"), rowsToInsert);

            return fndPlayersTable;
        });
    }
}
