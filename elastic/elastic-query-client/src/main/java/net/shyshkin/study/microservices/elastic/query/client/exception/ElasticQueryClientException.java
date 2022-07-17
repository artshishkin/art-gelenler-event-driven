package net.shyshkin.study.microservices.elastic.query.client.exception;

public class ElasticQueryClientException extends RuntimeException{

    public ElasticQueryClientException() {
        super();
    }

    public ElasticQueryClientException(String message) {
        super(message);
    }

    public ElasticQueryClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
