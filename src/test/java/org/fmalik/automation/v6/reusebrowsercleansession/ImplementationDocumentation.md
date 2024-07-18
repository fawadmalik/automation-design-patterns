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
