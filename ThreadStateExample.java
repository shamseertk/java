import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThreadStateExample {
  public static void main(String[] args) throws InterruptedException {
    // Each latch starts at 1 and opens permanently when countDown() reaches 0.
    // "ready" signals that the worker has reached the point before it waits;
    // "release" lets the main thread decide when the worker can finish.
    CountDownLatch ready = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);

    Thread worker = new Thread(() -> {
      ready.countDown();
      try {
        // await() blocks here until the main thread opens the release latch.
        // This is CountDownLatch.await(), not Object.wait().
        release.await();
      } catch (InterruptedException e) {
        // Preserve the interrupt request before this worker exits.
        Thread.currentThread().interrupt();
      }
    }, "state-worker");

    System.out.println("Before start: " + worker.getState());
    worker.start();

    // Wait until the worker has signaled that it started its waiting phase.
    ready.await();
    // The ready signal happens just before release.await(), so wait separately
    // until the observable thread state has actually changed to WAITING.
    waitForState(worker, Thread.State.WAITING);
    System.out.println("While waiting: " + worker.getState());

    // Opening the latch unblocks release.await(); join() then waits for exit.
    release.countDown();
    worker.join();
    System.out.println("After join: " + worker.getState());
  }

  private static void waitForState(Thread worker, Thread.State expected)
      throws InterruptedException {
    // getState() is only a snapshot, so poll briefly with a deadline rather
    // than assuming the state changes immediately after ready.await().
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (worker.getState() != expected) {
      if (!worker.isAlive() || System.nanoTime() >= deadline) {
        // Fail explicitly if the expected state is never reached.
        throw new IllegalStateException("Worker did not reach " + expected);
      }
      Thread.sleep(1);
    }
  }
}
