package com.tripMaster.tourguideclient.utils;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class that permit tracking position of user to verify performances of application
 *
 * @author Christine Duarte
 */
@Slf4j
public class Tracker extends Thread {
    private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
    private final ExecutorService executorService = Executors.newFixedThreadPool(100000);
    private final TourGuideClientServiceImpl tourGuideClientService;
    private boolean stop = false;

    public Tracker(TourGuideClientServiceImpl tourGuideClientService) {
        this.tourGuideClientService = tourGuideClientService;

        executorService.submit(this);
    }

    /**
     * Assures to shut down the Tracker thread
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                log.debug("Tracker stopping");
                break;
            }
            List<User> users = tourGuideClientService.getAllUsers();

            log.debug("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();

            users.forEach(user -> {
                tourGuideClientService.trackUserLocation(user);
            });

            stopWatch.stop();
            log.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
            stopWatch.reset();

            try {
                log.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackingPollingInterval);
            } catch (InterruptedException e) {
                break;
            }
        }

    }
}
