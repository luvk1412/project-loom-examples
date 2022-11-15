package performance.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tester {

  private HttpClient client;
  private int parallelRequests = 100;
  private int totalRequests = 1000;
  private final Queue<Long> timeMillis = new ConcurrentLinkedQueue();
  private Semaphore semaphore = new Semaphore(parallelRequests);
  private AtomicInteger errorCount = new AtomicInteger();
  private AtomicInteger threadNum = new AtomicInteger();
  Lock lock = new ReentrantLock();

  public Tester() {
    client = HttpClient.newBuilder()
      .connectTimeout(Duration.of(20, ChronoUnit.SECONDS))
      .build();
  }

  public Tester(int totalRequests, int parallelRequests) {
    this();
    this.totalRequests = totalRequests;
    this.parallelRequests = parallelRequests;
    this.semaphore = new Semaphore(parallelRequests);
  }

  public Tester(int totalRequests) {
    this(totalRequests, totalRequests);
  }

  private void makeRequest() {
    HttpResponse<String> resp;
    try {
      semaphore.acquire();
      long start = System.currentTimeMillis();
      resp = client.send(HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/demo/hi")).GET().build(), HttpResponse.BodyHandlers.ofString());
      timeMillis.add(System.currentTimeMillis() - start);
    } catch (Exception ignored) {
      errorCount.incrementAndGet();
    } finally {
      semaphore.release();
    }

  }

  public void startTest() {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < totalRequests; ++i) {
      threads.add(Thread.ofVirtual().start(this::makeRequest));
    }

    Instant start = Instant.now();
    int i = 0;
    for (var thread : threads) {
      try {
        thread.join();
//        System.out.println((i++) + ":" + timeMillis.size());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    Instant end = Instant.now();
    System.out.println("ThreadsListSize: " + threads.size());
    System.out.println("Total tasks: " + totalRequests);
    System.out.println("Parallel tasks: " + parallelRequests);
    System.out.println("Completed tasks: " + timeMillis.size());
    System.out.println("Error tasks: " + errorCount.get());
    System.out.println("Execution time: " + Duration.between(start, end));
    System.out.println("Average ms: " + timeMillis.stream()
      .mapToDouble(a -> a)
      .average());
    System.out.println("Min ms: " + timeMillis.stream()
      .mapToLong(a -> a)
      .min());
    System.out.println("Max ms: " + timeMillis.stream()
      .mapToLong(a -> a)
      .max());
    System.out.println("Throughput: " + (timeMillis.size() * 1000.0 / Duration.between(start, end).toMillis()) + " req/sec");
  }

  public static void main(String[] args) {
    Tester tester = new Tester(10000, 1000);
    tester.startTest();
  }


}
