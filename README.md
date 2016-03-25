# owmclient
A simple JAR-based command-line client for the OpenWeatherMap REST API.

## Build info
To build an executable JAR, run the Gradle task *fatJar* which is defined in the build.gradle file (all dependencies will be included in the JAR file). There is also a pre-built executable JAR in the root directory of the repo, for convenience of testing.

## Quick start
To test the client, run from the command-line with the following command: *java -jar weatherapp.jar*. For information about possible arguments, run with the *-help* flag.

## Troubleshooting
If you encounter HTTP 429 errors when querying for multiple cities, run with the *-st* flag.

## Code guidelines
* I have tried to keep the code somewhat minimalistic and simple, and to keep the number of classes as small as possible.
* For the webservice integration I use jersey-client.
* I chose to use JsonPath for parsing the JSON to a Java object. A more typical solution would be to use a library such as Jackson, but I saw this as a chance to experiment with a more novel approach, and it allowed me to keep the data model simpler (contained in a single class).
* I have used some of the Java Reflection API, particularly in the parts of the code related to sorting by columns, in order to eliminate unnecessary boilerplate.

## Potential improvements
* I see that OWM have a separate service for querying multiple services in a single request. A potential for improvement would be to implement support for using this service, instead of querying the cities one by one.
* Although I have included most, there are still some data points in the JSON result that are simply discarded. Could improve by including them in the data model.
* Unit tests.
* In the current application, the results are printed in a CSV-style table. For ease of viewing, this could be improved by providing an option to display as a (space padded) fixed-width table.
* Could be useful to have an option to save the result as a file.
* Could be useful to have an option to read the list of cities from a file.
* Better validation of input.

## Other comments
* Although it's very valuable service they're offering, I feel OpenWeatherMap could put more care into creating good documentation. In particular, some of their reference material is downright wrong/outdated. I.e. the field names in the JSON returned by the service is different from what the docs are saying.
* The task of creating an application which queries the service in a multi-threaded way does not seem to have been tested in practice. It seems that the service usually refuses multiple simultaneous requests by returning HTTP 429 Too Many Requests errors. In order to ameliorate this, I introduced the *-st* flag which runs the requests sequentially with a small delay.
