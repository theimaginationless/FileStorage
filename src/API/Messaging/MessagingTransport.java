package API.Messaging;

import API.Codes.ServiceError;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class MessagingTransport {
    private static Logger logger = Logger.getLogger(MessagingTransport.class.getClass().getName());
    
    public static ServiceError sendResponse(Response response, ObjectOutputStream objectOutputStream) {
        try {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch(IOException ex) {
            ex.printStackTrace();
            return ServiceError.CONNERROR;
        }
        return ServiceError.OK;
    }

    public static Response getResponse(Request request, Socket source) {
        Response response = null;
        try {
            InputStream inputStream = source.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            do {
                Object obj = objectInputStream.readObject();
                if(obj instanceof Response) {
                    Response tempResponse = (Response) obj;
                    logger.info("Get response with id: " + tempResponse.getRequestId().toString());
                    if(tempResponse.getRequestId().equals(request.getRequestId())) {
                        response = tempResponse;
                        logger.info("It's us response!");
                    }
                }
            } while(response == null);
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public static void sendRequest(Request request, Socket target) {
        try {
            OutputStream outputStream = target.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            logger.info("Sent request with id: " + request.getRequestId().toString());
        } catch(IOException ex) {
            logger.severe(ex.getMessage());
        }
    }

    public static Request getRequest(ObjectInputStream objectInputStream) {
        Request request = null;
        try {
            Object obj = objectInputStream.readObject();
            if(obj instanceof Request) {
                request = (Request) obj;
                logger.info("Get request with id: " + request.getRequestId().toString());
            }
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return request;
    }
}
