package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Education;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CandidateSimilarityMatchTest {

    private Candidate targetCandidate;
    private Candidate similarCandidate;

    @BeforeEach
    void setUp() {
        targetCandidate = new Candidate(
                "candidate",
                "candidate@example.com",
                Set.of(new Skill("Java", 3)),
                Education.BACHELORS,
                2
        );

        similarCandidate = new Candidate(
                "candidate",
                "candidate@example.com",
                Set.of(new Skill("Java", 3)),
                Education.BACHELORS,
                3
        );
    }

    @Test
    void testConstructorCreatesCandidateSimilarityMatchWhenArgumentsAreValid() {
        CandidateSimilarityMatch match = new CandidateSimilarityMatch(
                targetCandidate, similarCandidate, 0.7);

        assertSame(targetCandidate, match.getTargetCandidate());
        assertSame(similarCandidate, match.getSimilarCandidate());
        assertEquals(0.7, match.getSimilarityScore(), 1e-6);
    }

    @Test
    void testCandidateSimilarityMatchImplementsMatch() {
        CandidateSimilarityMatch match = new CandidateSimilarityMatch(targetCandidate, similarCandidate, 0.4);

        assertEquals(0.4, match.getSimilarityScore(), 1e-6);
    }

    @Test
    void testConstructorThrowsWhenTargetCandidateIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateSimilarityMatch(null, similarCandidate, 0.5));
    }

    @Test
    void testConstructorThrowsWhenSimilarCandidateIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateSimilarityMatch(targetCandidate, null, 0.5));
    }

    @Test
    void testConstructorThrowsWhenSimilarityScoreIsBelowLowerBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateSimilarityMatch(targetCandidate, similarCandidate, -0.01));
    }

    @Test
    void testConstructorThrowsWhenSimilarityScoreIsAboveUpperBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new CandidateSimilarityMatch(targetCandidate, similarCandidate, 1.01));
    }

    @Test
    void testConstructorAllowsBoundarySimilarityScores() {
        CandidateSimilarityMatch zero = new CandidateSimilarityMatch(targetCandidate, similarCandidate, 0.0);
        CandidateSimilarityMatch one = new CandidateSimilarityMatch(targetCandidate, similarCandidate, 1.0);

        assertEquals(0.0, zero.getSimilarityScore(), 1e-6);
        assertEquals(1.0, one.getSimilarityScore(), 1e-6);
    }
}
