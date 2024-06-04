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
    public void verifyTestConfigurationSetup(){
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
    }
}
