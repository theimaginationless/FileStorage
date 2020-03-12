package API.Messaging;

import API.Codes.MessagingCode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WriteFileRequest implements Messaging {
    private UUID requestId;
    private MessagingCode messagingCode;
    private String hash;
    private MessagingPayload offset;
    private MessagingPayload size;

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

    public WriteFileRequest(@NotNull String hash, @NotNull MessagingCode messagingCode) {
        this.messagingCode = messagingCode;
        this.hash = hash;
        this.requestId = UUID.randomUUID();
    }

    public void setPayload(MessagingPayload size, MessagingPayload offset) {
        this.size = size;
        this.offset = offset;
    }

    public MessagingPayload getSizePayload() {
        return this.size;
    }

    public MessagingPayload getOffsetPayload() {
        return this.offset;
    }
}
