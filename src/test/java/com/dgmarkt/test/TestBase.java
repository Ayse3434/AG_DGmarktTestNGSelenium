package com.dgmarkt.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.dgmarkt.utilities.BrowserUtils;
import com.dgmarkt.utilities.ConfigurationReader;
import com.dgmarkt.utilities.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestBase {
    protected WebDriver driver;
    protected Actions actions;
    protected WebDriverWait wait;
    //this class is used for starting and building reports
    protected ExtentReports report;
    //this class is used to create HTML report file
    protected ExtentHtmlReporter htmlReporter;

    //this will define a test, enable adding logs, authors and test steps
    protected ExtentTest extentLogger;

    @BeforeTest
    public void setUpTest() {
        //initialize the class
        report = new ExtentReports();

        //create a report path
        String projectPath = System.getProperty("user.dir");
        String path = projectPath + "/test-output/report.html";
        // to take dynamic report name
//        String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//        String path = projectPath + "/test-output/report"+date+".html";

        //initialize the html report with the report path
        htmlReporter = new ExtentHtmlReporter(path);
        //attach the html report to the report object
        report.attachReporter(htmlReporter);
        //title in report
        htmlReporter.config().setReportName("Audit DevBook Test");

        //set environment information
        report.setSystemInfo("Environment", "DevBook Production");
        report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
        report.setSystemInfo("OS", System.getProperty("os.name"));
        report.setSystemInfo("Test Engineer", "FT");
    }

    @AfterTest
    public void tearDownTest() {
        //this is when the report is created
        report.flush();
    }


    @BeforeMethod
    public void setUp() {
        driver = Driver.get();
        driver.manage().window().maximize();
        driver.get(ConfigurationReader.get("url"));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        actions = new Actions(driver);
        wait = new WebDriverWait(Driver.get(), 15);

    }

    @AfterMethod
    public void tearDown(ITestResult result) throws InterruptedException, IOException {

        if (result.getStatus() == ITestResult.FAILURE) {
            //Record the name of the failed test
            extentLogger.fail(result.getName());
            //Take the screenshot and return the location of screenshot
            String screenShotPath = BrowserUtils.getScreenshot(result.getName());
            //Add the screenshot to the report
            extentLogger.addScreenCaptureFromPath(screenShotPath);
            //capture the exception and put inside the report
            extentLogger.fail(result.getThrowable());
        }

        Thread.sleep(2000);
        //driver.close();
        Driver.closeDriver();
    }
}
