package com.thefa.audit.model.entity.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_squad")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Squad {

    @Id
    @Column(name = "squad")
    @EqualsAndHashCode.Include
    private String squad;

    @Column(name = "squad_order")
    private Integer order;

    @Column(name = "description")
    private String description;

}
