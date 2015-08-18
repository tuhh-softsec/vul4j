package scott_logic;

public class Exercise {

  public boolean isPowerOfTwo(final int n) {
    if (n == 1) {
      return true;
    }
    if (n % 2 == 0) {
      return isPowerOfTwo(n / 2);
    }
    return false;
  }

}
