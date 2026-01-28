package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

public record Employer(String companyName, String email) {

    public Employer(String companyName, String email){
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name argument for Employer cannot be null");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email argument for Employer cannot be null");
        }

        this.companyName = companyName;
        this.email = email;
    }
}
