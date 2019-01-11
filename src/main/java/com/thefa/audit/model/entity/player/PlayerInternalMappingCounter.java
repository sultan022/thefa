package com.thefa.audit.model.entity.player;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_player_internal_mapping_counter")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerInternalMappingCounter {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "counter")
    private Long counter;
}
