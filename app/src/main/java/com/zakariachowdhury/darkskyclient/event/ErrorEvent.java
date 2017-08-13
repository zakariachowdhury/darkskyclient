package com.zakariachowdhury.darkskyclient.event;

/**
 * Created by Zakaria Chowdhury on 8/12/17.
 */

public class ErrorEvent {

    private final String message;

    public ErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
