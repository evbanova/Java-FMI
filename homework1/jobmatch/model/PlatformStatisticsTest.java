package bg.sofia.uni.fmi.mjt.jobmatch.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformStatisticsTest {

    @Test
    void testConstructorCreatesPlatformStatisticsWhenArgumentsAreValid() {
        PlatformStatistics stats = new PlatformStatistics(
                10,
                5,
                7,
                "Java",
                "Senior Java Developer"
        );

        assertEquals(10, stats.totalCandidates());
        assertEquals(5, stats.totalEmployers());
        assertEquals(7, stats.totalJobPostings());
        assertEquals("Java", stats.mostCommonSkillName());
        assertEquals("Senior Java Developer", stats.highestPaidJobTitle());
    }

    @Test
    void testConstructorThrowsWhenTotalCandidatesIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlatformStatistics(
                        -1,
                        5,
                        7,
                        "Java",
                        "Senior Java Developer"
                )
        );
    }

    @Test
    void testConstructorThrowsWhenTotalEmployersIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlatformStatistics(
                        10,
                        -1,
                        7,
                        "Java",
                        "Senior Java Developer"
                )
        );
    }

    @Test
    void testConstructorThrowsWhenTotalJobPostingsIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlatformStatistics(
                        10,
                        5,
                        -1,
                        "Java",
                        "Senior Java Developer"
                )
        );
    }


    @Test
    void testConstructorThrowsWhenMostCommonSkillNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlatformStatistics(
                        10,
                        5,
                        7,
                        "   ",
                        "Senior Java Developer"
                )
        );
    }

    @Test
    void testConstructorThrowsWhenHighestPaidJobTitleIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlatformStatistics(
                        10,
                        5,
                        7,
                        "Java",
                        "   "
                )
        );
    }
}
