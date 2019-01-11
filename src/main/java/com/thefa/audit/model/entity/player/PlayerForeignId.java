package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.DataSourceType;
import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerForeignId implements Serializable {

    private Long fanId;

    private DataSourceType source;
}
