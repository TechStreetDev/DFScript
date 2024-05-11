package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.event.TickEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.loader.Loadable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class Scheduler implements Loadable {

    private static final List<MutablePair<Integer,Runnable>> tasks = new ArrayList<>();

    @Override
    public void load() {
        EventManager.getInstance().register(TickEvent.class, (e) -> {
            for(MutablePair<Integer,Runnable> task : new ArrayList<>(tasks)) {
                task.setLeft(task.getLeft() - 1);
                if(task.getLeft() <= 0) {
                    task.getRight().run();
                    tasks.remove(task);
                }
            }
        });
    }

    public static void schedule(int ticks, Runnable runnable) {
        tasks.add(new MutablePair<>(ticks, runnable));
    }
}
