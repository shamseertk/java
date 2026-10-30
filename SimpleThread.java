public class SimpleThread {
  public static void main(String[] args) {
    // Running simple thread single task.
    Thread singleWorker = new Thread(){
      @Override
      public void run() {
        System.out.println("First thread");
      };
    };
    singleWorker.start();
    // Running thread in a loop
    for (int number = 1; number <= 3; number++) {
      Thread worker = new Thread(() -> {
        for (int step = 1; step <= 2; step++) {
          System.out.println(Thread.currentThread().getName() + " - step " + step);
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }
        }
      }, "Worker " + number);

      worker.start();
    }
  }
}
