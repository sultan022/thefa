package com.thefa.audit.data.service.datastore;

import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.repository.datastore.PlayerObservationDSRepository;
import com.thefa.audit.dao.service.datastore.LiveObservationDatastoreService;
import com.thefa.audit.model.dto.datastore.PlayerObservationDTO;
import com.thefa.audit.model.kind.PlayerObservationKind;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class LiveObservationDatastoreServiceTest extends AbstractIntegrationTest {

    @Autowired
    private LiveObservationDatastoreService service;
    @Autowired
    private PlayerObservationDSRepository observationRepository;

    @Before
    public void setup() {

        insertPlayerObservations();
    }

    @Test
    public void findPlayersMethodShouldReturnCorrectResult() {

        List<PlayerObservationDTO> list = service.getPlayerObservations("fapl0001229");
        assertEquals("Expected player observations = 2", 2, list.size());

    }

    private void insertPlayerObservations() {

        Arrays.asList(
                new PlayerObservationKind("1", "1", "fapl0001228", "Nawaz", "Liverpool", "1", "2019-01-01", "TR", "Nigel Deeley", "Luton", "M", "Liverpool"),
                new PlayerObservationKind("2", "2", "fapl0001229", "Taimour", "Liverpool", "1", "2019-01-02", "TR", "Nigel Deeley", "Luton", "M", "Liverpool"),
                new PlayerObservationKind("3", "3", "fapl0001229", "Taimour", "Liverpool", "1", "2019-01-02", "TR", "Nigel Deeley", "Luton", "M", "Liverpool"),
                new PlayerObservationKind("4", "4", "falp0001111", "Ehsan", "Liverpool", "1", "2019-01-02", "TR", "Nigel Deeley", "Luton", "M", "Liverpool")
        ).forEach(ob -> observationRepository.save(ob));

    }

}
