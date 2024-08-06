### Page Object Model Design Pattern
A popular patterns in web automation is the Page Object Model design pattern.
To understand the primary goal of the pattern, first think about what your web automation tests are doing.
They navigate to different web pages and perform actions against various elements.
The page object wraps all elements, actions, and validations happening on a page in a single object.

### Creating the First Page Object Model

With the latest releases of the WebDriver .NET bindings, the way we create
page objects have changed compared to the Java ones. Let's review shortly what was the way of defining page objects before 3.11.0 release.

```java
public class SearchEngineMainPage {}
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
think about what actually makes sense when using C#. In other words:
>C# isn't Java, and therefore the things that work best for Java may not be entirely appropriate for C#.

"In the case of the .NET PageFactory, the implementation was problematic
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
creation and maintenance less verbose simply do not hold up under scrutiny.”

### New Way for Creating Page Objects

If you take a close look at the previous code, you will notice that you didn't
have control over how the elements are found. You couldn't integrate
PageObjectFactory with the WebDriverWait class so that more complex scenarios can
be handled. Lucky for us we can fully reuse the code we have already created
to stabilize and optimize our shopping cart tests. Instead of using the
IWebDriver directly, we can locate the elements through our decorated version
and return decorated web elements as well. Why not quickly recall how our
tests looked without the usage of page objects?

```java
import org.testng.annotations.Test;

@Test
public void CompletePurchaseSuccessfully_WhenNewClient(){}
```
Compare the tets code without the Page Objects.

In order to use the Page Object Model design pattern, we need to refactor
the code and create 5 different page objects for each specific page.
The first page object will be a representation of the main page of the website under test.
Move all the existing code related to this page to a page object class caslled MainPage.
Instead of using the IWebDriver interface directly to locate the
elements use the previously created improved version - the Driver
decorator. When you call the FindElement method, it will return the web
element decorated version Element. Also, we moved the AddRocketToShoppingCart
private method as a publicly accessible method of our first page object.

The next page object that we can create is the one that represents the
shopping cart page, called CartPage.
Again, all elements are represented here as private properties so that they
cannot be accessed outside the page object. The page object exposes only a
few public methods representing the so-called DSL making the tests more
readable.

### Note

Domain Specific languages (DSLs) are languages developed to solve problems in a specific domain, which distinguishes
them from general purpose languages (GPLs). One characteristic of DSLs is that they support a restricted set of
concepts, limited to the domain.

### Page Object Model Usage in Tests
Let's make the tests more readable and concise with the use of page object pattern?

During the class initialize phase, we create the page objects, and we can use
them directly in our tests. As you can see their usage has improved the
readability of our tests significantly. All the low-level WebDriver details and
finding of web elements are hidden from us. Moreover, the maintainability is
also improved significantly, since if some of the locators change over time,
we can go directly in the page object and change it in a single place.
We can apply the same technique for the rest of the tests, so I encourage you
to download the source code and try to refactor the tests by creating the rest
of the page objects.

### Handling Common Page Elements And Actions

As you will see maintainability and readability are closely related. With the
upcoming changes to our tests, we will make them easier to maintain and
read at the same time. Why not start by understanding which are the common page elements?

### Defining Common Page Elements

In the first implemented version of the Page Object Model design pattern,
we decided to group the elements and actions based on the physical web
pages, e.g. home page, cart page, billing page and so on. However, there are
many parts of the pages, where certain elements are repeated with the same
HTML markup and functionality they provide. Let us take a closer look at
our shop's home page.

The first such group is the logo and the search bar. The next one is the main
navigation. After that, the cart icon and current cart price label. We also have
the unique functionality of the page related to the e-shop - displaying all
items that the user can purchase. Lastly, there is one more common section -
the footer.
Why not examine our next page - the cart?

Common sections with the home page are the top section - logo and search
input, the navigation, the cart info, and the footer. The unique part here is
positioned below the cart title, that is one new section that is common only to
this and next cart pages - the breadcrumb.
After we analyzed the sections of our page, we found that some of them are
shared between many pages. We can allow the users to use search on the
pages or click on the menu with the current design of our page objects, this
means that we will have a duplicated logic between our pages, which is in
almost all cases a bad practice since with each copied code the
maintainability costs rise. Moreover, the pages will do more than one thing
which means that we won't follow the Single Responsibility Principle.

### Note
DRY - Do Not Repeat Yourself Principle
A well-implemented and Page Object library has one and only oneway to accomplish any action. This prevents duplicate
implementation of the same SearchItem or FillBillingInfo methods.

### Non-Dry Page Objects
To ease the comparison, we will review what the first two page objects will look like if we just transfer all elements
and logic available on the pages.
