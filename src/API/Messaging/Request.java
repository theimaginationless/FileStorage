package API.Messaging;

import java.util.UUID;

public interface Request extends Message {
    public UUID getRequestId();
}
