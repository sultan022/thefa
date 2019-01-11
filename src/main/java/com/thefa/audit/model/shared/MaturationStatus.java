package com.thefa.audit.model.shared;

public enum MaturationStatus {
    EARLY("Red"),
    ONTIME("Amber"),
    LATE("Green");

    private String colour;

    MaturationStatus(String colour){
        this.colour=colour;
    }

    public String colour(){
        return colour;
    }
}
