package bg.sofia.uni.fmi.mjt.jobmatch.api;

import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.List;

public interface JobMatchAPI {

    Candidate registerCandidate(Candidate candidate);

    Employer registerEmployer(Employer employer);

    JobPosting postJobPosting(JobPosting jobPosting);

    List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit, SimilarityStrategy strategy);

    List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail, int limit, SimilarityStrategy strategy);

    List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit, SimilarityStrategy strategy);

    List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail, int limit);

    PlatformStatistics getPlatformStatistics();

}