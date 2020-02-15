package TestClient;
import API.BaseAPI;
import API.Codes.FileStorageException;


public class TestClient {
    public static void main(String args[]) {
        try {
            BaseAPI baseApi = new BaseAPI(args[0]);
            baseApi.writeFile(args[1]);
        } catch(FileStorageException ex) {
            System.err.println("Error: " + ex.getErrorCode());
        }
    }
}
