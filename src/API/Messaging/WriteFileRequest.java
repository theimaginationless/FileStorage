package API.Messaging;

import API.Codes.MessageCode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.UUID;

public class WriteFileRequest implements Request {
    private UUID requestId;
    private MessageCode messageCode;
    private long offset;
    private DataInfo info;
    private boolean compressed;

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
        jsonObject.put("compressed", compressed);
        jsonObject.put("offset", offset);
        jsonObject.put(DataInfo.class.getName(), info.getJSONObject());
        return jsonObject;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public WriteFileRequest(@NotNull DataInfo info, @NotNull MessageCode messageCode) {
        this.info = info;
        this.messageCode = messageCode;
        this.requestId = UUID.randomUUID();
    }

    public WriteFileRequest(JSONObject jsonObject) throws ClassFormatError{
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }
        messageCode = MessageCode.valueOf(jsonObject.getString("messageCode"));
        requestId = UUID.fromString(jsonObject.getString("requestId"));
        offset = jsonObject.getLong("offset");
        compressed = jsonObject.getBoolean("compressed");
        JSONObject jsonObjectDataInfo = jsonObject.getJSONObject(DataInfo.class.getName());
        info = new DataInfo(jsonObjectDataInfo);
    }

    public void setData(long size, long offset) {
        this.info.setSize(size);
        this.offset = offset;
    }

    public DataInfo getInfo() {
        return info;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
