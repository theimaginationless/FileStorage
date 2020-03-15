package API.Messaging;

import API.Codes.MessageCode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.UUID;

public class BasicRequest implements Request {
    private UUID requestId;
    private MessageCode messageCode;
    private DataInfo info;

    @Override
    public MessageCode getMessageCode() {
        return messageCode;
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageCode", messageCode.name());
        jsonObject.put("requestId", requestId.toString());
        jsonObject.put(DataInfo.class.getName(), info);
        return jsonObject;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public BasicRequest(@NotNull DataInfo info, @NotNull MessageCode messageCode) {
        this.messageCode = messageCode;
        this.info = info;
        this.requestId = UUID.randomUUID();
    }

    public BasicRequest(JSONObject jsonObject) throws ClassFormatError {
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }

        messageCode = MessageCode.valueOf(jsonObject.getString("messageCode"));
        requestId = UUID.fromString(jsonObject.getString("requestId"));
        info = new DataInfo(jsonObject.getJSONObject(DataInfo.class.getName()));
    }

    public DataInfo getInfo() {
        return info;
    }
}
