package com.queue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//显示器输出阻塞队列
public class OutputBlockQueue extends BaseQueue{
    ArrayList<Process> outputBlockQueue = new ArrayList<>();
    public void AddProcess(Process process) {
        outputBlockQueue.add(process);
        this.num = outputBlockQueue.indexOf(process);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.date = df.format(System.currentTimeMillis());
    }
}
