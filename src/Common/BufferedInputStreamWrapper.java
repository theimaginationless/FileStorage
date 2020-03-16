package Common;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class BufferedInputStreamWrapper {
    private static Logger logger = Logger.getLogger(InputStreamWrapper.class.getName());
    private BufferedInputStream bis;
    private boolean compressed;
    private InflaterInputStream inflaterInputStream;
    private Inflater inflater;

    public BufferedInputStreamWrapper(BufferedInputStream bis, boolean compressed) {
        this.bis = bis;
        this.compressed = compressed;
        if(isCompressed()) {
            initInflaterBufferedInputStream();
        }
        logger.info("[" + Thread.currentThread().getId() + "] Channel compression is " + (isCompressed() ? "enabled" : "disabled"));
    }

    private void initInflaterBufferedInputStream() {
        inflater = new Inflater();
        inflaterInputStream = new InflaterInputStream(bis, inflater, Const.bufferSize);
    }

    public int read(@NotNull byte[] b) throws IOException {
        if(isCompressed()) {
            return inflaterInputStream.read(b);
        }
        return bis.read(b);
    }

    public int read(@NotNull byte[] b, boolean compressed) throws IOException {
        if(compressed) {
            return inflaterInputStream.read(b);
        }
        return bis.read(b);
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        if(compressed && !this.compressed) {
            initInflaterBufferedInputStream();
        } else if(!compressed && this.compressed) {
            try {
                this.closeInflaterBufferedInputStream();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            inflaterInputStream = null;
        }
        this.compressed = compressed;
        logger.info("[" + Thread.currentThread().getId() + "] Channel compression is " + (this.compressed ? "enabled" : "disabled"));
        this.compressed = compressed;
    }

    private void closeInflaterBufferedInputStream() throws IOException {
        inflaterInputStream.close();
    }

    public void close() throws IOException {
        if(isCompressed()) {
            closeInflaterBufferedInputStream();
        }
        bis.close();
    }
}
