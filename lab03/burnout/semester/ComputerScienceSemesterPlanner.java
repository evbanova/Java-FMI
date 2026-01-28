package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner
{
    public static void sortByRating(UniversitySubject[] subjects) 
    {
        int n = subjects.length;
        for (int i = 0; i < n - 1; i++) 
        {
            for (int j = 0; j < n - i - 1; j++) 
            {
                if (subjects[j].rating() < subjects[j+1].rating()) 
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

        sortByRating(semesterPlan.subjects());

        for (UniversitySubject s: semesterPlan.subjects())
        {
            countSubjects++;
            currCredits += s.credits();
             if(currCredits >= semesterPlan.minimalAmountOfCredits())
                break;
        }

        UniversitySubject[] result = new UniversitySubject[countSubjects];
        for (int i=0; i<countSubjects; i++)
            result[i] = semesterPlan.subjects()[i];


        int filledCategories = 0;
        for (SubjectRequirement req : semesterPlan.subjectRequirements())
        {
            int categoryCount = 0;

            for (UniversitySubject s: result)  
            {
                if (s.category().equals(req.category()))
                {
                    categoryCount++;

                    if(categoryCount >= req.minAmountEnrolled())
                    {
                        filledCategories++;
                        break;
                    }
                }
            } 
        }    

        try 
        {
            if(filledCategories < semesterPlan.subjectRequirements().length || currCredits < semesterPlan.minimalAmountOfCredits())
                throw new CryToStudentsDepartmentException();  
        }
        catch (Exception e)
        {
            System.err.println("Error: Couldn't fill all the categories needed.");
        }   

        return result;
    }
}
