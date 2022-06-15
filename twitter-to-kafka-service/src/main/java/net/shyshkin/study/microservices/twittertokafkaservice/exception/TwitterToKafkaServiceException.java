package net.shyshkin.study.microservices.twittertokafkaservice.exception;

public class TwitterToKafkaServiceException extends RuntimeException{

    public TwitterToKafkaServiceException() {
        super();
    }

    public TwitterToKafkaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterToKafkaServiceException(String message) {
        super(message);
    }
}
