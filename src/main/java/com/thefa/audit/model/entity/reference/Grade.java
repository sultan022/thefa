package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_grade")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Grade {

    @Id
    @Column(name = "grade")
    @EqualsAndHashCode.Include
    private String grade;

    @Column(name = "description")
    private String description;

}
