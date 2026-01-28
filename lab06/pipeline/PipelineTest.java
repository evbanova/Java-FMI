package bg.sofia.uni.fmi.mjt.pipeline;

import bg.sofia.uni.fmi.mjt.pipeline.stage.Stage;
import bg.sofia.uni.fmi.mjt.pipeline.step.Step;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineTest {

    //Tests for null arguments 
    @Test
    void testPipelineStartThrowsOnNullInitialStage() {
        Stage<?, ?> initialStage = null;
        
        assertThrows(IllegalArgumentException.class, () -> Pipeline.start(initialStage),
                "Should throw IllegalArgumentException if initialStage is null");
    }

    @Test
    void testStageStartThrowsOnNullInitialStage() {
        Step<?, ?> initialStage = null;
        
        assertThrows(IllegalArgumentException.class, () -> Stage.start(initialStage),
                "Should throw IllegalArgumentException if initialStage is null");
    }

    @Test
    void testCacheValueThrowsOnNullKeyOrValue() {
        Cache cache = new Cache();
        Object key = null;
        Object value = null;
        
        assertThrows(IllegalArgumentException.class, () -> cache.cacheValue(key, value),
                "Should throw IllegalArgumentException if key or value are null");
    }

    @Test
    void testGetCachedValueThrowsOnNullKey() {
        Cache cache = new Cache();
        Object key = null;
        
        assertThrows(IllegalArgumentException.class, () -> cache.getCachedValue(key),
                "Should throw IllegalArgumentException if key are null");
    }

    @Test
    void testCacheContainsKeyThrowsOnNullKey() {
        Cache cache = new Cache();
        Object key = null;
        
        assertThrows(IllegalArgumentException.class, () -> cache.containsKey(key),
                "Should throw IllegalArgumentException if key are null");
    }
    
    @Test
    void testPipelineExecutesChainedStages() {
        Step<String, String> step1 = input -> input + " World";
        Step<String, Integer> step2 = String::length;

        Pipeline<String, Integer> pipeline = Pipeline.start(Stage.start(step1))
                .addStage(Stage.start(step2));

        Integer result = pipeline.execute("Hello");
        assertEquals(11, result, "Pipeline should process data through all stages");
    }

    @Test
    void testPipelineCachingAvoidsRecomputation() {
        AtomicInteger executionCounter = new AtomicInteger(0);

        Step<String, String> countingStep = input -> {
            executionCounter.incrementAndGet();
            return input.toUpperCase();
        };

        Pipeline<String, String> pipeline = Pipeline.start(Stage.start(countingStep));

        String res1 = pipeline.execute("test");
        String res2 = pipeline.execute("test");

        assertEquals("TEST", res1);
        assertEquals("TEST", res2);
        assertEquals(1, executionCounter.get(), "Pipeline should retrieve result from cache on second execution");
    }

    @Test
    void testStageChainsStepsCorrectly() {
        Step<Integer, Integer> doubleStep = x -> x * 2;
        Step<Integer, Integer> addStep = x -> x + 1;

        Stage<Integer, Integer> stage = Stage.start(doubleStep).addStep(addStep);
        Integer result = stage.execute(5); // (5 * 2) + 1 = 11

        assertEquals(11, result, "Stage should execute steps sequentially");
    }

    @Test
    void testCacheOperations() {
        Cache cache = new Cache();
        String key = "key";
        String value = "value";

        assertTrue(cache.isEmpty());

        cache.cacheValue(key, value);

        assertFalse(cache.isEmpty());
        assertTrue(cache.containsKey(key));
        assertEquals(value, cache.getCachedValue(key));

        cache.clear();
        assertTrue(cache.isEmpty());
    }
}