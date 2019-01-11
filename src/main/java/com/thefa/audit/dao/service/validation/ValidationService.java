package com.thefa.audit.dao.service.validation;

import com.thefa.audit.dao.repository.common.CommonRepository;
import com.thefa.audit.dao.service.ReferenceService;
import com.thefa.audit.model.dto.rerference.GradeDTO;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CommonsLog
@Transactional
public class ValidationService {

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ReferenceService referenceService;

    public boolean fanIdsExistForGivenPlayerIdsForInternalSource(List<String> playerIds) {
        return commonRepository.fanIdsExistForGivenPlayerIdsForInternalSource(playerIds) == playerIds.size();
    }

    /**
     * Validates that all passed grade does exist in system.
     *
     * @param playerGrades player grades to be verified in system.
     * @return Set of non-existent grades.
     */
    public Set<String> validateIfAllGradeExistsInSystem(Set<String> playerGrades) {
        Set<String> existingGrades = referenceService.getAllGrades().stream().map(GradeDTO::getGrade).collect(Collectors.toSet());
        if (!playerGrades.equals(existingGrades)) {
            playerGrades.removeAll(existingGrades);
            return playerGrades;
        }
        return new HashSet<>();
    }
}
