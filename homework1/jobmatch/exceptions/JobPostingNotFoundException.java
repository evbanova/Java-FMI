package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

public class JobPostingNotFoundException extends java.lang.Exception 
{
    public JobPostingNotFoundException() 
    {
        super(); 
    }
    
    public JobPostingNotFoundException(String message) 
    {
        super(message); 
    }

    public JobPostingNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
}