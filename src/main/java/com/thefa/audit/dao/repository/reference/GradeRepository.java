package com.thefa.audit.dao.repository.reference;

import com.thefa.audit.model.entity.reference.Grade;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GradeRepository extends PagingAndSortingRepository<Grade, String> {
}
