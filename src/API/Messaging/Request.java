package API.Messaging;

import API.Codes.MessagingCode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Request implements Messaging {
    private UUID requestId;
    private MessagingCode messagingCode;
    private String hash;
    private MessagingPayload data;

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

    public Request(@NotNull String hash, @NotNull MessagingCode messagingCode) {
        this.messagingCode = messagingCode;
        this.hash = hash;
        this.requestId = UUID.randomUUID();
    }

    public void setPayload(MessagingPayload data) {
        this.data = data;
    }

    public MessagingPayload getPayload() {
        return this.data;
    }
}
