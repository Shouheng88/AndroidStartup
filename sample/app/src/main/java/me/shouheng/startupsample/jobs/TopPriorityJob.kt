package me.shouheng.startupsample.jobs

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.stability.L

@StartupJob class TopPriorityJob : ISchedulerJob{

    override fun priority(): Int = 100

    override fun run(context: Context) {
        L.d("Top level job done!")
    }
}