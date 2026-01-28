package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkillRecommendationTest {

    @Test
    void testConstructorCreatesSkillRecommendationWhenArgumentsAreValid() {
        SkillRecommendation recommendation = new SkillRecommendation("AWS", 0.347);

        assertEquals("AWS", recommendation.skillName());
        assertEquals(0.347, recommendation.improvementScore(), 1e-6);
    }

    @Test
    void testConstructorThrowsWhenSkillNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new SkillRecommendation(null, 0.1));
    }

    @Test
    void testConstructorThrowsWhenSkillNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new SkillRecommendation("   ", 0.1));
    }

    @Test
    void testConstructorThrowsWhenImprovementScoreIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new SkillRecommendation("AWS", -0.01));
    }

    @Test
    void testConstructorAllowsZeroImprovementScore() {
        SkillRecommendation recommendation = new SkillRecommendation("AWS", 0.0);

        assertEquals(0.0, recommendation.improvementScore(), 1e-6);
    }
}
