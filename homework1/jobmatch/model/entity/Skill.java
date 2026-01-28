package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

public record Skill (String name, int level){

    //level=0 = no expirinece
    //level=5 = expert
    public Skill (String name, int level){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name argument for Skill cannot be null");
        }

        if (level <0 || level >5) {
            throw new IllegalArgumentException("Level argument for Skill cannot be less than 0 or greater than 5");
        }

        this.name = name;
        this.level = level;
    }
}
