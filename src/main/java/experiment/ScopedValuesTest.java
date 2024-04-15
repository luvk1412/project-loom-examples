package experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScopedValuesTest {
  private final static ScopedValue<String> CONTEXT = ScopedValue.newInstance();
  Map<String, String> m = Map.of();

  public static void main(String[] args) throws InterruptedException {
    List<Thread> vt = new ArrayList<>();
    for (int j = 0; j < 5; ++j) {
      final int i = j;
      System.out.println("[1]. " + Thread.currentThread() + ": " + i);
      vt.add(Thread.ofVirtual().start(() -> {
        System.out.println("[2]. " + Thread.currentThread() + ": " + i);
        System.out.println("[2']. " + Thread.currentThread() + ": " + i);
        ScopedValue.runWhere(CONTEXT, String.valueOf(i), () -> {
          System.out.println("[3]. " + Thread.currentThread() + ": " + CONTEXT.get());
        });
        System.out.println("[4]. " + Thread.currentThread() + ": " + i);
      }));
    }
    for (Thread thread : vt) {
      thread.join();
    }
  }

}
