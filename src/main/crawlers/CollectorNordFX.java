package crawlers;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CollectorNordFX 
extends Collector
implements IGetData
{
	private WebDriver driver;
	
	@Override
	public void getData() {
		setWebsiteSource("https://www.facebook.com/NordFX");
		setFileName("test.txt");
		setOutputFile();
		createOutputFile();
		
        System.setProperty("webdriver.chrome.driver","C:\\Users\\duc28\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
		driver.get("https://www.facebook.com/NordFX");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		WebElement closeButton = driver.findElement(By.cssSelector("div.x1i10hfl.x1ejq31n.xd10rxx.x1sy0etr.x17r0tee.x1ypdohk.xe8uvvx.xdj266r.x11i5rnm.xat24cr.x1mh8g0r.x16tdsg8.x1hl2dhg.xggy1nq.x87ps6o.x1lku1pv.x1a2a7pz.x6s0dn4.xzolkzo.x12go9s9.x1rnf11y.xprq8jg.x972fbf.xcfux6l.x1qhh985.xm0m39n.x9f619.x78zum5.xl56j7k.xexx8yu.x4uap5.x18d9i69.xkhd6sd.x1n2onr6.xc9qbxq.x14qfxbe.x1qhmfi1"));
		closeButton.click();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < 1000 ; ++i) {
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done Scrolling!");
		
		int count = 1;
		WebElement post = null;
		String basePath = "/html/body/div[1]/div/div[1]/div/div[3]/div/div/div[1]/div[1]/div/div/div[4]/div[2]/div/div[2]/div[2]/div[", path;
		String content = "-", dateCreated = "-";
		try {
			do {
				path = basePath + String.valueOf(count) + "]";
	            
	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
	            WebElement divElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)));

	            // Ensure the element is available and scroll into view
	            if (divElement != null) {
	                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", divElement);
	                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'x1i10hfl') and contains(text(), 'Xem thêm')]")));
	                // Click the div (if it is the button)
	                button.click();
	                // Wait for a bit to ensure the scroll is complete
	                try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // 1 second delay to ensure smooth scrolling
	                content = divElement.getText().replace("\"", "\'");
	                dateCreated = divElement.findElement(By.cssSelector("#\\:r4am\\: > span.html-span.xdj266r.x11i5rnm.xat24cr.x1mh8g0r.xexx8yu.x4uap5.x18d9i69.xkhd6sd.x1hl2dhg.x16tdsg8.x1vvkbs > span > a > span")).toString();
	                writeOutputFile("https://www.facebook.com/NordFX", "Facebook post", "-", content, dateCreated, "NordFX", "-", "-", "-");
	                count++;
	            }
			} while (post != null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		driver.quit();
	}
	@Override
	public void webTraverse() {		
	}
	@Override
	public void pageTraverse(String url) {		
	}
}
