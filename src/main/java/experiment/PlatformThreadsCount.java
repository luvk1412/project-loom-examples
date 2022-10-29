package experiment;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.Utils.sleep;

public class PlatformThreadsCount {

  private static final int threadCount = 1_000;
  private static final int threadSleepMs = 2_000;


  public static void main(String[] args) throws InterruptedException {

    Set<String> pools = new HashSet<>();
    Set<String> pThreads = new HashSet<>();
    Pattern pool = Pattern.compile("ForkJoinPool-[\\d?]");
    Pattern worker = Pattern.compile("worker-[\\d?]");

    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < threadCount; ++i) {
      threads.add(Thread.ofVirtual()
        .unstarted(() -> {
          sleep(threadSleepMs);
          String name = Thread.currentThread().toString();
          Matcher poolMatcher = pool.matcher(name);
          if (poolMatcher.find()) {
            pools.add(poolMatcher.group());
          }
          Matcher workerMatcher = worker.matcher(name);
          if (workerMatcher.find()) {
            pThreads.add(workerMatcher.group());
          }
        }));
    }
    Instant start = Instant.now();
    threads.forEach(Thread::start);
    for (var thread : threads) {
      thread.join();
    }
    System.out.println("# cores = " + Runtime.getRuntime().availableProcessors());
    System.out.println("Time = " + Duration.between(start, Instant.now()));
    System.out.println("Pools");
    pools.forEach(System.out::println);
    System.out.println("Platform threads (" + pThreads.size() + ")");
    pThreads.forEach(System.out::println);

  }
}
