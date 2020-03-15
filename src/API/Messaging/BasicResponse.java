package API.Messaging;

import API.Codes.MessageCode;
import org.json.JSONObject;

import java.util.UUID;

public class BasicResponse implements Response {
    private UUID responseId;
    private MessageCode messageCode;
    private DataInfo info;

    @Override
    public MessageCode getMessageCode() {
        return messageCode;
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classType", this.getClass().getName());
        jsonObject.put("messageCode", messageCode.name());
        jsonObject.put("requestId", responseId.toString());
        jsonObject.put(DataInfo.class.getName(), info);
        return jsonObject;
    }

    public UUID getResponseId() {
        return responseId;
    }

    public BasicResponse(BasicRequest basicRequest) {
        this.messageCode = basicRequest.getMessageCode();
        this.info = basicRequest.getInfo();
        this.responseId = basicRequest.getRequestId();
    }

    public BasicResponse(JSONObject jsonObject) throws ClassFormatError {
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }
        messageCode = MessageCode.valueOf(jsonObject.getString("messageCode"));
        responseId = UUID.fromString(jsonObject.getString("requestId"));
        info = new DataInfo(jsonObject.getJSONObject(DataInfo.class.getName()));
    }

    public void setMessageCode(MessageCode messageCode) {
        this.messageCode = messageCode;
    }
}
