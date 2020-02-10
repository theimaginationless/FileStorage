package API;

import Common.Const;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class ServerConnection {
    private String addr;
    private Socket socket;

    public ServerConnection(@NotNull String addr) {
        this.addr = addr;
    }

    public ServerConnection Connect() throws IOException {
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
