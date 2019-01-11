package com.thefa.audit.dao.bigquery.faraw;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.TableId;
import com.thefa.audit.model.table.the_fa_raw.FndPlayersTable;
import com.thefa.common.bigquery.AbstractBigQueryService;
import com.thefa.common.helper.BQHelper;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@CommonsLog
public class FndPlayersBQService extends AbstractBigQueryService {

    private final static String DATASET = "the_fa_raw";
    private final static String TABLE = "fnd_players";

    private final TableId tableId;

    @Autowired
    public FndPlayersBQService(@Value("${bigquery.project.id}") String bigQueryProject,
                               BigQuery bigQuery) {
        super(bigQuery);
        this.tableId = TableId.of(bigQueryProject, DATASET, TABLE);
    }

    public CompletableFuture<FndPlayersTable> addFndRecord(@NonNull FndPlayersTable fndPlayersTable) {

        return CompletableFuture.supplyAsync(() -> {

            Map<String, Object> objectMap = BQHelper.toMap(fndPlayersTable);

            InsertAllRequest.RowToInsert rowToInsert = InsertAllRequest.RowToInsert.of(fndPlayersTable.getPlayerId(), objectMap);

            InsertAllResponse response = bigQuery.insertAll(
                    InsertAllRequest.newBuilder(tableId)
                            .setRows(Collections.singleton(rowToInsert))
                            .setIgnoreUnknownValues(true)
                            .setSkipInvalidRows(false)
                            .build()
            );

            if (response.hasErrors()) {
                log.error(response.getInsertErrors());
                throw new RuntimeException("Error while inserting BigQuery fnd_players Record");
            }

            return fndPlayersTable;
        });
    }
}
