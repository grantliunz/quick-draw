package nz.ac.auckland.se206.controllers;

import static nz.ac.auckland.se206.App.loadFxml;
import static nz.ac.auckland.se206.ml.DoodlePrediction.printPredictions;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.dict.DictionaryLookup;
import nz.ac.auckland.se206.dict.WordNotFoundException;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.scenes.SceneManager;
import nz.ac.auckland.se206.scenes.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.user.Data.Result;
import nz.ac.auckland.se206.user.User;
import nz.ac.auckland.se206.words.CategorySelector;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class CanvasController {

  public enum GameMode {
    CLASSIC,
    ZEN,
    HIDDEN
  }

  public static int MAX_TIME;

  public static String[] PEN_COLORS =
      new String[] {
        "white",
        "black",
        "lightgray",
        "gray",
        "brown",
        "red",
        "orange",
        "yellow",
        "lime",
        "green",
        "cyan",
        "blue",
        "violet",
        "indigo",
        "magenta",
        "pink",
      };

  private User user;
  private Difficulty accuracyDiffculty;
  private Difficulty wordsDiffculty;
  private Difficulty timeDiffculty;
  private Difficulty confidenceDiffculty;
  private int winningNum;

  @FXML private Canvas canvas;
  @FXML private Label wordLabel;
  @FXML private Label timerLabel;
  @FXML private Button startDrawButton;
  @FXML private ListView<String> predictionList;

  @FXML private Label resultLabel;
  @FXML private Button brushButton;
  @FXML private Button eraserButton;
  @FXML private Button clearButton;
  @FXML private Button newGameButton;

  @FXML private Button menuButton;
  @FXML private Button saveImageButton;
  @FXML private GridPane colorGrid;

  private Color currentColor;

  @FXML private ImageView fire;
  @FXML private ImageView hotFace;
  @FXML private ImageView coldFace;

  @FXML private Button hintButton;

  @FXML private Label gamemodeLabel;

  @FXML private Label definitionLabel;

  private GraphicsContext graphic;
  private DoodlePrediction model;
  private Timer timer;
  private boolean drawn;
  private int remainingTime;
  private Result result;

  private String randomWord;
  // mouse coordinates
  private double currentX;
  private double currentY;
  private int wordPos;
  private int confLevel;
  private static MediaPlayer player;

  private TextToSpeech tts = new TextToSpeech();

  private GameMode gameMode;

  /**
   * sets the user and the settings for the game
   *
   * @param passedUser user that is playing game
   * @throws Exception in cases retrieving sends an error
   */
  public void setUser(User passedUser) throws Exception {
    // sets the user picked in the menu
    user = passedUser;
    // using the difficulty array values set at the start
    accuracyDiffculty = user.getDifficulty().get(0);
    // calls the required method to implement settings
    setAccuracy(accuracyDiffculty);
    wordsDiffculty = user.getDifficulty().get(1);
    displayWord();
    timeDiffculty = user.getDifficulty().get(2);
    setTimerDiff(timeDiffculty);
    confidenceDiffculty = user.getDifficulty().get(3);
    setConf(confidenceDiffculty);
  }

  private void displayWord() throws Exception {
    CategorySelector selector = new CategorySelector();
    randomWord = selector.getRandomWord(wordsDiffculty);
    wordLabel.setText(randomWord);
    wordLabel.autosize();
  }

  private void setConf(Difficulty difficulty) {
    // depending on the difficulty confidence level is set
    if (difficulty == Difficulty.Ma) {
      confLevel = 50;
    } else if (difficulty == Difficulty.H) {
      confLevel = 25;
    } else if (difficulty == Difficulty.M) {
      confLevel = 10;
    } else {
      confLevel = 1;
    }
  }

  private void setTimerDiff(Difficulty difficulty) {
    // depending on difficulty timer value is set
    if (difficulty == Difficulty.Ma) {
      MAX_TIME = 15;
    } else if (difficulty == Difficulty.H) {
      MAX_TIME = 30;
    } else if (difficulty == Difficulty.M) {
      MAX_TIME = 45;
    } else {
      MAX_TIME = 60;
    }
    // sets the timer value for the user to see
    timerLabel.setText(Integer.toString(MAX_TIME));
    remainingTime = MAX_TIME;
  }

  public void setGameMode(GameMode gameMode) {
    this.gameMode = gameMode;
  }

  /**
   * this methods updates the result and all other stats after game is finished
   *
   * @param result enum for whether won or lost
   * @throws StreamReadException thrown if writing has a problem
   * @throws DatabindException thrown if writing has a problem
   * @throws IOException thrown if writing has a problem
   */
  public void updateResult(Result result)
      throws StreamReadException, DatabindException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    // List of users read from json file
    List<User> userList =
        mapper.readValue(new File(".profiles/users.json"), new TypeReference<List<User>>() {});
    User temp = null;
    int count = 0;
    // Accesses the current user
    for (User u : userList) {
      if (user.getName().equals(u.getName())) {
        temp = u;
        break;
      }
      count++;
    }

    // Updates the played words of user
    user.addData(randomWord, result, MAX_TIME - remainingTime, Difficulty.E, gameMode);
    userList
        .get(count)
        .addData(randomWord, result, MAX_TIME - remainingTime, Difficulty.E, gameMode);

    // Updates the score of the user
    if (result == Result.WIN) {
      int gamesWon = temp.getGamesWon() + 1;
      int score = userList.get(count).getScore();
      userList.get(count).setScore(getScore() + score);
      userList.get(count).setGamesWon(gamesWon);
      mapper.writeValue(new File(".profiles/users.json"), userList);
    } else {
      int gamesLost = temp.getGamesLost() + 1;
      userList.get(count).setGamesLost(gamesLost);
      mapper.writeValue(new File(".profiles/users.json"), userList);
    }
  }

  private int getScore() {
    if (gameMode == GameMode.CLASSIC) {
      return getSpecScore(accuracyDiffculty)
          + getSpecScore(timeDiffculty)
          + getSpecScore(wordsDiffculty)
          + getSpecScore(confidenceDiffculty);
    } else if (gameMode == GameMode.ZEN) {
      return getSpecScore(wordsDiffculty)
          + getSpecScore(confidenceDiffculty)
          + getSpecScore(accuracyDiffculty);
    } else {
      return getSpecScore(accuracyDiffculty)
          + getSpecScore(timeDiffculty)
          + getSpecScore(wordsDiffculty)
          + getSpecScore(confidenceDiffculty);
    }
  }

  private int getSpecScore(Difficulty difficulty) {
    if (difficulty == Difficulty.Ma) {
      return 4;
    } else if (difficulty == Difficulty.H) {
      return 3;
    } else if (difficulty == Difficulty.M) {
      return 2;
    } else {
      return 1;
    }
  }

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws Exception {
    graphic = canvas.getGraphicsContext2D();
    model = new DoodlePrediction();
    wordPos = 0;
    // Set up timer

    // Hide end game buttons
    newGameButton.setVisible(false);
    menuButton.setVisible(false);
    saveImageButton.setVisible(false);
    predictionList.setVisible(false);
    colorGrid.setVisible(false);
    hintButton.setVisible(false);
    definitionLabel.setVisible(false);
    gamemodeLabel.setText("Classic");
    resultLabel.setText("");
    fire.setVisible(false);
    hotFace.setVisible(false);
    coldFace.setVisible(false);

    createPenColors();
    currentColor = Color.BLACK;
  }

  public void setTimerLabel(String string) {
    this.timerLabel.setText(string);
  }

  /** this is for tts implementation where word prompted is said */
  public void speakWord() {
    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Uses Text to speech to speak given lines
            tts.speak("Draw", randomWord);
            return null;
          }
        };

    // Delegates speaking task to new thread to prevent blocking of GUI
    Thread thread = new Thread(task);
    thread.start();
  }

  /**
   * This method is for speaking a certain string that you provide
   *
   * @param toSpeak
   */
  public void speak(String toSpeak) {
    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Uses Text to speech to speak given lines
            tts.speak(toSpeak);
            return null;
          }
        };
    // Delegates speaking task to new thread to prevent blocking of GUI
    Thread thread = new Thread(task);
    thread.start();
  }

  /**
   * Upon pressing the start drawing button, the word to draw will be read out (unless hidden) and
   * tools will be enabled + timer + predictions
   */
  @FXML
  private void onStartDraw() {
    // Enables drawing controls
    brushButton.setDisable(false);
    eraserButton.setDisable(false);
    clearButton.setDisable(false);
    if (gameMode == GameMode.ZEN) {
      saveImageButton.setVisible(true);
    } else if (gameMode == GameMode.HIDDEN) {
      hintButton.setVisible(true);
    }
    onSwitchToBrush();

    if (gameMode != GameMode.HIDDEN) {
      speakWord();
    }

    // given game mode is zen there is no timer

    if (gameMode != GameMode.ZEN) {
      setTimer();
    } else {
      timer = new Timer();
      timer.scheduleAtFixedRate(
          new TimerTask() {
            public void run() {

              try {
                if (result != Result.WIN) {
                  populatePredictionList();
                }
              } catch (TranslateException e) {
                throw new RuntimeException(e);
              }
            }
          },
          1000,
          1000);
    }
    // lets the user draw
    startDrawButton.setVisible(false);
  }

  /** sets the corresponding labels to zen mode requirements */
  public void startZen() {
    timerLabel.setText("");
    newGameButton.setVisible(true);
    menuButton.setVisible(true);
    colorGrid.setVisible(true);
    gamemodeLabel.setText("Zen Mode");
    timerLabel.setText("∞");
  }

  @FXML
  private void onSwitchToBrush() {
    // Brush size
    final double size = 5.0;

    canvas.setCursor(new ImageCursor(new Image("images/colorPencil.png"), 0, 1000));

    // This is the colour of the brush.
    graphic.setFill(currentColor);

    canvas.setOnMousePressed(
        e -> {
          predictionList.setVisible(true);
          currentX = e.getX();
          currentY = e.getY();
          graphic.fillOval(e.getX() - size / 2, e.getY() - size / 2, size, size);
          drawn = true;
        });

    canvas.setOnMouseDragged(
        e -> {
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;
          // This is the colour of the brush.
          graphic.setFill(currentColor);
          graphic.setLineWidth(size);

          // Create a line that goes from the point (currentX, currentY) and (x,y)
          graphic.setStroke(currentColor);

          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
          drawn = true;
        });
  }

  @FXML
  private void onSwitchToEraser() {
    // brush size
    final double size = 10.0;

    canvas.setCursor(new ImageCursor(new Image("images/colorEraser.png"), 50, 250));

    canvas.setOnMousePressed(
        e -> {
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.clearRect(x, y, size, size);
        });

    canvas.setOnMouseDragged(
        e -> {
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          graphic.clearRect(x, y, size, size);
        });
  }

  private void finishGame() {
    timer.cancel();
    // Stop timer
    canvas.setOnMouseDragged(null);
    canvas.setOnMousePressed(null);
    canvas.setCursor(Cursor.DEFAULT);

    if (gameMode == GameMode.HIDDEN) {
      wordLabel.setText(randomWord);
      hintButton.setVisible(false);
    }

    // Disable drawing buttons and show end game buttons
    brushButton.setDisable(true);
    eraserButton.setDisable(true);
    clearButton.setDisable(true);
    newGameButton.setVisible(true);
    menuButton.setVisible(true);
    saveImageButton.setVisible(true);
    wordPos = 0;
  }

  @FXML
  private void onNewGame(ActionEvent event) throws Exception, WordNotFoundException {
    SceneManager.addUi(SceneManager.AppUi.CANVAS, loadFxml("canvas"));
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    drawn = false;
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
    // controller set so that methods are accessible
    CanvasController controller =
        (CanvasController) SceneManager.getUiController(SceneManager.AppUi.CANVAS);
    // sets user for canvas and the gamemode
    controller.setUser(user);
    controller.setGameMode(this.gameMode);
    // depending on game mode picked game functionality is different
    if (gameMode == GameMode.ZEN) {
      controller.startZen();

    } else if (gameMode == GameMode.HIDDEN) {
      controller.startHidden();
    }
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    drawn = false;
    fire.setVisible(false);
  }

  /**
   * This method executes when the user clicks the "Predict" button. It gets the current drawing,
   * queries the DL model and prints on the console the top 5 predictions of the DL model and the
   * elapsed time of the prediction in milliseconds.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  @FXML
  private void onPredict() throws TranslateException {
    System.out.println("==== PREDICTION  ====");
    System.out.println("Top 5 predictions");

    final long start = System.currentTimeMillis();
    printPredictions(model.getPredictions(getCurrentSnapshot(), 5));

    System.out.println("prediction performed in " + (System.currentTimeMillis() - start) + " ms");
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /**
   * Save the current snapshot on a bitmap file.
   *
   * @return The file of the saved image.
   * @throws IOException If the image cannot be saved.
   */
  private File saveCurrentSnapshotOnFile() throws IOException {
    // You can change the location as you see fit.
    final File tmpFolder = new File("tmp");

    if (!tmpFolder.exists()) {
      tmpFolder.mkdir();
    }

    // We save the image to a file in the tmp folder.
    final File imageToClassify =
        new File(tmpFolder.getName() + "/snapshot" + System.currentTimeMillis() + ".bmp");

    // Save the image to a file.
    ImageIO.write(getCurrentSnapshot(), "bmp", imageToClassify);

    return imageToClassify;
  }

  @FXML
  private void onSaveImage() throws IOException {

    // Finds directory and creates it if it doesnt exist
    FileChooser fileChooser = new FileChooser();
    final File tmpFolder = new File("tmp");
    if (!tmpFolder.exists()) {
      tmpFolder.mkdir();
    }

    // Opens for user to save image
    fileChooser.setInitialDirectory(tmpFolder);
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("BMP Files (*.bmp)", "*.bmp"));
    File file = fileChooser.showSaveDialog(null);
    if (file != null) {
      ImageIO.write(getCurrentSnapshot(), "bmp", file);
    }
  }

  @FXML
  private void onDisplayMenu(ActionEvent event) throws IOException {
    // Updates UI back to the main menu
    StatsController statsController = (StatsController) SceneManager.getUiController(AppUi.STATS);
    statsController.updateStats(user);
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();

    MenuController controller =
        (MenuController) SceneManager.getUiController(SceneManager.AppUi.MENU);
    controller.updateUser(user);
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.MENU));
  }

  /**
   * This method takes the top 10 predictions and displays them If the word is in the top x
   * depending on gamemode, you automatically win and a win sound plays If the word is close to
   * being in the top 10 its relative position will be displayed by a fire which varies in size
   *
   * @throws TranslateException
   */
  private void populatePredictionList() throws TranslateException {
    Platform.runLater(
        () -> {
          // Clear previous list
          predictionList.getItems().clear();

          int i = 1;

          try {
            // Loop through top 10 predictions
            if (drawn) {
              boolean isPredicted = false;
              for (final Classifications.Classification classification :
                  model.getPredictions(getCurrentSnapshot(), 40)) {

                String prediction = classification.getClassName().replace("_", " ");
                // Top 3 predictions are displayed in largest text
                if (i <= 10) {
                  predictionList.getItems().add(prediction);
                }
                if (i <= winningNum) {
                  // Check if prediction is correct
                  if (randomWord.equals(prediction)
                      && predictionList.isVisible()
                      && confLevel <= classification.getProbability() * 100) {
                    playSound("/sounds/mixkit-cartoon-positive-sound-2255.mp3");
                    resultLabel.setText("You win!");
                    isPredicted = true;
                    fire.setVisible(true);
                    fire.setFitWidth((10 - i) * 10);
                    result = Result.WIN;
                    try {
                      if (gameMode != GameMode.ZEN) {
                        updateResult(Result.WIN);
                      }
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    if (gameMode != GameMode.ZEN) {
                      finishGame();
                    }
                  } else if ((i > 10) && (!randomWord.equals(prediction))) {
                    continue;
                  }
                } else {
                  if (randomWord.equals(prediction) && predictionList.isVisible()) {
                    isPredicted = true;
                    if (i < wordPos) { // if the word position has increased, display the hot face
                      hotFace.setVisible(true);
                      coldFace.setVisible(false);

                    } else if (i > wordPos) { // if word position has decreased then display cold
                      coldFace.setVisible(true);
                      hotFace.setVisible(false);
                    }
                    fire.setVisible(true);
                    fire.setFitWidth((40 - i) * 10);
                  }
                }
                i++;
              }
              wordPos = i;
              if (!isPredicted) {
                fire.setVisible(false);
                hotFace.setVisible(false);
                coldFace.setVisible(false);
              }
            } else {
              fire.setVisible(false);
              hotFace.setVisible(false);
              coldFace.setVisible(false);
            }
          } catch (TranslateException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void setAccuracy(Difficulty difficulty) {
    // depending on user difficulty winning predicition becomes stricter
    if (difficulty == Difficulty.H) {
      winningNum = 1;
    } else if (difficulty == Difficulty.M) {
      winningNum = 2;
    } else if (difficulty == Difficulty.E) {
      winningNum = 3;
    }
  }

  /**
   * searches for the defintion of the random word chosen
   *
   * @throws WordNotFoundException if word is not found
   * @throws IOException file error is thrown
   */
  public void startHidden() throws WordNotFoundException, IOException {
    wordLabel.setText("????");
    definitionLabel.setVisible(true);
    definitionLabel.setWrapText(true);
    startDrawButton.setDisable(true);
    gamemodeLabel.setText("Hidden Mode");
    definitionLabel.setText("Getting word definition...");
    definitionLabel.setTextOverrun(OverrunStyle.CLIP);
    // add padding to definitonlabel
    definitionLabel.setPadding(new Insets(0, 0, 0, 10));

    // runs a background task for no freezing
    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {

          @Override
          protected Void call() throws Exception {
            String definition;
            // finds the defintion of word in background task
            while (true) {
              try {
                definition = DictionaryLookup.searchWordInfo(randomWord);
                break;
              } catch (WordNotFoundException e) {
                CategorySelector selector = new CategorySelector();
                randomWord = selector.getRandomWord(Difficulty.E);
              }
            }
            String finalDefinition = definition;
            // when defintion found word is shown on gui
            Platform.runLater(
                () -> {
                  definitionLabel.setText(finalDefinition);
                  startDrawButton.setDisable(false);
                });
            ;

            return null;
          }
        };
    // starts the thread
    Thread thread = new Thread(task);
    thread.start();
  }

  private void createPenColors() {
    colorGrid.setHgap(2);
    colorGrid.setVgap(2);
    for (int i = 0; i < PEN_COLORS.length; i++) {
      String color = PEN_COLORS[i];
      Button button = new Button();
      button.setStyle("-fx-background-color: " + color);
      button.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
      button.setOnAction(
          event -> {
            currentColor = Color.web(color);
            onSwitchToBrush();
          });
      colorGrid.add(button, i % 2, i / 2);
    }
  }

  @FXML
  private void onShowHint() {
    hintButton.setVisible(false);
    wordLabel.setText(randomWord.charAt(0) + " _".repeat(randomWord.length() - 1));
  }

  private void setTimer() {
    /*
     * Adapted from:
     *
     * @author mr mcwolf <https://stackoverflow.com/users/4815321/mr-mcwolf>
     *
     * @copyright 2017 mr mcwolf
     *
     * @license CC BY-SA 3.0
     *
     * @see {@link https://stackoverflow.com/a/47655973/1248177|JavaFX, countdown
     * timer in Label
     * setText}
     */
    timer = new Timer();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          public void run() {
            if (remainingTime == 15) {
              playSound("/sounds/mixkit-tick-tock-clock-timer-1045.mp3");
            }
            // Updated timer and predictions every second
            if (remainingTime == 30) {
              speak("30 seconds remaining");
            }
            if (remainingTime == 3) {
              speak("3");
            }
            if (remainingTime == 2) {
              speak("2");
            }
            if (remainingTime == 1) {
              speak("1");
            }
            if (remainingTime > 0) {
              Platform.setImplicitExit(false);
              Platform.runLater(() -> timerLabel.setText(Integer.toString(remainingTime)));
              remainingTime--;

              try {
                // updates the predicition as long as timer is active
                populatePredictionList();
              } catch (TranslateException e) {
                throw new RuntimeException(e);
              }
            } else {
              // Player looses game
              Platform.runLater(
                  () -> {
                    resultLabel.setText("Times Up, you lose!");
                    playSound("/sounds/mixkit-little-piano-game-over-1944 (1).mp3");
                    try {
                      updateResult(Result.LOSS);
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    finishGame();
                  });
            }
          }
        },
        1000,
        1000);
  }

  /**
   * Takes a string input of a file location to play and plays the sound
   *
   * @param s
   */
  public static void playSound(String s) {
    javafx.concurrent.Task<Void> task =
        new javafx.concurrent.Task<Void>() {
          @Override
          protected Void call() throws Exception {
            Media sound =
                new Media(App.class.getResource(s).toURI().toString()); // create new media object
            player = new MediaPlayer(sound);
            player.play(); // play the sound
            return null;
          }
        };
    // Delegates sound playing to new thread to prevent blocking of GUI
    Thread thread = new Thread(task);
    thread.start();
  }
}
