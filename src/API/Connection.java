package API;

import Common.Const;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class Connection {
    private String addr;
    private Socket socket;

    public Connection(@NotNull String addr) {
        this.addr = addr;
    }

    public Connection Connect() throws IOException {
        this.socket = new Socket(this.addr, Const.port);
        return this;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getAddr() {
        return this.addr;
    }
}
