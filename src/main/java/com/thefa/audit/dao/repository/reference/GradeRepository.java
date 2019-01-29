package com.thefa.audit.dao.repository.reference;

import com.thefa.audit.model.entity.reference.Grade;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

public interface GradeRepository extends PagingAndSortingRepository<Grade, String> {

    long countAllByGradeIn(Set<String> grades);
}
