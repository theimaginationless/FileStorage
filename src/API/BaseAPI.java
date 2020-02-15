package API;

import API.Codes.FileStorageException;
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
import java.util.Arrays;
import java.util.logging.Logger;

public class BaseAPI {
    private static Logger logger = Logger.getLogger(BaseAPI.class.getName());
    private ServerConnection conn;
    private Socket socket;
    private OutputStream os;

    public BaseAPI(@NotNull String addr) throws FileStorageException {
        conn = new ServerConnection(addr);
        logger.info("Initialize client-side; Host server: '" + conn.getAddr() + "'");
        initConnection();
    }

    private void initConnection() throws FileStorageException {
        try {
            socket = conn.Connect().getSocket();
        } catch(IOException ex) {
            logger.severe("Problem with connection. " + ex.getMessage());
            throw new FileStorageException(ServiceError.CONNERROR);
        }
    }

    /**
     * Returns length of written file part on server
     */
    private long getOffsetData(@NotNull String hash) {
        Request request = new Request(hash, MessagingCode.GETOFFSET);
        long offset = 0;
        MessagingTransport.sendRequest(request, socket);
        Response response = MessagingTransport.getResponse(request, socket);
        ResponsePayload rp = response.getResponse();
        OffsetMessageExtractor ome = (OffsetMessageExtractor) MessagingCode.valueOf(response.getMessagingCode().name()).getInstance();
        offset = ome.getMessage(rp);
        return offset;
    }

    public ServiceError writeData(@NotNull String filePath) throws FileStorageException {
        try {
            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buf = new byte[Const.bufferSize];
            int read = 0;
            String hash = Utils.getSHA256(filePath);
            long offset = getOffsetData(hash);
            long len = fis.getChannel().size();
            if(len == offset) {
                logger.info("File already exists!");
                return ServiceError.EXIST;
            }
            logger.info("Hash: " + hash);
            long readTotal = offset;
            long readTotalPerSec = 0;
            float avgSpeed = 0;
            long startTime = 0;
            long endTime = 0;
            long avgEta = 0;
            fis.skip(readTotal);
            while ((read = fis.read(buf, 0, buf.length)) != -1) {
                if(endTime == 0) {
                    startTime = System.currentTimeMillis();
                }

                readTotal += read;
                readTotalPerSec += read;
                os.write(buf, 0, read);
                endTime = System.currentTimeMillis();
                long delta = endTime - startTime;
                if(delta >= 1000) {
                    avgSpeed = (float)readTotalPerSec / (1024 * 1024);
                    avgEta = (len - readTotal) / readTotalPerSec;
                    endTime = 0;
                    readTotalPerSec = 0;
                }
                int perc = (int) ((double) readTotal / len * 100);
                System.out.printf("\r%d%% written, %.2f MB/s, ETA: %d s.", perc, avgSpeed, avgEta);
            }

            System.out.println();
            os.flush();
            os.close();
            fis.close();
            socket.close();
            if(readTotal != len) {
                throw new FileStorageException(ServiceError.Failed);
            }
        } catch(IOException ex) {
            logger.severe("Operation failed! " + Arrays.toString(ex.getStackTrace()));
            throw new FileStorageException(ServiceError.CONNERROR);
        }
        return ServiceError.OK;
    }

    public void writeFile(@NotNull String filePath) throws FileStorageException {
        int retries = Const.retriesOperationCount;
        ServiceError error;
        for(int retry = 0; retry < retries + 1; ++retry) {
            logger.info("Retries: " + retry + "/" + retries);
            try {
                error = writeData(filePath);
                if(error == ServiceError.OK) {
                    break;
                }
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
}
