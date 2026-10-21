package usecase.exceptions;

public class InvalidMatriculesException extends RuntimeException {
    public InvalidMatriculesException(String msg){
        super(msg);
    }
}
