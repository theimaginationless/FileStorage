package Server;

import API.Messaging.Request;
import API.Messaging.Response;
import Common.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class Job {
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private FileOutputStream fos;
    private Socket socket;
    private String fileName;

    public Job(Socket socket) {
        System.out.println("Create Server.Job for incoming connection");
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
        try {
            File checkedFile = new File(Const.storagePath + fileName);
            if(!checkedFile.exists()) {
                return 0;
            }
            FileInputStream fileInputStream = new FileInputStream(checkedFile);
            length = fileInputStream.available();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return length;
    }

    private Request getRequest() {
        Request request = null;
        try {
                Object obj = objectInputStream.readObject();
                if(obj instanceof Request) {
                    request = (Request) obj;
                    System.out.println("Get request with id: " + request.getRequestId().toString());
                }
        } catch(ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return request;
    }

    public Long start() throws Exception {
        System.out.println("Start Server.Job");
        File testDir = new File(Const.storagePath);
        if(!testDir.exists()) {
            testDir.mkdir();
        }
        byte[] buf = new byte[Const.bufferSize];
        int read;
        long totalReads = 0;
        Request request = getRequest();
        switch (request.getMessagingCode()) {
            case GETOFFSET:
                fileName = request.getHash();
                totalReads = getOffset(fileName);
                Response response = new Response(request);
                response.setResponse(totalReads);
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                inputStream.skip(totalReads);
        }
        fos = new FileOutputStream(Const.storagePath + fileName);
        RandomAccessFile raf = new RandomAccessFile(Const.storagePath + fileName, "rw");
        raf.seek(totalReads);
        while((read = inputStream.read(buf, 0, buf.length)) != -1) {
            totalReads += read;
            raf.write(buf, 0, read);
            System.out.print("\rReads: " + totalReads);
        }
        System.out.println();
        fos.flush();
        fos.close();
        return totalReads;
    }
}
