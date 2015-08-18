package multicom;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class JavaExerciseTest {

  private JavaExercise exercise;

  @Before
  public void setUp() throws Exception {
    exercise = new JavaExercise();
  }

  /**
   * Test of substr method, of class JavaExercise.
   */
  @Test
  public void testSubstr() {
    assertEquals("abcd", exercise.substr("abcdef", 0, 4));
    assertEquals("er", exercise.substr("qwerty", 2, 2));
    assertEquals("wert", exercise.substr("qwerty", 1, 4));
  }

  /**
   * Test of indexOf method, of class JavaExercise.
   */
  @Test
  public void testIndexof() {
    assertEquals(0, exercise.indexOf("ab", "abcabc"));
    assertEquals(2, exercise.indexOf("er", "qwerty"));
    assertEquals(-1, exercise.indexOf("zap", "uvwxyz"));
  }

}
