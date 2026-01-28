package  bg.sofia.uni.fmi.mjt.jobmatch.model;

public record PlatformStatistics(int totalCandidates, int totalEmployers, int totalJobPostings, 
                                 String mostCommonSkillName, String highestPaidJobTitle){

    public PlatformStatistics(int totalCandidates, int totalEmployers, int totalJobPostings, 
                                 String mostCommonSkillName, String highestPaidJobTitle){
        if (totalCandidates < 0) {
            throw new IllegalArgumentException("Total candidates argument for PlatformStatistics cannot be less than 0");
        }

        if (totalEmployers < 0) {
            throw new IllegalArgumentException("Total employers argument for PlatformStatistics cannot be less than 0");
        }

        if (totalJobPostings < 0) {
            throw new IllegalArgumentException("Total JobPostings argument for PlatformStatistics cannot be less than 0");
        }

        if (mostCommonSkillName == null || mostCommonSkillName.isBlank()) {
            throw new IllegalArgumentException("MostCommonSkillName argument for PlatformStatistics cannot be null");
        }

        if (highestPaidJobTitle == null || highestPaidJobTitle.isBlank()) {
            throw new IllegalArgumentException("HighestPaidJobTitle argument for PlatformStatistics cannot be null");
        }

        this.totalCandidates = totalCandidates;
        this.totalEmployers = totalEmployers;
        this.totalJobPostings = totalJobPostings;
        this.mostCommonSkillName = mostCommonSkillName;
        this.highestPaidJobTitle = highestPaidJobTitle;
    }                                

}