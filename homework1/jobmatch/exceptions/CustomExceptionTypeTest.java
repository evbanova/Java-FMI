package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomExceptionTypeTest {

    private boolean isUncheckedException(Class<? extends Throwable> exceptionClass) {
        return RuntimeException.class.isAssignableFrom(exceptionClass);
    }

    @Test
    void testCandidateNotFoundExceptionIsUnchecked() {
        assertTrue(isUncheckedException(CandidateNotFoundException.class));
    }

    @Test
    void testJobPostingNotFoundIsUnchecked() {
        assertTrue(isUncheckedException(JobPostingNotFoundException.class));
    }

    @Test
    void testUserAlreadyExistsIsUnchecked() {
        assertTrue(isUncheckedException(UserAlreadyExistsException.class));
    }

    @Test
    void testUserNotFoundExceptionIsUnchecked() {
        assertTrue(isUncheckedException(UserNotFoundException.class));
    }
}

