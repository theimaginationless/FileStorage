package API.Messaging.MessageExtractors;

import API.Messaging.ResponsePayload;

import java.io.Serializable;

public abstract class MessageExtractor implements Serializable {
    public Object getMessage(ResponsePayload payload) {
        return payload.getPayload();
    }
}
