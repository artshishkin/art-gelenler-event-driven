package net.shyshkin.study.microservices.elasticqueryservice;

import lombok.Getter;

public enum QueryType {

    KAFKA_STATE_STORE("KAFKA_STATE_STORE"), ANALYTICS_DATABASE("ANALYTICS_DATABASE");

    @Getter
    private String type;

    QueryType(String type) {
        this.type = type;
    }
}
