package me.back.gg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;

import me.back.gg.utils.Wrapper;

public class Main {
	public static Gson gson = new Gson();
	public static List<String> streamersList = new ArrayList<String>();
	public static List<String> onlineList = new ArrayList<String>();
    public static List<String> excludingList = new ArrayList<String>();
    public static List<String> alreadyWatched = new ArrayList<String>();
    public static int currentStreamer;

     public static String username = System.getProperty("user.name");
     
     public static ChromeOptions options = new ChromeOptions();
     public static WebDriverWait wait;
     public static WebDriver driver;
     
	public static void main(String[] args) throws Exception {
		Wrapper.init( );
		Wrapper.readID( );
		Wrapper.readOAuth( );
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		loadStreamers();
		streamersList.forEach(Wrapper::getStreamerInfo);
		onlineList.forEach(e->{
			System.out.println(e + " is Online");
		});
		
		
		options.addArguments("user-data-dir=C:\\Users\\" + username + "\\AppData\\Local\\Google\\Chrome\\User Data");
		options.addArguments("--log-level=3");
		options.addArguments("--output=/dev/null","--disable-logging","--silent");
		
		
		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 20);
		currentStreamer = 0;
		System.out.println("Claiming unclaimed drops...");
		claimDrop();
		Thread.sleep(4000);
		checkStream();

	}
	
	public static Gson getGson() {
		return gson;
	}
	
	public static void loadStreamers() {
		try {
		Path currentPath = Paths.get("");
		String currentDir = currentPath.toAbsolutePath().toString() + "\\streamers.txt";   
		List<String> streamers = Files.readAllLines(Paths.get(currentDir));
		System.out.println("Streamer list: " + streamers);
		streamersList.addAll(streamers);
		} catch (Exception aa) {}
	}
	
	public static void checkStream() throws Exception {
		int percentage = 0;
		
		while(onlineList.isEmpty()) {
		    System.out.println("All Offline... Waiting 5 minutes");
            Thread.sleep(300000);
    		streamersList.forEach(Wrapper::getStreamerInfo);
    		return;
		}
	
		if(alreadyWatched.contains(onlineList.get(currentStreamer))) {
			currentStreamer++;
		}
		
		if(onlineList.size()-1 < currentStreamer ){
			onlineList.clear();
			loadStreamers();
			streamersList.forEach(Wrapper::getStreamerInfo);
			onlineList.forEach(System.out::println);
		}
		
		
		
   	    System.out.println("Currently Watching: " + onlineList.get(currentStreamer).toString());

		driver.navigate().to("https://twitch.tv/" + onlineList.get(currentStreamer).toString());
		wait.until(e-> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
		((JavascriptExecutor) driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(0));
		Thread.sleep(30000);
		driver.switchTo().window(tabs.get(1));
		driver.navigate().to("https://www.twitch.tv/drops/inventory");
		Thread.sleep(5000);
		WebElement progressBar = wait.until(ExpectedConditions.visibilityOf(driver.findElements(By.className("tw-progress-bar")).get(0)));
		/***
		 * You could get "Exception in thread "main" java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0"
		 * if the classname "tw-progress-bar" doesn't exist.
		 * For fix this issue you need to put this function in a loop and then continue the loop if progressBar == null.
		 */
		
		percentage = Integer.parseInt(progressBar.getAttribute("aria-valuenow"));
		System.out.println("[1] Start Percentage: " + percentage);
		
		while(percentage != 100 || progressBar != null) {
			driver.switchTo().window(tabs.get(0));
			Thread.sleep(2000);
			driver.switchTo().window(tabs.get(1));
			driver.navigate().refresh();
			progressBar = wait.until(ExpectedConditions.visibilityOf(driver.findElements(By.className("tw-progress-bar")).get(0)));
			percentage = Integer.parseInt(progressBar.getAttribute("aria-valuenow"));
			System.out.println("[2] Refresh Percentage: " + percentage);
			Thread.sleep(2000);
			driver.switchTo().window(tabs.get(0));
			Thread.sleep(300000);
		}
		System.out.println("Progress bar null, Trying to claim Drops...");
		driver.switchTo().window(tabs.get(1));
		List<WebElement> listButtons = driver.findElements(By.xpath("//button[@data-test-selector ='DropsCampaignInProgressRewardPresentation-claim-button']"));
		 for(int i = 0 ; i < listButtons.size() ; i++) {
        	 listButtons.get(i).click();
        	 System.out.println("Claiming Drops...");
        	 Thread.sleep(1500);
        	 System.out.println("Claimed.");
         }
		 
    	 alreadyWatched.add(onlineList.get(currentStreamer));
    	 System.out.println(onlineList.get(currentStreamer) + "Added to the Already Watched list: " + alreadyWatched );
    	 System.out.println("Switch next streamer...");
    	 Thread.sleep(1500);
    	 currentStreamer++;
    	 again();
	}
	
	
	public static void claimDrop() throws InterruptedException { 
	 driver.navigate().to("https://www.twitch.tv/drops/inventory");
     Thread.sleep(3000);
    // WebElement buttons = driver.findElementByXPath("//button[@data-test-selector ='DropsCampaignInProgressRewardPresentation-claim-button']");
    // buttons.click();
     List<WebElement> listButtons = driver.findElements(By.xpath("//button[@data-test-selector ='DropsCampaignInProgressRewardPresentation-claim-button']"));
     try {
    	 for(int i = 0 ; i < listButtons.size() ; i++){
        	 listButtons.get(i).click();
        	 Thread.sleep(1500);
         }
    	} catch(Exception e){ }
	}
	
	public static void again() throws Exception {
		try {
		System.out.println("Claiming unclaimed drops...");
		claimDrop();
		Thread.sleep(3000);
		checkStream();
		} catch (InterruptedException ee) {}
	}
	

}
