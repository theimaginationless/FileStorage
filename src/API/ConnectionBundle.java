package API;

import API.Codes.FileStorageException;
import API.Codes.ServiceError;
import Common.*;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.logging.Logger;

public class ConnectionBundle {
    private static Logger logger = Logger.getLogger(ConnectionBundle.class.getName());
    private String addr;
    private Socket socket;
    private InputStreamWrapper inputStreamWrapper;
    private OutputStreamWrapper outputStreamWrapper;
    private InputStream pureInputStream;
    private OutputStream pureOutputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BufferedOutputStreamWrapper bufferedOutputStreamWrapper;
    private BufferedInputStreamWrapper bufferedInputStreamWrapper;
    private boolean compressed;

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
            pureOutputStream = socket.getOutputStream();
            outputStreamWrapper = new OutputStreamWrapper(pureOutputStream, compressed);
            objectOutputStream = new ObjectOutputStream(pureOutputStream);
            bufferedOutputStreamWrapper = new BufferedOutputStreamWrapper(new BufferedOutputStream(pureOutputStream, Const.bufferSize), compressed);
            pureInputStream = socket.getInputStream();
            inputStreamWrapper = new InputStreamWrapper(pureInputStream, compressed);
            objectInputStream = new ObjectInputStream(pureInputStream);
            bufferedInputStreamWrapper = new BufferedInputStreamWrapper(new BufferedInputStream(pureInputStream, Const.bufferSize), compressed);

            result = this;
        } catch(IOException ex) {
            logger.severe("[" + Thread.currentThread().getId() + "] Connection error: " + ex.getMessage());
            throw new FileStorageException(ServiceError.CONNERROR);
        }
        return result;
    }

    public BufferedOutputStreamWrapper getBufferedOutputStreamWrapper() {
        return bufferedOutputStreamWrapper;
    }

    public BufferedInputStreamWrapper getBufferedInputStreamWrapper() {
        return bufferedInputStreamWrapper;
    }

    public InputStreamWrapper getInputStreamWrapper() {
        return inputStreamWrapper;
    }

    public OutputStreamWrapper getOutputStreamWrapper() {
        return outputStreamWrapper;
    }

    public InputStream getPureInputStream() {
        return pureInputStream;
    }

    public OutputStream getPureOutputStream() {
        return pureOutputStream;
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
            //outputStreamWrapper.flush();
            //objectOutputStream.flush();
            //objectInputStream.close();
            //inputStreamWrapper.close();
            socket.close();
            logger.info("[" + Thread.currentThread().getId() + "] Socket: '" + getAddr() + "' and streams are closed successfully.");
        } catch(IOException ex) {
            logger.severe("[" + Thread.currentThread().getId() + "] Close connections error: " + ex.getMessage());
            throw new FileStorageException(ServiceError.CONNERROR);
        }
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
}
