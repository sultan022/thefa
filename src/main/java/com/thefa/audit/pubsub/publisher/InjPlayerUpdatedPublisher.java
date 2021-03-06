package com.thefa.audit.pubsub.publisher;

import com.google.cloud.ServiceOptions;
import com.google.pubsub.v1.ProjectTopicName;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InjPlayerUpdatedPublisher extends AbstractPublisher {

    private static final String TOPIC_NAME = "com.thefa.audit.player.inj";

    public InjPlayerUpdatedPublisher(PubsubHelper pubsubHelper) throws IOException {
        super(pubsubHelper.buildPublisher(ProjectTopicName.of(ServiceOptions.getDefaultProjectId(), TOPIC_NAME)));
    }
}
