package API.Messaging;

import API.Codes.MessageCode;
import org.json.JSONObject;

import java.util.UUID;

public class FileInfoRequest implements Request {
    private UUID requestId;
    private MessageCode messageCode;
    private DataInfo info;

    public FileInfoRequest(DataInfo info, MessageCode messageCode) {
        this.requestId = UUID.randomUUID();
        this.info = info;
        this.messageCode = messageCode;
    }

    public FileInfoRequest(JSONObject jsonObject) throws ClassFormatError {
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }
        JSONObject jsonObjectDataInfo = jsonObject.getJSONObject(DataInfo.class.getName());
        info = new DataInfo(jsonObjectDataInfo);
        messageCode = MessageCode.valueOf(jsonObject.getString("messageCode"));
        requestId = UUID.fromString(jsonObject.getString("requestId"));
    }

    @Override
    public MessageCode getMessageCode() {
        return messageCode;
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classType", this.getClass().getName());
        jsonObject.put("messageCode", messageCode.name());
        jsonObject.put("requestId", requestId.toString());
        jsonObject.put(DataInfo.class.getName(), info.getJSONObject());
        return jsonObject;
    }

    @Override
    public UUID getRequestId() {
        return requestId;
    }

    public DataInfo getInfo() {
        return info;
    }
}
