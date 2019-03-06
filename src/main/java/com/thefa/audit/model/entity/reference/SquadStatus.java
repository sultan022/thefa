package com.thefa.audit.model.entity.reference;

import com.thefa.audit.model.shared.SquadStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fa_squad_status")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadStatus {

    @Id
    @Column(name = "status")
    @EqualsAndHashCode.Include
    private String status;

    @Column(name = "description")
    private String description;

    public static SquadStatus fromStatusType(SquadStatusType statusType) {
        return new SquadStatus(statusType.name(), null);
    }

}
