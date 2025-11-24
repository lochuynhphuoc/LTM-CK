package com.ltm.listener;

import com.ltm.worker.WorkerThread;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private WorkerThread workerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        workerThread = new WorkerThread(sce.getServletContext());
        workerThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (workerThread != null) {
            workerThread.stopWorker();
        }
    }
}
