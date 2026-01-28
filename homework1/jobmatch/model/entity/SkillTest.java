package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkillTest {

    @Test
    void testConstructorShouldCreateSkillWhenArgumentsAreValid() {
        Skill skill = new Skill("Java", 3);

        assertEquals("Java", skill.name());
        assertEquals(3, skill.level());
    }

    @Test
    void testConstructorShouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Skill(null, 3));
    }

    @Test
    void testConstructorShouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Skill("   ", 3));
    }

    @Test
    void testConstructorShouldThrowWhenLevelIsBelowLowerBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new Skill("Java", -1));
    }

    @Test
    void testConstructorShouldThrowWhenLevelIsAboveUpperBound() {
        assertThrows(IllegalArgumentException.class,
                () -> new Skill("Java", 6));
    }

    @Test
    void testConstructorShouldAllowBoundaryLevels() {
        Skill min = new Skill("Java", 0);
        Skill max = new Skill("Java", 5);

        assertEquals(0, min.level());
        assertEquals(5, max.level());
    }
}
