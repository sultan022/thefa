package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_foreign_mapping")
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor @AllArgsConstructor
@IdClass(PlayerForeignId.class)
public class PlayerForeignMapping {

    @Id
    @Column(name = "player_id", nullable = false)
    @EqualsAndHashCode.Include
    private String playerId;

    @Id
    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private DataSourceType source;

    @Column(name = "foreign_id")
    private String foreignPlayerId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;
}
