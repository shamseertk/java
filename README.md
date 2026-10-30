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

Compile and run:

```sh
javac ThreadDaemonExample.java
java ThreadDaemonExample
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
