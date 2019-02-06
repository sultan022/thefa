package com.thefa.audit.model.entity.player;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_position")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(PlayerPositionId.class)
public class PlayerPosition {

    @Id
    @Column(name = "player_id")
    @EqualsAndHashCode.Include
    private String playerId;

    @Id
    @Column(name = "position_number")
    @EqualsAndHashCode.Include
    private Integer positionNumber;

    @Column(name = "position_order")
    private Integer positionOrder;

}
