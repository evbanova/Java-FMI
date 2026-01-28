package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployerTest {

    @Test
    void testConstructorShouldCreateEmployerWhenArgumentsAreValid() {
        Employer employer = new Employer("company", "employer@gmail.com");

        assertEquals("company", employer.companyName());
        assertEquals("employer@gmail.com", employer.email());
    }

    @Test
    void testConstructorShouldThrowWhenCompanyNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Employer(null, "employer@gmail.com"));
    }

    @Test
    void testConstructorShouldThrowWhenCompanyNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Employer("   ", "employer@gmail.com"));
    }

    @Test
    void testConstructorShouldThrowWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Employer("company", null));
    }

    @Test
    void testConstructorShouldThrowWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Employer("company", "   "));
    }
}
