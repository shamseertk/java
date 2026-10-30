public class ThreadDaemonExample {
  public static void main(String[] args) throws InterruptedException {
    Thread backgroundWorker = new Thread(() -> {
      try {
        while (true) {
          System.out.println("Background worker is running");
          Thread.sleep(300);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    backgroundWorker.setDaemon(true);
    backgroundWorker.start();

    Thread.sleep(1000);
    System.out.println("Main thread is finished");
  }
}
