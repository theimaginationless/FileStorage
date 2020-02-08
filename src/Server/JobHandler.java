package Server;


import java.io.InputStream;
import java.util.concurrent.*;

public class JobHandler implements Callable<Long> {
    private InputStream mIs;
    private Job mJob;

    public JobHandler(InputStream is) {
        mIs = is;
    }

    @Override
    public Long call() {
        try {
            System.out.println("Waiting for ExecutorService end...");
            mJob = new Job(mIs);
            Long result = mJob.start();
            System.out.println("ExecutorService finished! " + result + " write!");
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
