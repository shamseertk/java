public class ThreadInterruptExample {
  public static void main(String[] args) throws InterruptedException {
    Thread worker = new Thread(() -> {
      try {
        while (true) {
          System.out.println("Worker: doing work");
          Thread.sleep(300);
        }
      } catch (InterruptedException e) {
        // Sleep was interrupted, so stop the worker.
        System.out.println("Worker: interrupted, stopping");
      }
    });

    worker.start();
    Thread.sleep(1000);

    System.out.println("Main: asking worker to stop");
    // This asks the worker to stop; it does not forcibly kill the thread.
    worker.interrupt();
    worker.join();
    System.out.println("Main: worker has stopped");
  }
}
