package API.Messaging;

import java.io.Serializable;

public class MessagingPayload implements Serializable {
    private Object payload;

    public MessagingPayload(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
