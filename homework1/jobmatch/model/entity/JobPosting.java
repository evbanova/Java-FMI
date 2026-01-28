package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.HashSet;
import java.util.Set;

public class JobPosting{

    private String id;
    private String title;
    private String employerEmail;
    private Set<Skill> requiredSkills;
    private Education requiredEducation;
    private int requiredYearsOfExperience;
    private double salary;

    public JobPosting(String id, String title, String employerEmail, Set<Skill> requiredSkills, 
                      Education requiredEducation, int requiredYearsOfExperience, double salary){
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID argument for JobPosting cannot be null");
        }
        
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title argument for JobPosting cannot be null");
        }

        if (employerEmail == null || employerEmail.isBlank()) {
            throw new IllegalArgumentException("EmployerEmail argument for JobPosting cannot be null");
        }

        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new IllegalArgumentException("Required skills argument for JobPosting cannot be null");
        }

        if (requiredEducation == null) {
            throw new IllegalArgumentException("Required education argument for JobPosting cannot be null");
        }

        if (requiredYearsOfExperience < 0) {
            throw new IllegalArgumentException("Required yearsOfExperience argument for JobPosting cannot be less than 0");
        }

        if (salary < 0) {
            throw new IllegalArgumentException("Salary argument for JobPosting cannot be less than 0");
        }

        this.id = id;
        this.title = title;
        this.employerEmail = employerEmail;
        this.requiredSkills = new HashSet<>(requiredSkills);
        this.requiredEducation = requiredEducation;
        this.requiredYearsOfExperience = requiredYearsOfExperience;
        this.salary = salary;
    }

    public JobPosting(String id2, String title2, String employerEmail2, Set<Skill> emptySet, Object requiredEducation2,
            int requiredYearsOfExperience2, double salary2) {
        //TODO Auto-generated constructor stub
    }

    public String id(){
        return id;
    }

    public String title(){
        return title;
    }

    public String employerEmail(){
        return employerEmail;
    }

    public Set<Skill> requiredSkills(){
        return Set.copyOf(requiredSkills);
    }

    public Education requiredEducation(){
        return requiredEducation;
    }

    public int requiredYearsOfExperience(){
        return requiredYearsOfExperience;
    }
    
    public double salary(){
        return salary;
    }
}
