# Why did I develop the app this way?
- Because this is a small project, I decided to use the MVI architecture which best suits the current needs the project has. MVI provides nice separation between layers and is expandable.
- MVVI could have more boilerplate and harder state management for this project.
- Jetpack Compose and MVI work seamlessly together and state exposing allowed for more readable, maintanbale and more extensible code.
- Jetpack Compose and Kotlin (and it's libraries) are now the standard way to develop Android apps.
- Used Hilt for dependency injection because of ease of use and less boilerplate code and ease of testing. Koin could have work too.
- Used ViewModel for the connection between data layer and ui layer which is lifecycle-aware and preserves the state on configuration changes.
- Used coroutines for heavy workload and concurrent operations which works best with Jetpack Compose and ViewModels and is the way to go background work.
- Because this data source (local Json file) is not reactive and it will not be updated in anyway, flows were not used for it.
- Used Kotlins standard serialization library for fetching Json objects.

# What would I do if I had more time?
- Create tests for UI and for the data source (which in this case is a local Json file).
- I would have made the project modular (separation of data layer and ui layer in different modules) for parallel building which makes the build time faster, and more expandable in the future.
- I would have used GitFlow branching model for better collaboration in the future and more standard way for software publishing.
- I would have used better Git commit messages (See my current library for example [Baboomeh](https://github.com/Mohammad3125/Baboomeh/commits/develop/))
- I would have managed the edge-cases and errors in a better way (sealed class wrapper around fetching operations).
- Made sure that UI/UX follows the standard MaterialDesign system.
- Better state-mangement for the UI.
- Better documentation in the code.
