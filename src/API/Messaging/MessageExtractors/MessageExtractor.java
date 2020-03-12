package API.Messaging.MessageExtractors;

import API.Messaging.MessagingPayload;

import java.io.Serializable;

public abstract class MessageExtractor implements Serializable {
    public Object getMessage(MessagingPayload payload) {
        return payload.getPayload();
    }
}
