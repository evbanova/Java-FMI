package bg.sofia.uni.fmi.mjt.fittrack.exception;

public class OptimalPlanImpossibleException extends java.lang.Exception{
    
    public OptimalPlanImpossibleException(){
        super();
    }
    
    public OptimalPlanImpossibleException(String message){
        super(message);
    }

    public OptimalPlanImpossibleException(String message, Throwable cause){
        super(message,cause);
    }
}
