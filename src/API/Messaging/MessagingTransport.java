package API.Messaging;

import API.Codes.ServiceError;

import java.io.*;
import java.net.Socket;

public class MessagingTransport {
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

    public static Response sendRequest(Request request, Socket target) {
        Response response = null;
        try {
            OutputStream outputStream = target.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = target.getInputStream();
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            System.out.println("Sent request with id: " + request.getRequestId().toString());
            do {
                System.out.println("Waiting read object");
                Object obj = objectInputStream.readObject();
                System.out.println("Object has been read!");
                if(obj instanceof Response) {
                    Response tempResponse = (Response) obj;
                    System.out.println("Get response with id: " + tempResponse.getRequestId().toString());
                    if(tempResponse.getRequestId().equals(request.getRequestId())) {
                        response = tempResponse;
                        System.out.println("Catch it!");
                    }
                }
            } while(response == null);
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public static Request getRequest(ObjectInputStream objectInputStream) {
        Request request = null;
        try {
            Object obj = objectInputStream.readObject();
            if(obj instanceof Request) {
                request = (Request) obj;
                System.out.println("Get request with id: " + request.getRequestId().toString());
            }
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return request;
    }
}
