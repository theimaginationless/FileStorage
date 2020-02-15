package API.Codes;

public class FileStorageException extends Exception {
    private ServiceError errorCode;

    public FileStorageException(ServiceError errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public FileStorageException(String message, ServiceError errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileStorageException(String message, Throwable cause, ServiceError errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public FileStorageException(Throwable cause, ServiceError errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public FileStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ServiceError errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public ServiceError getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " ErrorCode: " + errorCode.name();
    }
}
