package com.thefa.audit.config;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BigQueryConfig {

    private String bigQueryProjectId;

    @Autowired
    public BigQueryConfig(@Value("${bigquery.project.id}") String bigQueryProjectId) {
        this.bigQueryProjectId = bigQueryProjectId;
    }

    @Bean
    public BigQuery getBigQuery() {
        return BigQueryOptions.getDefaultInstance()
                .toBuilder()
                .setProjectId(bigQueryProjectId)
                .build()
                .getService();
    }
}
