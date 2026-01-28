package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;

public class CandidateJobMatch{
    
    private Candidate candidate;
    private JobPosting jobPosting;
    private double similarityScore;

    public CandidateJobMatch(Candidate candidate, JobPosting jobPosting, double similarityScore) {
        if(candidate == null){
            throw new IllegalArgumentException("Candidate argument for CandidateJobMatch cannot be null");
        }

        if(jobPosting == null){
            throw new IllegalArgumentException("JobPosting argument for CandidateJobMatch cannot be null");
        }

        if(similarityScore < 0 || similarityScore > 1){
            throw new IllegalArgumentException("JobPosting argument for CandidateJobMatch cannot be less than 0 or greater than 1");
        }

        this.candidate = candidate;
        this.jobPosting = jobPosting;
        this.similarityScore = similarityScore;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }
}    