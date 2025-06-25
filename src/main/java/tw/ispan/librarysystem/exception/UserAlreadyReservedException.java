package tw.ispan.librarysystem.exception;

public class UserAlreadyReservedException extends RuntimeException {
    public UserAlreadyReservedException(String message) {
        super(message);
    }
}
