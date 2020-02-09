package TestClient;
import API.BaseAPI;
import API.ServerConnection;
import Common.*;
import java.io.*;
import java.net.Socket;


public class TestClient {
    public static void main(String args[]) {
        BaseAPI baseApi = new BaseAPI(args[0]);
        baseApi.writeFile(args[1]);
        /*try {
            System.out.println("Initialize client-side; Writing data: '" + args[1] + "' to '" + args[0] + "'");
            FileInputStream fis = new FileInputStream(args[1]);
            Socket socket = new Socket(args[0], Const.port);
            OutputStream os = socket.getOutputStream();
            byte[] buf = new byte[Const.bufferSize];
            int read = 0;
            long readTotal = 0;
            long len = fis.available();
            while((read = fis.read(buf, 0, buf.length)) != -1) {
                readTotal += read;
                os.write(buf, 0, read);
                int perc = (int)((double)readTotal/len * 100);
                System.out.print("\r" +  perc + "% written");
            }

            System.out.println();
            os.flush();
            os.close();
            System.out.println("File write!");
        } catch(Throwable ex) {
            ex.printStackTrace();
        }*/
    }
}
