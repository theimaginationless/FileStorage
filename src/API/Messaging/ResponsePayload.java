package API.Messaging;

import java.io.Serializable;

public class ResponsePayload implements Serializable {
    private Object payload;

    public ResponsePayload(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
