package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

public class UserNotFoundException extends java.lang.Exception 
{
    public UserNotFoundException() 
    {
        super(); 
    }
    
    public UserNotFoundException(String message) 
    {
        super(message); 
    }

    public UserNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
}