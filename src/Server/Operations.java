package Server;
import Common.Const;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class Operations {
    private static Logger logger = Logger.getLogger(Operations.class.getName());

    public static void initStorage() {
        File testDir = new File(Const.storagePath);
        if(!testDir.exists()) {
            testDir.mkdir();
            logger.info("[" + Thread.currentThread().getId() + "] '" + Const.storagePath + "' is not exists and was created");
        }
    }

    private static long getOffset(@NotNull String fileName) {
        long length = 0;
        File checkedFile = new File(Const.storagePath + fileName);
        if(!checkedFile.exists()) {
            return 0;
        }
        length = checkedFile.length();
        return length;
    }

    public static Long writeFile(String fileName, InputStream inputStream, long offset) {
        initStorage();
        logger.info("[" + Thread.currentThread().getId() + "] Start writing file: '" + fileName + "'; offset: " + offset);
        byte[] buf = new byte[Const.bufferSize];
        int read;
        long totalReads = offset;
        try {
            RandomAccessFile raf = new RandomAccessFile(Const.storagePath + fileName, "rw");
            raf.seek(totalReads);
            while ((read = inputStream.read(buf, 0, buf.length)) != -1) {
                totalReads += read;
                raf.write(buf, 0, read);
            }
            raf.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return totalReads;
    }
}
