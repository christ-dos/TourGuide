package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DAO.InternalUserMapDAO;
import tourGuide.exception.VisitedLocationNotFoundException;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TourGuideService {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;

    private InternalUserMapDAO internalUserMapDAO = new InternalUserMapDAO();

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            internalUserMapDAO.initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

//    public User getUser(String userName) {
//        return internalUserMap.get(userName);
//    }
//
//    public List<User> getAllUsers() {
//        return internalUserMap.values().stream().collect(Collectors.toList());
//    }
//
//    public void addUser(User user) {
//        if (!internalUserMap.containsKey(user.getUserName())) {
//            internalUserMap.put(user.getUserName(), user);
//        }
//    }
    public User getUser(String userName) {
    return internalUserMapDAO.getUser(userName);
    }

    public List<User> getAllUsers() {
        return internalUserMapDAO.getAllUsers();
    }

    public void addUser(User user) {
        internalUserMapDAO.addUser(user);
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(internalUserMapDAO.getTripPricerApiKey(), user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

//  public VisitedLocation trackUserLocation(User user) {
//		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
//		user.addToVisitedLocations(visitedLocation);
//		rewardsService.calculateRewards(user);
//		return visitedLocation;
//	}

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = null;
//        final ExecutorService executorService = Executors.newFixedThreadPool(1600, r -> {
//            Thread t = new Thread(r);
//            t.setDaemon(true);
//            return t;
//        });
        final ExecutorService executorService = Executors.newFixedThreadPool(1000);
        try {
            Locale.setDefault(new Locale("en", "US"));

            CompletableFuture<VisitedLocation> future = CompletableFuture.supplyAsync(() ->
                    gpsUtil.getUserLocation(user.getUserId()),executorService);
            visitedLocation = future.join();

//            System.out.println("visited location in service:" + Thread.currentThread().getName());
//            future.thenAccept(System.out::println);


            //TODO enlever sys.out et clean code
//            visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        } catch (NumberFormatException ex) {
            logger.debug("NumberFormatException:" + ex.getMessage());
        }
        if (visitedLocation == null) {
            throw new VisitedLocationNotFoundException("Service: Visited location not found");
        }
        user.addToVisitedLocations(visitedLocation);
//        CompletableFuture.runAsync(() ->
//               rewardsService.calculateRewards(user),executorService);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }else{
                distances.add(rewardsService.getDistance(attraction, visitedLocation.location));
            }
        }
        rewardsService.setAttractionProximityRange((int)getAverageDistanceByAttraction(distances));

        return nearbyAttractions;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    private double getAverageDistanceByAttraction(List<Double> distances){

       return distances.stream().mapToDouble(s->s)
                .average()
                .orElse(0D);
    }

//    /**********************************************************************************
//     *
//     * Methods Below: For Internal Testing
//     *
//     **********************************************************************************/
//    private static final String tripPricerApiKey = "test-server-api-key";
//    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
//    private final Map<String, User> internalUserMap = new HashMap<>();
//
//    private void initializeInternalUsers() {
//        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
//            String userName = "internalUser" + i;
//            String phone = "000";
//            String email = userName + "@tourGuide.com";
//            User user = new User(UUID.randomUUID(), userName, phone, email);
//            generateUserLocationHistory(user);
//
//            internalUserMap.put(userName, user);
//        });
//        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
//    }
//
//    private void generateUserLocationHistory(User user) {
//        IntStream.range(0, 3).forEach(i -> {
//            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
//        });
//    }
//
//    private double generateRandomLongitude() {
//        double leftLimit = -180;
//        double rightLimit = 180;
//        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
//    }
//
//    private double generateRandomLatitude() {
//        double leftLimit = -85.05112878D;
//        double rightLimit = 85.05112878D;
//        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
//    }
//
//    private Date getRandomTime() {
//        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
//        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
//    }

}
