package Server;
import API.Messaging.MessagingTransport;
import API.Messaging.Request;
import API.Messaging.Response;
import API.Messaging.ResponsePayload;
import Common.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class Job {
    private static Logger logger = Logger.getLogger(Job.class.getClass().getName());
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;
    private String fileName;

    public Job(Socket socket) {
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            inputStream = this.socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        } catch(IOException ex) {
            ex.printStackTrace();
        }

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
        response.setResponse(new ResponsePayload(offset));
        return response;
    }

    public long start() throws Exception {
        logger.info("Running service...");
        File testDir = new File(Const.storagePath);
        if(!testDir.exists()) {
            testDir.mkdir();
        }
        byte[] buf = new byte[Const.bufferSize];
        int read;
        long totalReads = 0;
        Request request = MessagingTransport.getRequest(objectInputStream);

        switch (request.getMessagingCode()) {
            case GETOFFSET:
                fileName = request.getHash();
                totalReads = getOffset(fileName);
                Response response = createOffsetResponse(request, totalReads);
                MessagingTransport.sendResponse(response, objectOutputStream);
                inputStream.skip(totalReads);
        }
        RandomAccessFile raf = new RandomAccessFile(Const.storagePath + fileName, "rw");
        raf.seek(totalReads);
        while((read = inputStream.read(buf, 0, buf.length)) != -1) {
            totalReads += read;
            raf.write(buf, 0, read);
            System.out.print("\rReads: " + totalReads);
        }
        System.out.println();
        raf.close();
        objectOutputStream.close();
        objectInputStream.close();
        inputStream.close();
        socket.close();
        return totalReads;
    }
}
