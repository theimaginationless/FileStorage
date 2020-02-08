package Server;

import Common.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler implements Runnable {

    private ExecutorService mJobExecutorService;
    private volatile boolean isRunning;
    private Thread mThread;
    private ServerSocket mServerSocket;
    private List<Future<Long>> taskList;

    ConnectionHandler() {
        try {
            mServerSocket = new ServerSocket(Const.port);
        } catch(IOException e) {
            System.err.println("Server.ConnectionHandler initialize error: " + e);
            return;
        }


        mJobExecutorService = Executors.newFixedThreadPool(16);
        taskList = new ArrayList<>();
        mThread = new Thread(this);
        mThread.start();
    }
    
    private void taskListJanitor() {
        for (Future<Long> task: taskList) {
            if(task.isDone() || task.isCancelled()) {
                taskList.remove(task);
            }
        }
    }

    public void stop() throws IOException {
        isRunning = false;
        mServerSocket.close();
        mJobExecutorService.shutdown();
        try {
            if(mJobExecutorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                mJobExecutorService.shutdownNow();
            }
        } catch(InterruptedException ex) {
            mJobExecutorService.shutdownNow();
        }
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Server.ConnectionHandler is running...");
        while(isRunning) {
            try {
                System.out.println("Ready for incoming to bar!");
                Socket incomingConnection = mServerSocket.accept();
                JobHandler jobHandler = new JobHandler(incomingConnection.getInputStream());
                Future<Long> task = mJobExecutorService.submit(jobHandler);
                taskList.add(task);
                taskListJanitor();

            } catch(IOException e) {
                System.out.println("IOError! Continue...");
            }
        }
        System.out.println("Shutting down Server.ConnectionHandler...");
    }
}
