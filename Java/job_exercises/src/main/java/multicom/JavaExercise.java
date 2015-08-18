package multicom;

/**
 * Phone test taken with MultiCom on 18 August 2015 at 14:30.
 * Done on a web-based collaborative editor.
 * 
 * Interviewer: Sam
 * Duration: 20 min
 */
public class JavaExercise {

  /**
   * Part1 - substring method
   * 
   * Define your own implementation of substring by writing a method.
   * Your method should not make use of the inbuilt substring method.
   * The method should have the following signature:
   * 
   * String substr(String input, int offset, int length)
   * 
   * Some examples:
   * 
   * substr("abcder",0,4) --> returns "abcd"
   * substr("qwerty",2,2) --> returns "er"
   * substr("qwerty",1,4) --> returns "";
   * 
   * Run this class to test the method with the following inputs:
   */
  public String substr(String input, int offset, int length) {
    char[] characters = input.toCharArray();
    StringBuilder sb = new StringBuilder(length);
    for (int i = offset, n = offset + length; i < n; i++) {
      sb.append(characters[i]);
    }
    return sb.toString();
  }

  /**
   * Part2 - indexOf method
   * 
   * Define your own implementation of indesOf by writing a method
   * which reuses the substr method you have just written to return
   * the position of the first occurrence of a needle string within
   * a haystack string.
   * Your method should not make use of the inbuilt indexOf method.
   * The method should have the following signature:
   * 
   * int indexOf(String needle, String haystack)
   * 
   * Some examples:
   * 
   * indexOf("ab","abcabc") --> returns 0
   * indexOf("er","qwerty") --> returns 2
   * indexOf("zap","uvwxyz") --> returns -1
   * 
   * Run this class to test the method with the example inputs:
   */
  public int indexOf(String needle, String haystack) {
    int needleLength = needle.length();
    for (int index = 0; index < haystack.length() - needleLength; index++) {
      if (substr(haystack, index, needleLength).equals(needle)) {
        return index;
      }
    }
    return -1;
  }

}
