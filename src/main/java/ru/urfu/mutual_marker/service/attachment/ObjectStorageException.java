package ru.urfu.mutual_marker.service.attachment;

public class ObjectStorageException extends RuntimeException{
    public ObjectStorageException() {
    }

    public ObjectStorageException(String message) {
        super(message);
    }

    public ObjectStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectStorageException(Throwable cause) {
        super(cause);
    }
}
