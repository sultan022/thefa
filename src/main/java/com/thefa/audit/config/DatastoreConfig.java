package com.thefa.audit.config;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatastoreConfig {

    @Bean
    public Datastore getDatastore() {
        return DatastoreOptions.getDefaultInstance().getService();
    }


}