# Java Interview Example

This repository contains a small Java example in `interview.java`. It applies a
set of range updates to an array and counts how many positions end with a value
greater than a threshold.

## How it works

`findMemoryStackCount(n, k, queries)` creates an array of `n` positions. Each
query has the form `{start, end, amount}` and adds `amount` to the positions
from `start` (inclusive) to `end` (exclusive). The method returns the number of
positions whose final value is greater than `k`.

The sample in `main` uses `n = 7`, a threshold of `10`, and three range updates.
For those values, the method returns `3`.

## Compile and run

From the repository directory, compile and run with a JDK:

```sh
javac interview.java
java interview
```

The sample currently computes the result without printing it.
