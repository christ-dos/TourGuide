package tourGuide.exception;

public class VisitedLocationNotFoundException extends RuntimeException {
    public VisitedLocationNotFoundException(String message) {
        super(message);
    }
}
