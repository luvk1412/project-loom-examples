package experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ManyToOneMappingCheck {

  /**
   * Helps in checking that same virtual thread after sleep is picked up by another platform thread.
   */
  public static void main(String[] args) throws InterruptedException {
    manyToOneNormalCheck();
    System.out.println("\n\n\n\n --- Completed --- \n\n\n\n");
    manyToOneLockCheck();
    System.out.println("\n\n\n\n --- Completed --- \n\n\n\n");
    manyToOneSynchronizedCheck();
  }

  private static void manyToOneNormalCheck() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Thread t;
    for (int i = 0; i < 100; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        printCurrentThread(ii, "NormalCheck");
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        printCurrentThread(ii, "NormalCheck");
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  private static void manyToOneLockCheck() throws InterruptedException {
    ReentrantLock lock = new ReentrantLock();
    List<Thread> threads = new ArrayList<>();
    Thread t;
    for (int i = 0; i < 100; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        lock.lock();
        printCurrentThread(ii, "LockCheck");
        lock.unlock();
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        lock.lock();
        printCurrentThread(ii, "LockCheck");
        lock.unlock();
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  private static void manyToOneSynchronizedCheck() throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Object lock = new Object();
    Thread t;
    for (int i = 0; i < 100; ++i) {
      final int ii = i;
      t = Thread.ofVirtual().factory().newThread(() -> {
        synchronized (lock) {
          printCurrentThread(ii, "SynchronisedCheck");
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        synchronized (lock) {
          printCurrentThread(ii, "SynchronisedCheck");
        }
      });
      t.start();
      threads.add(t);
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  private static void printCurrentThread(final int count, String suffix) {
    System.out.println(suffix + ":" + count + ": " + Thread.currentThread());
  }

}
