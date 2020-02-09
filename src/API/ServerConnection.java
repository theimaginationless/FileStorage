package API;

import API.Messaging.Request;
import API.Messaging.Response;
import Common.Const;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class ServerConnection {
    private String addr;
    private Socket socket;

    public ServerConnection(@NotNull String addr) {
        this.addr = addr;
    }

    public ServerConnection Connect() throws IOException {
        this.socket = new Socket(this.addr, Const.port);
        return this;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getAddr() {
        return this.addr;
    }

    public Response sendRequest(Request request) throws IOException {
        OutputStream outputStream = this.socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        InputStream inputStream = this.socket.getInputStream();
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        System.out.println("Sent request with id: " + request.getRequestId().toString());
        Response response = null;
        try {
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
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return response;
    }
}
