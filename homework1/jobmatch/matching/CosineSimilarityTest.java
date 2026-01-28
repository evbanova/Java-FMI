package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosineSimilarityTest {

    private SimilarityStrategy similarity;

    @BeforeEach
    void setUp() {
        similarity = new CosineSimilarity();
    }

    @Test
    void testCalculateSimilarityThrowsWhenCandidateSkillsNull() {
        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> similarity.calculateSimilarity(null, jobSkills));
    }

    @Test
    void testCalculateSimilarityThrowsWhenJobSkillsNull() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> similarity.calculateSimilarity(candidateSkills, null));
    }

    @Test
    void testSimilarityBothEmptySetsIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();
        Set<Skill> jobSkills = new HashSet<>();

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityCandidateEmptyJobNonEmptyIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));
        jobSkills.add(new Skill("Python", 4));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityCandidateNonEmptyJobEmptyIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));
        candidateSkills.add(new Skill("Python", 4));

        Set<Skill> jobSkills = new HashSet<>();

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityDisjointSkillsIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Python", 4));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityIdenticalSkillVectorsIsOne() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));
        candidateSkills.add(new Skill("Python", 4));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));
        jobSkills.add(new Skill("Python", 4));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(1.0, result, 1e-9);
    }

    @Test
    void testSimilarityPartialOverlapMatchesExpectedValue() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 4));
        candidateSkills.add(new Skill("Python", 3));
        candidateSkills.add(new Skill("SQL", 2));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 5));
        jobSkills.add(new Skill("Python", 2));
        jobSkills.add(new Skill("JavaScript", 3));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        double expected = 26.0 / (Math.sqrt(29.0) * Math.sqrt(38.0));
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testSimilaritySensitiveToLevels() {
        Set<Skill> lowLevelCandidate = new HashSet<>();
        lowLevelCandidate.add(new Skill("Java", 1));
        lowLevelCandidate.add(new Skill("Python", 1));

        Set<Skill> highLevelCandidate = new HashSet<>();
        highLevelCandidate.add(new Skill("Java", 5));
        highLevelCandidate.add(new Skill("Python", 5));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 5));
        jobSkills.add(new Skill("Python", 5));

        double lowSimilarity = similarity.calculateSimilarity(lowLevelCandidate, jobSkills);
        double highSimilarity = similarity.calculateSimilarity(highLevelCandidate, jobSkills);

        assertTrue(highSimilarity > lowSimilarity);
    }

    @Test
    void testSimilarityIndependentOfOrder() {
        Set<Skill> candidateSkills1 = new HashSet<>();
        candidateSkills1.add(new Skill("Java", 3));
        candidateSkills1.add(new Skill("Python", 4));
        candidateSkills1.add(new Skill("SQL", 2));

        Set<Skill> candidateSkills2 = new HashSet<>();
        candidateSkills2.add(new Skill("SQL", 2));
        candidateSkills2.add(new Skill("Java", 3));
        candidateSkills2.add(new Skill("Python", 4));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 5));
        jobSkills.add(new Skill("Python", 2));
        jobSkills.add(new Skill("SQL", 1));

        double result1 = similarity.calculateSimilarity(candidateSkills1, jobSkills);
        double result2 = similarity.calculateSimilarity(candidateSkills2, jobSkills);

        assertEquals(result1, result2, 1e-9);
    }
}
