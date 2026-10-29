public class SimpleThread {
  public static void main(String[] args) throws InterruptedException {
    System.out.println("=== One new thread ===");
    Thread worker = createWorker("worker-1");
    System.out.println("Starting " + worker.getName());
    worker.start();
    worker.join();

    System.out.println("\n=== Several new threads at once ===");
    Thread[] workers = {
      createWorker("worker-2"),
      createWorker("worker-3"),
      createWorker("worker-4")
    };

    for (Thread thread : workers) {
      System.out.println("Starting " + thread.getName());
      thread.start();
    }

    for (Thread thread : workers) {
      thread.join();
    }
  }

  private static Thread createWorker(String name) {
    return new Thread(name) {
      @Override
      public void run() {
        System.out.println(getName() + " is running on " + Thread.currentThread().getName());

        for (int step = 1; step <= 3; step++) {
          System.out.println(getName() + " doing work " + step);
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }
        }

        System.out.println(getName() + " finished");
      }
    };
  }
}
