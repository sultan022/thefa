package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.reference.*;
import com.thefa.audit.model.dto.rerference.*;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ReferenceService {

    private final CountryRepository countryRepository;
    private final DataSourceRepository dataSourceRepository;
    private final GradeRepository gradeRepository;
    private final IntelTypeRepository intelTypeRepository;
    private final PositionRepository positionRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final SquadRepository squadRepository;
    private final SquadStatusRepository squadStatusRepository;
    private final ClubRepository clubRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ReferenceService(CountryRepository countryRepository,
                            DataSourceRepository dataSourceRepository,
                            GradeRepository gradeRepository,
                            IntelTypeRepository intelTypeRepository,
                            PositionRepository positionRepository,
                            SocialMediaRepository socialMediaRepository,
                            SquadRepository squadRepository,
                            SquadStatusRepository squadStatusRepository,
                            ClubRepository clubRepository,
                            ModelMapper modelMapper) {

        this.countryRepository = countryRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.gradeRepository = gradeRepository;
        this.intelTypeRepository = intelTypeRepository;
        this.positionRepository = positionRepository;
        this.socialMediaRepository = socialMediaRepository;
        this.squadRepository = squadRepository;
        this.squadStatusRepository = squadStatusRepository;
        this.clubRepository = clubRepository;

        this.modelMapper = modelMapper;
    }


    public List<CountryDTO> getAllCountries() {
        return StreamEx.of(countryRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, CountryDTO.class))
                .toList();
    }

    public List<DataSourceDTO> getAllDataSources() {
        return StreamEx.of(dataSourceRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, DataSourceDTO.class))
                .toList();
    }

    public List<GradeDTO> getAllGrades() {
        return StreamEx.of(gradeRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, GradeDTO.class))
                .toList();
    }

    public List<IntelDTO> getAllIntelTypes() {
        return StreamEx.of(intelTypeRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, IntelDTO.class))
                .toList();
    }

    public List<PositionDTO> getAllPositions() {
        return StreamEx.of(positionRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, PositionDTO.class))
                .toList();
    }

    public List<SocialMediaDTO> getAllSocialMedia() {
        return StreamEx.of(socialMediaRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, SocialMediaDTO.class))
                .toList();
    }

    public List<SquadDTO> getAllSquads() {
        return StreamEx.of(squadRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, SquadDTO.class))
                .toList();
    }

    public List<SquadStatusDTO> getAllSquadStatuses() {
        return StreamEx.of(squadStatusRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, SquadStatusDTO.class))
                .toList();
    }

    public List<ClubDTO> getAllClubs() {
        return StreamEx.of(clubRepository.findAll().iterator())
                .map(entity -> modelMapper.map(entity, ClubDTO.class))
                .toList();
    }

    public boolean doAllGradesExist(Set<String> grades) {
        return grades.size() == gradeRepository.countAllByGradeIn(grades);
    }

}
