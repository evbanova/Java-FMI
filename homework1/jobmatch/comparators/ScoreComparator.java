package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

public class ScoreComparator implements Comparator<SkillRecommendation>{
    public int compare(SkillRecommendation e1, SkillRecommendation e2){

        return e1.improvementScore() == e2.improvementScore() ?
                (e1.skillName()).compareTo(e2.skillName()) :
                (int)(e2.improvementScore() - e1.improvementScore());
    }   
    
}
 
        