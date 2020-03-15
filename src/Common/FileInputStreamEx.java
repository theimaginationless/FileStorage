package Common;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.DeflaterInputStream;

public class FileInputStreamEx extends java.io.FileInputStream {

    public FileInputStreamEx(@NotNull String name) throws FileNotFoundException {
        super(name);
    }

    public FileInputStreamEx(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public FileInputStreamEx(@NotNull FileDescriptor fdObj) {
        super(fdObj);
    }

    public int read(@NotNull byte[] b, int off, int len, boolean compressed) throws IOException {
        if(compressed) {
            DeflaterInputStream deflater = new DeflaterInputStream(this);
            deflater.read(b, off, len);
        }
        return super.read(b, off, len);
    }
}
