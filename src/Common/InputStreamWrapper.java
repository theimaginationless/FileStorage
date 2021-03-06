package Common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class InputStreamWrapper {
    private static Logger logger = Logger.getLogger(InputStreamWrapper.class.getName());
    private InputStream is;
    private boolean compressed;
    private InflaterInputStream inflaterInputStream;
    private Inflater inflater;

    public InputStreamWrapper(InputStream is, boolean compressed) {
        this.is = is;
        this.compressed = compressed;
        if(isCompressed()) {
            initInflaterInputStream();
        }
        logger.info("[" + Thread.currentThread().getId() + "] Channel compression is " + (isCompressed() ? "enabled" : "disabled"));
    }

    private void initInflaterInputStream() {
        inflater = new Inflater();
        inflaterInputStream = new InflaterInputStream(is, inflater, Const.bufferSize);
    }

    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        if(isCompressed()) {
            return inflaterInputStream.read(b, off, len);
        }
        return is.read(b, off, len);
    }

    public int read(@NotNull byte[] b, boolean compressed) throws IOException {
        if(compressed) {
            return inflaterInputStream.read(b);
        }
        return is.read(b);
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        if(compressed && !this.compressed) {
            initInflaterInputStream();
        } else if(!compressed && this.compressed) {
            try {
                this.closeInflaterInputStream();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            inflaterInputStream = null;
        }
        this.compressed = compressed;
        logger.info("[" + Thread.currentThread().getId() + "] Channel compression is " + (this.compressed ? "enabled" : "disabled"));
        this.compressed = compressed;
    }

    private void closeInflaterInputStream() throws IOException {
        inflaterInputStream.close();
    }

    public void close() throws IOException {
        if(isCompressed()) {
            closeInflaterInputStream();
        }
        is.close();
    }
}
