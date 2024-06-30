### The @BeforeMethod test annotation
The testSetup method with this annotation, sets up the test environment before executing each test method :
- initializes the WebDriver, 
- sets up the ChromeDriver, 
- and configures implicit wait.
The browser selected for the test needs a browser driver downloaded, which gets updated by the driver creator from time to time. We use a library called WebDriverManager to automatically download the correct version of the driver matching the browser.

### The @AfterMethod test annotation
The testTeardown method with this annotation, cleans up the test environment after each test method execution
- Quits the WebDriver instance to close the browser.

### First test: verifyPurchaseSuccessWithNewClient
The test steps and Asserts the expected outcomes at each step.
- Navigate to the demo e-commerce website
- Add Falcon 9 product to the cart
- Wait for 10 seconds to ensure the product is added to the cart
- Click on the "View Cart" button
- Apply coupon code "happybirthday"
- Wait for 5 seconds to ensure the coupon is applied
- Verify the coupon application message
- Update product quantity to 2
- Short wait before sending keys
- Wait for 5 seconds to ensure the quantity is updated
- Click on "Update Cart" button
- Wait for 5 seconds to ensure the cart is updated
- Verify the total amount
- Proceed to checkout
- Fill in billing details
- Pause for 5 seconds before placing the order
- Place the order
- Pause for 10 seconds to ensure the order is processed
- Verify the order received message

Most modern websites use modern JavaScript technologies where most operations are asynchronous. 
We have used hardcoded pauses with Thread.sleep(5000) for a 5-second wait. This is one area that 
we will focus on to optimize in later versions of this test.
The static variables are for sharing data between other tests in the class.

### When tests fail
It could mean any one or all of the following:
1. Real defect or problem in the SUT (System Under test)
2. Test environment issue (network was down, test environment was not ready etc.)
3. Defect in the test code, specially if the tests fail intermittently. These are called non-deterministic or flaky tests.

### Second Test : verifyPurchaseSuccessWithExistingClient
The second test will:
- reuse most of the test steps from the forst tests because teh order placement steps are the same
- reuse the email from the first test and repeat roughtly the steps from the first test. This will test prefilling of all user info fields and verify complete the purchase.
- extract the generated order number displayed on the success order page at the end of the test.

This captured order number will be used in the third test to verify that the information is displayed correctly in the "My Account" section.

### Refactoring using best practices
The code in the tests made sense when they were written. Let's examine how fragile they are as they can potentially fail at the slightest provocation.
So far the mistakes in the tests that need attention are:
1. Test on test dependency
if the first test does not run or fails for some reason, then the second one won't authenticate and will fail and the third one will fail order verification.

2. Hard-coded test data
Every test has website related data hardcoded; URL of the product page and title of the product being tested are put in the test steps.
If there is the slightest change in these details, the hardcoded data in every test will need to be updated.
If the product data is different between the test environments (for instance dev, test or stage), these tests cannot be reused.

3. Code duplication
Several of the test steps are repeated and duplicated between tests, such as clicking on specific links and filling out form data.

The purpose and requirements of the tests were to test the features of the website. However despite making several adequately correct decisions, the resulting test suite is unstable.
Here are some ways to expose the instability(flakiness) of these tests:
1. Put an @Ignore on the first test and run the second one only
    The second test will fail on the line of code: userName.sendKeys(purchaseEmail); because purchaseEmail has not been defined by the first test.
2. Run the third test alone without running the first two
    The third test will fail on the line of code: userName.sendKeys(purchaseEmail); because purchaseEmail has not been defined by the first test.
    Even if you run the first test to set purchaseEmail, third test will fail because without the second test Order # will not be set.
    The test will fail the last assertion: Assert.assertEquals(expectedMessage, orderName.getText());
    java.lang.AssertionError:
    Expected :Order #6931
    Actual   :Order #null
3. Decrease the seconds in the pauses that use Thread.sleep
    Any of the tests may fail because the pauses were not enough of a wait. This is expected, specially of the website/webpage performance deteriorates due to load or some other reason. Which means this is not a best practice to lessen the time to speed up the tests.

### Executing/Running the tests
When running tests as a suite or running the test class, the order of execution matters.
The order is defined through the priority attribute of the @Test annotation on the test method. So when running the
whole class, execution order will follow the priority attribute. Smaller the priority value, higher will be the
test priority and will be executed first. So if executing manually the priority should be honoured, otherwise tests 2 and 3 will fail.

### Refactoring for better tests
We used pauses(Thread.sleep) to avoid NoSuchElementException when an element cannot be found.
- Replace some of the pauses with WebDriver built-in explicit wait feature through WebDriverWait.
   Implicit vs Explicit waits

This topic falls under synchronization.
One way to handle synchronization is through global implicit wait timeout
driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); 

Sometimes you may need a larger wait interval that implicit wait.
One option is to increase the global implicit timeout and affect all existing tests.
Another option is to mix implicit d explicit wait.

