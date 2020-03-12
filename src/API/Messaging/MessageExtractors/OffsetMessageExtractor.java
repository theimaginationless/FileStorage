package API.Messaging.MessageExtractors;

import API.Messaging.MessagingPayload;

public class OffsetMessageExtractor extends MessageExtractor {
    @Override
    public Long getMessage(MessagingPayload responsePayload) {
        return (long) responsePayload.getPayload();
    }
}
