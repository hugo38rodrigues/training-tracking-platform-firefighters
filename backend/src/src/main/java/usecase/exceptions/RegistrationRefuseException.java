package usecase.exceptions;

public class RegistrationRefuseException extends RuntimeException{

    public RegistrationRefuseException(String message) {
        super(message);
    }
    
}
