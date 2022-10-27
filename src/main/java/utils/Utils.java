package utils;

public class Utils {

  public static void sleep(long sleepMs) {
    try {
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
