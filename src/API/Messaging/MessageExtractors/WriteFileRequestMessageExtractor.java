package API.Messaging.MessageExtractors;

import API.Messaging.MessagingPayload;

public class WriteFileRequestMessageExtractor extends MessageExtractor {
    @Override
    public Long getMessage(MessagingPayload responsePayload) {
        return (Long) responsePayload.getPayload();
    }
}
