package parkeon;

/**
 * Face to face interview with Parkeon on 19 August 2015 at 11:15.
 * The interview comprised a written technical test 10 min long.
 * 
 * Interviewers: Jacki O’Shea, Chris Warnes
 * Duration: 30 min
 */
public class Exercise {

  public static void main(String[] args) {
    questionOne();
    questionTwo();
    questionThree();
    questionFour();
    questionFive();
    // Question Six: Can you overwrite private, static methods?
    // Question Seven: What does ... keyword means in ant?
    // Question Eight: ... on ant
    // Question Nine: What does ...Transaction means in Hibernate?
    // Question Ten: ... on Hibernate
  }

  private static void questionOne() {
    if (null == null) {
      System.out.println("Hello!");
    }
  }

  private static void questionTwo() {
    System.out.println(Math.min(Double.MIN_VALUE, 0.0d));
  }

  private static void questionThree() {
    method(null);
  }

  // Answer: it will not compile, integer number too large: 08
  private static void questionFour() {
    char c = 0;
    int a = 07;
    int b = 08;
    int d = 12345;
    // ...
  }

  private static void questionFive() {
    String s1 = "abc";
    String s2 = s1;
    s1 += "d";
    System.out.println(s1 + " " + s2 + " " + (s1 == s2));

    StringBuilder sb1 = new StringBuilder("abc");
    StringBuilder sb2 = sb1;
    sb1.append("d");
    System.out.println(sb1 + " " + sb2 + " " + (sb1 == sb2));
  }

  private static void method(Exception e) {
    System.out.println("Exception");
  }

  private static void method(RuntimeException e) {
    System.out.println("RuntimeException");
  }

  private static void method(Object o) {
    System.out.println("Object");
  }

}
