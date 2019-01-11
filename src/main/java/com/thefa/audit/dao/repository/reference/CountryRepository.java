package com.thefa.audit.dao.repository.reference;

import com.thefa.audit.model.entity.reference.Country;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CountryRepository extends PagingAndSortingRepository<Country, String> {
}
