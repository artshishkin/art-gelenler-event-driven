package net.shyshkin.study.microservices.elasticqueryservice.security;

public enum PermissionType {

    READ("READ"), WRITE("WRITE"), ADMIN("ADMIN");

    private String type;

    PermissionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
