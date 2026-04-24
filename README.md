# Quiz Leaderboard System

## Overview

This project implements a leaderboard system by consuming quiz data from an external API. The system processes multiple API responses, removes duplicate data, aggregates scores, and generates a final leaderboard.

## Problem Statement

The API returns quiz events across multiple polls. Due to system behavior, the same event may appear multiple times. The objective is to:

* Fetch data from 10 API polls
* Remove duplicate events using `(roundId + participant)`
* Aggregate scores correctly
* Generate a leaderboard sorted by total score
* Submit the final result

## Approach

### 1. Data Fetching

* Polled the API 10 times using `poll=0` to `poll=9`
* Maintained a 5-second delay between each request as required

### 2. Deduplication

* Used a `HashSet` to track unique events
* Unique key: `roundId + "|" + participant`
* Ensured duplicate events across polls were ignored

### 3. Score Aggregation

* Used a `HashMap<String, Integer>` to store total scores
* Aggregated scores per participant using summation

### 4. Leaderboard Generation

* Converted map to list
* Sorted in descending order of totalScore

### 5. Submission

* Constructed JSON payload with leaderboard
* Sent POST request to submission API

## Tech Stack

* Java 11
* HttpClient (for API calls)
* Gson (for JSON parsing)

## How to Run

1. Download Gson JAR
2. Compile:

   ```bash
   javac -cp gson-2.10.1.jar QuizLeaderBoard.java
   ```
3. Run:

   ```bash
   java -cp .:gson-2.10.1.jar QuizLeaderBoard
   ```

## Key Highlights

* Correct handling of duplicate API data
* Efficient use of HashSet and HashMap
* Clean and minimal implementation
* Accurate leaderboard generation

## Note

The API enforces idempotent behavior and tracks submission attempts. After multiple attempts, it may return summary responses instead of validation messages. However, the implemented logic correctly processes data and produces accurate results.

## Output Example

```
Leaderboard:
Diana  -> 470
Ethan  -> 455
Fiona  -> 440

Total Score: 1365
```
