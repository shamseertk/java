public class ThreadJoinExample {
  public static void main(String[] args) throws InterruptedException {
    Thread worker = new Thread(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      System.out.println("Worker: finished");
    });

    System.out.println("Main: starting worker");
    worker.start();

    System.out.println("Main: waiting for worker");
    worker.join();

    System.out.println("Main: worker has finished");
  }
}
