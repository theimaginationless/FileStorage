package API.Messaging;

import API.Codes.ServiceError;
import API.ConnectionBundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;
import java.util.logging.Logger;

public class MessageTransport {
    private static Logger logger = Logger.getLogger(MessageTransport.class.getName());

    public static ServiceError sendResponse(Response response, ConnectionBundle targetConnBundle) {
        JSONObject jsonObjectResponse = response.getJSONObject();
        ObjectOutputStream objectOutputStream = targetConnBundle.getObjectOutputStream();
        try {
            objectOutputStream.writeObject(jsonObjectResponse.toString());
            objectOutputStream.flush();
            logger.info("[" + Thread.currentThread().getId() + "] Sent response with id: " + response.getResponseId().toString() + "; type: " + response.getMessageCode());
            logger.info(jsonObjectResponse.toString(4));
        } catch(IOException ex) {
            ex.printStackTrace();
            return ServiceError.CONNERROR;
        }
        return ServiceError.OK;
    }

    public static JSONObject getResponse(Request request, ConnectionBundle sourceConnBundle) {
        JSONObject jsonObjectResponse = null;
        ObjectInputStream objectInputStream = sourceConnBundle.getObjectInputStream();
        try {
            do {
                Object obj = objectInputStream.readObject();
                if(obj instanceof String) {
                    JSONObject jsonObjectTempResponse = null;
                    try {
                        jsonObjectTempResponse = new JSONObject((String) obj);
                    } catch(JSONException ex) {
                        logger.severe(ex.getMessage());
                        break;
                    }
                    String responseId = jsonObjectTempResponse.getString("responseId");
                    String classType = jsonObjectTempResponse.getString("classType");
                    logger.info("[" + Thread.currentThread().getId() + "] Get response with id: " + responseId + "; classType: " + classType);
                    if(UUID.fromString(responseId).equals(request.getRequestId())) {
                        jsonObjectResponse = jsonObjectTempResponse;
                        logger.info("[" + Thread.currentThread().getId() + "] It's our response!");
                        logger.info(jsonObjectResponse.toString(4));
                    }
                }
            } while(jsonObjectResponse == null);
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return jsonObjectResponse;
    }

    public static void sendRequest(Request request, ConnectionBundle targetConnBundle) {
        ObjectOutputStream objectOutputStream = targetConnBundle.getObjectOutputStream();
        JSONObject jsonObjectRequest = request.getJSONObject();
        try {
            objectOutputStream.writeObject(jsonObjectRequest.toString());
            objectOutputStream.flush();
            logger.info("[" + Thread.currentThread().getId() + "] Sent request with id: " + request.getRequestId() + "; messageCode: " + request.getMessageCode());
            logger.info(jsonObjectRequest.toString(4));
        } catch(IOException ex) {
            logger.severe(ex.getMessage());
        }
    }

    public static JSONObject getRequest(ConnectionBundle sourceConnBundle) {
        JSONObject jsonObjectRequest = null;
        ObjectInputStream objectInputStream = sourceConnBundle.getObjectInputStream();
        try {
            do {
                Object obj = objectInputStream.readObject();
                if (obj instanceof String) {
                    try {
                        jsonObjectRequest = new JSONObject((String) obj);
                    } catch(JSONException ex) {
                        logger.severe(ex.getMessage());
                        break;
                    }
                    String requestId = jsonObjectRequest.getString("requestId");
                    String classType = jsonObjectRequest.getString("classType");
                    logger.info("[" + Thread.currentThread().getId() + "] Get request with id: " + requestId + "; classType: " + classType);
                    logger.info(jsonObjectRequest.toString(4));
                }
            } while(jsonObjectRequest == null);
        } catch(EOFException ex) {
            logger.info("[" + Thread.currentThread().getId() + "] End of communicating with client.");
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return jsonObjectRequest;
    }
}
