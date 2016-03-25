# owmclient
A simple JAR-based command-line client for the OpenWeatherMap REST API.

## Build info
To build an executable JAR, run the Gradle task *fatJar* which is defined in the build.gradle file (all dependencies will be included in the JAR file).

## Quick start
To test the client, run from the command-line with the following command: *java -jar weatherapp.jar*. For information about possible arguments, run with the *-help* flag.

## Troubleshooting
If you encounter HTTP 429 errors when querying for multiple cities, run with the *-st* flag.
