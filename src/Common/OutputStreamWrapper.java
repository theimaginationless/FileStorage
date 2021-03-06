package Common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class OutputStreamWrapper {
    private static Logger logger = Logger.getLogger(OutputStreamWrapper.class.getName());
    private OutputStream os;
    private boolean compressed;
    private Deflater deflater;
    private DeflaterOutputStream deflaterOutputStream;

    public OutputStreamWrapper(OutputStream os, boolean compressed) {
        this.os = os;
        this.compressed = compressed;
        if(this.compressed) {
            initInflaterOutputStream();
        }
    }

    private void initInflaterOutputStream() {
        deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflaterOutputStream = new DeflaterOutputStream(os, deflater, Const.bufferSize);
    }

    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        if(compressed) {
            deflaterOutputStream.write(b, off, len);
        } else {
            os.write(b, off, len);
        }
    }

    private void closeDeflaterOutputStream() throws IOException {
        deflaterOutputStream.close();
    }

    public void close() throws IOException {
        if(isCompressed()) {
            this.closeDeflaterOutputStream();
        }
        os.close();
    }

    public void flush() throws IOException {
        if(isCompressed()) {
            deflaterOutputStream.flush();
        }
        os.flush();
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        if(compressed && !this.compressed) {
            initInflaterOutputStream();
        } else if(!compressed && this.compressed) {
            try {
                this.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            deflater = null;
            deflaterOutputStream = null;
        }
        this.compressed = compressed;
        logger.info("[" + Thread.currentThread().getId() + "] Channel compression is " + (this.compressed ? "enabled" : "disabled"));
    }
}
