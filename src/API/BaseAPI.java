package API;

import API.Codes.FileStorageException;
import API.Codes.ServiceError;
import API.Codes.MessageCode;
import API.Messaging.*;
import Common.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
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

    public ServiceError writeData(@NotNull String filePath, boolean isCompressed) throws FileStorageException {
        try {
            logger.info("[" + Thread.currentThread().getId() + "] Start writing file. Buffer size: " + Const.bufferSize);
            OutputStreamWrapper os = connectionBundle.getOutputStreamWrapper();
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buf = new byte[Const.bufferSize];
            int read = 0;
            long len = fis.getChannel().size();
            String hash = Utils.getSHA256(filePath);
            DataInfo info = new DataInfo(hash, len);
            FileInfoRequest fileInfoRequest = new FileInfoRequest(info, MessageCode.INFO_REQUEST);
            MessageTransport.sendRequest(fileInfoRequest, connectionBundle);
            FileInfoResponse fileInfoResponse = new FileInfoResponse(MessageTransport.getResponse(fileInfoRequest, connectionBundle));
            long remoteSize = fileInfoResponse.getInfo().getSize();

            WriteFileRequest writeFileRequest = new WriteFileRequest(info, MessageCode.WRITEFILE_REQUEST);
            writeFileRequest.setOffset(remoteSize);
            writeFileRequest.setCompressed(isCompressed);
            MessageTransport.sendRequest(writeFileRequest, connectionBundle);
            WriteFileResponse writeFileResponse = new WriteFileResponse(MessageTransport.getResponse(writeFileRequest, connectionBundle));
            if(writeFileResponse.getCode() != ServiceError.OK) {
                System.err.println("Something wrong! Code: " + writeFileResponse.getCode());
                throw new FileStorageException(writeFileResponse.getCode());
            }

            if(len <= remoteSize) {
                System.out.println("File already exists!");
                return ServiceError.EXIST;
            }
            System.out.println("Hash: " + hash);
            os.setCompressed(isCompressed);
            long readTotal = remoteSize;
            long readTotalPerSec = 0;
            float avgSpeed = 0;
            long startTime = 0;
            long endTime = 0;
            long avgEta = 0;
            long rounds = 0;
            fis.skip(readTotal);
            while ((read = fis.read(buf, 0, buf.length)) != -1) {
                ++rounds;
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

            System.out.println("\nWritten total: " + readTotal + " bytes for " + rounds + " rounds");
            fis.close();
            os.flush();
            connectionBundle.close();
            if(readTotal != len) {
                throw new FileStorageException(ServiceError.Failed);
            }
        } catch(IOException ex) {
            System.err.println("Operation failed! ");
            ex.printStackTrace();
            throw new FileStorageException(ServiceError.CONNERROR);
        }
        return ServiceError.OK;
    }

    public void writeFile(@NotNull String filePath, boolean isCompressed) throws FileStorageException {
        int retries = Const.retriesOperationCount;
        ServiceError error;
        for(int retry = 0; retry < retries + 1; ++retry) {
            System.out.println("Retries: " + retry + "/" + retries);
            try {
                error = writeData(filePath, isCompressed);
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
