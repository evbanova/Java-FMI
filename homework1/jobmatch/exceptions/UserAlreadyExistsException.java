package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

public class UserAlreadyExistsException extends java.lang.Exception 
{
    public UserAlreadyExistsException() 
    {
        super(); 
    }
    
    public UserAlreadyExistsException(String message) 
    {
        super(message); 
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
            super(message, cause);
        }
}