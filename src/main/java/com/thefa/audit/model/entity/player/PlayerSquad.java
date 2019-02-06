package com.thefa.audit.model.entity.player;


import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.audit.model.shared.SquadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(name = "squad", nullable = false)
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private SquadType squad;

    @Enumerated(EnumType.STRING)
    @Column(name="squad_status")
    private SquadStatusType status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;

}
