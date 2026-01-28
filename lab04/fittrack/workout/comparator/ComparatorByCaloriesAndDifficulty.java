package bg.sofia.uni.fmi.mjt.fittrack.workout.comparator;

import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class ComparatorByCaloriesAndDifficulty implements Comparator<Workout>{

        public int compare(Workout w1, Workout w2){
            if (w2.getCaloriesBurned() - w1.getCaloriesBurned() != 0) {
                return w2.getCaloriesBurned() - w1.getCaloriesBurned();
            }
            
            return w2.getDifficulty() - w1.getDifficulty();
        }
}