package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

public class CosineSimilarity implements SimilarityStrategy{
    public CosineSimilarity(){}

    private List<Skill> unionOfSets(Set<Skill> candidateSkills, Set<Skill> jobSkills){
        Set<Skill> result = new HashSet<>(candidateSkills);
        result.addAll(jobSkills);

        List<Skill> resultList = new ArrayList<>(result);
        resultList.sort(Comparator.comparing(Skill::name));

        return resultList;
    }

    private int[] makeVector(List<Skill> sortedSkills, Set<Skill> skills){
        int[] vector = new int[sortedSkills.size()];
        int index = 0;
        for (Skill skill : sortedSkills) {
            if(skills.contains(skill)) {
                vector[index] = skill.level();
            }

            else{
                vector[index] = 0;
            }
            index++;
        }

        return vector;
    }

    private int dotProduct(int[] v1, int[] v2){
        if(v1.length != v2.length){
            throw new IllegalArgumentException("The vectors should be the same size to make a dot product");
        }
        int sum = 0;

        for (int i=0; i<v1.length; i++){
            sum += (v1[i]*v2[i]);
        }

        return sum;
    }

    private double norm(int[] v1){
        double sum = 0.0;

        for (int i=0; i<v1.length; i++){
            sum += (v1[i]*v1[i]);
        }

        return Math.sqrt(sum);
    }

    private boolean checkIfVectorIsZero( int[] v1){
        int sum = 0;

        for(int i =0; i<v1.length; i++){
            sum += v1[i];
        }

        return sum == 0;
    }

    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills){
                if(candidateSkills == null || candidateSkills.isEmpty()){
            throw new IllegalArgumentException("candidateSkills arguments for CosineSimilarity cannot be null or empty");
        }

        if(jobSkills == null || jobSkills.isEmpty()){
            throw new IllegalArgumentException("jobSkills arguments for CosineSimilarity cannot be null or empty");
        }

        List<Skill> sortedSkills = unionOfSets(candidateSkills, jobSkills);

        int[] candidateVector = makeVector(sortedSkills, candidateSkills);
        int[] jobVector = makeVector(sortedSkills, jobSkills);

        if(checkIfVectorIsZero(candidateVector) && checkIfVectorIsZero(jobVector)){
            return 0;
        }

        return (double)dotProduct(candidateVector, jobVector) / (norm(candidateVector) * norm(jobVector));
    }
}