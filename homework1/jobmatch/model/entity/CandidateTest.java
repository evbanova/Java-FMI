package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CandidateTest {

    @Test
    void testConstructorShouldCreateCandidateWhenArgumentsAreValid() {
        Set<Skill> skills = new HashSet<>();
        skills.add(new Skill("Java", 3));

        Candidate candidate = new Candidate(
                "candidate",
                "candidate@example.com",
                skills,
                Education.BACHELORS,
                2
        );

        assertEquals("candidate", candidate.getName());
        assertEquals("candidate@example.com", candidate.getEmail());
        assertEquals(Education.BACHELORS, candidate.getEducation());
        assertEquals(2, candidate.getYearsOfExperience());
        assertEquals(1, candidate.getSkills().size());
        assertTrue(candidate.getSkills().contains(new Skill("Java", 3)));
    }

    @Test
    void testConstructorShouldCopySkills() {
        Set<Skill> skills = new HashSet<>();
        skills.add(new Skill("Java", 3));

        Candidate candidate = new Candidate(
                "candidate",
                "candidate@example.com",
                skills,
                Education.BACHELORS,
                2
        );

        skills.add(new Skill("Python", 2));
        assertEquals(1, candidate.getSkills().size());
        assertThrows(UnsupportedOperationException.class,
                () -> candidate.getSkills().add(new Skill("C++", 4)));
    }

    @Test
    void testConstructorShouldThrowWhenNameIsNull() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate(null, "candidate@example.com", skills, Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenNameIsBlank() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("   ", "candidate@example.com", skills, Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenEmailIsNull() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", null, skills, Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenEmailIsBlank() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", "   ", skills, Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenSkillsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", "candidate@example.com", null, Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenSkillsIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", "candidate@example.com", Set.of(), Education.BACHELORS, 2));
    }

    @Test
    void testConstructorShouldThrowWhenEducationIsNull() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", "candidate@example.com", skills, null, 2));
    }

    @Test
    void testConstructorShouldThrowWhenYearsOfExperienceIsNegative() {
        Set<Skill> skills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new Candidate("candidate", "candidate@example.com", skills, Education.BACHELORS, -1));
    }
}
