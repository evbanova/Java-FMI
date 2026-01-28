package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.JaccardSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Education;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JobMatchTest {

    private JobMatch jobMatch;
    private SimilarityStrategy cosineSimilarity;
    private SimilarityStrategy jaccardSimilarity;

    @BeforeEach
    public void setUp() {
        jobMatch = new JobMatch();
        cosineSimilarity = new CosineSimilarity();
        jaccardSimilarity = new JaccardSimilarity();
    }

    // ============================================================
    // registerCandidate
    // ============================================================

    @Test
    public void testRegisterCandidateThrowsOnNull() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class, () -> api.registerCandidate(null));
    }

    @Test
    public void testRegisterCandidateThrowsIfEmailAlreadyExists() {
        JobMatchAPI api = jobMatch;

        Set<Skill> skills1 = new HashSet<>();
        skills1.add(new Skill("Java", 3));
        Candidate c1 = new Candidate("Alice", "a@a.com", skills1, Education.BACHELORS, 3);
        api.registerCandidate(c1);

        Set<Skill> skills2 = new HashSet<>();
        skills2.add(new Skill("Java", 4));
        Candidate c2 = new Candidate("Bob", "a@a.com", skills2, Education.BACHELORS, 5);

        assertThrows(UserAlreadyExistsException.class, () -> api.registerCandidate(c2));
    }

    @Test
    public void testRegisterCandidateRegistersAndUpdatesMostCommonSkill() {
        // c1: Java
        Set<Skill> s1 = new HashSet<>();
        s1.add(new Skill("Java", 3));
        Candidate c1 = new Candidate("Alice", "a@a.com", s1, Education.BACHELORS, 3);

        // c2: Python
        Set<Skill> s2 = new HashSet<>();
        s2.add(new Skill("Python", 4));
        Candidate c2 = new Candidate("Bob", "b@b.com", s2, Education.BACHELORS, 3);

        // c3: Python
        Set<Skill> s3 = new HashSet<>();
        s3.add(new Skill("Python", 2));
        Candidate c3 = new Candidate("Carol", "c@c.com", s3, Education.BACHELORS, 3);

        jobMatch.registerCandidate(c1); // Java:1
        jobMatch.registerCandidate(c2); // Python:1
        jobMatch.registerCandidate(c3); // Python:2

        PlatformStatistics stats = jobMatch.getPlatformStatistics();
        assertEquals("Python", stats.mostCommonSkillName());
        assertEquals(3, stats.totalCandidates());
    }

    // ============================================================
    // registerEmployer
    // ============================================================

    @Test
    public void testRegisterEmployerThrowsOnNull() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class, () -> api.registerEmployer(null));
    }

    @Test
    public void testRegisterEmployerThrowsIfEmailAlreadyExists() {
        JobMatchAPI api = jobMatch;

        Employer e1 = new Employer("company", "company@gmail.com");
        api.registerEmployer(e1);

        Employer e2 = new Employer("Other", "company@gmail.com");
        assertThrows(UserAlreadyExistsException.class, () -> api.registerEmployer(e2));
    }

    @Test
    public void testRegisterEmployerRegistersSuccessfully() {
        JobMatchAPI api = jobMatch;

        Employer e = new Employer("company", "company@gmail.com");
        Employer returned = api.registerEmployer(e);

        assertSame(e, returned);

        PlatformStatistics stats = jobMatch.getPlatformStatistics();
        assertEquals(0, stats.totalCandidates());
        assertEquals(1, stats.totalEmployers());
    }

    // ============================================================
    // postJobPosting
    // ============================================================

    @Test
    public void testPostJobThrowsOnNull() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class, () -> api.postJobPosting(null));
    }

    @Test
    public void testPostJobThrowsIfEmployerNotFound() {
        JobMatchAPI api = jobMatch;

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));

        JobPosting j = new JobPosting("id1", "Dev", "company@gmail.com",
                jobSkills, Education.BACHELORS, 2, 1000.0);

        assertThrows(UserNotFoundException.class, () -> api.postJobPosting(j));
    }

    @Test
    public void testPostJobUpdatesHighestPaidJobTitleAndSalary() {
        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        Set<Skill> js1 = new HashSet<>();
        js1.add(new Skill("Java", 3));
        JobPosting j1 = new JobPosting("j1", "Backend Developer", e.email(),
                js1, Education.BACHELORS, 2, 2000.0);

        Set<Skill> js2 = new HashSet<>();
        js2.add(new Skill("Java", 3));
        JobPosting j2 = new JobPosting("j2", "Android Developer", e.email(),
                js2, Education.BACHELORS, 2, 1500.0);

        Set<Skill> js3 = new HashSet<>();
        js3.add(new Skill("Java", 3));
        JobPosting j3 = new JobPosting("j3", "Backend Engineer", e.email(),
                js3, Education.BACHELORS, 2, 2000.0);

        jobMatch.postJobPosting(j1);
        PlatformStatistics stats1 = jobMatch.getPlatformStatistics();
        assertEquals("Backend Developer", stats1.highestPaidJobTitle());
        assertEquals(1, stats1.totalJobPostings());

        jobMatch.postJobPosting(j2);
        PlatformStatistics stats2 = jobMatch.getPlatformStatistics();
        assertEquals("Backend Developer", stats2.highestPaidJobTitle());
        assertEquals(2, stats2.totalJobPostings());

        jobMatch.postJobPosting(j3);
        PlatformStatistics stats3 = jobMatch.getPlatformStatistics();
        assertEquals("Backend Developer", stats3.highestPaidJobTitle());
        assertEquals(3, stats3.totalJobPostings());
    }

    // ============================================================
    // findTopNCandidatesForJob
    // ============================================================

    @Test
    public void testFindTopNCandidatesForJobThrowsOnNullJobPostingId() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNCandidatesForJob(null, 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNCandidatesForJobThrowsOnBlankJobPostingId() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNCandidatesForJob("   ", 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNCandidatesForJobThrowsOnNonPositiveLimit() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNCandidatesForJob("id", 0, cosineSimilarity));
    }

    @Test
    public void testFindTopNCandidatesForJobThrowsOnNullStrategy() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNCandidatesForJob("id", 1, null));
    }

    @Test
    public void testFindTopNCandidatesForJobThrowsIfJobPostingNotFound() {
        JobMatchAPI api = jobMatch;
        assertThrows(JobPostingNotFoundException.class,
                () -> api.findTopNCandidatesForJob("unknown", 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNCandidatesForJobReturnsEmptyListIfNoCandidatesOrZeroSimilarity() {
        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));
        JobPosting j = new JobPosting("j1", "Dev", e.email(),
                jobSkills, Education.BACHELORS, 2, 1000.0);
        jobMatch.postJobPosting(j);

        JobMatchAPI api = jobMatch;

        List<CandidateJobMatch> result1 =
                api.findTopNCandidatesForJob("j1", 5, jaccardSimilarity);
        assertTrue(result1.isEmpty());

        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Python", 1));
        Candidate c = new Candidate("Zero", "z@z.com", candSkills,
                Education.BACHELORS, 1);
        jobMatch.registerCandidate(c);

        List<CandidateJobMatch> result2 =
                api.findTopNCandidatesForJob("j1", 5, jaccardSimilarity);
        assertTrue(result2.isEmpty());
    }

    @Test
    public void testFindTopNCandidatesForJobSortedBySimilarityThenNameAndLimited() {
        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 3));
        jobSkills.add(new Skill("Python", 2));
        JobPosting j = new JobPosting("j1", "Dev", e.email(),
                jobSkills, Education.BACHELORS, 2, 1000.0);
        jobMatch.postJobPosting(j);

        Set<Skill> c1Skills = new HashSet<>();
        c1Skills.add(new Skill("Java", 3));
        c1Skills.add(new Skill("Python", 2));
        Candidate c1 = new Candidate("Charlie", "c@c.com", c1Skills,
                Education.BACHELORS, 3);

        Set<Skill> c2Skills = new HashSet<>();
        c2Skills.add(new Skill("Java", 3));
        Candidate c2 = new Candidate("Alice", "a@a.com", c2Skills,
                Education.BACHELORS, 3);

        Set<Skill> c3Skills = new HashSet<>();
        c3Skills.add(new Skill("Java", 3));
        Candidate c3 = new Candidate("Bob", "b@b.com", c3Skills,
                Education.BACHELORS, 3);

        jobMatch.registerCandidate(c1);
        jobMatch.registerCandidate(c2);
        jobMatch.registerCandidate(c3);

        JobMatchAPI api = jobMatch;
        List<CandidateJobMatch> all =
                api.findTopNCandidatesForJob("j1", 10, jaccardSimilarity);

        assertEquals(3, all.size());
        assertEquals("Charlie", all.get(0).getCandidate().getName());
        assertEquals("Alice", all.get(1).getCandidate().getName());
        assertEquals("Bob", all.get(2).getCandidate().getName());

        List<CandidateJobMatch> limited =
                api.findTopNCandidatesForJob("j1", 2, jaccardSimilarity);
        assertEquals(2, limited.size());
        assertEquals("Charlie", limited.get(0).getCandidate().getName());
        assertEquals("Alice", limited.get(1).getCandidate().getName());

        assertThrows(UnsupportedOperationException.class, () -> limited.add(all.get(2)));
    }

    // ============================================================
    // findTopNJobsForCandidate
    // ============================================================

    @Test
    public void testFindTopNJobsForCandidateThrowsOnNullEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNJobsForCandidate(null, 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNJobsForCandidateThrowsOnBlankEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNJobsForCandidate("   ", 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNJobsForCandidateThrowsOnNonPositiveLimit() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNJobsForCandidate("a@a.com", 0, cosineSimilarity));
    }

    @Test
    public void testFindTopNJobsForCandidateThrowsOnNullStrategy() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findTopNJobsForCandidate("a@a.com", 1, null));
    }

    @Test
    public void testFindTopNJobsForCandidateThrowsIfCandidateNotFound() {
        JobMatchAPI api = jobMatch;
        assertThrows(CandidateNotFoundException.class,
                () -> api.findTopNJobsForCandidate("unknown@a.com", 1, cosineSimilarity));
    }

    @Test
    public void testFindTopNJobsForCandidateEmptyWhenNoJobsOrZeroSimilarity() {
        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Java", 1));
        Candidate c = new Candidate("Alice", "a@a.com", candSkills,
                Education.BACHELORS, 1);
        jobMatch.registerCandidate(c);

        JobMatchAPI api = jobMatch;
        List<CandidateJobMatch> result1 =
                api.findTopNJobsForCandidate("a@a.com", 5, jaccardSimilarity);
        assertTrue(result1.isEmpty());

        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Python", 3));
        JobPosting j = new JobPosting("j1", "Dev", e.email(),
                jobSkills, Education.BACHELORS, 2, 1000.0);
        jobMatch.postJobPosting(j);

        List<CandidateJobMatch> result2 =
                api.findTopNJobsForCandidate("a@a.com", 5, jaccardSimilarity);
        assertTrue(result2.isEmpty());
    }

    @Test
    public void testFindTopNJobsForCandidateSortedBySimilarityThenJobTitleAndLimited() {
        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Java", 3));
        candSkills.add(new Skill("Python", 2));
        Candidate c = new Candidate("Alice", "a@a.com", candSkills,
                Education.BACHELORS, 3);
        jobMatch.registerCandidate(c);

        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        Set<Skill> j1Skills = new HashSet<>();
        j1Skills.add(new Skill("Java", 3));
        j1Skills.add(new Skill("Python", 2));
        JobPosting j1 = new JobPosting("j1", "Z-Job", e.email(),
                j1Skills, Education.BACHELORS, 2, 1000.0);

        Set<Skill> j2Skills = new HashSet<>();
        j2Skills.add(new Skill("Java", 3));
        JobPosting j2 = new JobPosting("j2", "A-Job", e.email(),
                j2Skills, Education.BACHELORS, 2, 1000.0);

        Set<Skill> j3Skills = new HashSet<>();
        j3Skills.add(new Skill("Java", 3));
        JobPosting j3 = new JobPosting("j3", "B-Job", e.email(),
                j3Skills, Education.BACHELORS, 2, 1000.0);

        jobMatch.postJobPosting(j1);
        jobMatch.postJobPosting(j2);
        jobMatch.postJobPosting(j3);

        JobMatchAPI api = jobMatch;

        List<CandidateJobMatch> all =
                api.findTopNJobsForCandidate("a@a.com", 10, jaccardSimilarity);
        assertEquals(3, all.size());
        assertEquals("Z-Job", all.get(0).getJobPosting().getTitle());
        assertEquals("A-Job", all.get(1).getJobPosting().getTitle());
        assertEquals("B-Job", all.get(2).getJobPosting().getTitle());

        List<CandidateJobMatch> limited =
                api.findTopNJobsForCandidate("a@a.com", 2, jaccardSimilarity);
        assertEquals(2, limited.size());
        assertEquals("Z-Job", limited.get(0).getJobPosting().getTitle());
        assertEquals("A-Job", limited.get(1).getJobPosting().getTitle());

        assertThrows(UnsupportedOperationException.class, () -> limited.add(all.get(2)));
    }

    // ============================================================
    // findSimilarCandidates
    // ============================================================

    @Test
    public void testFindSimilarCandidatesThrowsOnNullEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findSimilarCandidates(null, 1, cosineSimilarity));
    }

    @Test
    public void testFindSimilarCandidatesThrowsOnBlankEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findSimilarCandidates("   ", 1, cosineSimilarity));
    }

    @Test
    public void testFindSimilarCandidatesThrowsOnNonPositiveLimit() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findSimilarCandidates("a@a.com", 0, cosineSimilarity));
    }

    @Test
    public void testFindSimilarCandidatesThrowsOnNullStrategy() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.findSimilarCandidates("a@a.com", 1, null));
    }

    @Test
    public void testFindSimilarCandidatesThrowsIfCandidateNotFound() {
        JobMatchAPI api = jobMatch;
        assertThrows(CandidateNotFoundException.class,
                () -> api.findSimilarCandidates("unknown@a.com", 1, cosineSimilarity));
    }

    @Test
    public void testFindSimilarCandidatesExcludesTargetAndFiltersZeroSimilarity() {
        // target: Java
        Set<Skill> targetSkills = new HashSet<>();
        targetSkills.add(new Skill("Java", 3));
        Candidate target = new Candidate("Target", "t@t.com", targetSkills,
                Education.BACHELORS, 3);

        // zero: Python
        Set<Skill> zeroSkills = new HashSet<>();
        zeroSkills.add(new Skill("Python", 1));
        Candidate zero = new Candidate("Zero", "z@z.com", zeroSkills,
                Education.BACHELORS, 1);

        // other: Java
        Set<Skill> otherSkills = new HashSet<>();
        otherSkills.add(new Skill("Java", 3));
        Candidate other = new Candidate("Other", "o@o.com", otherSkills,
                Education.BACHELORS, 2);

        jobMatch.registerCandidate(target);
        jobMatch.registerCandidate(zero);
        jobMatch.registerCandidate(other);

        JobMatchAPI api = jobMatch;

        List<CandidateSimilarityMatch> result =
                api.findSimilarCandidates("t@t.com", 10, jaccardSimilarity);

        assertEquals(1, result.size());
        assertEquals("Other", result.get(0).getSimilarCandidate().getName());
        assertThrows(UnsupportedOperationException.class, () -> result.clear());
    }

    @Test
    public void testFindSimilarCandidatesSortedBySimilarityThenNameAndLimited() {
        // target: Java+Python
        Set<Skill> targetSkills = new HashSet<>();
        targetSkills.add(new Skill("Java", 3));
        targetSkills.add(new Skill("Python", 2));
        Candidate target = new Candidate("Target", "t@t.com", targetSkills,
                Education.BACHELORS, 3);

        // c1: Java+Python -> similarity 1
        Set<Skill> c1Skills = new HashSet<>();
        c1Skills.add(new Skill("Java", 3));
        c1Skills.add(new Skill("Python", 2));
        Candidate c1 = new Candidate("Charlie", "c@c.com", c1Skills,
                Education.BACHELORS, 3);

        // c2, c3: само Java -> еднакъв score
        Set<Skill> c2Skills = new HashSet<>();
        c2Skills.add(new Skill("Java", 3));
        Candidate c2 = new Candidate("Alice", "a@a.com", c2Skills,
                Education.BACHELORS, 3);

        Set<Skill> c3Skills = new HashSet<>();
        c3Skills.add(new Skill("Java", 3));
        Candidate c3 = new Candidate("Bob", "b@b.com", c3Skills,
                Education.BACHELORS, 3);

        jobMatch.registerCandidate(target);
        jobMatch.registerCandidate(c1);
        jobMatch.registerCandidate(c2);
        jobMatch.registerCandidate(c3);

        JobMatchAPI api = jobMatch;

        List<CandidateSimilarityMatch> all =
                api.findSimilarCandidates("t@t.com", 10, jaccardSimilarity);

        assertEquals(3, all.size());
        assertEquals("Charlie", all.get(0).getSimilarCandidate().getName());
        assertEquals("Alice", all.get(1).getSimilarCandidate().getName());
        assertEquals("Bob", all.get(2).getSimilarCandidate().getName());

        List<CandidateSimilarityMatch> limited =
                api.findSimilarCandidates("t@t.com", 2, jaccardSimilarity);
        assertEquals(2, limited.size());
        assertEquals("Charlie", limited.get(0).getSimilarCandidate().getName());
        assertEquals("Alice", limited.get(1).getSimilarCandidate().getName());
    }

    // ============================================================
    // getSkillRecommendationsForCandidate
    // ============================================================

    @Test
    public void testGetSkillRecommendationsThrowsOnNullEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.getSkillRecommendationsForCandidate(null, 1));
    }

    @Test
    public void testGetSkillRecommendationsThrowsOnBlankEmail() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.getSkillRecommendationsForCandidate("   ", 1));
    }

    @Test
    public void testGetSkillRecommendationsThrowsOnNonPositiveLimit() {
        JobMatchAPI api = jobMatch;
        assertThrows(IllegalArgumentException.class,
                () -> api.getSkillRecommendationsForCandidate("a@a.com", 0));
    }

    @Test
    public void testGetSkillRecommendationsThrowsIfCandidateNotFound() {
        JobMatchAPI api = jobMatch;
        assertThrows(CandidateNotFoundException.class,
                () -> api.getSkillRecommendationsForCandidate("unknown@a.com", 1));
    }

    @Test
    public void testGetSkillRecommendationsEmptyWhenNoJobsOrNoMissingSkills() {
        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Java", 4));
        candSkills.add(new Skill("Python", 3));
        Candidate c = new Candidate("Alice", "a@a.com", candSkills,
                Education.BACHELORS, 3);
        jobMatch.registerCandidate(c);

        JobMatchAPI api = jobMatch;

        List<SkillRecommendation> rec1 =
                api.getSkillRecommendationsForCandidate("a@a.com", 5);
        assertTrue(rec1.isEmpty());

        Employer e = new Employer("ACME", "hr@acme.com");
        jobMatch.registerEmployer(e);

        Set<Skill> jobSkills = new HashSet<>();
        jobSkills.add(new Skill("Java", 4));
        jobSkills.add(new Skill("Python", 3));
        JobPosting j = new JobPosting("j1", "Dev", e.email(),
                jobSkills, Education.BACHELORS, 2, 2000.0);
        jobMatch.postJobPosting(j);

        List<SkillRecommendation> rec2 =
                api.getSkillRecommendationsForCandidate("a@a.com", 5);
        assertTrue(rec2.isEmpty());
    }

    @Test
    public void testGetSkillRecommendationsSortedByImprovementDescendingAndLimited() {
        // candidate: Java
        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Java", 4));
        Candidate c = new Candidate("Alice", "a@a.com", candSkills,
                Education.BACHELORS, 3);
        jobMatch.registerCandidate(c);

        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        // j1: Java + AWS
        Set<Skill> j1Skills = new HashSet<>();
        j1Skills.add(new Skill("Java", 4));
        j1Skills.add(new Skill("AWS", 3));
        JobPosting j1 = new JobPosting("j1", "Job1", e.email(),
                j1Skills, Education.BACHELORS, 2, 2000.0);

        // j2: Java + AWS + Docker
        Set<Skill> j2Skills = new HashSet<>();
        j2Skills.add(new Skill("Java", 4));
        j2Skills.add(new Skill("AWS", 3));
        j2Skills.add(new Skill("Docker", 3));
        JobPosting j2 = new JobPosting("j2", "Job2", e.email(),
                j2Skills, Education.BACHELORS, 2, 2000.0);

        jobMatch.postJobPosting(j1);
        jobMatch.postJobPosting(j2);

        JobMatchAPI api = jobMatch;

        List<SkillRecommendation> rec =
                api.getSkillRecommendationsForCandidate("a@a.com", 10);

        assertTrue(rec.size() >= 2);
        for (int i = 0; i < rec.size() - 1; i++) {
            assertTrue(rec.get(i).improvementScore() >= rec.get(i + 1).improvementScore());
        }

        List<SkillRecommendation> limited =
                api.getSkillRecommendationsForCandidate("a@a.com", 1);
        assertEquals(1, limited.size());

        assertThrows(UnsupportedOperationException.class, () -> limited.add(rec.get(0)));
    }

    @Test
    public void testGetSkillRecommendationsWithEqualImprovementSortedAlphabeticallyBySkillName() {
        // candidate: Core
        Set<Skill> candSkills = new HashSet<>();
        candSkills.add(new Skill("Core", 3));
        Candidate c = new Candidate("Alice", "a@a.com", candSkills,
                Education.BACHELORS, 3);
        jobMatch.registerCandidate(c);

        Employer e = new Employer("company", "company@gmail.com");
        jobMatch.registerEmployer(e);

        // j1: Core + AWS
        Set<Skill> j1Skills = new HashSet<>();
        j1Skills.add(new Skill("Core", 3));
        j1Skills.add(new Skill("AWS", 3));
        JobPosting j1 = new JobPosting("j1", "Job1", e.email(),
                j1Skills, Education.BACHELORS, 2, 2000.0);

        // j2: Core + Docker
        Set<Skill> j2Skills = new HashSet<>();
        j2Skills.add(new Skill("Core", 3));
        j2Skills.add(new Skill("Docker", 3));
        JobPosting j2 = new JobPosting("j2", "Job2", e.email(),
                j2Skills, Education.BACHELORS, 2, 2000.0);

        jobMatch.postJobPosting(j1);
        jobMatch.postJobPosting(j2);

        JobMatchAPI api = jobMatch;

        List<SkillRecommendation> rec =
                api.getSkillRecommendationsForCandidate("a@a.com", 10);

        assertEquals(2, rec.size());
        assertEquals("AWS", rec.get(0).skillName());
        assertEquals("Docker", rec.get(1).skillName());
    }

    // ============================================================
    // getPlatformStatistics
    // ============================================================

    @Test
    public void testPlatformStatisticsWhenEmpty() {
        JobMatchAPI api = jobMatch;

        PlatformStatistics stats = api.getPlatformStatistics();
        assertEquals(0, stats.totalCandidates());
        assertEquals(0, stats.totalEmployers());
        assertEquals(0, stats.totalJobPostings());
        assertNull(stats.mostCommonSkillName());
        assertNull(stats.highestPaidJobTitle());
    }

    @Test
    public void testPlatformStatisticsCountsAndFieldsAfterOperations() {
        // employers
        Employer e1 = new Employer("company", "company@gmail.com");
        Employer e2 = new Employer("company2", "company2@gmail.com");
        jobMatch.registerEmployer(e1);
        jobMatch.registerEmployer(e2);

        // candidates
        Set<Skill> c1Skills = new HashSet<>();
        c1Skills.add(new Skill("Java", 3));
        Candidate c1 = new Candidate("Alice", "a@a.com", c1Skills,
                Education.BACHELORS, 3);

        Set<Skill> c2Skills = new HashSet<>();
        c2Skills.add(new Skill("Java", 4));
        c2Skills.add(new Skill("Python", 2));
        Candidate c2 = new Candidate("Bob", "b@b.com", c2Skills,
                Education.BACHELORS, 3);

        Set<Skill> c3Skills = new HashSet<>();
        c3Skills.add(new Skill("Python", 5));
        Candidate c3 = new Candidate("Carol", "c@c.com", c3Skills,
                Education.BACHELORS, 3);

        jobMatch.registerCandidate(c1);
        jobMatch.registerCandidate(c2);
        jobMatch.registerCandidate(c3);

        PlatformStatistics statsBeforeJobs = jobMatch.getPlatformStatistics();
        assertEquals(3, statsBeforeJobs.totalCandidates());
        assertEquals(2, statsBeforeJobs.totalEmployers());
        assertEquals("Java", statsBeforeJobs.mostCommonSkillName());
        assertNull(statsBeforeJobs.highestPaidJobTitle());

        // jobs
        Set<Skill> j1Skills = new HashSet<>();
        j1Skills.add(new Skill("Java", 3));
        JobPosting j1 = new JobPosting("j1", "JobA", e1.email(),
                j1Skills, Education.BACHELORS, 2, 1500.0);

        Set<Skill> j2Skills = new HashSet<>();
        j2Skills.add(new Skill("Java", 3));
        JobPosting j2 = new JobPosting("j2", "JobB", e1.email(),
                j2Skills, Education.BACHELORS, 2, 1500.0);

        Set<Skill> j3Skills = new HashSet<>();
        j3Skills.add(new Skill("Python", 5));
        JobPosting j3 = new JobPosting("j3", "JobC", e2.email(),
                j3Skills, Education.BACHELORS, 2, 2000.0);

        jobMatch.postJobPosting(j1);
        jobMatch.postJobPosting(j2);
        jobMatch.postJobPosting(j3);

        PlatformStatistics stats = jobMatch.getPlatformStatistics();
        assertEquals(3, stats.totalCandidates());
        assertEquals(2, stats.totalEmployers());
        assertEquals(3, stats.totalJobPostings());
        assertEquals("Java", stats.mostCommonSkillName());
        assertEquals("JobC", stats.highestPaidJobTitle());
    }
}
