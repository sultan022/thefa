package com.thefa.audit.model.entity.history;

import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.audit.model.shared.SquadType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_squad_history")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PlayerSquadHistory extends AbstractHistory {

    @Column(name = "squad")
    @Enumerated(EnumType.STRING)
    private SquadType squad;

    @Column(name="squad_status")
    @Enumerated(EnumType.STRING)
    private SquadStatusType status;

    @Column(name = "assignment")
    @Enumerated(EnumType.STRING)
    private Assignment assignment;

    public PlayerSquadHistory() {
    }

    public PlayerSquadHistory(Long fanId, SquadType squad, SquadStatusType status, Assignment assignment) {
        super();
        this.fanId = fanId;
        this.squad = squad;
        this.status = status;
        this.assignment = assignment;
    }
}
