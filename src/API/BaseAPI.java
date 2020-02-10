package API;

import API.Codes.ServiceError;
import API.Codes.MessagingCode;
import API.Messaging.MessageExtractors.OffsetMessageExtractor;
import API.Messaging.MessagingTransport;
import API.Messaging.Request;
import API.Messaging.Response;
import API.Messaging.ResponsePayload;
import Common.Const;
import Common.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class BaseAPI {
    private ServerConnection conn;
    private Socket socket;
    private OutputStream os;

    public BaseAPI(@NotNull String addr) {
        conn = new ServerConnection(addr);
        System.out.println("Initialize client-side; Host server: '" + conn.getAddr() + "'");
        initConnection();
    }

    private boolean initConnection() {
        try {
            socket = conn.Connect().getSocket();
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Returns length of written file part on server
     */
    private long getOffsetData(@NotNull String hash) {
        Request request = new Request(hash, MessagingCode.GETOFFSET);
        long offset = 0;
        Response response = MessagingTransport.sendRequest(request, socket);
        ResponsePayload rp = response.getResponse();
        OffsetMessageExtractor ome = (OffsetMessageExtractor) MessagingCode.valueOf(response.getMessagingCode().name()).getInstance();
        offset = ome.getMessage(rp);
        return offset;
    }

    public ServiceError writeData(@NotNull String filePath) {
        try {
            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buf = new byte[Const.bufferSize];
            int read = 0;
            String hash = Utils.getSHA256(filePath);
            long offset = getOffsetData(hash);
            long len = fis.available();
            if(len == offset) {
                System.out.println("File already exists!");
                return ServiceError.EXIST;
            }
            System.out.println("Hash: " + hash);
            long readTotal = offset;
            fis.skip(readTotal);
            while ((read = fis.read(buf, 0, buf.length)) != -1) {
                readTotal += read;
                os.write(buf, 0, read);
                int perc = (int) ((double) readTotal / len * 100);
                System.out.print("\r" + perc + "% written");
            }

            System.out.println();
            os.flush();
            os.close();
            fis.close();
            socket.close();
            if(readTotal != len) {
                return ServiceError.CONNERROR;
            }
            return ServiceError.OK;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return ServiceError.Failed;
    }

    public ServiceError writeFile(@NotNull String filePath) {
        int retries = 5;
        ServiceError error = ServiceError.Unknown;
        for(int retry = 0; retry < retries + 1; ++retry) {
            System.out.println("Retries: " + retry + "/" + retries);
            error = writeData(filePath);
            if(error != ServiceError.CONNERROR) {
                break;
            }
            try {
                Thread.currentThread().sleep(1000);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return error;
    }
}
