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
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {
    private static Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
    private ExecutorService mJobExecutorService;
    private volatile boolean isRunning;
    private Thread mThread;
    private ServerSocket mServerSocket;
    private List<Future<Long>> taskList;

    ConnectionHandler() {
        try {
            mServerSocket = new ServerSocket(Const.port);
        } catch(IOException e) {
            logger.severe(e.getMessage());
            return;
        }


        mJobExecutorService = Executors.newFixedThreadPool(Const.connectionHandlerThreadPoolSize);
        taskList = new ArrayList<>();
        mThread = new Thread(this);
        mThread.start();
    }
    
    private void taskListJanitor() {
        taskList.removeIf(task -> task.isDone() || task.isCancelled());
    }

    public void stop() throws IOException {
        logger.info("[" + Thread.currentThread().getId() + "] Stopping service...");
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
        logger.info("Stop service successful!");
    }

    @Override
    public void run() {
        isRunning = true;
        logger.info("[" + Thread.currentThread().getId() + "] Running service...");
        while(isRunning) {
            try {
                logger.info("[" + Thread.currentThread().getId() + "] Service started! Waiting incoming connections.");
                Socket incomingConnection = mServerSocket.accept();
                JobHandler jobHandler = new JobHandler(incomingConnection);
                Future<Long> task = mJobExecutorService.submit(jobHandler);
                taskList.add(task);
                taskListJanitor();

            } catch(IOException e) {
                logger.severe("[" + Thread.currentThread().getId() + "] Oops? We have a some IO problem? " + e.getMessage());
            }
        }
        logger.info("[" + Thread.currentThread().getId() + "] Shutting down service...");
    }
}
