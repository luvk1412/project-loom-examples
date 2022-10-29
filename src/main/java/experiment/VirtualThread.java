package experiment;

public class VirtualThread {

  public static void main(String[] args) throws InterruptedException {
    Thread.ofVirtual().start(() -> System.out.println(Thread.currentThread())).join();
  }

}
