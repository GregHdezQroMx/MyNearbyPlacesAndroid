# MyNearbyPlacesAndroid
Android Project with java, for locate near places about food (restaurants) with google map API. Using some Lottie animations.

## Project Contains
1. Splash Activity with lottie animation
1. MainActivity wih google map an recyclerview for showing near places
1. Fragment for showing place details

## Execution
1. After splash activity runs for a while, app show Nearby Places activity, where a google maps waits for show location and near places markers and list, if not gps detected then app ask for it, so places are searched and showed.
2. if app can shows places a toast message appear indicating it. then user can tap on "my location" button on Superior-right corner of the screen for trying again.
3. If user tap on a particular place's marker, then the list scroll to the item relates to initial information showing.
4. If user tap on a particular item in place's list, then a new screen is showed for more details about Particular near place.
5. For return to main screen, user can use android native "back" button.

## Next Steps:
1. Extract API KEY from repository (done):  Add GOOGLE_API_KEY=<Your Api Key> to your local.properties file
2. Migration to Kotlin
3. Re structuration of Clean Architecture
4. Migration of UI to Jetpack Compose
5. Include Coroutines and dependency injection with Koin
