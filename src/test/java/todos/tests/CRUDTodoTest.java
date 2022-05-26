package todos.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import todos.webpages.HomePage;

public class CRUDTodoTest {

  WebDriver driver;
  HomePage home;

  @AfterClass
  public static void tearDownClass() {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    HomePage home = new HomePage(driver);
    home.deleteTodos();
    driver.quit();
  }

  @Before
  public void setup() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    home = new HomePage(driver);
    home.generateStarterTodos(5);
  }

  @After
  public void onTearDown() {
    driver.close();
  }

  /* 1) GIVEN I am at the todoPage
WHEN I add text then press enter
THEN It is added to the list */
  @Test
  public void addTodoUpdatesTodoListTest() {
    WebElement todoInput = home.getTodoInput();
    String todoExpected = home.generateTodo();

    home.submitText(todoInput, todoExpected);
    String todoActual = home.getTodo(todoExpected).getText();

    Assert.assertEquals(todoExpected, todoActual);
  }

  /*2) GIVEN I am at the todoPage
  AND there is an item on the list
  WHEN I hover over the item
  AND X out on the item
  THEN the item is removed */
  @Test
  public void removeTodoUpdatesListTest() {
    WebElement todo = home.findFirstTodo();
    String unexpected = todo.getText();

    home.deleteTodo(todo);

    String actual = home.getTodo(unexpected).getText();
    Assert.assertNotEquals(unexpected, actual);
  }

  /*3) GIVEN I am at the todoPage
  WHEN I click on the circle next to the item
  THEN the item is crossed out
  AND the item is greyed out */
  @Test
  public void checkingTodoMarksCompletedTest() {
    WebElement todo = home.findFirstTodo();
    String expectedName = todo.getText();

    home.markFirstTodoCompleted();

    WebElement todoActual = home.getFirstTodo();
    Assert.assertEquals(expectedName, todoActual.getText());
    Assert.assertEquals("todo completed", todoActual.getAttribute("class"));
  }

  /*4) GIVEN I am at the todoPage
  WHEN I hover over an item
  AND I click on the X next to the item
  THEN the list will collapse
  AND not reorder the list
  END */
  @Test
  public void delTodoDoesNotReorderListTest() {
    List<String> todosExpected = home.stringify(home.getUpdatedTodos());

    todosExpected.remove(2);
    home.delTodo(3);

    List<String> todosActual = home.stringify(home.getUpdatedTodos());
    Assert.assertEquals(todosExpected, todosActual);
  }
}
