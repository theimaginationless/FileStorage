package Server;
import Common.Const;
import Common.InputStreamWrapper;
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

    public static Long writeFile(String fileName, InputStreamWrapper inputStreamWrapper, long offset, boolean isCompressed) {
        initStorage();
        inputStreamWrapper.setCompressed(isCompressed);
        logger.info("[" + Thread.currentThread().getId() + "] Start writing file: '" + fileName + "'; offset: " + offset + "; Buffer size: " + Const.bufferSize);
        byte[] buf = new byte[Const.bufferSize];
        int read;
        long totalReads = offset;
        long rounds = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(Const.storagePath + fileName, "rw");
            raf.seek(totalReads);
            while ((read = inputStreamWrapper.read(buf, 0, buf.length)) != -1) {
                ++rounds;
                totalReads += read;
                raf.write(buf, 0, read);
            }
        } catch(IOException ex) {
            if(ex.getMessage().equals("Unexpected end of ZLIB input stream")) {
                logger.info("[" + Thread.currentThread().getId() + "] We have EOFException: '" + ex.getMessage() + "', but ignore it for workaround");
            } else {
                ex.printStackTrace();
            }
        } finally {
            try {
                if(raf != null) {
                    raf.close();
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Written total: " + totalReads + " bytes for " + rounds + " rounds");

        return totalReads;
    }
}
