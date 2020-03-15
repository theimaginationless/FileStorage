package Common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

public class InputStreamWrapper {
    private InputStream is;

    public InputStreamWrapper(InputStream is) {
        this.is = is;
    }

    public int read(@NotNull byte[] b, int off, int len, boolean compressed) throws IOException {
        if(compressed) {
            InflaterInputStream inflaterInputStream = new InflaterInputStream(is);
            return inflaterInputStream.read(b, off, len);
        }
        return is.read(b, off, len);
    }

    public void close() throws IOException {
        is.close();
    }
}
