package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

public record SkillRecommendation(String skillName, double improvementScore){

    //level=0 = no expirinece
    //level=5 = expert
    public SkillRecommendation(String skillName, double improvementScore){
        if (skillName == null  || skillName.isBlank()) {
            throw new IllegalArgumentException("Skill name argument for SkillRecommendation cannot be null");
        }

        if (improvementScore < 0) {
            throw new IllegalArgumentException("ImprovementScore argument for SkillRecommendation cannot be less than 5");
        }

        this.skillName = skillName;
        this.improvementScore = improvementScore;
    }
}