public class SynchronizedFunctionExample {
  public static void main(String[] args) {
    new Thread(SynchronizedFunctionExample::requestData, "Thread A").start();
    new Thread(SynchronizedFunctionExample::requestData, "Thread B").start();
    new Thread(SynchronizedFunctionExample::requestData, "Thread C").start();
  }

  private static void requestData() {
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + ": calling getData");

    try {
      String data = getData();
      System.out.println(threadName + ": received " + data);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println(threadName + ": interrupted");
    }
  }

  private static synchronized String getData() throws InterruptedException {
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + ": inside getData");
    Thread.sleep(500);
    return "data for " + threadName;
  }
}
