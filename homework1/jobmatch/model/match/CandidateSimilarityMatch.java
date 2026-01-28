package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;

public class CandidateSimilarityMatch{
    private Candidate targetCandidate;
    private Candidate similarCandidate;
    private double similarityScore;

    public CandidateSimilarityMatch(Candidate targetCandidate, Candidate similarCandidate, double similarityScore){
        if(targetCandidate == null){
            throw new IllegalArgumentException("Target candidate argument for CandidateSimilarityMatch cannot be null");
        }

        if(similarCandidate == null){
            throw new IllegalArgumentException("Similar candidate argument for CandidateSimilarityMatch cannot be null");
        }

        if(similarityScore < 0 || similarityScore > 1){
            throw new IllegalArgumentException("JobPosting argument for CandidateSimilarityMatch cannot be less than 0 or greater than 1");
        }

        this.targetCandidate = targetCandidate;
        this.similarCandidate = similarCandidate;
        this.similarityScore = similarityScore;
    }

    public Candidate getTargetCandidate() {
        return targetCandidate;
    }

    public Candidate getSimilarCandidate() {
        return similarCandidate;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }
}    