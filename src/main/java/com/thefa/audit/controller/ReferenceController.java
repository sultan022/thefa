package com.thefa.audit.controller;

import com.thefa.audit.dao.service.ReferenceService;
import com.thefa.audit.model.dto.rerference.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CommonsLog
@RestController
@RequestMapping("/reference")
@Api(value = "Reference", description = "Get all reference data")
public class ReferenceController {

    private final ReferenceService referenceService;

    @Autowired
    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @ApiOperation(value = "Get list of all countries")
    @GetMapping("/countries")
    public List<CountryDTO> getAllCountries() {
        return referenceService.getAllCountries();
    }

    @ApiOperation(value = "Get list of all data sources")
    @GetMapping("/dataSources")
    public List<DataSourceDTO> getAllDataSources() {
        return referenceService.getAllDataSources();
    }

    @ApiOperation(value = "Get list of all grades")
    @GetMapping("/grades")
    public List<GradeDTO> getAllGrades() {
        return referenceService.getAllGrades();
    }

    @ApiOperation(value = "Get list of all intel types")
    @GetMapping("/intelTypes")
    public List<IntelDTO> getAllIntelTypes() {
        return referenceService.getAllIntelTypes();
    }

    @ApiOperation(value = "Get list of all positions")
    @GetMapping("/positions")
    public List<PositionDTO> getAllPositions() {
        return referenceService.getAllPositions();
    }

    @ApiOperation(value = "Get list of all social media types")
    @GetMapping("/socialMedia")
    public List<SocialMediaDTO> getAllSocialMedia() {
        return referenceService.getAllSocialMedia();
    }

    @ApiOperation(value = "Get list of all squads")
    @GetMapping("/squads")
    public List<SquadDTO> getAllSquads() {
        return referenceService.getAllSquads();
    }

    @ApiOperation(value = "Get list of all squad statuses")
    @GetMapping("/squadStatuses")
    public List<SquadStatusDTO> getAllSquadStatuses() {
        return referenceService.getAllSquadStatuses();
    }

    @ApiOperation(value = "Get list of all clubs")
    @GetMapping("/clubs")
    public List<ClubDTO> getAllClubs() {
        return referenceService.getAllClubs();
    }
}
