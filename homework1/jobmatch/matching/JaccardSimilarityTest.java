package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JaccardSimilarityTest {

    private SimilarityStrategy similarity;

    @BeforeEach
    void setUp() {
        similarity = new JaccardSimilarity();
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
        jobSkills.add(new Skill("Python", 2));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityCandidateNonEmptyJobEmptyIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));
        candidateSkills.add(new Skill("Python", 2));

        Set<Skill> jobSkills = new HashSet<>();

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityDisjointSkillSetsIsZero() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));
        candidateSkills.add(new Skill("Python", 2));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("C++", 4));
        jobSkills.add(new Skill("Go", 1));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void testSimilarityPartialOverlap() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 3));
        candidateSkills.add(new Skill("Python", 4));
        candidateSkills.add(new Skill("SQL", 2));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 5));
        jobSkills.add(new Skill("SQL", 3));
        jobSkills.add(new Skill("AWS", 1));

        // Intersection: {Java, SQL} -> 2
        // Union: {Java, Python, SQL, AWS} -> 4
        // J = 2 / 4 = 0.5
        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(0.5, result, 1e-9);
    }

    @Test
    void testSimilarityFullOverlapDifferentLevelsLevelsAreIgnored() {
        Set<Skill> candidateSkills = new HashSet<>();
        candidateSkills.add(new Skill("Java", 1));
        candidateSkills.add(new Skill("Python", 2));

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 5));
        jobSkills.add(new Skill("Python", 4));

        double result = similarity.calculateSimilarity(candidateSkills, jobSkills);

        assertEquals(1.0, result, 1e-9);
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
        jobSkills.add(new Skill("Python", 1));
        jobSkills.add(new Skill("SQL", 3));

        double result1 = similarity.calculateSimilarity(candidateSkills1, jobSkills);
        double result2 = similarity.calculateSimilarity(candidateSkills2, jobSkills);

        assertEquals(result1, result2, 1e-9);
    }
}