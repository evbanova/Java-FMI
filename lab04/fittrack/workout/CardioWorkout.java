package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;

public final class CardioWorkout implements Workout{
    
    private String name;
    private int duration;
    private int caloriesBurned;
    private int difficulty;
 
    // name не трябва да е null или празен/само whitespace
    // duration трябва да е положително число (> 0)
    // caloriesBurned трябва да е положително число (> 0)
    // difficulty трябва да е в интервала [1, 5] (1 – лесна, 5 – “няма да си усещаш краката”)
    public CardioWorkout(String name, int duration, int caloriesBurned, int difficulty){

        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new InvalidWorkoutException("invalid workout name");
        }        

        if (duration <= 0) {
            throw new InvalidWorkoutException("invalid workout duration");
        }
         
        if (caloriesBurned <= 0) {
            throw new InvalidWorkoutException("invalid workout caloriesBurned");
        }
            
        if (difficulty < 1 || difficulty > 5) {
            throw new InvalidWorkoutException("invalid workout difficulty");
        }

        this.name = name;    
        this.duration = duration; 
        this.caloriesBurned = caloriesBurned; 
        this.difficulty= difficulty; 
    }

    public String getName(){
        return name;
    }

    public int getDuration(){
        return duration;
    }

    public int getCaloriesBurned(){
        return caloriesBurned;
    }

    public int getDifficulty(){
        return difficulty;
    }

    public WorkoutType getType()
    {
        return WorkoutType.CARDIO;
    }
}
