package com.thefa.audit.model.entity.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="fa_player_eligibility")
@IdClass(PlayerEligibility.class)
@Data @NoArgsConstructor
public class PlayerEligibility implements Serializable {

    @Id
    @Column(name = "fan_id")
    private Long fanId;

    @Id
    @Column(name="country_code")
    private String countryCode;

    public PlayerEligibility(String countryCode) {
        this.countryCode = countryCode;
    }

}
