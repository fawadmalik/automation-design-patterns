### Configure Browser Behavior via Attribute
Let's optimize the browser initialization flow.
The new attribute is used to configure the tests to use the Chrome browser for all tests in the class.
However, the test method attribute is going to override the value at the class level. So, before the
CompletePurchaseSuccessfully_WhenExistingClient test starts, the browser initialized is going to be
Firefox instead of Chrome.

The BrowserAttribute holds a property of the BrowserConfiguration class which has two properties.
The first one holds the BrowserBehavior enum which controls the behavior of the browser.
```java
public enum BrowserBehavior
{
    NotSet = 0,
    ReuseIfStarted = 1,
    RestartEveryTime = 2,
    RestartOnFail = 3,
}
```
This attribute is configured to be available on test class and method level
through the AttributeUsage attribute.
The browser controlling and the extraction of the attributes’ information is
happening in the concrete observer implementation called
BrowserLaunchTestBehaviorObserver. It inherits our base observer
BaseTestBehaviorObserver class.
```java
public class BrowserLaunchTestBehaviorObserver extends BaseTestBehaviorObserver{}
```
Many things are happening in this class so let's tackle then one by one? 
The values from the attributes are extracted via .NET Reflection API when using C#.
```C#
var executionBrowserAttribute = type.GetCustomAttribute<ExecutionBrowserAttribute>(true);
```
In the PreTestInit and PostTestCleanup methods we call the method ShouldRestart to determine whether we should restart
the current browser instance. 
```C#
private bool ShouldRestartBrowser(BrowserConfiguration browserConfiguration)
{
    if (_previousBrowserConfiguration == null)
    {
        return true;
    }
    bool shouldRestartBrowser =
    browserConfiguration.BrowserBehavior == BrowserBehavior.RestartEveryTime ||
        browserConfiguration.Browser == Browser.NotSet;
    return shouldRestartBrowser;
}
```
For java things are a bit different
???????????????????

If the method is called for the first time the _previousBrowserConfiguration variable
is still null, so the method will return true. For the rest of the flow, we check
the values of the BrowserBehavior enum of the current browser configuration.
Also, in the PostTestCleanup method, we check whether the test has failed or not.
If it has failed, we will restart the browser, since it may have been left in an
inconsistent state.

```C#
public override void PostTestCleanup(TestContext context, MemberInfo memberInfo)
{
    if (_currentBrowserConfiguration.BrowserBehavior ==
        BrowserBehavior.RestartOnFail && context.CurrentTestOutcome.Equals(TestOutcome.Failed))
    {
        RestartBrowser();
    }
}
```
The last part of the puzzle is to combine all these classes. This happens in the
BaseTest class.
```java
public class BaseTest{}
```
If the test classes need to add its TestInitialize or TestCleanup logic, they will now
have to override the TestInit and TestCleanup methods in the BaseTest class, instead of using the
TestInitialize and TestCleanup attributes.
There are three important methods that are invoked in the base CoreTestInit
method. First, we invoke the PreTestInit method of the current subject class
which has the responsibility to invoke PreTestInit for each observer. After that,
the TestInit method executes or its overridden version. Finally, all observers'
PostTestInit methods are invoked through the current subject again. The same
flow is valid for the cleanup methods.
_driver = new LoggingDriver(new WebDriver());
new BrowserLaunchTestBehaviorObserver(CurrentTestExecutionSubject, _driver);
In the constructor are created the instances of all desired observers through
passing them the current subject as a parameter. There we moved the creation
of our WebDriver decorator which we pass as an argument to the
BrowserLaunchTestBehaviorObserver.
After the base constructor is executed, the TestContext property is populated
from the MSTest execution engine. It is used to retrieve the currently
executed test’s MemberInfo.

public static void AssemblyCleanup()
{
_driver?.Quit();
}
Since all our tests may reuse the current browser, we need to make sure that
at the end of the test run we will close the browser. We do that in the
AssemblyCleanup method, which is executed after all tests.

#### Isolated Browser Initialization for Each Test

There still is a problem with our new solution. Depending on which test runs first, weird behavior may occur.
For example, if the tests that have logged in the website are executed first,
then the test with anonymous user purchases will fail since we reuse the
browser and the login session. Also, because of this dependency, we broke
the Hermetic test pattern.
One way to handle this situation is to extend our solution to clear the cache
and cookies of the browser in case we reuse it - that way we would have a
clean session for each test.
First, we need to add a new method for deleting all cookies to our WebDriver
decorator.
public override void DeleteAllCookies()
{
_webDriver.Manage().Cookies.DeleteAllCookies();
}
After that we need to change the PreTestInit and PostTestCleanup method of the
BrowserLaunchTestBehaviorObserver class.

```java
public override void PreTestInit(TestContext context, MemberInfo memberInfo) {}
```
In case we are reusing the browser, we delete all cookies, ensuring that we
won't mess up with the state of the next test.
