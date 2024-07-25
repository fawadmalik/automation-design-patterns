### Black Hole Proxy Approach

The Black Hole Proxy approach tries to reduce test instability by getting rid
of as many third-party uncertainties as possible. Modern websites have a lot
of third-party content loaded on every page. There are social networking
buttons, images coming from CDNs, tracking pixels, analytics, and much
more. All these items can destabilize our tests at any point. Black Hole
Proxy takes all HTTP requests going to third-party websites and blocks
them, as if the request was sucked into a black hole.

## Implementing the Black Hole Proxy

Our website integrates with a couple of third-party user tracking services that
collect analytics data and use others to download the fonts. We may not need
to test these services in all our tests, so we can use the Black Hole Proxy
approach to block them.
We will be taking advantage of the HTTP proxy settings that all browsers
use. Our tests will send all the HTTP traffic to a fake proxy that will swallow
all the requests and will send only the relevant ones.

## The code implemented in this package performs the following actions:
- It creates an instance of the ChromeOptions class
- It configures the HTTP proxy to point to a non-existing proxy on 127.0.0.1 with port of 18882

Before all the tests in the class, we start the proxy server. Also, we use
special collections that can handle a parallel code since most of the web
requests are happening in parallel. If we use regular collections deadlocks
may occur when the waiting process is still holding on to another resource,
that the first needs before it can finish
We subscribe to the BeforeRequest event, which is raised before each request in
the handler method. If the URL of the request was set to be blocked, we
would prevent it by returning status code 200 with no content instead of
making the actual request.
Through the Black Hole Proxy pattern, our tests can speed up significantly
by not waiting for 3rd-party services loading. Furthermore, the tests will be
more hermetically sealed by blocking the third-party requests, reducing the
external dependencies that often cause test failures.

## The code can be further integrated
It can be integrated into the browser initialization through attributes.

### Summary
In the last 3 packages, we investigated how we can speed up our test suite in many
ways.
First, it was by instrumenting the code so that we can list all
potential optimization points.
Next, There was improvement in the login processes by using API calls.
Next, we handled async requests and thus eliminating all hard-coded pauses.
Next, we created a comprehensive solution for browser initialization using the Observer design
pattern that allowed us to reuse the existing browser, saving a couple of
seconds for each test.
Lastly, saw some benefits of the Black Hole Proxy approach by blocking irrelevant for the test 3rd-party services.

Let's answer some questions to gain more understanding on these approaches.
## Questions
1. How would you decide which part of your code should be optimized?
2. What is the typical approach for speeding up the login process?
3. How can you use WebDriver API to handle asynchronous forms?
4. Can you list the main participants in the Observer design pattern?
5. Can you create the Observer design pattern UML diagram?
6. Which WebDriver method can be used to make sure that each test is not depending on the previous
   one if we reuse the browser?
7. What are the advantages of using the Black Hole Proxy approach?