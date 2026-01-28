package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;

public class CandidateSimilarityMatchComparator implements Comparator<CandidateSimilarityMatch>{

    public int compare(CandidateSimilarityMatch e1, CandidateSimilarityMatch e2){

        if (e1.getSimilarityScore() == e2.getSimilarityScore()) {
            return (e1.getSimilarCandidate().name()).compareTo(e2.getSimilarCandidate().name());
        }    
        else {
            return (int)(e2.getSimilarityScore() - e1.getSimilarityScore());
        }
    }    
}