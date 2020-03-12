package API.Messaging;

import API.Codes.ServiceError;
import API.ConnectionBundle;

import java.io.*;
import java.util.logging.Logger;

public class MessagingTransport {
    private static Logger logger = Logger.getLogger(MessagingTransport.class.getName());

    public static ServiceError sendResponse(Response response, ConnectionBundle targetConnBundle) {
        ObjectOutputStream objectOutputStream = targetConnBundle.getObjectOutputStream();
        try {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            logger.info("[" + Thread.currentThread().getId() + "] Sent response with id: " + response.getRequestId().toString() + "; type: " + response.getMessagingCode());
        } catch(IOException ex) {
            ex.printStackTrace();
            return ServiceError.CONNERROR;
        }
        return ServiceError.OK;
    }

    public static Response getResponse(Request request, ConnectionBundle sourceConnBundle) {
        Response response = null;
        ObjectInputStream objectInputStream = sourceConnBundle.getObjectInputStream();
        try {
            do {
                Object obj = objectInputStream.readObject();
                if(obj instanceof Response) {
                    Response tempResponse = (Response) obj;
                    logger.info("[" + Thread.currentThread().getId() + "] Get response with id: " + tempResponse.getRequestId().toString() + "; type: " + tempResponse.getMessagingCode());
                    if(tempResponse.getRequestId().equals(request.getRequestId())) {
                        response = tempResponse;
                        logger.info("[" + Thread.currentThread().getId() + "] It's our response!");
                    }
                }
            } while(response == null);
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public static void sendRequest(Request request, ConnectionBundle targetConnBundle) {
        ObjectOutputStream objectOutputStream = targetConnBundle.getObjectOutputStream();
        try {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            logger.info("[" + Thread.currentThread().getId() + "] Sent request with id: " + request.getRequestId().toString() + "; type: " + request.getMessagingCode());
        } catch(IOException ex) {
            logger.severe(ex.getMessage());
        }
    }

    public static Request getRequest(ConnectionBundle sourceConnBundle) {
        Request request = null;
        ObjectInputStream objectInputStream = sourceConnBundle.getObjectInputStream();
        try {
            Object obj = objectInputStream.readObject();
            if (obj instanceof Request) {
                request = (Request) obj;
                logger.info("[" + Thread.currentThread().getId() + "] Get request with id: " + request.getRequestId().toString() + "; type: " + request.getMessagingCode());
            }
        } catch(EOFException ex) {
            logger.info("[" + Thread.currentThread().getId() + "] End of communicating with client.");
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return request;
    }
}
