package API.Messaging.MessageExtractors;

import API.Messaging.MessagingPayload;

public class WriteFileResponseMessageExtractor extends MessageExtractor {
    @Override
    public Boolean getMessage(MessagingPayload responsePayload) {
        return (Boolean) responsePayload.getPayload();
    }
}
