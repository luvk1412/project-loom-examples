package experiment;

import java.util.ArrayList;
import java.util.List;

public class ManyToOneMappingCheck {

  /**
   * Helps in checking that same thread after sleep is picked up by another platform thread.
   */
  public static void main(String[] args) throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Thread t;
    for (int i = 0; i < 100; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        System.out.println(ii + ": " + Thread.currentThread());
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        System.out.println(ii + ": " + Thread.currentThread());
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

}
