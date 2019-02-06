package com.thefa.audit.model.entity.history;

import com.thefa.audit.model.shared.InjuryStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "fa_player_injury_status_history")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PlayerInjuryStatusHistory extends AbstractHistory {

    @Enumerated(EnumType.STRING)
    @Column(name = "injury_status")
    private InjuryStatus injuryStatus;

    public PlayerInjuryStatusHistory(String playerId, InjuryStatus injuryStatus) {
        this.playerId = playerId;
        this.injuryStatus = injuryStatus;
    }
}
