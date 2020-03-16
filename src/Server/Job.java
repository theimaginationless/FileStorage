package Server;
import API.Codes.FileStorageException;
import API.Codes.MessageCode;
import API.Codes.ServiceError;
import API.ConnectionBundle;
import API.Messaging.*;
import Common.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

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

    private long getFileSize(@NotNull String fileName) {
        long length = 0;
        File checkedFile = new File(Const.storagePath + fileName);
        if(!checkedFile.exists()) {
            return 0;
        }
        length = checkedFile.length();
        return length;
    }

    private void closeConnections() throws FileStorageException {
        connectionBundle.close();
        logger.info("[" + Thread.currentThread().getId() + "] Socket: '" + connectionBundle.getAddr() + "' and streams are closed successfully.");
    }

    public long start() throws Exception {
        long result = 0;
        boolean isRunning = true;
        while(isRunning) {
            BasicRequest basicRequest = null;
            JSONObject jsonObjectRequest;
            if((jsonObjectRequest = MessageTransport.getRequest(connectionBundle)) != null) {
                MessageCode requestCode = MessageCode.valueOf(jsonObjectRequest.getString("messageCode"));
                switch (requestCode) {
                    case INFO_REQUEST:
                        FileInfoRequest infoRequest = new FileInfoRequest(jsonObjectRequest);
                        fileName = infoRequest.getInfo().getHash();
                        long size = getFileSize(fileName);
                        DataInfo info = new DataInfo(fileName, size);
                        FileInfoResponse response = new FileInfoResponse(infoRequest, info, MessageCode.INFO_RESPONSE, ServiceError.OK);
                        MessageTransport.sendResponse(response, connectionBundle);
                        result = size;
                        break;
                    case WRITEFILE_REQUEST:
                        WriteFileRequest writeFileRequest = new WriteFileRequest(jsonObjectRequest);
                        fileName = writeFileRequest.getInfo().getHash();
                        long offset_req = writeFileRequest.getOffset();
                        long writeFileSize = getFileSize(fileName);
                        boolean isCompressed = writeFileRequest.isCompressed();
                        DataInfo writeFileInfo = new DataInfo(writeFileRequest.getInfo().getHash(), writeFileSize);
                        ServiceError code = ServiceError.OK;
                        if(writeFileSize != 0) {
                            code = ServiceError.EXIST;
                        }
                        WriteFileResponse writeFileResponse = new WriteFileResponse(writeFileRequest, writeFileInfo, MessageCode.WRITEFILE_RESPONSE, code);
                        MessageTransport.sendResponse(writeFileResponse, connectionBundle);
                        if(code != ServiceError.OK) {
                            logger.info("[" + Thread.currentThread().getId() + "] Connection close because code: '" + code.name() + "'");
                            return 0;
                        }
                        result = Operations.writeFile(fileName, connectionBundle.getInputStreamWrapper(), offset_req, isCompressed);
                        connectionBundle.close();
                        break;
                    default:
                        logger.info("[" + Thread.currentThread().getId() + "] Received request: '" + basicRequest.getMessageCode().name() + "'; Skip it.");
                }
            } else {
                isRunning = false;
            }
        }
        closeConnections();
        return result;
    }
}
