package API;

import API.Codes.FileStorageException;
import API.Codes.ServiceError;
import API.Codes.MessagingCode;
import API.Messaging.MessageExtractors.OffsetMessageExtractor;
import API.Messaging.MessageExtractors.WriteFileResponseMessageExtractor;
import API.Messaging.MessagingTransport;
import API.Messaging.Request;
import API.Messaging.Response;
import API.Messaging.MessagingPayload;
import Common.Const;
import Common.Utils;
import org.jetbrains.annotations.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class BaseAPI {
    private static Logger logger = Logger.getLogger(BaseAPI.class.getName());
    private ConnectionBundle connectionBundle;
    private OutputStream os;

    public BaseAPI(@NotNull String addr) throws FileStorageException {
        connectionBundle = new ConnectionBundle(addr);
        System.out.println("Initialize client-side; Host server: '" + addr + "'");
        initConnection();
        System.out.println("Connected to: '" + connectionBundle.getAddr() + "'");
    }

    private void initConnection() throws FileStorageException {
        connectionBundle.Connect();
    }

    /**
     * Returns length of written file part on server
     */
    private long getOffsetData(@NotNull String hash) {
        Request request = new Request(hash, MessagingCode.GETOFFSET);
        long offset = 0;
        MessagingTransport.sendRequest(request, connectionBundle);
        Response response = MessagingTransport.getResponse(request, connectionBundle);
        MessagingPayload rp = response.getPayload();
        OffsetMessageExtractor ome = (OffsetMessageExtractor) MessagingCode.valueOf(response.getMessagingCode().name()).getInstance();
        offset = ome.getMessage(rp);
        return offset;
    }

    public ServiceError writeData(@NotNull String filePath) throws FileStorageException {
        try {
            OutputStream os = connectionBundle.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buf = new byte[Const.bufferSize];
            int read = 0;
            String hash = Utils.getSHA256(filePath);
            long offset = getOffsetData(hash);
            long len = fis.getChannel().size();
            if(len == offset) {
                System.out.println("File already exists!");
                return ServiceError.EXIST;
            }
            System.out.println("Hash: " + hash);

            Request writeFileRequest = new Request(hash, MessagingCode.WRITEFILE_REQUEST);
            writeFileRequest.setPayload(new MessagingPayload(offset));
            MessagingTransport.sendRequest(writeFileRequest, connectionBundle);
            Response writeFileResponse = MessagingTransport.getResponse(writeFileRequest, connectionBundle);
            WriteFileResponseMessageExtractor wfme = (WriteFileResponseMessageExtractor) MessagingCode.valueOf(writeFileResponse.getMessagingCode().name()).getInstance();
            boolean ready = wfme.getMessage(writeFileResponse.getPayload());
            if(!ready) {
                System.err.println("Something wrong!");
                throw new FileStorageException(ServiceError.Critical);
            }

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
//            os.flush();
//            os.close();
            fis.close();
            connectionBundle.close();
            if(readTotal != len) {
                throw new FileStorageException(ServiceError.Failed);
            }
        } catch(IOException ex) {
            System.err.println("Operation failed! " + Arrays.toString(ex.getStackTrace()));
            throw new FileStorageException(ServiceError.CONNERROR);
        }
        return ServiceError.OK;
    }

    public void writeFile(@NotNull String filePath) throws FileStorageException {
        int retries = Const.retriesOperationCount;
        ServiceError error;
        for(int retry = 0; retry < retries + 1; ++retry) {
            System.out.println("Retries: " + retry + "/" + retries);
            try {
                error = writeData(filePath);
                if(error != ServiceError.CONNERROR) {
                    break;
                }
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
