### Configure Browser Behavior via Attribute
Let's optimize the browser initialization flow.
The new attribute is used to configure the tests to use the Chrome browser for all tests in the class.
However, the test method attribute is going to override the value at the class level. So, before the
CompletePurchaseSuccessfully_WhenExistingClient test starts, the browser initialized is going to be
Firefox instead of Chrome.

The BrowserAttribute holds a property of the BrowserConfiguration class which has two properties.
The first one holds the BrowserBehavior enum which controls the behavior of the browser.