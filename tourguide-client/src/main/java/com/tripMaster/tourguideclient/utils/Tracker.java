package com.tripMaster.tourguideclient.utils;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Tracker extends Thread {
    private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
    //    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);
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

//            final ExecutorService executorService = Executors.newFixedThreadPool(1600, r -> {
//                Thread t = new Thread(r);
//                t.setDaemon(true);
//                return t;
//            });
            log.debug("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();

//            CompletableFuture<VisitedLocation> completableFuture = new CompletableFuture<>();
//            users.forEach(user -> completableFuture.supplyAsync(() -> tourGuideService.trackUserLocation(user), executorService));
            //TODO enlever sys.out
            users.forEach(user -> {
                tourGuideClientService.trackUserLocation(user);
                System.out.println("visited location in service:" + Thread.currentThread().getName());

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
