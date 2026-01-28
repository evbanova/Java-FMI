package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

// Аргументи: min, max - продължителност в минути.
// Проверява дали min <= duration <= max.
// Ако min > max, min < 0 или max < 0, хвърли IllegalArgumentException.
public class DurationWorkoutFilter implements WorkoutFilter{

    private int min;
    private int max;

    public DurationWorkoutFilter(int min, int max){
         if (min > max || min <0 || max < 0) {
            throw new IllegalArgumentException("invalid min or max duration of workout");
         }

         this.min = min;
         this.max = max;   
    }

    public boolean matches(Workout workout){

        return min <= workout.getDuration() && max >= workout.getDuration();
    }
    
    public int getMin(){
        return min;
    }

    public int getMax(){
        return max;
    }
}
