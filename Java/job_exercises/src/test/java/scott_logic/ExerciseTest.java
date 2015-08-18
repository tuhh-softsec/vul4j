package scott_logic;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExerciseTest {

  public ExerciseTest() {}

  @Test
  public void testIsPowerOfTwo() {
    Exercise exercise = new Exercise();

    assertTrue(exercise.isPowerOfTwo(8));
    assertTrue(exercise.isPowerOfTwo(2));
    assertTrue(exercise.isPowerOfTwo(1));
    assertFalse(exercise.isPowerOfTwo(7));
  }

}
