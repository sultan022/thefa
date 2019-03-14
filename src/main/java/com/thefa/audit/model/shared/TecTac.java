package com.thefa.audit.model.shared;

import com.thefa.audit.model.kind.PlayerSportsCodeKind;
import one.util.streamex.StreamEx;

import java.util.function.Predicate;

import static com.thefa.audit.dao.service.datastore.PlayerSportsCodeDatastoreService.ONE;

public enum TecTac {

    ACTIONS_P90("Actions P90", code -> ONE.equals(code.getActionP90())),
    IN_POSSESSION_ACTIONS("In Possession Actions", code -> ONE.equals(code.getInPossession())),
    DISTRIBUTION_SUCCESS("Distribution Success", code -> ONE.equals(code.getInPossession()) ||
            ONE.equals(code.getCompletition())),
    BIG_SAVES_P90("Big Saves P90", code -> ONE.equals(code.getBigSaveP90())),
    DANGEROUS_POSSESSIONS("Dangerous Possessions", code -> ONE.equals(code.getDangPosession())),
    CHANCES_CREATED("Chances Created", code -> ONE.equals(code.getChanceCreated())),
    PRESS_ATTEMPTS("Press Attempts", code -> ONE.equals(code.getPressAttempt())),
    V1_DEFENSIVE_DUELS("1V1 Defensive Duels", code -> ONE.equals(code.getDefendingWonOppDuel())),
    V1_DEFENSIVE_DUELS_P_WON("1V1 Defensive Duels (WON %)", code -> ONE.equals(code.getDefendingWonOppDuel()) ||
            ONE.equals(code.getDefendingWonDuel())),
    AV_PLAYER_TAKEN_OUT_OF_GAME_PER_PASS("Av Player Taken Out Of Game Per Pass", code -> ONE.equals(code.getDefendersBeaten01()) ||
            ONE.equals(code.getDefendersBeaten02()) || ONE.equals(code.getDefendersBeaten03()) || ONE.equals(code.getDefendersBeaten04()) ||
            ONE.equals(code.getDefendersBeaten05()) || ONE.equals(code.getDefendersBeaten06()) || ONE.equals(code.getDefendersBeaten07()) ||
            ONE.equals(code.getDefendersBeaten08()) || ONE.equals(code.getDefendersBeaten09()) || ONE.equals(code.getDefendersBeaten10()) ||
            ONE.equals(code.getInPossession())),
    PASS_FROM_GROUND("Pass From Ground (OP)", code -> ONE.equals(code.getPass()) || ONE.equals(code.getInPossession())),
    THROW_IN("THROW (OP)", code -> ONE.equals(code.getThrowIn()) || ONE.equals(code.getInPossession())),
    KICK_FROM_HAND("Kick From Hand (OP)", code -> ONE.equals(code.getSideVolleyPunt()) || ONE.equals(code.getInPossession())),
    OUT_OF_POSSESSION_ACTIONS_P90("Out Of Possession Actions P90", code -> (ONE.equals(code.getOutOfPossessionActionP90()))),
    GOALS_CONCEDED_P90("Goals Conceded P90", code -> ONE.equals(code.getGoalsConcededP90())),
    DEFEND_THE_GOAL("Defend The Goal %", code -> ONE.equals(code.getDefendTheGoalOutOfPoss())
            || ONE.equals(code.getDefendTheGoal())),
    DEFEND_THE_AREA("Defend The Area %", code -> ONE.equals(code.getDefendTheAreaOutOfPoss())
            || ONE.equals(code.getDefendTheArea())),
    DEFEND_THE_SPACE("Defend The Space %", code -> ONE.equals(code.getDefendTheSpaceOutOfPoss())
            || ONE.equals(code.getDefendTheSpace())),
    CROSSES("Crosses", code -> ONE.equals(code.getCrosses())),
    CROSSES_FIRST_CONTACT("Crosses (1st Contact %)", code -> ONE.equals(code.getCrosses()) || ONE.equals(code.getCrossesFirstContact())),
    UNDER_CP("Under CP", code -> ONE.equals(code.getUnderCP())),
    UNDER_CP_RETAINED("Under CP (% Retained)", code -> ONE.equals(code.getUnderCPPossessionRetain())
            || ONE.equals(code.getUnderCPPossessionLost())),
    DEFENSIVE_AERIAL_DUELS("Defensive Aerial Duels", code -> ONE.equals(code.getDefensiveAerialDuel())),
    DEFENSIVE_AERIAL_DUELS_WON("Defensive Aerial Duels (% WON)", code -> ONE.equals(code.getDefensiveAerialOutcome()) ||
            ONE.equals(code.getDefensiveAerialDuel())),
    POSSESSION_PROGRESSION_RATIO("Possession Progression Ratio (%)", code -> ONE.equals(code.getPosessionLostOrRetained()) ||
            ONE.equals(code.getSuccUnSuccPenetration())),
    KEY_DEFENSIVE_INTERVENTION("Key Defensive Intervention", code -> ONE.equals(code.getKeyDefensiveIntervention())),
    CREATED_DANGEROUS_POSSESSIONS("Created Dangerous Possessions", code -> ONE.equals(code.getCreatedDangPosession())),
    CHANCES("Chances", code -> ONE.equals(code.getChance())),
    CREATED_CHANCES("Created Chances", code -> ONE.equals(code.getTotalTeamChanceCreated())),
    ATTACKING_ACTION("Attacking Action", code -> ONE.equals(code.getAttackingAction())),
    SHOT_OUT_SIDE_ZONE("Shot Out Side Zone", code -> ONE.equals(code.getShotOutSideZone())),
    SHOTS("Shots (% Outside Zone)", code -> ONE.equals(code.getShotOZ())),
    GOALS("Goals", code -> ONE.equals(code.getGoal())),
    RECEIVED_IN_PENALTY_BOX("Received In Penalty Box", code -> ONE.equals(code.getRecInPenaltyBox()));

    public final String name;
    public final Predicate<PlayerSportsCodeKind> predicate;

    TecTac(String name, Predicate<PlayerSportsCodeKind> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public String getFriendlyName() {
        return this.name;
    }

    public static TecTac fromFriendlyName(String name) {
        return StreamEx.of(TecTac.values())
                .findAny(tecTac -> tecTac.name.toLowerCase().trim().equals(name.toLowerCase().trim()))
                .orElse(null);
    }
}
