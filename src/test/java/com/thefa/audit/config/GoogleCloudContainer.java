package com.thefa.audit.config;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Stream;

@CommonsLog
public class GoogleCloudContainer extends GenericContainer<GoogleCloudContainer> {

    private GoogleCloudContainer() {
        super("google/cloud-sdk:latest");
    }

    public static GoogleCloudContainer buildDatastore() {
        return new GoogleCloudContainer()
                .withExposedPorts(8432)
                .withCommand("/bin/sh", "-c", "gcloud beta emulators datastore start --no-legacy " +
                        "--project the-fa-api-dev " +
                        "--host-port=0.0.0.0:8432 " +
                        "--consistency=1 ");
    }

    public static GoogleCloudContainer buildPubsub() {

        try {
            File file = ResourceUtils.getFile("classpath:pubsub.commands.txt");

            Stream<String> lines = Files.lines(file.toPath(), Charset.forName("UTF-8"));

            GoogleCloudContainer googleCloudContainer = new GoogleCloudContainer()
                    .withExposedPorts(8433)
                    .withCommand("/bin/sh", "-c", "gcloud beta emulators pubsub start " +
                            "--project the-fa-api-dev " +
                            "--host-port=0.0.0.0:8433 ");

            googleCloudContainer.start();
            googleCloudContainer.waitUntilContainerStarted();

            lines.filter(command -> !StringUtils.isEmpty(command.trim())).forEach(command -> {
                try {
                    ExecResult execResult = googleCloudContainer.execInContainer("/bin/sh", "-c", command);
                    log.info(execResult.getStdout());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            return googleCloudContainer;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}
