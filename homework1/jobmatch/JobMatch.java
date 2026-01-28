package bg.sofia.uni.fmi.mjt.jobmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
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
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidatesJobMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidateJobsMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidateSimilarityMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.ScoreComparator;

public class JobMatch implements JobMatchAPI{
    List<Candidate> candidates;
    List<Employer> employers;
    List<JobPosting> postings;

    public JobMatch(){
        candidates = new ArrayList<>();
        employers = new ArrayList<>();
        postings = new ArrayList<>();
    }
    
    public Candidate registerCandidate(Candidate candidate){
        if (candidate == null){
            throw new IllegalArgumentException("Candidate argument cannot be null");
        }

        for(Candidate can : candidates){
            try{
                if(can.email().equals(candidate.email())){
                    throw new UserAlreadyExistsException("Cannot register two users with the same email");
                }
            }   
            catch(UserAlreadyExistsException e){
                return null;
            }
        }

        candidates.add(candidate);
        return candidate;
    }

    public Employer registerEmployer(Employer employer){
        if (employer == null){
            throw new IllegalArgumentException("Employer argument cannot be null");
        }

        for(Employer empl : employers){
            try{
                if(empl.email().equals(employer.email())){
                    throw new UserAlreadyExistsException("Cannot register two users with the same email");
                }
            }
            catch(UserAlreadyExistsException e){
                return null;
            }
        }

        employers.add(employer);
        return employer;
    }

    public JobPosting postJobPosting(JobPosting jobPosting){
        if (jobPosting == null){
            throw new IllegalArgumentException("Job posting argument cannot be null");
        }

        boolean unregisteredEmployer = false;
        for(Employer empl : employers){
            if(empl.email().equals(jobPosting.employerEmail())){
                unregisteredEmployer = true;
                break;
            }
        }

        try{
            if(!unregisteredEmployer) {
                throw new UserNotFoundException("Not existing employer to this job posting");
            }
        }
        catch(UserNotFoundException e){
            return null;
        }

        postings.add(jobPosting);
        return jobPosting;
    }

