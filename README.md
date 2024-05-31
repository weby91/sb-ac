# Before you start
## About this challenge
We have prepared a simple app that needs improvement, and we need your help!

### Description and Objectives
While the provided [TODO](#todo) and [Methodology](#methodology) might seem detailed, they are not meant to constrain you. 

The purpose of this test is to assess your understanding, skills and experience by completing [TODO](#todo) tasks.

### You can ignore the Methodology 👍
Please follow the [TODO](#todo) stated below, including [Tech stack](#tech-stack). 

On the other hand, you don't need to follow the [Methodology](#methodology) strictly. Please feel free to leverage your knowledge and creativity to approach the task and enhance the app's efficiency and effectiveness in your own unique way. 

We would love for you to ask us questions beforehand and discuss further in the interview about your approach 🙌

### Time
There is no specific time limit for this task. You can take your time. Please take it easy ✌🏻

# Fact app
This app shows a cat fact from [fact free api](https://catfact.ninja/#/).  

![Screenshot](./fact_app.png)

It works, but does not satisfy the [TODO](#todo) below, so please implement them!

If you still have time after that, please check & try *[Optional](#optional)*.

## TODO
### Tech stack
| Topic                   | Tech stack       |
|-------------------------|------------------|
| Language                | Kotlin           |
| Asynchronous processing | Coroutine & Flow |
| UI                      | Jetpack Compose  |

### UI and features
#### Home screen (Existing screen. Please add features below)
- Save the latest cat fact to local storage, and show it when a user relaunches the app.
- Show the (character) `length` of a fact only when the length is **greater than 100**.
  - `length` is contained in the fact response data.
  - The place to show the length is not specified. You can place it anywhere.
- Show the text "Multiple cats!" when the fact contains the word cats.
  - No context check is required. Simply finding the word cats is fine
- Unit testing
  - You can decide which components or logic should be tested

#### A new screen
- Add 1 more screen which you think is good to have
- Any content is fine. Below are some examples for your reference
  - Fact history (show the fact list the user has seen)
  - Fact search (search cat fact(s) by keyword, length, etc)
- Unit testing
  - You can decide which components or logic should be tested

## Methodology
Below is our standard approach from Job Description, sharing for your reference 🙏
- MVVM
- [Room](https://developer.android.com/training/data-storage/room) and/or [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for local data
- Dependency injection ([Hilt](https://developer.android.com/training/dependency-injection/hilt-android))
- [Modularization](https://developer.android.com/topic/modularization)
- [Material Design](https://m3.material.io/)

## Optional
### UI and features
- (Design) Add the `Top app bar` and update the design of other components as you want 🏰
- (Future growth) Add another `New feature or tool` which you think is better to have 💪

### Bonus (even more)
- (Testing) Use [JUnit5](https://github.com/mannodermaus/android-junit5) and add `fake` or `mockk`
- (Testing) UI tests ✅
- (Gradle) Migrate to [version catalogs](https://developer.android.com/build/migrate-to-catalogs) 📗
- (Future growth) Add `Domain layer` 🚴‍️

# How to submit?
Please 
- Create a new public repository **under your own GitHub account**
- Create a pull request there
- Send the link of the pull request to us 🙏
