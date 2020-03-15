package API.Messaging;

import API.Codes.MessageCode;
import API.Codes.ServiceError;
import org.json.JSONObject;

import java.util.UUID;

public class DataInfo {
    private String hash;
    private long size;

    public DataInfo(String hash, long size) {
        this.hash = hash;
        this.size = size;
    }

    public DataInfo(JSONObject jsonObject) throws ClassFormatError {
        String classType = jsonObject.getString("classType");
        if(!classType.equals(this.getClass().getName())) {
            throw new ClassFormatError();
        }
        this.hash = jsonObject.getString("hash");
        this.size = jsonObject.getLong("size");
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classType", DataInfo.class.getName());
        jsonObject.put("hash", hash);
        jsonObject.put("size", size);
        return jsonObject;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
