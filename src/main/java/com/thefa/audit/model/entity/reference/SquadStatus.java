package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_squad_status")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadStatus {

    @Id
    @Column(name = "status")
    @EqualsAndHashCode.Include
    private String status;

    @Column(name = "description")
    private String description;

}