    public List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit, SimilarityStrategy strategy){
        if(jobPostingId == null || jobPostingId.isBlank() || jobPostingId.isEmpty()){
            throw new IllegalArgumentException("jobPostingId cannot be null or empty");
        }

        if(limit <= 0){
            throw new IllegalArgumentException("Limit cannot be less than or equal to 0");
        }

        if(strategy == null){
            throw new IllegalArgumentException("Stragedy cannot be null");
        }

        boolean jobFound = false;
        Set<Skill> emptySet = new HashSet<>();
        emptySet.add(new Skill("", 0));
        JobPosting job = new JobPosting(jobPostingId, " ", " ", emptySet, Education.HIGH_SCHOOL, 0, 0.0);
        
        for(JobPosting post : postings){
            if(post.id().equals(jobPostingId)){
                jobFound = true;
                job = post;
                break;
            }
        }

        try{
            if(!jobFound){
                throw new JobPostingNotFoundException("Cannot find a user with that email");
            }
        } 
        catch(JobPostingNotFoundException e){
            return new ArrayList<>();
        } 

        List<CandidateJobMatch> result = new ArrayList<>();

        for(Candidate can : candidates){
            CandidateJobMatch match = new CandidateJobMatch(can, job, strategy.calculateSimilarity(can.skills(), job.requiredSkills()));
            result.add(match);    
        }

        result.sort(new CandidatesJobMatchComparator());

        if(result.size()<=limit){
            return List.copyOf(result);
        }
        else{
            return List.copyOf(result.subList(0, limit+1));
        }
    }

    public List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail, int limit, SimilarityStrategy strategy){
        if(candidateEmail == null || candidateEmail.isBlank() ){
            throw new IllegalArgumentException("candidateEmail cannot be null or empty");
        }

        if(limit <= 0){
            throw new IllegalArgumentException("Limit cannot be less than or equal to 0");
        }

        if(strategy == null){
            throw new IllegalArgumentException("Stragedy cannot be null");
        }

        boolean userFound = false;
        Set<Skill> emptySet = new HashSet<>();
        emptySet.add(new Skill("", 0));
        Candidate user = new Candidate("", candidateEmail, emptySet, Education.HIGH_SCHOOL, 0);
        
        for(Candidate can : candidates){
            if(can.email().equals(candidateEmail)){
                userFound = true;
                user = can;
                break;
            }
        }

        try{
            if(!userFound){
                throw new CandidateNotFoundException("Cannot find a user with that email");
            }
        } 
        catch(CandidateNotFoundException e){
            return new ArrayList<>();
        }  

        List<CandidateJobMatch> result = new ArrayList<>();

        for(JobPosting job : postings){
            CandidateJobMatch match = new CandidateJobMatch(user, job,
                strategy.calculateSimilarity(user.skills(), job.requiredSkills()));

            result.add(match);    
        }

        result.sort(new CandidateJobsMatchComparator());

        if(result.size()<=limit){
            return List.copyOf(result);
        }
        else{
            return List.copyOf(result.subList(0, limit+1));
        }
    }

    public List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit, SimilarityStrategy strategy){
        if(candidateEmail == null || candidateEmail.isBlank()){
            throw new IllegalArgumentException("Candidate emial cannot be null or empty");
        }

        if(limit <= 0){
            throw new IllegalArgumentException("Limit cannot be less than or equal to 0");
        }

        if(strategy == null){
            throw new IllegalArgumentException("Stragedy cannot be null");
        }

        boolean userFound = false;
        Set<Skill> emptySet = new HashSet<>();
        emptySet.add(new Skill("", 0));
        Candidate user = new Candidate("", candidateEmail, emptySet, Education.HIGH_SCHOOL, 0);
        
        for(Candidate can : candidates){
            if(can.email().equals(candidateEmail)){
                userFound = true;
                user = can;
                break;
            }
        }

        try{
            if(!userFound){
                throw new CandidateNotFoundException("Cannot find a user with that email");
            }
        } 
        catch(CandidateNotFoundException e){
            return new ArrayList<>();
        }  

        List<CandidateSimilarityMatch> result = new ArrayList<>();

        for(Candidate can : candidates){
            CandidateSimilarityMatch match = new CandidateSimilarityMatch(user, can, 
                strategy.calculateSimilarity(user.skills(), can.skills()));

            result.add(match);    
        }
        result.sort(new CandidateSimilarityMatchComparator());


        if(result.size()<=limit){
            return List.copyOf(result);
        }
        else{
            return List.copyOf(result.subList(0, limit+1));
        }
    }

    /**
     * Provides intelligent skill recommendations for a candidate to improve their job match scores.
     * <p>
     * This method analyzes ALL job postings in the system.
     * <p>
     * The algorithm works as follows:
     * <p>
     * 1. For each job posting, calculate current similarity score with the candidate
     * 2. For each skill the candidate is MISSING (present in job but not in candidate profile):
     * - Temporarily add that skill to candidate's profile with level equal to the max. required
     *   level across all job postings
     * - Recalculate similarity score
     * - Calculate improvement: new_score - old_score
     * 3. Aggregate (sum up) improvements across all job postings for each missing skill
     * 4. Return top N skills ranked by total improvement potential
     * <p>
     * Results are sorted by:
     * 1. Total improvement score in descending order (highest impact first)
     * 2. If improvement scores are equal, by skill name alphabetically (case-sensitive)
     * <p>
     * Example:
     * - Candidate has: {Java:4, Python:3}
     * - Job1 requires: {Java:5, Python:4, AWS:3} - similarity: 0.905
     * - Job2 requires: {Java:4, AWS:4, Docker:3} - similarity: 0.500
     * <p>
     * Missing skills analysis:
     * - Adding AWS:4 to candidate → Job1 similarity becomes 0.972 (improvement: 0.067)
     * - Adding AWS:4 to candidate → Job2 similarity becomes 0.780 (improvement: 0.280)
     * - Total AWS improvement: 0.347
     * <p>
     * - Adding Docker:3 to candidate → Job1 similarity becomes 0.776 (improvement: -0.129)
     * - Adding Docker:3 to candidate → Job2 similarity becomes 0.670 (improvement: 0.170)
     * - Total Docker improvement: 0.041
     * <p>
     * Result: [SkillRecommendation(AWS, 0.347), SkillRecommendation(Docker, 0.041)]
     * <p>
     * IMPLEMENTATION NOTE:
     * The platform's default similarity strategy is Cosine Similarity (considers skill levels).
     *
     * @param candidateEmail The email of the candidate
     * @param limit          The maximum number of skill recommendations to return
     * @return An unmodifiable list of SkillRecommendation objects, sorted as described above.
     * If there are no missing skills across all job postings, return an empty list.
     * If there are fewer than 'limit' missing skills, return all of them.
     * @throws IllegalArgumentException   if candidateEmail is null, empty or blank or limit is non-positive
     * @throws CandidateNotFoundException if no candidate with this email exists
     */
    public List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail, int limit){
        if (candidateEmail == null || candidateEmail.isBlank()) {
            throw new IllegalArgumentException("candidateEmail cannot be null or empty");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit cannot be less than or equal to 0");
        }

        CosineSimilarity strategy = new CosineSimilarity(); 
        boolean userFound = false;
        Set<Skill> emptySet = new HashSet<>();
        emptySet.add(new Skill("", 0));
        Candidate user = new Candidate("", candidateEmail, emptySet, Education.HIGH_SCHOOL, 0);
        
        for(Candidate can : candidates){
            if(can.email().equals(candidateEmail)){
                userFound = true;
                user = can;
                break;
            }
        }

        try{
            if(!userFound){
                throw new CandidateNotFoundException("Cannot find a user with that email");
            }
        } 
        catch(CandidateNotFoundException e){
            return new ArrayList<>();
        }  

        Map<String, Double> skillImprovementScores = new HashMap<>(); 
        Set<Skill> allMissingSkills = new HashSet<>();
    
        for (JobPosting job : postings) {
            for (Skill requiredSkill : job.requiredSkills()) {
                boolean candidateHasSkill = user.skills().stream()
                    .anyMatch(candidateSkill -> candidateSkill.name().equals(requiredSkill.name()));
                
                if (!candidateHasSkill) {
                    allMissingSkills.add(requiredSkill);
                }
            }
        }
    
        if (postings.isEmpty() || allMissingSkills.isEmpty()) {
            return List.of(); 
        }
    
        Map<String, Integer> maxLevelPerSkillName = new HashMap<>();

        for (Skill missingSkill : allMissingSkills) {
            String skillName = missingSkill.name();
            int maxLevel = 0;
            
            for (JobPosting job : postings) {
                for (Skill requiredSkill : job.requiredSkills()) {
                    if (requiredSkill.name().equals(skillName)) {
                        maxLevel = Math.max(maxLevel, requiredSkill.level());
                    }
                }
            }
            maxLevelPerSkillName.put(skillName, maxLevel);
        }
    
        for (String missingSkillName : maxLevelPerSkillName.keySet()) {
            double totalImprovement = 0.0;
            int maxLevel = maxLevelPerSkillName.get(missingSkillName);
            
            Skill tempSkill = new Skill(missingSkillName, maxLevel); 

            Set<Skill> candidateSkillsWithNewSkill = new HashSet<>(user.skills());
            candidateSkillsWithNewSkill.add(tempSkill);
            for (JobPosting job : postings) {
                double oldScore = strategy.calculateSimilarity(user.skills(), job.requiredSkills());

                double newScore = strategy.calculateSimilarity(candidateSkillsWithNewSkill, job.requiredSkills());

                totalImprovement += (newScore - oldScore);
            }

            skillImprovementScores.put(missingSkillName, totalImprovement);
        }

        List<SkillRecommendation> result = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : skillImprovementScores.entrySet()) {
            if (entry.getValue() > 0) { 
                result.add(new SkillRecommendation(entry.getKey(), entry.getValue()));
            }
        }

        result.sort(new ScoreComparator());        
        
        if (result.size() <= limit) {
            return List.copyOf(result);
        } 
        else {
            return List.copyOf(result.subList(0, limit +1));
        }
    }

    private String getMostCommonSkillName(){
        Map<Skill, Integer> skillsCount = new HashMap<>();
        int maxCount = 0;
        Skill max = new Skill("", 0);

        for(Candidate can : candidates){
            for(Skill skill : can.skills()) {
                if (skillsCount.containsKey(skill)){
                    int count = skillsCount.get(skill);
                    skillsCount.replace(skill, count +1);
                }
                else{
                    skillsCount.put(skill, 1);
                }
            }
        }

        for(Skill key : skillsCount.keySet()){
            if(skillsCount.get(key) > maxCount){
                maxCount = skillsCount.get(key);
                max = key;
            }
            else if (skillsCount.get(key) == maxCount){
                if(key.name().compareTo(max.name()) > 0){
                    max = key;
                }
            }
        }

        if(maxCount == 0){
            return null;
        }

        return max.name();
    }

    private String getHighestPaidJobTitle(){
        double maxSalary = 0.0;
        Set<Skill> emptySet = new HashSet<>();
        emptySet.add(new Skill("", 0));
        JobPosting max = new JobPosting("", "", "", emptySet, Education.HIGH_SCHOOL, 0, 0.0);

        for(JobPosting post : postings){
            if(maxSalary < post.salary()){
                maxSalary = post.salary();
                max = post;
            }
            else if( maxSalary == post.salary()){
                if(post.title().compareTo(max.title()) > 0){
                    max = post;
                }
            }
        }

        if(maxSalary == 0.0){
            return null;
        }

        return max.title();
    }

    public PlatformStatistics getPlatformStatistics(){        
        return new PlatformStatistics(candidates.size(), employers.size(), postings.size(), 
        getMostCommonSkillName(), getHighestPaidJobTitle());
    }
} 