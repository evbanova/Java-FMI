package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import java.util.HashSet;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

public class JaccardSimilarity implements SimilarityStrategy{

    public JaccardSimilarity(){}

    private int intersection(Set<Skill> candidateSkills, Set<Skill> jobSkills){
       int countIntersectedSkills = 0;
       
        for (Skill skill : candidateSkills){
            if (jobSkills.contains(skill)){
                countIntersectedSkills++;
            }
        }

        return countIntersectedSkills;
    }

    private int union(Set<Skill> candidateSkills, Set<Skill> jobSkills){
        Set<Skill> result = new HashSet<>(candidateSkills);
        result.addAll(jobSkills);

        return result.size();
    }
    
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills){
        if (jobSkills.isEmpty() && candidateSkills.isEmpty()){
            return 0;
        }
        
        if(candidateSkills == null || candidateSkills.isEmpty()){
            throw new IllegalArgumentException("candidateSkills arguments for JaccardSimilarity cannot be null or empty");
        }

        if(jobSkills == null || jobSkills.isEmpty()){
            throw new IllegalArgumentException("jobSkills arguments for JaccardSimilarity cannot be null or empty");
        }

        return (double) intersection(candidateSkills, jobSkills) / union(candidateSkills, jobSkills);
    }
}