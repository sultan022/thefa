package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.entity.reference.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@Entity
@Table(name="fa_player_eligibility")
@IdClass(PlayerEligibilityId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class PlayerEligibility implements Serializable {

    @Id
    @Column(name = "player_id", nullable = false)
    private String playerId;

    @Id
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;

    public PlayerEligibility(Country country) {
        this.country = country;
    }

    public String getCountryCode() {
        return Optional.ofNullable(country).map(Country::getCountryCode).orElse(null);
    }

}
