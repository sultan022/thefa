package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_country")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Country {

    @Id
    @Column(name = "country_code")
    @EqualsAndHashCode.Include
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "points")
    private Integer points;

    @Column(name = "rank")
    private Integer rank;
}
