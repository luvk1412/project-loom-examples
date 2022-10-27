package experiment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;
import static utils.Utils.sleep;

public class ManyToOneMappingCheck {

  private static final int iterationCount = 10;
  private static final int threadSleepMs = 10;
  private static final String separator = "\n\n Completed \n\n";

  /**
   * Helps in checking that same virtual thread after sleep is picked up by another platform thread.
   */
  public static void main(String[] args) throws InterruptedException {
    manyToOneNormalCheck();
    System.out.println(separator);
    manyToOneLockNonBlockingCheck();
    System.out.println(separator);
    manyToOneLockBlockingCheck();
    System.out.println(separator);
    manyToOneSynchronizedNonBlockingCheck();
    System.out.println(separator);
    manyToOneSynchronizedBlockingCheck();
  }

  private static void manyToOneNormalCheck() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Map<Integer, Set<String>> threadNamesMap = new ConcurrentHashMap<>();
    Thread t;
    for (int i = 0; i < iterationCount; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        addCurrentThreadToMap(ii, threadNamesMap);
        sleep(threadSleepMs);
        addCurrentThreadToMap(ii, threadNamesMap);
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
    printThreadNamesMap("NormalCheck", threadNamesMap);
  }

  private static void manyToOneLockNonBlockingCheck() throws InterruptedException {
    ReentrantLock lock = new ReentrantLock();
    List<Thread> threads = new ArrayList<>();
    Map<Integer, Set<String>> threadNamesMap = new ConcurrentHashMap<>();
    Thread t;
    for (int i = 0; i < iterationCount; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        lock.lock();
        addCurrentThreadToMap(ii, threadNamesMap);
        lock.unlock();
        sleep(threadSleepMs);
        lock.lock();
        addCurrentThreadToMap(ii, threadNamesMap);
        lock.unlock();
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
    printThreadNamesMap("LockNonBlockingCheck", threadNamesMap);
  }

  private static void manyToOneLockBlockingCheck() throws InterruptedException {
    ReentrantLock lock = new ReentrantLock();
    List<Thread> threads = new ArrayList<>();
    Map<Integer, Set<String>> threadNamesMap = new ConcurrentHashMap<>();
    Thread t;
    for (int i = 0; i < iterationCount; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        addCurrentThreadToMap(ii, threadNamesMap);
        lock.lock();
        sleep(threadSleepMs);
        lock.unlock();
        addCurrentThreadToMap(ii, threadNamesMap);
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
    printThreadNamesMap("LockBlockingCheck", threadNamesMap);
  }

  private static void manyToOneSynchronizedNonBlockingCheck() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Map<Integer, Set<String>> threadNamesMap = new ConcurrentHashMap<>();
    Object lock = new Object();
    Thread t;
    for (int i = 0; i < iterationCount; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        synchronized (lock) {
          addCurrentThreadToMap(ii, threadNamesMap);
        }
        sleep(threadSleepMs);
        synchronized (lock) {
          addCurrentThreadToMap(ii, threadNamesMap);
        }
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
    printThreadNamesMap("SynchronizedNonBlocking", threadNamesMap);
  }

  private static void manyToOneSynchronizedBlockingCheck() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Map<Integer, Set<String>> threadNamesMap = new ConcurrentHashMap<>();
    Object lock = new Object();
    Thread t;
    for (int i = 0; i < iterationCount; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        addCurrentThreadToMap(ii, threadNamesMap);
        synchronized (lock) {
          sleep(threadSleepMs);
        }
        addCurrentThreadToMap(ii, threadNamesMap);
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
    printThreadNamesMap("SynchronizedBlocking", threadNamesMap);
  }

  private static void printCurrentThread(final int count, String suffix) {
    System.out.println(suffix + ":" + count + ": " + Thread.currentThread());
  }

  private static void addCurrentThreadToMap(int i, Map<Integer, Set<String>> threadMapping) {
    threadMapping.compute(i, (key, val) -> {
      if (isNull(val)) {
        val = new HashSet<>();
      }
      val.add(Thread.currentThread().toString());
      return val;
    });
  }

  private static void printThreadNamesMap(String prefix, Map<Integer, Set<String>> threadPerCount) {
    threadPerCount.forEach((iteration, threadNames) -> {
      System.out.println(prefix + ": Itr: " + iteration + ", threadNames: " + threadNames);
    });
  }

}
