package Server;

import Common.Const;
import Common.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class Job implements Callable<Integer> {
    InputStream mInputStream;
    FileOutputStream fos;

    public Job(InputStream is) {
        System.out.println("Create Server.Job for incoming connection");
        mInputStream = is;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Start Server.Job");
        File testDir = new File(Const.storagePath);
        if(!testDir.exists()) {
            testDir.mkdir();
        }
        fos = new FileOutputStream(Const.storagePath + Utils.getRandomString(16));
        byte[] buf = new byte[Const.bufferSize];
        int read;
        int totalReads = 0;
        while((read = mInputStream.read(buf, 0, buf.length)) != -1) {
            totalReads += read;
            fos.write(buf, 0, read);
            System.out.println("Reads: " + totalReads);
        }
        fos.flush();
        fos.close();
        return totalReads;
    }
}
