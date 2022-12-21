package test.flow.project;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import test.flow.project.step.StepFlow;

public class ProjectFlow {

    private StepFlow stepFlow;
    private WebDriver driver;
    private WebDriverWait wait;
    public ProjectFlow(WebDriver driver, WebDriverWait wait) {
        this.stepFlow = new StepFlow();
        this.driver = driver;
        this.wait = wait;
    }

    public void createProject() {
        stepFlow.createStep();


        //TODO:
        Assert.assertTrue(false);
    };
}
