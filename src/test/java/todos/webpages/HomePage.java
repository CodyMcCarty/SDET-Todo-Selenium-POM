package todos.webpages;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends LoadableComponent<HomePage> {

  private static String PAGE_URL = "http://localhost:3000/";
  private WebDriver driver;
  private WebDriverWait wait;
  private Actions action;

  @FindBy(id = "add-todo")
  private WebElement todoInput;

  @FindBy(css = "#todo-list > li")
  private List<WebElement> todos;

  @FindBy(css = "#todo-list > li:nth-child(1)")
  private WebElement firstTodo;


  public HomePage(WebDriver driver) {
    this.driver = driver;
    driver.get(PAGE_URL);
    PageFactory.initElements(driver, this);
    wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    action = new Actions(driver);
    isLoaded();
  }

  public WebElement getTodoInput() {
    waitForElement(todoInput);
    return todoInput;
  }

  public WebElement getTodo(String contains) {
    try {
      waitForElement(todos);
      return todos.stream() // FIXME: add a wait
          .filter((e) -> e.getText().contains(contains))
          .findFirst()
          .orElseThrow(
              () -> new NoSuchElementException("Expected TODO: " + contains + " Was not found"));
    } catch (Exception e) {
      return driver.findElements(By.id("todo-list"))
          .get(0);
    }
  }


  public WebElement getFirstTodo() {
    waitForElement(firstTodo);
    return firstTodo;
  }

  public WebElement findFirstTodo() {
    waitForElement(firstTodo);
    return todos.stream().findFirst().get();
  }

  public List<WebElement> getUpdatedTodos() {
    waitForElement(todos);
    return todos;
  }

  public void waitForElement(WebElement element) {
    try {
      wait.until(ExpectedConditions.visibilityOf(element));
    } catch (Exception e) {
      driver.navigate().refresh();
      wait.until(ExpectedConditions.visibilityOf(element));
    }
  }

  public void waitForElement(List<WebElement> elements) {
    try {
      wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    } catch (Exception e) {
      driver.navigate().refresh();
      wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }
  }

  public void waitForElement() {
    try {
      wait.until(ExpectedConditions.visibilityOf(todoInput));
    } catch (Exception e) {
      driver.navigate().refresh();
      wait.until(ExpectedConditions.visibilityOf(todoInput));
    }
  }

  public String generateTodo() {
    return "Todo." + new SimpleDateFormat("mm.ss.SSSZ").format(new java.util.Date());
  }

  public void generateStarterTodos(int numOfTodos) {
    submitText(todoInput, generateTodo());
    List<WebElement> todos = new ArrayList<>();
    try {
      todos = getUpdatedTodos();
    } catch (Exception e) {
    }
    int howManyTodosToAdd = numOfTodos - todos.size();
    if (howManyTodosToAdd <= 0) {
      return;
    }

    for (int i = 0; i < howManyTodosToAdd; i++) {
      todoInput = getTodoInput();
      String todo = generateTodo();
      submitText(todoInput, todo);
    }
    driver.navigate().refresh();
  }

  public void submitText(WebElement element, String text) {
    waitForElement();
    element.clear();
    element.sendKeys(text);
    element.sendKeys(Keys.RETURN);
  }

  public void deleteTodo(WebElement todo) {
    waitForElement(firstTodo);
    action.moveToElement(todo).moveToElement(
            driver.findElement(By.cssSelector("#todo-list > li:nth-child(1) > div > button"))).click()
        .build().perform();
  }

  public void delTodo(int index) {
    waitForElement(todos);
    String css = "#todo-list > li:nth-child(" + index + ")";
    String cssButton = "#todo-list > li:nth-child(" + index + ") > div > button";
    WebElement todo = driver.findElement(By.cssSelector(css));
    action.moveToElement(todo).moveToElement(driver.findElement(By.cssSelector(cssButton))).click()
        .build().perform();
  }


  public void deleteTodos() {
    driver.navigate().refresh();
    waitForElement(firstTodo);
    List<WebElement> todos = getUpdatedTodos();
    for (int i = 0; i < todos.size() + 5; i++) {
      try {
      getUpdatedTodos();
      WebElement todo = findFirstTodo();
      deleteTodo(todo);
      driver.navigate().refresh();
      } catch (Exception e) {
        break;
      }
    }
  }

  public void markFirstTodoCompleted() {
    WebElement todo = firstTodo;
    if (todo.getAttribute("class").equals("todo completed")) {
      action.moveToElement(todo).moveToElement(
              driver.findElement(By.cssSelector("#todo-list > li:nth-child(1) > div > .toggle")))
          .click().build().perform();
    }
    action.moveToElement(todo).moveToElement(
            driver.findElement(By.cssSelector("#todo-list > li:nth-child(1) > div > .toggle"))).click()
        .build().perform();
  }

  public List<String> stringify(List<WebElement> todos) {
    waitForElement(firstTodo);
    List<String> todosExpected = new ArrayList<>();
    for (int i = 0; i < todos.size(); i++) {
      todosExpected.add(todos.get(i).getText());
    }
    return todosExpected;
  }

  @Override
  protected void load() {
  }

  @Override
  protected void isLoaded() throws Error {
    waitForElement(todoInput);
  }


}
