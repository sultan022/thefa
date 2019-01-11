package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_position")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Position {

    @Id
    @Column(name = "position_number")
    @EqualsAndHashCode.Include
    private Integer positionNumber;

    @Column(name = "description")
    private String description;

}
