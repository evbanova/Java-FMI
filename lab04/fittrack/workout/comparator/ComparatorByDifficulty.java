package bg.sofia.uni.fmi.mjt.fittrack.workout.comparator;

import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class ComparatorByDifficulty implements Comparator<Workout>{

        public int compare(Workout w1, Workout w2){
            return w2.getDifficulty() - w1.getDifficulty();
        }
}