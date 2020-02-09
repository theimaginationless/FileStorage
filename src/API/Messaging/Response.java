package API.Messaging;

import API.Codes.MessagingCode;

import java.util.UUID;

public class Response<T> implements Messaging {
    private UUID requestId;
    private MessagingCode messagingCode;
    private String hash;
    private T data;

    @Override
    public MessagingCode getMessagingCode() {
        return messagingCode;
    }

    @Override
    public String getHash() {
        return hash;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Response(Request request) {
        this.messagingCode = request.getMessagingCode();
        this.hash = request.getHash();
        this.requestId = request.getRequestId();
    }

    public void setResponse(T data) {
        this.data = data;
    }

    public T getResponse() {
        return this.data;
    }
}
