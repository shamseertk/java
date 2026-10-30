# Java Interview Example

This repository contains a small Java example in `interview.java`. It applies a
set of range updates to an array and counts how many positions end with a value
greater than a threshold.

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
