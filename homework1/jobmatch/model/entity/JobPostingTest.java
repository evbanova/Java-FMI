package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobPostingTest {

    @Test
    void testConstructorShouldCreateJobPostingWhenArgumentsAreValid() {
        Set<Skill> requiredSkills = new HashSet<>();
        requiredSkills.add(new Skill("Java", 3));

        JobPosting posting = new JobPosting(
                "job-1",
                "Junior Java Developer",
                "employer@gmail.com",
                requiredSkills,
                Education.BACHELORS,
                1,
                2000.0
        );

        assertEquals("job-1", posting.getID());
        assertEquals("Junior Java Developer", posting.getTitle());
        assertEquals("employer@gmail.com", posting.getEmployerEmail());
        assertEquals(Education.BACHELORS, posting.getRequiredEducation());
        assertEquals(1, posting.getRequiredYearsOfExperience());
        assertEquals(2000.0, posting.getSalary());
        assertEquals(1, posting.getRequiredSkills().size());
    }

    @Test
    void testConstructorShouldCopyRequiredSkills() {
        Set<Skill> requiredSkills = new HashSet<>();
        requiredSkills.add(new Skill("Java", 3));

        JobPosting posting = new JobPosting(
                "job-1",
                "Junior Java Developer",
                "employer@gmail.com",
                requiredSkills,
                Education.BACHELORS,
                1,
                2000.0
        );

        requiredSkills.add(new Skill("Python", 2));
        assertEquals(1, posting.getRequiredSkills().size());

        assertThrows(UnsupportedOperationException.class,
                () -> posting.getRequiredSkills().add(new Skill("C++", 4)));
    }

    @Test
    void testConstructorShouldThrowWhenIdIsNull() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting(null, "Title", "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenIdIsBlank() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("   ", "Title", "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenTitleIsNull() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", null, "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenTitleIsBlank() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "   ", "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenEmployerEmailIsNull() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", null,
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenEmployerEmailIsBlank() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "   ",
                        requiredSkills, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenRequiredSkillsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "employer@gmail.com",
                        null, Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenRequiredSkillsIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "employer@gmail.com",
                        Set.of(), Education.BACHELORS, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenRequiredEducationIsNull() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "employer@gmail.com",
                        requiredSkills, null, 1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenRequiredYearsOfExperienceIsNegative() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, -1, 1000.0));
    }

    @Test
    void testConstructorShouldThrowWhenSalaryIsNegative() {
        Set<Skill> requiredSkills = Set.of(new Skill("Java", 3));

        assertThrows(IllegalArgumentException.class,
                () -> new JobPosting("job-1", "Title", "employer@gmail.com",
                        requiredSkills, Education.BACHELORS, 1, -1.0));
    }
}
