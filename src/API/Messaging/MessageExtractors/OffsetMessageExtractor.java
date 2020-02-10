package API.Messaging.MessageExtractors;

import API.Messaging.ResponsePayload;

public class OffsetMessageExtractor extends MessageExtractor {
    @Override
    public Long getMessage(ResponsePayload responsePayload) {
        return (long) responsePayload.getPayload();
    }
}