However, the best practices warn against it. Reason being that implicit waits are often implemented on the remote side of the WebDriver system. That means that it is built into the browser WebDriver executable. It could change with the implementation change of the WebDriver executable.
Explicit wait is implemented excusively in the local language bindings.
Therefore, mixing is not recommended because things get more complicated when using Remote-WebDriver, because you could be using both the local and remote sides of the system multiple times-adapted from a quote from Jim Evans; one of the core contributors of Selenium

- Replace duplicated code with reusable methods

### Using Design Patterns
#### Decorator Pattern
First up is the implementation of the Decorator design pattern for IWebElement. This will
enable us to optimize the IWebElement's actions such as typing or clicking. We will use it to attach additional responsibilities to an object dynamically.
The decorator pattern is a flexible alternative to subclassing in order to extend functionality.
The steps involved are:
- Wrap the component with several decorators
- Change the behaviour of its component by adding new functionality before and/or after the component method is called
- The decorator class mirrors the type of component it decorates
- and ofcourse provides an alternative to subclassing for extending behavior

##### Participants
- Component
Defines the interface for objects that can have responsibilities added to them dynamically
- Decorator
Implements the same interface (abstratct class) as the component they will decorate. It has a HAS-A relationship 
with the object it is extending, so the Component has an instance variable that holds a reference to the latter
- ConcreteComponent
The object that is going to be enhanced dynamically. It inherits the Component
- ConcreteDecorator
Decorators can enhance the state of the component. They can add new methods. The new behavior is typically
added before and/or after an existing method in the component

Note: Implementation of this pattern invovles the Composition. Composition over inheritance (oe composite reuse principle) in
OOP is the principle where classes should achieve polymorphic behavios and code reuse by their composition (by containing
an instance of other classes that implement the desired functionality) rather than inheritance from the base or parent class. 
This is especially important where languages like java do not allow multiple inheritance.

##### Implementation
1. Create the Component
    This is the interface for alla element objects
    Implement the pattern for the IWebElement, in order to optimize the wen element's actions such as typing or clicking
2. Create the ConcreteComponent
   Create the class called WebCoreElement that derives from the abstract Element class
   The constructor for the WebCoreElement accepts the WebElement, which is the component. Using composition by having an 
   instance of the native WebElement and keeping a reference to the By locator. The reference to the WebElement is later
   used in creating the WebDriverWait for using Explicit wait condition in the Click method
3. Create Element decorator
4. Create WebElement decorator implementation
5. Create Driver decorator
6. Create WebDriver decorator implementation

#### Creational Pattern
The simple Factory Design pattern is a form of Creational design pattern. In its simplest form, compared to the more involved
Factory method or Abstract Factory design patterns, the WebCoreDriver class as a method that returns different types of
browser objects based on input. It solves the design problems related to basic form of object creation, by centralizing
object creation belonging to a certain type, using a simple design pattern. This will avoid design complexity.


#### Test independence-isolation principle
We have added a test with decorator pattern implementation
and we will add the remaining 2, but the last 2 are all dependent on earlier tests.
According to the test independence-isolation principle, each test should be independent and self-sufficient.

Benefits
- Resilience: If a test is not dependent on another test or 3rd party services that fail to set up or delete the required
data or leave the system in an unexpected state, it will be harder to fail or crash.
- Faster test development: Speed up the long-term maintenance and test development since a long analysis session is not required
- to determine the effects of changing or deleting the test dependency.
- Random run order: Allows running the tests in random order in different groups.
- Parallel testing: This will make tests run independently in parallel and not have to rely on other tests to set up the required data.

There's a need to make the second and third tests independent of test 1.
If a unique email is created every time by using a method such as:
private String generateUniqueEmail(){
    return String.format("%s@berlinspaceflowers.com", UUID.randomUUID().toString());
}
a 128-bit long value such as a GUID generated here is unique for all practical purposes.

Another approach would be to generate the data (orders and users) using internal API services before each test is run.

#### Finally
A start to writing selenium automation tests is without using any complex design patterns and using a simple WebDriver syntax. Such tests tend to be unstable, with poor readability.
An easy refactoring strategy is to use WebDriverWait class to stabilize the tests, demonstrated in the code.
A more involved strategy to promote code reuse by implementing the Decorator design pattern demostrated in the code.
Simple factory design pattern???
Another strategy is Test Independence-Isolation principle suggested but not implemented.

Let's answer a few questions for improving understanding in this topic:
1. Describe the common problems in automated tests using simple WebDriver syntax.
2. Describe the use of the WebDriverWait class to wait for an element to exist on a webpage before using it in any way.
3. Describe a way to wait for an element to be clickable
4. Describe the participants in the Decorator design pattern.
5. Describe the difference between implicit and explicit wait
6. List the benefits of the Test Independence-Isolation principle.

### Version 4: Speeding up tests
Let's attempt to make the existing tests faster and less flaky. 
#### Instrumentation
Instrumenting the Test Code to Find Possible Points for Optimization:
Optimization to make the tests faster and
less flaky ( fails and passes intermittently )
Faster tests will integrate better in CI
- faster turn around of results
- prompt finding of problems in the product.

