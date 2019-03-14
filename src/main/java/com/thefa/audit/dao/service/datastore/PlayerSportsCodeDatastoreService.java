package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerSportsCodeDSRepository;
import com.thefa.audit.dao.repository.datastore.PlayerSportsCodeVideosDSRepository;
import com.thefa.audit.model.dto.tectac.TecTacAggregatedDTO;
import com.thefa.audit.model.dto.tectac.TecTacDetailDTO;
import com.thefa.audit.model.kind.PlayerSportsCodeKind;
import com.thefa.audit.model.shared.TeamType;
import com.thefa.audit.model.shared.TecTac;
import com.thefa.common.dto.shared.PageResponse;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thefa.audit.model.shared.TecTacPossession.*;

@Service
public class PlayerSportsCodeDatastoreService {

    public static final Integer ONE = 1;

    private final String projectId;
    private final PlayerSportsCodeDSRepository playerSportsCodeDSRepository;
    private final PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository;

    @Autowired
    public PlayerSportsCodeDatastoreService(@Value("${project.id}") String projectId,
                                            PlayerSportsCodeDSRepository playerSportsCodeDSRepository,
                                            PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository) {
        this.projectId = projectId;
        this.playerSportsCodeDSRepository = playerSportsCodeDSRepository;
        this.playerSportsCodeVideosDSRepository = playerSportsCodeVideosDSRepository;
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
                v1DefensiveDuelsWonP(sportsCodes),
                avPlayerTakenOutOfGamePerPass(sportsCodes),
                passFromGroundAgg(sportsCodes),
                throwAgg(sportsCodes),
                kickFromHandAgg(sportsCodes),
                outOfPossessionActionsP90Agg(sportsCodes),
                goalsConcededP90Agg(sportsCodes),
                defendTheGoalAgg(sportsCodes),
                defendTheAreaAgg(sportsCodes),
                defendTheSpaceAgg(sportsCodes),
                underCpAgg(sportsCodes),
                underCounterPressAgg(sportsCodes),
                crossesAgg(sportsCodes),
                crossesFirstContactAgg(sportsCodes),
                defensiveAerialDuelSAgg(sportsCodes),
                defensiveAerialDuelWonSAgg(sportsCodes),
                possessionProgressionRatioAgg(sportsCodes),
                keyDefensiveInterventionAgg(sportsCodes),
                createdDangPossessionsAgg(sportsCodes),
                chancesAgg(sportsCodes),
                totalTeamChanceCreatedAgg(sportsCodes),
                attackingActionAgg(sportsCodes),
                shotOutSideZoneAgg(sportsCodes),
                shotsAgg(sportsCodes),
                goalsAgg(sportsCodes),
                receiveInPenaltyBoxAgg(sportsCodes)
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
            pageList = chunks.get(page);
        } else {
            pageList = Collections.emptyList();
        }

        Map<String, String> mainVideoLinks = new HashMap<>();

