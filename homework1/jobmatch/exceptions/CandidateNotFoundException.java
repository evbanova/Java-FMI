package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

public class CandidateNotFoundException extends java.lang.Exception 
{
    public CandidateNotFoundException() 
    {
        super(); 
    }
    
    public CandidateNotFoundException(String message) 
    {
        super(message); 
    }

    public CandidateNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
}
