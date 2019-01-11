package com.thefa.audit.model.entity.history;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "fa_player_grade_history")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PlayerGradeHistory extends AbstractHistory {

    @Column(name = "player_grade")
    private String grade;

}
