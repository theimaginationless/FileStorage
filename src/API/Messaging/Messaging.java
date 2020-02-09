package API.Messaging;

import API.Codes.MessagingCode;

import java.io.Serializable;

public interface Messaging extends Serializable {
    public MessagingCode getMessagingCode();
    public String getHash();
}
