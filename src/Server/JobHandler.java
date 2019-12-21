package Server;


import java.io.InputStream;
import java.util.concurrent.*;

public class JobHandler implements Callable<Integer> {
    private InputStream mIs;
    private ExecutorService executorService;
    private ExecutorService mDataGetterExecutorService;

    public JobHandler(InputStream is) {
        mIs = is;
        executorService = Executors.newFixedThreadPool(4);
        mDataGetterExecutorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public Integer call() {
        try {
            Future<Integer> writeFileTask = executorService.submit(new Job(mIs));
            System.out.println("Waiting for ExecutorService end...");
            while (!writeFileTask.isDone()) ;
            System.out.println("ExecutorService finished! " + writeFileTask.get().intValue() + " write!");
            return writeFileTask.get().intValue();
        } catch(SecurityException e) {
            System.out.println(e.getMessage());
        } catch(Exception ex) {
                ex.printStackTrace();
        } finally {
            mDataGetterExecutorService.shutdown();
            executorService.shutdown();
            return 0;
        }
    }
}
