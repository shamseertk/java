# Java Interview Example

This repository contains a small Java example in `interview.java`. It applies a
set of range updates to an array and counts how many positions end with a value
greater than a threshold.

## Contents

- [Chapter 1: Starting threads](#chapter-1-starting-threads)
- [Chapter 2: Waiting for a thread](#chapter-2-waiting-for-a-thread)
- [Chapter 3: Synchronized function calls](#chapter-3-synchronized-function-calls)
- [Chapter 4: Interrupting a thread](#chapter-4-interrupting-a-thread)
- [Chapter 5: Daemon threads](#chapter-5-daemon-threads)
- [Chapter 6: Thread states and lifecycle](#chapter-6-thread-states-and-lifecycle)
- [Sample test](#sample-test)
- [Compile and run](#compile-and-run)

## Chapter 1: Starting threads

[`SimpleThread.java`](./SimpleThread.java) shows how to start a thread with
`start()`. It starts one simple thread and then starts three named threads in a
loop. Their output can appear in different orders because they run
independently.

Here is the single-thread example:

```java
Thread singleWorker = new Thread() {
  @Override
  public void run() {
    System.out.println("First thread");
  }
};
singleWorker.start();
```

Compile and run:

```sh
javac SimpleThread.java
java SimpleThread
```

## Chapter 2: Waiting for a thread

[`ThreadJoinExample.java`](./ThreadJoinExample.java) introduces `join()`. After
starting the worker, the main thread calls `worker.join()` and waits until the
worker finishes before printing that it has finished. The worker's short pause
makes the waiting easier to observe.

The main thread starts the worker, then waits for it to finish:

```java
System.out.println("Main: starting worker");
worker.start();

System.out.println("Main: waiting for worker");
worker.join();

System.out.println("Main: worker has finished");
```

Compile and run:

```sh
javac ThreadJoinExample.java
java ThreadJoinExample
```

`main` declares `throws InterruptedException` because `join()` can be
interrupted while waiting.

## Chapter 3: Synchronized function calls

[`SynchronizedFunctionExample.java`](./SynchronizedFunctionExample.java) starts
three threads that all call `getData()`. The method is `static synchronized`,
so only one thread can execute it at a time; the others wait for their turn.
Each caller continues with the data returned to it. A short sleep inside the
method simulates work so the waiting is easier to observe.

Here is the synchronized method:

```java
private static synchronized String getData() throws InterruptedException {
  String threadName = Thread.currentThread().getName();
  System.out.println(threadName + ": inside getData");
  Thread.sleep(500);
  return "data for " + threadName;
}
```

Compile and run:

```sh
javac SynchronizedFunctionExample.java
java SynchronizedFunctionExample
```

Unlike `join()`, `synchronized` controls which thread can enter a method at a
time. A regular function call already waits for its own return value; it does
not, by itself, make other threads wait. Once `getData()` returns, another
thread may enter it even if the first caller has not yet printed or processed
the returned data.

## Chapter 4: Interrupting a thread

[`ThreadInterruptExample.java`](./ThreadInterruptExample.java) shows how to
request that a thread stop. The worker repeats its task until `main` calls
`worker.interrupt()`. Since the worker is sleeping, it receives an
`InterruptedException`, handles it in `catch`, and exits.

In everyday terms, `worker.interrupt()` is a request to stop, not a way to
forcibly kill the worker. Since `Thread.sleep()` is interruptible, the request
wakes the worker by throwing `InterruptedException`. The `catch` block is where
the worker responds to that request; here it prints a message and stops.

`catch` does not require another `interrupt()` call. This example does not call
`Thread.currentThread().interrupt()` because the worker exits immediately. If
code catches `InterruptedException` but needs to continue or return to a caller
that should know about the request, it can restore the interrupt flag with
`Thread.currentThread().interrupt()`. The exception itself clears that flag
when thrown.

```java
try {
  Thread.sleep(300);
} catch (InterruptedException e) {
  System.out.println("Worker: interrupted, stopping");
}
```

```java
worker.start();
Thread.sleep(1000);
worker.interrupt();
worker.join();
```

Compile and run:

```sh
javac ThreadInterruptExample.java
java ThreadInterruptExample
```

The main thread calls `join()` so it waits until the worker has stopped.

## Chapter 5: Daemon threads

[`ThreadDaemonExample.java`](./ThreadDaemonExample.java) creates a background
thread and marks it as a daemon with `setDaemon(true)` before starting it. The
worker prints while the main thread is active. When `main` finishes, the JVM
can exit without waiting for the daemon thread, so its work may stop abruptly.
Daemon threads are for background work that should not keep the program alive;
do not use them for work that must finish or save important data.

<details>
<summary>Show the daemon thread example</summary>

```java
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
```

</details>

Compile and run:

```sh
javac ThreadDaemonExample.java
java ThreadDaemonExample
```

## Chapter 6: Thread states and lifecycle

[`ThreadStateExample.java`](./ThreadStateExample.java) shows a worker moving
from `NEW` to `WAITING` and then `TERMINATED`. The latches keep the worker in a
known waiting state until the main thread releases it. Java also reports
`RUNNABLE`, `BLOCKED`, and `TIMED_WAITING`; `getState()` is useful for observing
a thread, but its result is only a momentary snapshot and should not be used to
coordinate threads.

`CountDownLatch` is a one-way gate with a count. It starts at the count passed
to its constructor; each `countDown()` reduces that count, and any thread
blocked in `await()` can continue once the count reaches zero. Here, `ready`
signals that the worker has reached its waiting phase, while `release` holds it
there until `main` opens that gate.

The worker's `release.await()` blocks the worker; it is `CountDownLatch.await()`,
not `Object.wait()`. While blocked there, the worker's thread state is
`WAITING`. In `main`, `ready.await()` blocks until the worker calls
`ready.countDown()`. That signal is immediately before `release.await()`, so
the worker might not have entered `WAITING` yet when `ready.await()` returns.
`waitForState()` checks the state snapshot until it observes `WAITING`, with a
short deadline so the example fails clearly instead of waiting forever.

Finally, `release.countDown()` opens the worker's gate. The worker returns from
`release.await()` and exits; `worker.join()` makes `main` wait for that exit
before it reads the final `TERMINATED` state.

<details>
<summary>Show the thread states example</summary>

```java
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
```

</details>

Compile and run:

```sh
javac ThreadStateExample.java
java ThreadStateExample
```

## Sample test

`findMemoryStackCount(n, k, queries)` creates an array of `n` positions. Each
query has the form `{start, end, amount}` and adds `amount` to the positions
from `start` (inclusive) to `end` (exclusive). The method returns the number of
positions whose final value is greater than `k`.

The sample in `main` uses `n = 7`, a threshold of `10`, and three range updates.
For those values, the method returns `3`.

## Compile and run

From the repository directory, compile and run the range-update example with a
JDK:

```sh
javac interview.java
java interview
```

The sample currently computes the result without printing it.
