package com.db.exception;

public class StorageCapacityExceededException extends RuntimeException{
    public StorageCapacityExceededException() {
        super();
    }

    public StorageCapacityExceededException(String msg) {
        super(msg);
    }
}
