import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.concurrent.TimeUnit;

public class PurchaseSuccessTests {
    private WebDriver driver;

    @BeforeMethod
    public void testSetup(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void testTeardown(){
        driver.quit();
    }

    @Test
    public void verifyOrderPlacementSuccessWithCouponApplied(){
        driver.navigate().to("http://demos.bellatrix.solutions/");
        WebElement addToCartFalcon9 = driver.findElement(By.cssSelector("*[data-product_id*='28']"));
        addToCartFalcon9.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        WebElement couponCodeTextField = driver.findElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        WebElement applyCouponButton = driver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement messageAlert = driver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        
        WebElement quantityBox = driver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        quantityBox.sendKeys("2");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement updateCart = driver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement totalSpan = driver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assert.assertEquals("114.00€", totalSpan.getText());

        WebElement proceedToCheckout = driver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();
        WebElement billingFirstName = driver.findElement(By.id("billing_first_name"));
        billingFirstName.sendKeys("Anton");
        WebElement bilingLastName = driver.findElement(By.id("billing_last_name"));
        bilingLastName.sendKeys("Angelov");
        WebElement billingCompany = driver.findElement(By.id("billing_company"));
        billingCompany.sendKeys("Space Flowers");
        WebElement billingCountryWrapper = driver.findElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        WebElement billingCountryFilter = driver.findElement(By.className("select2-search__field"));
        billingCountryFilter.sendKeys("Germany");
        WebElement germanyOption  = driver.findElement(By.xpath("//*[contains(text(), 'Germany')]"));
        germanyOption.click();
        WebElement billingAddress1 = driver.findElement(By.id("billing_address_1"));
        billingAddress1.sendKeys("1 Willi Brandt Avenue TearGarten");
        WebElement billingAddress2 = driver.findElement(By.id("billing_address_2"));
        billingAddress2.sendKeys("Lotzowplatz");
        WebElement billingCity = driver.findElement(By.id("billing_city"));
        billingCity.sendKeys("Berlin");
        WebElement billingZip = driver.findElement(By.id("billing_postcode"));
        billingZip.clear();
        billingZip.sendKeys("10115");
        WebElement billingPhone = driver.findElement(By.id("billing_phone"));
        billingPhone.sendKeys("+0048888999281");
        WebElement billingEmail = driver.findElement(By.id("billing_email"));
        billingEmail.sendKeys("info@berlinspaceflowers.com");
        var purchaseEmail = "info@berlinspaceflowers.com";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement recievedMessage = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(recievedMessage.getText(), "Order received");
    }
}
