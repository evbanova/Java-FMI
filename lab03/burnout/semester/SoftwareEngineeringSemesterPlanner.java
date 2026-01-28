package bg.sofia.uni.fmi.mjt.burnout.semester;

import java.util.Arrays;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner
{
    public static void sortByCredits(UniversitySubject[] subjects) 
    {
        int n = subjects.length;
        for (int i = 0; i < n - 1; i++) 
        {
            for (int j = 0; j < n - i - 1; j++) 
            {
                if (subjects[j].credits() < subjects[j+1].credits()) 
                {
                    UniversitySubject temp = subjects[j];
                    subjects[j] = subjects[j+1];
                    subjects[j+1] = temp;
                }
            }
        }
    }

    /**
	 * Calculates the subject combination for this semester type based on the subjectRequirements.
	 *
	 * @param semesterPlan the current semester plan needed for the calculation
	 * @throws CryToStudentsDepartmentException when a student cannot cover his semester credits.
	 * @throws IllegalArgumentException if the semesterPlan is missing or is null
	 * @throws InvalidSubjectRequirementsException if the subjectRequirements contain duplicate categories
	 * @return the subject list that balances credits, study time, and requirements
	 */
    @Override
	public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException
    {
         if(semesterPlan == null)
            throw new IllegalArgumentException();

        if(containsDuplicateSubjectRequirement(semesterPlan))
            throw new InvalidSubjectRequirementsException();    

        int currCredits = 0; 
        int countSubjects = 0;
        boolean[] taken = new boolean[semesterPlan.subjects().length];
        Arrays.fill(taken, false);
        sortByCredits(semesterPlan.subjects());

        for (SubjectRequirement req : semesterPlan.subjectRequirements())
        {
            int categoryCount = 0;

            for (int i =0; i<semesterPlan.subjects().length; i++)
            {
                if (semesterPlan.subjects()[i].category().equals(req.category()))
                {
                    countSubjects++;
                    currCredits += semesterPlan.subjects()[i].credits();
                    categoryCount++;

                    taken[i]= true;

                    if(categoryCount >= req.minAmountEnrolled())
                        break;
                }
            }
        }

        if(currCredits < semesterPlan.minimalAmountOfCredits())
        {
            for (int i =0; i<semesterPlan.subjects().length; i++)
            {
                if(!taken[i])
                {
                    taken[i]=true;
                    countSubjects++;
                    currCredits +=semesterPlan.subjects()[i].credits();

                    if(currCredits >= semesterPlan.minimalAmountOfCredits())
                        break;
                }
            }
        }
            
        UniversitySubject[] result = new UniversitySubject[countSubjects];
        int j =0;
        for (int i =0; i< taken.length; i++)
        {
            if (taken[i])
            {
                result[j] = semesterPlan.subjects()[i];
                j++;
            }
        }

        try 
        {
            if(currCredits < semesterPlan.minimalAmountOfCredits())
                throw new CryToStudentsDepartmentException();  
        }
        catch (Exception e)
        {
            System.err.println("Error: Couldn't get all the credits needed.");
        }   

        return result;
    }
}
