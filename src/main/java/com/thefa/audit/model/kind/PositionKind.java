package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PositionKind {

    private Integer number;
    private Integer order;

}
