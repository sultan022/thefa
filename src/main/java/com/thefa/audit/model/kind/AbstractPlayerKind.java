package com.thefa.audit.model.kind;

import lombok.Data;
import one.util.streamex.StreamEx;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public abstract class AbstractPlayerKind {

    @Field(name = "playerSquad.squad")
    protected List<String> playerSquadSquad;

    @Field(name = "playerSquad.squadStatus")
    protected List<String> playerSquadStatus;

    @Field(name = "position.number")
    protected List<Integer> positionNumber;

    @Field(name = "position.order")
    protected List<Integer> positionOrder;

    public List<PlayerSquadKind> playerSquad() {
        if (this.playerSquadSquad == null || this.playerSquadStatus == null) {
            return null;
        }
        return IntStream.range(0, Math.min(playerSquadSquad.size(), playerSquadStatus.size()))
                .mapToObj(i -> new PlayerSquadKind(playerSquadSquad.get(i), playerSquadStatus.get(i)))
                .collect(Collectors.toList());
    }

    public List<PositionKind> position() {
        if (this.positionNumber == null || this.positionOrder == null) {
            return null;
        }
        return IntStream.range(0, Math.min(positionNumber.size(), positionOrder.size()))
                .mapToObj(i -> new PositionKind(positionNumber.get(i), positionOrder.get(i)))
                .collect(Collectors.toList());
    }

    public void setPlayerSquad(List<PlayerSquadKind> squads) {
        if (squads == null) {
            this.playerSquadSquad = null;
            this.playerSquadStatus = null;
        } else {
            this.playerSquadSquad =  StreamEx.of(squads).map(PlayerSquadKind::getSquad).toList();
            this.playerSquadStatus = StreamEx.of(squads).map(PlayerSquadKind::getSquadStatus).toList();
        }
    }

    public void setPosition(List<PositionKind> positions) {
        if (positions == null) {
            this.positionNumber = null;
            this.positionOrder = null;
        } else {
            this.positionNumber = StreamEx.of(positions).map(PositionKind::getNumber).toList();
            this.positionOrder = StreamEx.of(positions).map(PositionKind::getOrder).toList();
        }
    }
}
