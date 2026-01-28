package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

// Аргументи: keyword, caseSensitive.
// Сравнява за частично или пълно съвпадение на името на тренировката (substring matching).
// Ако keyword е null или празен стринг – хвърли IllegalArgumentException.
public class NameWorkoutFilter implements WorkoutFilter{
    
    private String keyword;
    private boolean caseSensitive;

    public NameWorkoutFilter(String keyword, boolean caseSensitive){
         if (keyword == null || keyword.equals("")) {
            throw new IllegalArgumentException("invalid keyword for workout name");
         }

         this.keyword = keyword;
         this.caseSensitive = caseSensitive;   
    }

    public boolean matches(Workout workout){

        if (caseSensitive) {
            return keyword.equals(workout.getName());
        }
        else {
            return keyword.equalsIgnoreCase(workout.getName());
        }
    }
    
    public String getKeyword(){
        return keyword;
    }

    public boolean getCaseSensitive(){
        return caseSensitive;
    }
}
