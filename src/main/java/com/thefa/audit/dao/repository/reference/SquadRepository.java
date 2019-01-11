package com.thefa.audit.dao.repository.reference;

import com.thefa.audit.model.entity.reference.Squad;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SquadRepository extends PagingAndSortingRepository<Squad, String> {
}
