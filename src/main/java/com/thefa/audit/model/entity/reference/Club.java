package com.thefa.audit.model.entity.reference;

import com.thefa.audit.model.shared.TeamType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "fa_club")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Club {

    @Id
    @Column(name = "club_id")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "club_name")
    private String name;

    @Column(name = "club_name_abbr")
    private String abbr;

    @Column(name = "club_nickname")
    private String nickname;

    @Column(name = "club_city")
    private String city;

    @Column(name = "club_country")
    private String country;

    @Column(name = "club_stadium")
    private String stadium;

    @Column(name = "club_website")
    private String website;

    @Column(name = "year_founded")
    private Integer yearFounded;

    @Column(name = "team_type")
    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @Column(name = "is_active")
    private boolean isActive;

}
