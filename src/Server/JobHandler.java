package Server;


import java.net.Socket;
import java.util.concurrent.*;

public class JobHandler implements Callable<Long> {
    private Socket socket;
    private Job mJob;

    public JobHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public Long call() {
        try {
            mJob = new Job(this.socket);
            long result = mJob.start();
            return result;
        } catch(SecurityException e) {
            System.out.println(e.getMessage());
        } catch(Exception ex) {
                ex.printStackTrace();
        } finally {
            System.out.println();
            return 0L;
        }
    }
}
