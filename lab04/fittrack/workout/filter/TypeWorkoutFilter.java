package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;

// Филтрира по тип (CARDIO, STRENGTH, YOGA).
//Ако типът е null, хвърли IllegalArgumentException.
public class TypeWorkoutFilter implements WorkoutFilter{

    private WorkoutType type;

    public TypeWorkoutFilter(WorkoutType type){
         if (type == null) {
            throw new IllegalArgumentException("invalid workout type");
         }

         this.type = type;   
    }

    public boolean matches(Workout workout){

        return type == workout.getType();
    }

    public WorkoutType getWorkoutType(){
        return type;
    }
}
