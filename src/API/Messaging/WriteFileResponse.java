package API.Messaging;

import API.Codes.MessageCode;
import API.Codes.ServiceError;
import org.json.JSONObject;

import java.util.UUID;

public class WriteFileResponse implements Response {
    private UUID responseId;
    private MessageCode messageCode;
    private ServiceError code;
    private DataInfo info;
    private boolean compressed;

    public WriteFileResponse(WriteFileRequest request, DataInfo info, MessageCode messageCode, ServiceError code) {
        this.responseId = request.getRequestId();
        this.info = info;
        this.messageCode = messageCode;
        this.code = code;
        this.compressed = request.isCompressed();
    }

    public WriteFileResponse(JSONObject jsonObject) throws ClassFormatError {
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }
        JSONObject jsonObjectDataInfo = jsonObject.getJSONObject(DataInfo.class.getName());
        info = new DataInfo(jsonObjectDataInfo);
        messageCode = MessageCode.valueOf(jsonObject.getString("messageCode"));
        responseId = UUID.fromString(jsonObject.getString("responseId"));
        code = ServiceError.valueOf((String) jsonObject.get("code"));
    }

    @Override
    public UUID getResponseId() {
        return responseId;
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
        jsonObject.put("responseId", responseId.toString());
        jsonObject.put("compressed", compressed);
        jsonObject.put("code", code.name());
        jsonObject.put(DataInfo.class.getName(), info.getJSONObject());
        return jsonObject;
    }

    public DataInfo getInfo() {
        return info;
    }

    public ServiceError getCode() {
        return code;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
}
