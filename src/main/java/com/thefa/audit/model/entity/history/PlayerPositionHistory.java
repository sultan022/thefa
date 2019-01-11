package com.thefa.audit.model.entity.history;

import com.thefa.audit.model.shared.Assignment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "fa_player_position_history")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PlayerPositionHistory extends AbstractHistory {

    @Column(name="position_number")
    private int positionNumber;

    @Column(name="position_order")
    private Integer positionOrder;

    @Column(name = "assignment")
    @Enumerated(EnumType.STRING)
    private Assignment assignment;

}
