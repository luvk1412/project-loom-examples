package performance;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.sleep;

public class MaxVirtualThreads {

  private static final int threadCount = 1_000_000;
  private static final int threadSleepMs = 2_000;


  public static void main(String[] args) throws InterruptedException {
    launchVirtualThreads();
  }

  private static void launchVirtualThreads() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < threadCount; ++i) {
      threads.add(Thread.ofVirtual().unstarted(() -> sleep(threadSleepMs)));
    }
    Instant start = Instant.now();
    threads.forEach(Thread::start);
    for (Thread thread : threads) {
      thread.join();
    }
    System.out.println("Time: " + Duration.between(start, Instant.now()));
  }

}
