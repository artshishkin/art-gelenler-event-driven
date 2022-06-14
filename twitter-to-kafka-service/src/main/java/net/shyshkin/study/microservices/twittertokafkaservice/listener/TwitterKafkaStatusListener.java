package net.shyshkin.study.microservices.twittertokafkaservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    @Override
    public void onStatus(Status status) {
        log.debug("Status is: {}", status);
        log.debug("Twitter status with text: {}", status.getText());
    }
}
