package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

import java.util.Set;
import java.util.HashSet;

public abstract sealed class AbstractSemesterPlanner implements  SemesterPlannerAPI 
                    permits SoftwareEngineeringSemesterPlanner, ComputerScienceSemesterPlanner
{
    /**
	 * Calculates the subject combination for this semester type based on the subjectRequirements.
	 *
	 * @param semesterPlan the current semester plan needed for the calculation
	 * @throws CryToStudentsDepartmentException when a student cannot cover his semester credits.
	 * @throws IllegalArgumentException if the semesterPlan is missing or is null
	 * @throws InvalidSubjectRequirementsException if the subjectRequirements contain duplicate categories
	 * @return the subject list that balances credits, study time, and requirements
	 */
	public abstract UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException;

	/**
	 * Calculates the amount of jars grandma will send you
	 *
	 * @param subjects the subjects to calculate jar count for
	 * @param maximumSlackTime the rest days grandma gave as limit before stopping the jar food deliveries
	 * @param semesterDuration the duration of the semester in days
	 * @throws IllegalArgumentException if the subjects are missing or null, or maximumSlackTime/semesterDuration are not positive integers
	 * @throws DisappointmentException if you cannot make grandma happy.
	 *
	 * @return the number of jars grandma sends that are needed for survival
	 */
	public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration)
    {
        if (subjects == null || maximumSlackTime <=0 || semesterDuration <=0)
            throw new IllegalArgumentException();

        double sumSlackTime = 0;
        double sumStudyTime = 0;
        for(UniversitySubject s: subjects)
        {
            sumSlackTime += s.neededStudyTime() * s.category().getValue();
            sumStudyTime += s.neededStudyTime();
        }
        int jars = (int)(sumStudyTime/5);

        if((int)((sumSlackTime + sumStudyTime)) > semesterDuration)
            jars = jars*2;

        try 
        {
            if(sumSlackTime > maximumSlackTime)
                throw new DisappointmentException();  
        }
        catch (Exception e)
        {
            System.err.println("Error: Grandma is disappointed.");
        }    

        return jars;
    }   

    public boolean containsDuplicateSubjectRequirement(SemesterPlan semesterPlan){
        boolean dup = false;

        Set<SubjectRequirement> unique = new HashSet<>();

        for (SubjectRequirement req: semesterPlan.subjectRequirements()) {
            if(unique.contains(req)) {
                dup = true;
                break;
            }

            else
                unique.add(req);
        }

        return dup;
    }
}