Targets for this version:
- How to Wait for AJAX in tests?
- Optimize Browser Initialization- Observer Design Pattern
- Isolated Browser Initialization for Each Test
- Black Hole Proxy Approach

Running the instrumented code a number of times will produce a number of key measurements
In most test suites the login tests are simply to validate login and serve as a starting point for tests that
need to login for the rest of the workflow
One optimization is to use authentication cookies to speed up login.
Usually this is implemented by calling an internal web API to generate a value of the authentication cookie.

#### Waiting for All AJAX Requests Completion
jQuery is one of the JavaScript libraries in popular use that eases the work with asynchronous JavaScript.
Using some of its methods will help get the total count of the currently active async requests.
jQuery.active returns 0 if the page is fully loaded.
It will return a positive number if there are still executing requests.
WebDriver's JavascriptExecutor class provides APIs for executing JavaScript code within the current web page.
The method is JavascriptExecutor.executeScript(...)
Note:
- If the SUT website does not use jQuery, this method will not work.
- The wait for ajax method must be used after the load event, because the call will throw UncaughtTypeError if jQuery is not defined.
  - _UncaughtTypeError: Cannot read property 'active' of undefined._

In WebDriver, the Until method in the Wait class is simply a loop that executes
the contents of the code block passed to it until the code returns a true value.

The JavaScript returns 'jQuery.active' count.
In the case of the WaitForAjax method, the exit loop condition is reached when there are 0 active AJAX requests.
If the conditional returns true, all the AJAX requests have finished, and we are ready to move to the next step.
Use the new WaitForAjax method when there's a need to wait for async requests.

The test execution time can also be improved by optimizing the browser initialization.
Also, after the click of the ‘Proceed to checkout’ button there's need for a more special wait logic that will replace the hard pause.
There we need to wait for the entire page to load. Since there aren’t asynchronous requests, the WaitForAjax method 
won’t work in this case. Therefore, we can add one more helper function to our WebDriver decorator: waitUntilPageLoadsCompletely(...)

Optimize Browser Initialization- Observer Design Pattern
The repeating and time-consuming browser initialization ca be optimized as it can take up to 2 seconds per test.
Currently, the browser is set to start and close for each test. One optimization can be to reuse the browser so that
it starts at the beginning of the whole test suite, and closes it after all tests have finished their execution.
This will bring considerable execution time improvement
However, if there's still need to restart the browser before some of the tests, we will need to expand the solution, where
by adding the ability to configure this defaukt behavior per test class or test method.
Let's use the Observer design pattern for this improvement.
Observer Design Pattern
The Observer design pattern defines a one-to-many relation between objects, so that when one object changes its state, all 
dependents are notified and updated automatically. Benefits:
- Strives for loosely coupled designs between objects that interact.
- Allows you to send data to many other objects in a very efficient manner.
- No modification needs to be done to the subject in case you need to add new observers.
- You can add and remove observers at any time.
- The order of Observer notifications is unpredictable.

#### The participants in this pattern are:
- ITestExecutionSubject - Objects use this interface to register as observers and to remove
themselves from being observers.
- MSTestExecutionSubject - The concrete subject always implements the ITestExecutionSubject
interface. In addition, to attach and detach methods, the specific subject implements different
notification methods that are used to update all the subscribed observers whenever the state changes.
- ITestBehaviorObserver - All potential observers need to implement the observer interface. The
methods of this interface are called at different points, when the subject’s state changes.
- OwnerTestBehaviorObserver - A concrete observer can be any class that implements
ITestBehaviorObserver interface. Each observer registers with a specific subject to receive updates.
- BaseTest - The parent class for all test classes in the framework. Uses the TestExecutionSubject to
extend its test execution capabilities via test method/class level defined attributes and concrete observers.

#### Observer Design Pattern Implementation
With our implementation, we will provide an easy way for automation engineers to add additional logic to the current test
execution via class/test level attributes. For example, start and reuse the browser if the previous test hasn't failed.

Here is one way to implement a typical or classic Observer pattern implementation
However, there are other types of implementations through Events and Delegates.

The Subject is the object that contains the state and controls it. So, there is only one subject with a state.
The observers, on the other hand, use the state, even though they don’t own it. There are many observers, and they rely
on the Subject to tell them when its state changes. So, there is a relationship between the one Subject and the many observers.
Why not first look at the ISubject interface?

There are  Attach and Detach methods that are used by the observer classes to associate themselves with the subject. The rest of the
methods are invoked in the various steps of the test execution workflow to notify the observers about changes in the state
of the subject. In order for the subject to be able to notify the observers, they need to implement
the ITestBehaviorObserver interface where all notification methods are defined.

The specific subject knows nothing about the implementations of the observers. It is working with a list of ITestBehaviorObserver objects.
The Attach and Detach methods add and remove observers to/from the collection. In this classic implementation of the observer
design pattern, the observers are responsible to associate themselves with the subject class. Not all the observers need
to implement all the notification methods. In order to support this requirement, we are going to add a base class that is
going to implement the ITestBehaviorObserver interface.