package com.sro.SpringCoreTask1.exception;

public class StorageInitializationException extends RuntimeException{
    
    public StorageInitializationException(String message) {
        super(message);
    }

    public StorageInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}