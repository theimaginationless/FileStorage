package Common;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class BufferedOutputStreamWrapper {
    private static Logger logger = Logger.getLogger(OutputStreamWrapper.class.getName());
    private BufferedOutputStream bos;
    private boolean compressed;
    private Deflater deflater;
    private DeflaterOutputStream deflaterOutputStream;

    public BufferedOutputStreamWrapper(BufferedOutputStream bos, boolean compressed) {
        this.bos = bos;
        this.compressed = compressed;
        if(this.compressed) {
            initInflaterBufferedOutputStream();
        }
    }

    private void initInflaterBufferedOutputStream() {
        deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflaterOutputStream = new DeflaterOutputStream(bos, deflater, Const.bufferSize);
    }

    public void write(@NotNull byte[] b) throws IOException {
        if(compressed) {
            deflaterOutputStream.write(b);
        } else {
            bos.write(b);
        }
    }

    private void closeDeflaterBufferedOutputStream() throws IOException {
        deflaterOutputStream.close();
    }

    public void close() throws IOException {
        if(isCompressed()) {
            this.closeDeflaterBufferedOutputStream();
        }
        bos.close();
    }

    public void flush() throws IOException {
        if(isCompressed()) {
            deflaterOutputStream.flush();
        }
        bos.flush();
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        if(compressed && !this.compressed) {
            initInflaterBufferedOutputStream();
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
