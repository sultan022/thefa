package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_intel")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Intel {

    @Id
    @Column(name = "intel_type")
    @EqualsAndHashCode.Include
    private String intelType;

    @Column(name = "description")
    private String description;
}
