package com.ltm.worker;

import com.ltm.model.Task;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    private static TaskQueue instance;
    private BlockingQueue<Task> queue;

    private TaskQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public static synchronized TaskQueue getInstance() {
        if (instance == null) {
            instance = new TaskQueue();
        }
        return instance;
    }

    public void addTask(Task task) {
        queue.offer(task);
    }

    public Task takeTask() throws InterruptedException {
        return queue.take();
    }
}
