### Page Object Model Design Pattern
A popular patterns in web automation is the Page Object Model design pattern.
To understand the primary goal of the pattern, first think about what your web automation tests are doing.
They navigate to different web pages and perform actions against various elements.
The page object wraps all elements, actions, and validations happening on a page in a single object.

### Creating the First Page Object Model

With the latest releases of the WebDriver .NET bindings, the way we create
page objects have changed compared to the Java ones. Let's review shortly what was the way of defining page objects before 3.11.0 release.

```java
public class SearchEngineMainPage
```
Firstly to locate the different web elements FindsBy attribute was used where
you can specify the finding strategy How - find by Id, Class, XPath, etc. and
then assigning the actual locator to the property used. In order for all the web
elements to be populated on page load you had to call the InitElements methods
of the PageFactory class. However, in March 2018 with the release of 3.11.0 of
the WebDriver, this API was marked as obsolete.
Here is a quote from the website of the primary maintainer of the WebDriver
.NET bindings Jim Evans, explaining why they decided to deprecate this part
of the API.

“The .NET implementation of these constructs was created mostly because
some users asked, "Java has it, so why doesn't .NET?" Rather than blindly
copying the Java implementations as was done, it would have been better to
think about what actually makes sense when using C#. In other words, "C#
isn't Java, and therefore the things that work best for Java may not be
entirely appropriate for C#."

In the case of the .NET PageFactory, the implementation was problematic
and cumbersome, as well as not nearly flexible enough for the myriad ways
people wanted to create Page Objects. Additionally, when .NET Core 2.0 was
released, the classes upon which the .NET PageFactory relied were not
included .NET Core 2.0. This meant that to get the PageFactory working
under .NET Core, the project either had to take on a new dependency,
mangle the code with conditional compile directives, or leave it unsupported
in .NET Core. The first approach is a non-starter for the Selenium project's
.NET bindings, the reasons for which should be a subject of its own blog post.
The second approach made the code nearly impossible to properly maintain.
Furthermore, with respect to the PageFactory in particular, there is no
benefit to be gained by identifying elements via an attribute over doing it
directly in runtime code. Claims that the PageFactory made Page Object
creation and maintenance less verbose simply do not hold up under close scrutiny.”

