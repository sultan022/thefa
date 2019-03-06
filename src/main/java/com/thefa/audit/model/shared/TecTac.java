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
            ONE.equals(code.getDefendingWonDuel()));

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
