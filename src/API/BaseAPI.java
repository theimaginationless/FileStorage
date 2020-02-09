package API;

import API.Codes.ServiceError;
import API.Codes.MessagingCode;
import API.Messaging.Request;
import API.Messaging.Response;
import Common.Const;
import Common.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

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
        try {
            Response response = conn.sendRequest(request);
            offset = (Long) response.getResponse();
            System.out.println("Get response with id: " + response.getRequestId());

        } catch(IOException ex) {
            ex.printStackTrace();
        }

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
            System.out.println(hash + "; OFFSET: " + offset);
            long readTotal = offset;
            long len = fis.available();
            System.out.println("LEN: " + len);
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
            if(error == ServiceError.OK) {
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
