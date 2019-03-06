package com.thefa.audit.model.entity.player;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_position")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(PlayerPositionId.class)
@NoArgsConstructor @AllArgsConstructor
public class PlayerPosition {

    @Id
    @Column(name = "player_id", nullable = false)
    @EqualsAndHashCode.Include
    private String playerId;

    @Id
    @Column(name = "position_number", nullable = false)
    @EqualsAndHashCode.Include
    private Integer positionNumber;

    @Column(name = "position_order")
    private Integer positionOrder;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;

}
