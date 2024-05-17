# Before you start
## About this challenge
We prepared a very simple app but we need to improve it. Please help us!

### Descriptions and the purpose
The specifications & requirements you'll see below can be too strict.

The reason is that we would like to see your understandings, skills, and experiences by the tasks.

### You can ignore the description 👍
If you already have the knowledge and you have other effective, efficient, or creative idea, it's more than welcome!

In that case, let's ignore the descriptions, and implement in that way.  Then, let us discuss it in the interview 🙌

## Time
**We don't set the time limitation**. You can take your time. Please take it easy ✌🏻

# Fact app
This app shows a cat fact from [fact free api](https://catfact.ninja/#/).  

![Screenshot](./fact_app.png)

It works, but does not satisfy **[Specifications](#specifications)** below, so please implement them!

If you still have a time after that, please check & try *[Optional](#optional)*.

## Specifications

### Screens (features)
#### Home screen (Existing)
- Save the latest cat fact to local storage, and show it when a user relaunches the app.
- Show the (character) `length` of a fact **only when the length is greater than 100**.
  - `length` is contained in the `fact` response data.
  - The place to show the lenght is not specified. Anywhere is okay.
- Show the text "**Multiple cats!**" when the fact contains the word cats.
  - No context check is required. Simply finding the word `cats` is fine
#### New screen
- Add 1 more screen which you think is good to have
- Any content is fine. Here are some examples
  - Fact history (show the fact list the user has seen)
  - Fact search (search cat fact(s) by keyword, length, etc)

### Other requirements
Sorry to limit the freedom, but please check below as well 🙏

- Kotlin with Coroutine & Flow
- Jetpack Compose for UI
- Dependency injection (library is not specified)
- Unit and UI tests

## Optional
### Screens or features
- (Design) Add the `Top app bar` and update the design as you want 🏰
- (Future growth) Add a `New feature or tool` which you think is better to have 💪

### Other requirements
- (Testing) Use [JUnit5](https://github.com/mannodermaus/android-junit5) and add `fake` or `mockk`
- (Gradle) Add a `version catalog` 📗
- (Future growth) `Modularize` the app ✌🏻
- (Future growth) Add `Domain layer` 🚴‍️

# How to submit?
Please 
- Create a new public repository under your GitHub account 
- Send your the pull request's link to us 🙏
