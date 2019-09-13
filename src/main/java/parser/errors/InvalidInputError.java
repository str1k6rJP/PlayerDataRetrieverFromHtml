package parser.errors;

public class InvalidInputError extends RuntimeException {
    public InvalidInputError(String errMsg){
        super(errMsg);
    }

}
