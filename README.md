# Proximity
Live Air Quality index

#### Time Taken: 5.5 Hours (3.5 hours development, 2 hours research on Channels/MPChart)

#### Architecture:
MVVM architecture has been used at the presentation layer. Unfortunately, due to lack of time; I have not used clean architecture.

Current architecture used is as follow:
* View communicates with ViewModel for data
* ViewModel depends on WebSocketClient, this client uses Kotlin Channel to provide the hot stream of data to the view model.
* ViewModel has been reused for both List and Detail screen because the nature and format of data received remains the same.
* Model carries history within itself, that is later used to show the data on graph.
* For Dependency Injection, Customn DependencyInjector is created.


#### Build
Signed APK file can be found in the root directory of the project under "sample_apk" folder.

#### Future Enhancements
* Adding Unit/Instrumentation test
* Adapting clean architecture
* Making graph more attractive and better accessible
* Using Hilt for Dependency Injection
* Adapter is doing certain operations that can be pulled out of it.
* Colors used in Adapter can be pulled out and can be put in resources.

