package API;

import API.Codes.FileStorageException;
import API.Codes.ServiceError;
import Common.Const;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ConnectionBundle {
    private static Logger logger = Logger.getLogger(ConnectionBundle.class.getName());
    private String addr;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ConnectionBundle(String addr) {
        this.addr = addr;
    }

    public ConnectionBundle(Socket socket) {
        this.socket = socket;
        /*
        * Start index is 1 for removing leading '/' symbol
         */
        this.addr = socket.getInetAddress().toString().substring(1);
    }

    public ConnectionBundle Connect() throws FileStorageException {
        ConnectionBundle result = null;
        try {
            if(socket == null) {
                socket = new Socket(addr, Const.port);
            }
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            result = this;
        } catch(IOException ex) {
            logger.severe("[" + Thread.currentThread().getId() + "] Connection error: " + ex.getMessage());
            throw new FileStorageException(ServiceError.CONNERROR);
        }
        return result;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getAddr() {
        return addr;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void close() throws FileStorageException {
        try {
            objectInputStream.close();
            objectOutputStream.close();
            inputStream.close();
            outputStream.close();
            socket.close();
            logger.info("[" + Thread.currentThread().getId() + "] Socket: '" + getAddr() + "' and streams are closed successfully.");
        } catch(IOException ex) {
            logger.severe("[" + Thread.currentThread().getId() + "] Close connections error: " + ex.getMessage());
            throw new FileStorageException(ServiceError.CONNERROR);
        }
    }
}
