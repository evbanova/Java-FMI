package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Education;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CandidateJobMatchTest {

    private Candidate candidate;
    private JobPosting jobPosting;

    @BeforeEach
    void setUp() {
        candidate = new Candidate(
                "candidate",
                "candidate@example.com",
                Set.of(new Skill("Java", 3)),
                Education.BACHELORS,
                2
        );

        jobPosting = new JobPosting(
                "job-1",
                "Junior Java Developer",
                "employer@gmail.com",
                Set.of(new Skill("Java", 3)),
                Education.BACHELORS,
                1,
                2000.0
        );
    }

    @Test
    void testConstructorCreatesCandidateJobMatchWhenArgumentsAreValid() {
        CandidateJobMatch match = new CandidateJobMatch(candidate, jobPosting, 0.8);

        assertSame(candidate, match.getCandidate());
        assertSame(jobPosting, match.getJobPosting());
        assertEquals(0.8, match.getSimilarityScore(), 1e-6);
    }

    @Test
    void testCandidateJobMatchImplementsMatch() {
        CandidateJobMatch match = new CandidateJobMatch(candidate, jobPosting, 0.5);

        assertEquals(0.5, match.getSimilarityScore(), 1e-6);
    }

    @Test
    void testConstructorThrowsWhenCandidateIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateJobMatch(null, jobPosting, 0.5));
    }

    @Test
    void testConstructorThrowsWhenJobPostingIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateJobMatch(candidate, null, 0.5));
    }

    @Test
    void testConstructorThrowsWhenSimilarityScoreIsBelowLowerBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateJobMatch(candidate, jobPosting, -0.01));
    }

    @Test
    void testConstructorThrowsWhenSimilarityScoreIsAboveUpperBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateJobMatch(candidate, jobPosting, 1.01));
    }

    @Test
    void testConstructorAllowsBoundarySimilarityScores() {
        CandidateJobMatch zero = new CandidateJobMatch(candidate, jobPosting, 0.0);
        CandidateJobMatch one = new CandidateJobMatch(candidate, jobPosting, 1.0);

        assertEquals(0.0, zero.getSimilarityScore(), 1e-6);
        assertEquals(1.0, one.getSimilarityScore(), 1e-6);
    }
}
