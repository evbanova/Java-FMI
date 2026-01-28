package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

// Аргументи: min, max 
// Проверява дали min <= calories <= max.
// Ако min > max, min < 0 или max < 0, хвърли IllegalArgumentException.
public class CaloriesWorkoutFilter implements WorkoutFilter{

    private int min;
    private int max;

    public CaloriesWorkoutFilter(int min, int max){
         if (min > max || min <0 || max < 0) {
            throw new IllegalArgumentException("invalid  min or max calories of workout");
         }

         this.min = min;
         this.max = max;   
    }

    public boolean matches(Workout workout){

        return min <= workout.getCaloriesBurned() && max >= workout.getCaloriesBurned();
    }

    public int getMin(){
        return min;
    }

    public int getMax(){
        return max;
    }
}
