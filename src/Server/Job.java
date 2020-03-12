package Server;
import API.Codes.FileStorageException;
import API.Codes.MessagingCode;
import API.ConnectionBundle;
import API.Messaging.MessageExtractors.WriteFileRequestMessageExtractor;
import API.Messaging.MessagingTransport;
import API.Messaging.Request;
import API.Messaging.MessagingPayload;
import API.Messaging.Response;
import Common.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class Job {
    private static Logger logger = Logger.getLogger(Job.class.getName());
    private String fileName;
    private ConnectionBundle connectionBundle;

    public Job(Socket socket) {
        connectionBundle = new ConnectionBundle(socket);
        try {
            connectionBundle.Connect();
        } catch(FileStorageException ex) {
            logger.info("[" + Thread.currentThread().getId() + "] Connection error: " + ex.getMessage());
            return;
        }
        logger.info("[" + Thread.currentThread().getId() + "] Running service...");
    }

    private long getOffset(@NotNull String fileName) {
        long length = 0;
        File checkedFile = new File(Const.storagePath + fileName);
        if(!checkedFile.exists()) {
            return 0;
        }
        length = checkedFile.length();
        return length;
    }

    private Response createOffsetResponse(Request request, long offset) {
        Response response = new Response(request);
        response.setPayload(new MessagingPayload(offset));
        return response;
    }

    private void closeConnections() throws FileStorageException {
        connectionBundle.close();
        logger.info("[" + Thread.currentThread().getId() + "] Socket: '" + connectionBundle.getAddr() + "' and streams are closed successfully.");
    }

    public long start() throws Exception {
        long result = 0;
        boolean isRunning = true;
        while(isRunning) {
            Request request = null;
            if((request = MessagingTransport.getRequest(connectionBundle)) != null) {
                switch (request.getMessagingCode()) {
                    case GETOFFSET:
                        fileName = request.getHash();
                        long offset = getOffset(fileName);
                        Response response = createOffsetResponse(request, offset);
                        MessagingTransport.sendResponse(response, connectionBundle);
                        result = offset;
                        break;
                    case WRITEFILE_REQUEST:
                        WriteFileRequestMessageExtractor wfme = (WriteFileRequestMessageExtractor) MessagingCode.valueOf(request.getMessagingCode().name()).getInstance();
                        fileName = request.getHash();
                        long offset_req = wfme.getMessage(request.getPayload());
                        Response writeFileResponse = new Response((request));
                        writeFileResponse.setMessagingCode(MessagingCode.WRITEFILE_RESPONSE);
                        writeFileResponse.setPayload(new MessagingPayload(true));
                        MessagingTransport.sendResponse(writeFileResponse, connectionBundle);
                        result = Operations.writeFile(fileName, connectionBundle.getInputStream(), offset_req);
                        break;
                    default:
                        logger.info("[" + Thread.currentThread().getId() + "] Received request: '" + request.getMessagingCode().name() + "'; Skip it.");
                }
            } else {
                isRunning = false;
            }
        }
        closeConnections();
        return result;
    }
}
