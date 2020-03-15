package Common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class OutputStreamWrapper {
    private OutputStream os;

    public OutputStreamWrapper(OutputStream os) {
        this.os = os;
    }

    public void write(@NotNull byte[] b, int off, int len, boolean compressed) throws IOException {
        if(compressed) {
            Deflater deflater = new Deflater(Deflater.BEST_SPEED);
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(os);
            deflaterOutputStream.write(b, off, len);
        }
        os.write(b, off, len);
    }

    public void close() throws IOException {
        os.close();
    }
}
