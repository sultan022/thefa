package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_source")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DataSource {

    @Id
    @Column(name = "source")
    @EqualsAndHashCode.Include
    private String source;

    @Column(name = "description")
    private String description;

}
