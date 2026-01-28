package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

public class CandidatesJobMatchComparator implements Comparator<CandidateJobMatch>{
    public int compare(CandidateJobMatch e1, CandidateJobMatch e2){

        if (e1.getSimilarityScore() == e2.getSimilarityScore()) {
            return (e1.getCandidate().name()).compareTo(e2.getCandidate().name());
        }    
        else {
            return (int)(e2.getSimilarityScore() - e1.getSimilarityScore());
        }
    }   
}
