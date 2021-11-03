package tourGuide.tracker;

import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tracker extends Thread {
    private final Logger logger = LoggerFactory.getLogger(Tracker.class);
    private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);
    private final TourGuideService tourGuideService;
    private boolean stop = false;

    public Tracker(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;

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
                logger.debug("Tracker stopping");
                break;
            }
            List<User> users = tourGuideService.getAllUsers();

//            final ExecutorService executorService = Executors.newFixedThreadPool(1600, r -> {
//                Thread t = new Thread(r);
//                t.setDaemon(true);
//                return t;
//            });
            logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();

//            CompletableFuture<VisitedLocation> completableFuture = new CompletableFuture<>();
//            users.forEach(user -> completableFuture.supplyAsync(() -> tourGuideService.trackUserLocation(user), executorService));
            //TODO enlever sys.out
            users.forEach(u -> {
                tourGuideService.trackUserLocation(u);
                System.out.println("visited location in service:" + Thread.currentThread().getName());

            });
            stopWatch.stop();
            logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
            stopWatch.reset();
            try {
                logger.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackingPollingInterval);
            } catch (InterruptedException e) {
                break;
            }

        }

    }

}
