package com.thefa.audit.dao.repository.reference;

import com.thefa.audit.model.entity.reference.DataSource;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DataSourceRepository extends PagingAndSortingRepository<DataSource, String> {
}
