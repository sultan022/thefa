package com.thefa.audit.model.shared;

public enum InjuryStatus {
    GREEN("Fit"),
    AMBER("Fit to Train"),
    RED("Injured/Illness");

    public final String description;

    InjuryStatus(String description) {
        this.description = description;
    }
}
