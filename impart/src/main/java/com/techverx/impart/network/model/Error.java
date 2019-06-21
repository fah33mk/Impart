package com.techverx.impart.network.model;

public class Error {
    private String id;
    private String cause;

    public Error(){}

    public Error(String id, String cause) {
        this.id = id;
        this.cause = cause;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
