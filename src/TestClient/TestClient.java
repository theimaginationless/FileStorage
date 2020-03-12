package TestClient;
import API.BaseAPI;
import API.Codes.FileStorageException;

import java.util.Arrays;


public class TestClient {
    public static void main(String args[]) {
        try {
            BaseAPI baseApi = new BaseAPI(args[0]);
            baseApi.writeFile(args[1]);
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
    }
}
