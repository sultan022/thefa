package com.thefa.audit.model.entity.reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_social_media")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SocialMedia {

    @Id
    @Column(name = "name")
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "icon")
    private String icon;

}
