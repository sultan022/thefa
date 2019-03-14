package com.thefa.audit.controller;

import com.thefa.audit.dao.repository.datastore.PlayerSportsCodeVideosDSRepository;
import com.thefa.audit.dao.service.datastore.PlayerSportsCodeDatastoreService;
import com.thefa.audit.model.dto.tectac.TecTacAggregatedDTO;
import com.thefa.audit.model.dto.tectac.TecTacDetailDTO;
import com.thefa.audit.model.shared.TeamType;
import com.thefa.audit.model.shared.TecTac;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.exception.BadRequestException;
import com.thefa.common.exception.RecordNotFoundException;
import com.thefa.common.helper.DeferredResults;
import com.thefa.common.storage.CloudStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/tectac")
@Api(value = "Tec Tac", description = "Tec Tac operations")
public class TecTacController {

    private final PlayerSportsCodeDatastoreService playerSportsCodeDatastoreService;
    private final PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository;
    private final CloudStorageService cloudStorageService;

    private final String tecTacBucket;

    @Autowired
    public TecTacController(@Value("{bucket.tectac}") String bucket,
                            PlayerSportsCodeDatastoreService playerSportsCodeDatastoreService,
                            PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository,
                            CloudStorageService cloudStorageService) {
        this.tecTacBucket = bucket;
        this.playerSportsCodeDatastoreService = playerSportsCodeDatastoreService;
        this.playerSportsCodeVideosDSRepository = playerSportsCodeVideosDSRepository;
        this.cloudStorageService = cloudStorageService;
    }

    @ApiOperation(value = "Get TecTac Aggregated Details")
    @GetMapping("/{playerId}")
    public List<TecTacAggregatedDTO> getTecTac(
            @PathVariable() String playerId,
            @RequestParam(value = "season") String season,
            @RequestParam(value = "position") Integer position,
            @RequestParam(value = "matchType", required = false) TeamType teamType
    ) {
        return playerSportsCodeDatastoreService.getAggregatedTecTacs(playerId, season, position, teamType);
    }

    @ApiOperation(value = "Get TecTac Details")
    @GetMapping("/{playerId}/videos")
    public PageResponse<TecTacDetailDTO> getTecTacDetail(
            @PathVariable() String playerId,
            @RequestParam(value = "tectac") String tecTac,
            @RequestParam(value = "season") String season,
            @RequestParam(value = "position") Integer position,
            @RequestParam(value = "matchType", required = false) TeamType teamType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        TecTac tecTacEnum = TecTac.fromFriendlyName(tecTac);
        if (tecTacEnum == null) {
            throw new BadRequestException("Invalid TecTac value");
        }

        return playerSportsCodeDatastoreService.getTecTacDetails(
                playerId, tecTacEnum, season, position, teamType, page, size
        );
    }

    @ApiOperation(value = "Download a tectac video", response = Byte.class)
    @GetMapping("/videos/{videoId}")
    public DeferredResult<ResponseEntity<InputStreamResource>> getTecTacVideo(@PathVariable() String videoId) {
        return DeferredResults.from(
                playerSportsCodeVideosDSRepository.findById(videoId)
                    .map(kind -> cloudStorageService.retrieveFileInputStreamAsync(tecTacBucket, kind.getFilePath())
                            .thenApplyAsync(pair -> {
                                String[] fileParts = kind.getFilePath().split("/");
                                String filename = fileParts[fileParts.length - 1];
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.setContentType(MediaType.valueOf(pair.getFirst()));
                                httpHeaders.setContentDispositionFormData("attachment", filename);
                                return new ResponseEntity<>(new InputStreamResource(pair.getSecond()), httpHeaders, HttpStatus.OK);
                            }))
                    .orElseThrow(RecordNotFoundException::new)
        );
    }

}
