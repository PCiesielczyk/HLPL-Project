package operationResolver;

import java.util.ArrayList;
import java.util.List;

public class OperationResolver implements Runnable{
    private List<Runnable> runnableList;
    Thread thread;

    public OperationResolver(){
        this.thread = new Thread(this);
        this.runnableList = new ArrayList<>();
    }

    @Override
    public void run() {
        Thread thread2 = new Thread();
        while (!thread.isInterrupted()){
            if (runnableList.size()>0 && !thread2.isAlive()){
                thread2 = new Thread(runnableList.get(0));
                thread2.start();
                runnableList.remove(0);
            }
        }
    }

    public void start(){
        thread.start();
    }

    public void addRunnable(Runnable runnable){
        runnableList.add(runnable);
    }
}
