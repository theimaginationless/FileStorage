package API.Messaging;

import API.Codes.MessageCode;
import org.json.JSONObject;

import java.io.Serializable;

public interface Message extends Serializable {
    public MessageCode getMessageCode();
    public JSONObject getJSONObject();
}
