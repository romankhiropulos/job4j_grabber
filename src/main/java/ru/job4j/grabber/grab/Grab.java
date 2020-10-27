package ru.job4j.grabber.grab;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.grabber.parser.Parse;
import ru.job4j.grabber.storage.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
