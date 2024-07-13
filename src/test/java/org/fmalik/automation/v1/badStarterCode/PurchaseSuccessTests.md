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
We have used hardcoded pauses with Thread.sleep(5000) for a 5 second wait. This is one area that 
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

