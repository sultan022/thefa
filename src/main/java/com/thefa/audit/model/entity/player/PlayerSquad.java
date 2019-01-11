package com.thefa.audit.model.entity.player;


import com.thefa.audit.model.shared.SquadType;
import com.thefa.audit.model.shared.SquadStatusType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_squad")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(PlayerSquadId.class)
public class PlayerSquad {

    @Id
    @Column(name = "fan_id")
    @EqualsAndHashCode.Include
    private Long fanId;

    @Id
    @Column(name = "squad")
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private SquadType squad;

    @Enumerated(EnumType.STRING)
    @Column(name="squad_status")
    private SquadStatusType status;

}
