package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.entity.reference.Club;
import com.thefa.audit.model.entity.reference.Grade;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.model.shared.IntelType;
import com.thefa.audit.model.shared.MaturationStatus;
import com.thefa.common.dto.shared.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "fa_player")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Player {

    @Id
    @Column(name = "player_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String playerId;

    @Column(name = "first_name")
    @ToString.Include
    private String firstName;

    @Column(name = "middle_name")
    @ToString.Include
    private String middleName;

    @Column(name = "last_name")
    @ToString.Include
    private String lastName;

    @Column(name = "known_name")
    private String knownName;

    @Column(name = "date_of_birth")
    @ToString.Include
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    @ToString.Include
    private Gender gender;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "player_grade")
    private Grade playerGrade;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PlayerForeignMapping> foreignMappings = new HashSet<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PlayerEligibility> eligibilities = new HashSet<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PlayerSquad> playerSquads = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    @Where(clause = "archived = false")
    private List<PlayerIntel> playerIntels = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PlayerPosition> playerPositions = new HashSet<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlayerSocial> playerSocials = new ArrayList<>();

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "maturation_status")
    private MaturationStatus maturationStatus;

    @Column(name = "vulnerability_status")
    private Integer vulnerabilityStatus;

    @Column(name = "vulnerability_status_week4")
    private Integer vulnerabilityStatus4Weeks;

    @Column(name = "vulnerability_status_week8")
    private Integer vulnerabilityStatus8Weeks;

    @Column(name = "vulnerability_status_week12")
    private Integer vulnerabilityStatus12Weeks;

    @Column(name = "maturation_date")
    @Temporal(TemporalType.DATE)
    private Date maturationDate;

    @Column(name = "vulnerability_date")
    @Temporal(TemporalType.DATE)
    private Date vulnerabilityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "injury_status")
    private InjuryStatus injuryStatus;

    @Column(name = "expected_return_date")
    @Temporal(TemporalType.DATE)
    private Date expectedReturnDate;

    @Version
    @Column(name = "version")
    private Integer version;

    public Integer getEducationIntelNoteLength() {
        return playerIntels.stream().filter(playerIntel -> (!playerIntel.isArchived() && playerIntel.getIntelType() == IntelType.EDUCATION)).toArray().length;
    }

    public Integer getGeneralIntelNoteLength() {
        return playerIntels.stream().filter(playerIntel -> (!playerIntel.isArchived() && playerIntel.getIntelType() == IntelType.GENERAL)).toArray().length;
    }

    public Integer getEligibilityIntelNoteLength() {
        return playerIntels.stream().filter(playerIntel -> (!playerIntel.isArchived() && playerIntel.getIntelType() == IntelType.ELIGIBILITY)).toArray().length;
    }

}
