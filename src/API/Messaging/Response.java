package API.Messaging;

import java.util.UUID;

public interface Response extends Message {
    public UUID getResponseId();
}
