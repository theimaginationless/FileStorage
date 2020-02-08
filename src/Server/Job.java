package Server;

import Common.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class Job {
    InputStream mInputStream;
    FileOutputStream fos;

    public Job(InputStream is) {
        System.out.println("Create Server.Job for incoming connection");
        mInputStream = is;
    }

    public Long start() throws Exception {
        System.out.println("Start Server.Job");
        File testDir = new File(Const.storagePath);
        if(!testDir.exists()) {
            testDir.mkdir();
        }
        fos = new FileOutputStream(Const.storagePath + Utils.getRandomString(16));
        byte[] buf = new byte[Const.bufferSize];
        int read;
        long totalReads = 0;
        while((read = mInputStream.read(buf, 0, buf.length)) != -1) {
            totalReads += read;
            fos.write(buf, 0, read);
            System.out.print("\rReads: " + totalReads);
        }
        System.out.println();
        fos.flush();
        fos.close();
        return totalReads;
    }
}
