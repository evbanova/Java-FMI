package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.HashSet;
import java.util.Set;

public class Candidate{

    private String name;
    private String email;
    private Set<Skill> skills;
    private Education education;
    private int yearsOfExperience;

    public Candidate(String name, String email, Set<Skill> skills,
                     Education education, int yearsOfExperience){

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name argument for Candidate cannot be null");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email argument for Candidate cannot be null");
        }

        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skills argument for Candidate cannot be null");
        }

        if (education == null) {
            throw new IllegalArgumentException("Education argument for Candidate cannot be null");
        }

        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("YearsOfExperience argument for Candidate cannot be less than 0");
        }

        this.name = name;
        this.email = email;
        this.skills = new HashSet<>(skills);
        this.education = education;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String name(){
        return name;
    }

    public String email(){
        return email;
    }

    public Set<Skill> skills(){
        return Set.copyOf(skills);
    }

    public Education education(){
        return education;
    }

    public int yearsOfExperience(){
        return yearsOfExperience;
    }
}