        return PageResponse.<TecTacDetailDTO>builder()
                .page(page)
                .size(size)
                .totalPages(chunks.size())
                .totalSize(filteredSportsCodes.size())
                .content(StreamEx.of(pageList)
                        .map(code -> new TecTacDetailDTO(
                                code.getDate(),
                                PlayerSportsCodeDatastoreService.getVideoLink(code, mainVideoLinks, playerSportsCodeVideosDSRepository),
                                PlayerSportsCodeDatastoreService.getDownloadLink(code, mainVideoLinks, playerSportsCodeVideosDSRepository, projectId),
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

    private static String getVideoLink(PlayerSportsCodeKind sportsCodeKind, Map<String, String> links,
                                       PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository) {
        //TODO: Provide Wowza Link
        links.computeIfAbsent(sportsCodeKind.getFileId(), k -> playerSportsCodeVideosDSRepository.findById(k).map(a -> "").orElse(null));
        return null;
    }

    private static String getDownloadLink(PlayerSportsCodeKind sportsCodeKind, Map<String, String> links,
                                          PlayerSportsCodeVideosDSRepository playerSportsCodeVideosDSRepository,
                                          String projectId) {
        if (links.containsKey(sportsCodeKind.getFileId())) {
            return playerSportsCodeVideosDSRepository.findById(sportsCodeKind.getVideoId())
                    .map(kind -> "https://" + projectId + ".appspot.com/audit/tectac/videos/" + kind.getId()).orElse(null);
        }
        return null;
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

    private static TecTacAggregatedDTO avPlayerTakenOutOfGamePerPass(List<PlayerSportsCodeKind> sportsCodes) {
        long defendersBeaten01 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten01())).count();
        long defendersBeaten02 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten02())).count();
        long defendersBeaten03 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten03())).count();
        long defendersBeaten04 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten04())).count();
        long defendersBeaten05 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten05())).count();
        long defendersBeaten06 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten06())).count();
        long defendersBeaten07 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten07())).count();
        long defendersBeaten08 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten08())).count();
        long defendersBeaten09 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten09())).count();
        long defendersBeaten10 = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendersBeaten10())).count();
        long inPossession = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count();

        return new TecTacAggregatedDTO(
                TecTac.AV_PLAYER_TAKEN_OUT_OF_GAME_PER_PASS.getFriendlyName(),
                (inPossession == 0 ? "0" : Long.toString(
                        (1 * defendersBeaten01 +
                                2 * defendersBeaten02 +
                                3 * defendersBeaten03 +
                                4 * defendersBeaten04 +
                                5 * defendersBeaten05 +
                                6 * defendersBeaten06 +
                                7 * defendersBeaten07 +
                                8 * defendersBeaten08 +
                                9 * defendersBeaten09 +
                                10 * defendersBeaten10) / inPossession
                )),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO passFromGroundAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long pass = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getPass())).count();
        long inPossession = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count();
        return new TecTacAggregatedDTO(
                TecTac.PASS_FROM_GROUND.getFriendlyName(),
                (pass == 0 || inPossession == 0) ? "0" : Long.toString(pass / inPossession),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO throwAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long throwIn = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getThrowIn())).count();
        long inPossession = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count();
        return new TecTacAggregatedDTO(
                TecTac.THROW_IN.getFriendlyName(),
                ((throwIn == 0 || inPossession == 0) ? "0" : Long.toString((throwIn / inPossession))),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO kickFromHandAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long sideVollyPunt = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getSideVolleyPunt())).count();
        long inPossession = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getInPossession())).count();
        return new TecTacAggregatedDTO(
                TecTac.KICK_FROM_HAND.getFriendlyName(),
                ((sideVollyPunt == 0 || inPossession == 0) ? "0" : Long.toString((sideVollyPunt / inPossession))),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO outOfPossessionActionsP90Agg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.OUT_OF_POSSESSION_ACTIONS_P90.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getOutOfPossessionActionP90())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO goalsConcededP90Agg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.GOALS_CONCEDED_P90.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getGoalsConcededP90())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO defendTheGoalAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long defendTheGoalOutOfPoss = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheGoalOutOfPoss())).count();
        long defendTheGoal = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheGoal())).count();

        return new TecTacAggregatedDTO(
                TecTac.DEFEND_THE_GOAL.getFriendlyName(),
                (defendTheGoalOutOfPoss == 0 || defendTheGoal == 0) ? "0%" : (defendTheGoal / defendTheGoalOutOfPoss) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO defendTheAreaAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long defendTheAreaOutOfPoss = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheAreaOutOfPoss())).count();
        long defendTheArea = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheArea())).count();

        return new TecTacAggregatedDTO(
                TecTac.DEFEND_THE_AREA.getFriendlyName(),
                (defendTheAreaOutOfPoss == 0 || defendTheArea == 0) ? "0%" : (defendTheArea / defendTheAreaOutOfPoss) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO defendTheSpaceAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long defendTheSpaceOutOfPoss = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheSpaceOutOfPoss())).count();
        long defendTheSpace = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefendTheSpace())).count();

        return new TecTacAggregatedDTO(
                TecTac.DEFEND_THE_SPACE.getFriendlyName(),
                (defendTheSpaceOutOfPoss == 0 || defendTheSpace == 0) ? "0%" : (defendTheSpace / defendTheSpaceOutOfPoss) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO underCpAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.UNDER_CP.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getUnderCP())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO underCounterPressAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long cpRetain = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getUnderCPPossessionRetain())).count();
        long cpLost = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getUnderCPPossessionLost())).count();

        return new TecTacAggregatedDTO(
                TecTac.UNDER_CP_RETAINED.getFriendlyName(),
                (cpRetain == 0 || cpLost == 0) ? "0%" : (cpRetain / cpLost + cpRetain) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO crossesAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.CROSSES.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getCrosses())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO crossesFirstContactAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long crosses = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getCrosses())).count();
        long crossesFirstContact = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getCrossesFirstContact())).count();

        return new TecTacAggregatedDTO(
                TecTac.CROSSES_FIRST_CONTACT.getFriendlyName(),
                (crosses == 0 || crossesFirstContact == 0) ? "0%" : (crossesFirstContact / crosses) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO defensiveAerialDuelSAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.DEFENSIVE_AERIAL_DUELS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefensiveAerialDuel())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO defensiveAerialDuelWonSAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long aerialOutCome = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefensiveAerialOutcome())).count();
        long aerialDuel = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getDefensiveAerialDuel())).count();

        return new TecTacAggregatedDTO(
                TecTac.DEFENSIVE_AERIAL_DUELS_WON.getFriendlyName(),
                (aerialOutCome == 0 || aerialDuel == 0) ? "0%" : (aerialOutCome / aerialDuel) + "%",
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO possessionProgressionRatioAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long posessionLostOrRetained = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getPosessionLostOrRetained())).count();
        long succUnSuccPenetration = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getSuccUnSuccPenetration())).count();

        return new TecTacAggregatedDTO(
                TecTac.POSSESSION_PROGRESSION_RATIO.getFriendlyName(),
                (posessionLostOrRetained == 0 || succUnSuccPenetration == 0) ? "0%" : (posessionLostOrRetained / succUnSuccPenetration) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO keyDefensiveInterventionAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.KEY_DEFENSIVE_INTERVENTION.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getKeyDefensiveIntervention())).count()),
                OUT_POSSESSION
        );
    }

    private static TecTacAggregatedDTO createdDangPossessionsAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.CREATED_DANGEROUS_POSSESSIONS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getCreatedDangPosession())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO chancesAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.CHANCES.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getChance())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO totalTeamChanceCreatedAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.CREATED_CHANCES.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getTotalTeamChanceCreated())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO attackingActionAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.ATTACKING_ACTION.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getAttackingAction())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO shotOutSideZoneAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.SHOT_OUT_SIDE_ZONE.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getShotOutSideZone())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO shotsAgg(List<PlayerSportsCodeKind> sportsCodes) {
        long shotOutSideZone = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getShotOutSideZone())).count();
        long shots = StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getShotOZ())).count();

        return new TecTacAggregatedDTO(
                TecTac.SHOTS.getFriendlyName(),
                (shots == 0 || shotOutSideZone == 0) ? "0%" : (shots / shotOutSideZone) + "%",
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO goalsAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.GOALS.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getGoal())).count()),
                IN_POSSESSION
        );
    }

    private static TecTacAggregatedDTO receiveInPenaltyBoxAgg(List<PlayerSportsCodeKind> sportsCodes) {
        return new TecTacAggregatedDTO(
                TecTac.RECEIVED_IN_PENALTY_BOX.getFriendlyName(),
                Long.toString(StreamEx.of(sportsCodes).filter(kind -> ONE.equals(kind.getRecInPenaltyBox())).count()),
                IN_POSSESSION
        );
    }

}
