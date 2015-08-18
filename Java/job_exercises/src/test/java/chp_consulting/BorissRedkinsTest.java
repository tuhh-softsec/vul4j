package chp_consulting;

import fragment.submissions.BorissRedkins;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BorissRedkinsTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(null);
  }

  /**
   * Test of main method, of class BorissRedkins.
   */
  @Test
  public void testMainWithInput1() {
    BorissRedkins.main(new String[] {"src/test/resources/chp_consulting/input1.txt"});
    assertEquals("O draconian devil! Oh lame saint!\n", outContent.toString());
  }


  /**
   * Test of main method, of class BorissRedkins.
   */
  @Test
  public void testMainWithInput2() {
    BorissRedkins.main(new String[] {"src/test/resources/chp_consulting/input2.txt"});
    assertEquals(
        "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n",
        outContent.toString());
  }
}
