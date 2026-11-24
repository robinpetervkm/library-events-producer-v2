package com.learnjava.exception;

public class InvalidLibraryEventException extends RuntimeException {

    public InvalidLibraryEventException(String message) {
        super(message);
    }
}