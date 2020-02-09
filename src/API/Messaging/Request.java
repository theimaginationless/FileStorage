package API;

import org.jetbrains.annotations.NotNull;

public class Request {
    private RequestCode requestCode;
    private byte[] hash;

    public Request(@NotNull byte[] hash, @NotNull RequestCode requestCode) {
        this.requestCode = requestCode;
        this.hash = hash;
    }
}
