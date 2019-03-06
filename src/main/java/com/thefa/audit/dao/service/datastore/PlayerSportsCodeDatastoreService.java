package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerSportsCodeDSRepository;
import com.thefa.audit.model.dto.tectac.TecTacAggregatedDTO;
import com.thefa.audit.model.dto.tectac.TecTacDetailDTO;
import com.thefa.audit.model.kind.PlayerSportsCodeKind;
import com.thefa.audit.model.shared.TeamType;
import com.thefa.audit.model.shared.TecTac;
import com.thefa.common.dto.shared.PageResponse;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thefa.audit.model.shared.TecTacPossession.*;

@Service
public class PlayerSportsCodeDatastoreService {

    public static final Integer ONE = 1;

    private final PlayerSportsCodeDSRepository playerSportsCodeDSRepository;

    @Autowired
    public PlayerSportsCodeDatastoreService(PlayerSportsCodeDSRepository playerSportsCodeDSRepository) {
        this.playerSportsCodeDSRepository = playerSportsCodeDSRepository;
    }

    public List<TecTacAggregatedDTO> getAggregatedTecTacs(String playerId,
                                                          String season,
                                                          Integer position,
                                                          TeamType teamType) {
        List<PlayerSportsCodeKind> sportsCodes = (teamType == null) ? playerSportsCodeDSRepository.findAllByPlayerIdAndSeasonIdAndPositionOrderByDateDesc(playerId, season, position)
                : playerSportsCodeDSRepository.findAllByPlayerIdAndSeasonIdAndPositionAndTeamTypeOrderByDateDesc(playerId, season, position, teamType.friendlyName());

        return Arrays.asList(
                actionP90Agg(sportsCodes),
                inPossessionActionsAgg(sportsCodes),
                distributionSuccessAgg(sportsCodes),
                bigSavesP90Agg(sportsCodes),
                dangPossessionsAgg(sportsCodes),
                chancesCreatedAgg(sportsCodes),
                pressAttempts(sportsCodes),
                v1DefensiveDuels(sportsCodes),
                v1DefensiveDuelsWonP(sportsCodes)
        );
    }

    public PageResponse<TecTacDetailDTO> getTecTacDetails(String playerId,
                                         TecTac tecTac,
                                         String season,
                                         Integer position,
                                         TeamType teamType,
                                         int page,
                                         int size) {
        List<PlayerSportsCodeKind> sportsCodes = (teamType == null) ? playerSportsCodeDSRepository.findAllByPlayerIdAndSeasonIdAndPositionOrderByDateDesc(playerId, season, position)
                : playerSportsCodeDSRepository.findAllByPlayerIdAndSeasonIdAndPositionAndTeamTypeOrderByDateDesc(playerId, season, position, teamType.friendlyName());

        List<PlayerSportsCodeKind> filteredSportsCodes = StreamEx.of(sportsCodes).filter(tecTac.predicate).toList();

        AtomicInteger counter = new AtomicInteger(0);

        List<PlayerSportsCodeKind> pageList;

        List<List<PlayerSportsCodeKind>> chunks = StreamEx.of(filteredSportsCodes).groupRuns((prev, next) -> counter.incrementAndGet() % size != 0).toList();

        if (chunks.size() >= page + 1) {
            pageList =  chunks.get(page);
        } else {
            pageList =  Collections.emptyList();
        }

        return PageResponse.<TecTacDetailDTO>builder()
                .page(page)
                .size(size)
                .totalPages(chunks.size())
                .totalSize(filteredSportsCodes.size())
                .content(StreamEx.of(pageList)
                    .map(code -> new TecTacDetailDTO(
                            code.getDate(),
                            null, //TODO: Video Link
                            code.getStartTime(),
                            code.getEndTime(),
                            code.getHomeTeam(),
                            code.getHomeScore(),
                            code.getAwayTeam(),
                            code.getAwayScore()
                    ))
                    .toList())
                .build();

    }

    private static TecTacAggregatedDTO actionP90Agg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.ACTIONS_P90.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getActionP90())).count()),
                BOTH
        );
    }

    private static TecTacAggregatedDTO inPossessionActionsAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.IN_POSSESSION_ACTIONS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO distributionSuccessAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long inPossession = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count();
        long completition = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getCompletition())).count();

        return new TecTacAggregatedDTO(
                TecTac.DISTRIBUTION_SUCCESS.getFriendlyName(),
                (inPossession == 0 || completition == 0) ? "0%" : (completition / inPossession) + "%",
                IN_POSSESSION
        );

    }

    private static TecTacAggregatedDTO bigSavesP90Agg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.BIG_SAVES_P90.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getBigSaveP90())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO dangPossessionsAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.DANGEROUS_POSSESSIONS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDangPosession())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO chancesCreatedAgg(List<PlayerSportsCodeKind> sportsCode) {
        return new TecTacAggregatedDTO(
                TecTac.CHANCES_CREATED.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCode).filter(kind -> ONE.equals(kind.getChanceCreated())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO pressAttempts(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.PRESS_ATTEMPTS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getPressAttempt())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO v1DefensiveDuels(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.V1_DEFENSIVE_DUELS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendingWonOppDuel())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO v1DefensiveDuelsWonP(List<PlayerSportsCodeKind> sportsCodes) {
        long wonOppDuel = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendingWonOppDuel())).count();
        long wonDuel = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendingWonDuel())).count();

        return new TecTacAggregatedDTO(
                TecTac.V1_DEFENSIVE_DUELS_P_WON.getFriendlyName(),
                (wonDuel == 0 || wonOppDuel == 0) ? "0%" : (wonDuel / wonOppDuel) + "%",
                OUT_POSSESSION
        );
    }



}
