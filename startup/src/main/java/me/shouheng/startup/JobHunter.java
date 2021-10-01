package me.shouheng.startup;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import me.shouheng.scheduler.ISchedulerJob;

/** The job hunter. */
public interface JobHunter {

    /** Hunt jobs by annotations. */
    List<ISchedulerJob> hunt() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

}
