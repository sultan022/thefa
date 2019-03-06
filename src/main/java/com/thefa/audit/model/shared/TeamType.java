package com.thefa.audit.model.shared;

public enum TeamType {
    CLUB("Club"), INTERNATIONAL("International"), FRIENDLY("Friendly");

    private final String name;

    TeamType(String name) {
        this.name = name;
    }

    public String friendlyName() {
        return this.name;
    }
}
