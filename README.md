# Purpose

This app is targeted towards parents who are searching for high schools
that will maximize the chances of their child going to the university
after high school.

To accomplish this the list of high schools is sorted by the
**percentage of students** that take the SAT. The hypothesis is that
being around more academically oriented students increases the student's chances of doing well on the SAT.

## More sorting and filtering options

In a real app there would be more than one way to sort and filter.

Some options are:

* filter by distance from a certain location
* filter by estimated travel time from a certain location
* sort by percentage of students that take the SAT (only sort that is currently implemented)
* sort by average SAT score (math+reading+writing)
* sort by math SAT score
* sort by average reading+writing SAT scores

# Disambiguation / Functional choices

I have been given the freedom to make decisions regarding functional details.

The requirements say "Get your data here". The data could be downloaded once as a JSON file which would be included with the app. But to make things more realistic I'll

1. retrieve the data using the REST API
2. Cache the data in a local DB.

# Implementation Plan

## Data Layer

### Data validation

Requirements:

1. Data from external sources must be validated ASAP. E.g. a REST API, user input, etc.
2. Invalid data is rejected. The anomalies are logged to help fix the external data sources.
3. Internal data is validated and strongly typed.

### Implementation

            loosely typed data                         Strongly typed and validated data
            (e.g. Kotlin String?)                      + Log entries for the data anomalies                            
REST API ---------------------------> Data validation -------------------------------------->  App usage

* Use Retrofit and Gson to get the School data and convert the JSON into loosely typed Data classes
* Create a SQLite DB and use it to store a local copy of the validated data
* Nice to haves
    * include a pre-cached copy of the Schools DB. This would make the app usable even when the network
      isn't available when the app is launched for the first time.
    * avoid loading the entire School list into RAM. This would be necessary if the amount of
      data in the list was much larger.

## Dependency injection

Decision → use Hilt.

Pros

* I know Hilt; and it's relatively easy to set up.

Cons

* Hilt/Dagger depend on Java and the JVM; so it can't be used for multi-platform Kotlin code.

## Logging

Decision → use Timber.

## Repository

Requirements

* load the data from both API endpoints in parallel
* parse the JSON responses in the background

Decision → Coroutines for handling concurrency

Decision → Non-reactive code in the Repository.
The data is not being updated (at least not since 2018); so I'll for this Repository I'll Keep It Simple and non-reactive.

Decision → The data set is not large; so the Repository can return a List of all 440 schools. For a larger data set the Repository would need to return individual items or pages of items.

## ViewModels

Decision → LiveData for exposing data streams
The LiveData API is simple (simplistic?). For more advanced use cases it could be worth it to use Kotlin Flows. Although it could be argued that not exposing Flows to the MVVM View is an advantage because it reduces the temptation to add logic into the View.

## MVVM View implementation

Decision → Use 1 Activity with multiple Fragments.
Decision → Use classic Android Views; because that's what I'm familiar with.

## Screens

### School List

Requirements

* Show a spinner while the data is loading
* Show a list of HighSchool with their SAT info

DONE - Implement the ViewModel
DONE - add RecyclerView
DONE - display spinner
DONE - display error
SKIP - display empty view

### School Details

Same info as in the List view with contact info.

TODO - centralize the UCE hacks that are in both ViewModels
TODO - add tests
