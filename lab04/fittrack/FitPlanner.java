package bg.sofia.uni.fmi.mjt.fittrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.comparator.ComparatorByCalories;
import bg.sofia.uni.fmi.mjt.fittrack.workout.comparator.ComparatorByCaloriesAndDifficulty;
import bg.sofia.uni.fmi.mjt.fittrack.workout.comparator.ComparatorByDifficulty;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;
import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;


public class FitPlanner implements FitPlannerAPI{

    private List<Workout> availableWorkouts;

    public FitPlanner(Collection<Workout> availableWorkouts){
        if (availableWorkouts == null) {
            throw new IllegalArgumentException("invalid available workouts : collection is null");
        }

        this.availableWorkouts = List.copyOf(availableWorkouts);    
    }

    /**
     * Returns a list of workouts that match all provided filters.
     *
     * @param filters a list of filters to be applied.
     * @return a list of workouts that satisfy all filters.
     * @throws IllegalArgumentException if filters is null.
     */
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters){
        if (filters == null) {
            throw new IllegalArgumentException("invalid given list of filters : null");
        }

        if (availableWorkouts.isEmpty()) {
            return List.of();
        }

        List<Workout> result = new ArrayList<>();
        
        for (Workout w : availableWorkouts) {
            boolean allFilters = true;

            for (WorkoutFilter filter : filters) {
                if (!filter.matches(w)){
                    allFilters = false;
                    break;
                }
            }

            if (allFilters){
                result.add(w);
            }
        }
        
        return result;
    }

    /**
     * Generates an optimal weekly workout plan that maximizes burned calories
     * while fitting within the specified total time limit.
     *
     * @param totalMinutes total available time (in minutes) for workouts during the week
     * @return a list of optimally selected workouts, sorted by calories, then by difficulty, in descending order.
     *         Returns an empty list if totalMinutes is 0.
     * @throws OptimalPlanImpossibleException if a valid plan cannot be generated (e.g., all workouts exceed the time limit)
     * @throws IllegalArgumentException       if totalMinutes is negative
     */
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) throws OptimalPlanImpossibleException{
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("invalid given total minutes : negative");
        }

        if (availableWorkouts.isEmpty() || totalMinutes == 0) {
            return List.of();
        }

        //сортираме по калории с наш компаратор и взимаме най-добрите 
        List<Workout> bestWorkouts = new ArrayList<>(availableWorkouts);
        bestWorkouts.sort(new ComparatorByCaloriesAndDifficulty());

        //дп алгоритъм за намиране на най-дорбо време в съотношение с калории
        int[] dp = new int[totalMinutes + 1]; 
        int[] trackWorkouts = new int[totalMinutes + 1]; 

        for (int i = 0; i < bestWorkouts.size(); i++) {
            Workout curr = bestWorkouts.get(i);
            int weight = curr.getDuration();
            int value = curr.getCaloriesBurned();
    
            for (int t = totalMinutes; t >= weight; t--) {
                if (dp[t - weight] + value > dp[t]) {
                    dp[t] = dp[t - weight] + value;
                    trackWorkouts[t] = i; 
                }
            }
        }
        
        List<Workout> optimalPlan = new ArrayList<>();
        int currentTime = totalMinutes;

        //от масива с тренировки, ще видим кои сме избрали спрямо горния алгоритъм, 
        //за да го добавим към решението
        while (currentTime > 0) {
            int workoutIndex = trackWorkouts[currentTime];
            
            if (workoutIndex != -1) { 
                optimalPlan.add(bestWorkouts.get(workoutIndex));
                
                currentTime -= bestWorkouts.get(workoutIndex).getDuration();
            } else {
                currentTime--; 
            }
        }

        if (optimalPlan.isEmpty()) {
            throw new OptimalPlanImpossibleException("all workouts exceed the time limit");   
        }

        return optimalPlan;
    }

    /**
     * Groups all available workouts by type.
     *
     * @return an unmodifiable Map where the key is WorkoutType and the value is a list of workouts of that type.
     */
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType(){
        if (availableWorkouts.isEmpty()) {
            return Map.of();
        }

        Map<WorkoutType, List<Workout>> result = new HashMap<>();

        result.put(WorkoutType.CARDIO, new ArrayList<>());
        result.put(WorkoutType.STRENGTH, new ArrayList<>());
        result.put(WorkoutType.YOGA, new ArrayList<>());

        for (Workout w: availableWorkouts) {
            switch (w.getType()) {
                case CARDIO -> result.get(WorkoutType.CARDIO).add(w);
                case STRENGTH -> result.get(WorkoutType.STRENGTH).add(w);
                case YOGA -> result.get(WorkoutType.YOGA).add(w);
            }
        }

        return Map.copyOf(result);
    }

    /**
     * Returns a list of all workouts, sorted by burned calories in descending order.
     *
     * @return an unmodifiable list of workouts sorted by calories in descending order.
     */
    public List<Workout> getWorkoutsSortedByCalories(){
        if(availableWorkouts.isEmpty()) {
            return List.of();
        }

        availableWorkouts.sort(new ComparatorByCalories());

        List<Workout> result = List.copyOf(availableWorkouts);    
        return result;
    }

    /**
     * Returns a list of all workouts, sorted by difficulty in ascending order.
     *
     * @return an unmodifiable list of workouts sorted by difficulty in ascending order.
     */
    public List<Workout> getWorkoutsSortedByDifficulty(){
        if(availableWorkouts.isEmpty()) {
            return List.of();
        }
        
        availableWorkouts.sort(new ComparatorByDifficulty());

        List<Workout> result = List.copyOf(availableWorkouts);    
        return result;    
    }

    /**
     * Returns an unmodifiable set of all available workouts.
     *
     * @return an unmodifiable Set containing all workouts.
     */
    public Set<Workout> getUnmodifiableWorkoutSet(){
        if (availableWorkouts.isEmpty()) {
            return Set.of();
        }

        return Set.copyOf(availableWorkouts);
    }
    
}
