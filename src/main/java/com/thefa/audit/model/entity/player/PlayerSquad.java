package com.thefa.audit.model.entity.player;


import com.thefa.audit.model.entity.reference.Squad;
import com.thefa.audit.model.entity.reference.SquadStatus;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "fa_player_squad")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(PlayerSquadId.class)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSquad {

    @Id
    @Column(name = "player_id", nullable = false)
    @EqualsAndHashCode.Include
    private String playerId;

    @Id
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "squad", nullable = false)
    @EqualsAndHashCode.Include
    private Squad squad;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "squad_status", nullable = false)
    private SquadStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;

    public SquadStatusType getStatusType() {
        return Optional.ofNullable(status)
                .map(SquadStatus::getStatus)
                .map(SquadStatusType::valueOf)
                .orElse(null);
    }

    public SquadType getSquadType() {
        return Optional.ofNullable(squad)
                .map(Squad::getSquad)
                .map(SquadType::valueOf)
                .orElse(null);
    }

    public void setStatusType(SquadStatusType statusType) {
        this.status = Optional.ofNullable(statusType)
                .map(s -> new SquadStatus(s.name(), null))
                .orElse(null);
    }

    public void setSquadType(SquadType squadType) {
        this.squad = Optional.ofNullable(squadType)
                .map(s -> new Squad(s.name(), null, null))
                .orElse(null);
    }

}
