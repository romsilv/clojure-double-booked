# Double Booked Task

## Description 
When maintaining a calendar of events, it is important to know if an event overlaps with another event. 

Given a sequence of events, each having a start and end time, write a program that will return the sequence of all pairs of overlapping events.

Each function must have its unit test.

---
# Solution
The provided code in Clojure identifies overlapping events based on their start and end times
 # Require and Imports
- clojure.string for string operations.
- Classes from java.time for date, format and time handling.

# Core.clj Implementation

**parse-date**
- Converts a string to a LocalDateTime and validate if the string has a valid value or throw an exception.

**find-overlapping-events**
- Finds and filter all pairs of valid overlapping events and iterate though the events to return a list of pairs of event IDs that overlap.

**overlap?**
- Determines if two events overlap according to start time and end time parsed using LocalDateTime comparison methods.

## Execution
- To Execute the Clojure code:
Lein Execution

Execute core.clj main code
```
lein run
```
Execute coretest.clj unit tests 
``` 
lein test
```  