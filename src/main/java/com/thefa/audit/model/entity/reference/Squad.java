package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_squad")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Squad {

    @Id
    @Column(name = "squad")
    @EqualsAndHashCode.Include
    private String squad;

    @Column(name = "description")
    private String description;

}
