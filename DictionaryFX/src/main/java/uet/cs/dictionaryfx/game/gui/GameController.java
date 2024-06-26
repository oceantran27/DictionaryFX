package uet.cs.dictionaryfx.game.gui;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import uet.cs.dictionaryfx.game.model.*;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import uet.cs.dictionaryfx.game.model.Game;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    //const
    private static final String DICE_IMAGE_PATH_FORMAT = "/uet/cs/dictionaryfx/game/gui/Assets/dice-%d.png";
    private final String DICE_SOUND_PATH = getClass().getResource("Assets/dice-roll.mp3").toExternalForm();
    private final String QUIZ_SOUND_PATH = getClass().getResource("Assets/quiz-sound.mp3").toExternalForm();
    private final String DOLPHIN_SOUND_PATH = getClass().getResource("Assets/dolphin-sound.mp3").toExternalForm();
    private final String PIRATE_SOUND_PATH = getClass().getResource("Assets/laugh.mp3").toExternalForm();
    private final String VOLCANO_SOUND_PATH = getClass().getResource("Assets/volcano_sound.mp3").toExternalForm();
    private final String GUNSHOT_SOUND_PATH = getClass().getResource("Assets/gunshot.mp3").toExternalForm();
    private final String CORRECT_ANSWER_SOUND_PATH = getClass().getResource("Assets/correct-answer.mp3").toExternalForm();
    private final String WIN_GAME_SOUND_PATH = getClass().getResource("Assets/win_music.mp3").toExternalForm();
    private final String LOSE_GAME_SOUND_PATH = getClass().getResource("Assets/loose_music.mp3").toExternalForm();


    private static final String STYLE_NO_COLOR_ANSWER_BUTTON = "-fx-background-color: white; -fx-border-color: yellow; -fx-border-width: 5; -fx-background-radius: 20; -fx-border-radius: 20";
    private static final String STYLE_CORRECT_ANSWER_BUTTON = "-fx-background-color: lightgreen; -fx-border-color: yellow; -fx-border-width: 5; -fx-background-radius: 20; -fx-border-radius: 20";
    private static final String STYLE_INCORRECT_ANSWER_BUTTON = "-fx-background-color: red; -fx-border-color: yellow; -fx-border-width: 5; -fx-background-radius: 20; -fx-border-radius: 20";


    //data field
    @FXML
    private ImageView diceImage;
    @FXML
    private ImageView shipImage;
    @FXML
    private ImageView loadingImage;
    @FXML
    private ImageView quizStateImage;
    @FXML
    private Label turnLable;
    @FXML
    private Label scoreLable;
    @FXML
    private Label question;
    @FXML
    private Label answer1Content;
    @FXML
    private Label answer2Content;
    @FXML
    private Label answer3Content;
    @FXML
    private Label answer4Content;
    @FXML
    private Button answer1Button;
    @FXML
    private Button answer2Button;
    @FXML
    private Button answer3Button;
    @FXML
    private Button answer4Button;
    @FXML
    private Button diceButton;
    @FXML
    private Button backMenuButton;
    @FXML
    private Button quizNextButton;

    @FXML
    private Pane darkPane;
    @FXML
    private Pane quizPane;

    @FXML
    private Text winText;
    @FXML
    private Text looseText;
    @FXML
    private Text scoreText;
    @FXML
    private ImageView winTreasure;
    @FXML
    private ImageView looseTreasure;
    @FXML
    private Pane endPane;

    private Game game;
    private static GameLoop gameLoop;
    private Quiz quiz;
    private static TranslateTransition tt;

    private int userAnswer;
    private boolean isAnswered;
    private boolean needAnswerQuizz;
    private boolean isStartAgain;
    private boolean isMoving;
    private int step;
    private static MediaPlayer mediaPlayer;
    private Parent menuRoot;


    //Method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Test();
    }

    public void Test() {
        shipImage.setLayoutX(230);
        shipImage.setLayoutY(44);
        tt = new TranslateTransition(Duration.millis(1000), shipImage);
        game = new Game();
        gameLoop = new GameLoop();

        isAnswered = false;

        step = 0;
        isMoving = false;
        isStartAgain = false;
        needAnswerQuizz = false;

        gameLoop.start();
    }

    public class OpenGameMenuEvent extends Event {
        public static final EventType<GameController.OpenGameMenuEvent> OPEN_GAME_MENU_EVENT_TYPE = new EventType<>(Event.ANY, "OPEN_GAME_MENU");

        public OpenGameMenuEvent() {
            super(OPEN_GAME_MENU_EVENT_TYPE);
        }
    }

    public class GameLoop extends AnimationTimer {
        @Override
        public void handle(long now) {
            if (step != 0 || !game.isGameOver() || needAnswerQuizz) {
                updateView();
            } else {
                diceButton.setDisable(true);
                scoreText.setText("Your score: " + game.getScore());
                if (game.isWin()) {
                    winText.setVisible(true);
                    winTreasure.setVisible(true);
                    createAndPlayMediaPlayer(WIN_GAME_SOUND_PATH, 0.5);
                } else if (game.isLoose()) {
                    looseText.setVisible(true);
                    looseTreasure.setVisible(true);
                    createAndPlayMediaPlayer(LOSE_GAME_SOUND_PATH, 0.5);
                }
                endPane.setVisible(true);
                gameLoop.stop();
            }
        }
    }

    public void updateView() {
        if (game != null) {
            changeDiceImage(game.getDiceValue());
            turnLable.setText("TURN: " + game.getTurn() + "/" + game.MAX_TURNS);
            scoreLable.setText("SCORE: " + game.getScore());
        }

        if (quiz != null) {
            question.setText(quiz.getQuestion());
            List<String> answerList = quiz.getAnswers();
            answer1Content.setText(answerList.get(0));
            answer2Content.setText(answerList.get(1));
            answer3Content.setText(answerList.get(2));
            answer4Content.setText(answerList.get(3));
        }

        if (!isSpecialIsland()) {
            if (step == 0 && !isMoving && needAnswerQuizz) {
                needAnswerQuizz = false;
                showQuiz();
            }
        }

        if (step != 0 && !isMoving) {
            isMoving = true;
            loopAnimationShipMoving();
        }

        if (isStartAgain) {
           // System.out.println("start again");
            isMoving = true;
            isStartAgain = false;
            game.setCurrentIsland(0);
            animationShipMoving(Ship.START_X, Ship.START_Y, () -> {
                isMoving = false;
            });
        }
    }

    public void changeDiceImage(int index) {
        String imagePath = String.format(DICE_IMAGE_PATH_FORMAT, index);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        diceImage.setImage(image);
    }

    public void handleDiceButton(ActionEvent event) throws IOException {
        if (!isMoving) {
            createAndPlayMediaPlayer(DICE_SOUND_PATH, 0.3);

            animationDiceRolling(game.getDiceValue());
            game.roll();
            step = game.getDiceValue();

            needAnswerQuizz = true;
            isAnswered = false;

            quiz = game.getQuiz();
            System.out.println(quiz.getCorrectAnswer());
        }
    }

    public void handleBackMenuButton(ActionEvent event) {
        if (!game.isGameOver()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Your game will not be saved");
            alert.setContentText("Do you want to continue ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                clear();
                OpenGameMenuEvent openGameMenuEvent = new OpenGameMenuEvent();
                backMenuButton.fireEvent(openGameMenuEvent);
            }
        } else {
            clear();
            OpenGameMenuEvent openGameMenuEvent = new OpenGameMenuEvent();
            backMenuButton.fireEvent(openGameMenuEvent);
        }
    }

    public void handleAnswerButton(ActionEvent event) {
        if (!isAnswered) {
            Button answerButton = (Button) event.getSource();
            if (answerButton == answer1Button) {
                userAnswer = 1;
                answer1Content.setStyle(STYLE_INCORRECT_ANSWER_BUTTON);
            } else if (answerButton == answer2Button) {
                userAnswer = 2;
                answer2Content.setStyle(STYLE_INCORRECT_ANSWER_BUTTON);
            } else if (answerButton == answer3Button) {
                userAnswer = 3;
                answer3Content.setStyle(STYLE_INCORRECT_ANSWER_BUTTON);
            } else if (answerButton == answer4Button) {
                userAnswer = 4;
                answer4Content.setStyle(STYLE_INCORRECT_ANSWER_BUTTON);
            }

            if (quiz.getCorrectAnswer() == 1) {
                answer1Content.setStyle(STYLE_CORRECT_ANSWER_BUTTON);
            } else if (quiz.getCorrectAnswer() == 2) {
                answer2Content.setStyle(STYLE_CORRECT_ANSWER_BUTTON);
            } else if (quiz.getCorrectAnswer() == 3) {
                answer3Content.setStyle(STYLE_CORRECT_ANSWER_BUTTON);
            } else if (quiz.getCorrectAnswer() == 4) {
                answer4Content.setStyle(STYLE_CORRECT_ANSWER_BUTTON);
            }

            Platform.runLater(() -> {
                isAnswered = true;
                checkAnswer();
                quizNextButton.setDisable(false);
            });
        }
    }

    public void handleQuizNextButton(ActionEvent event) {
        closeQuiz();
        quizNextButton.setDisable(true);
    }

    public void loopAnimationShipMoving() {
        if (step != 0) {
            int nextIslandX, nextIslandY;

            if (step > 0) {
                nextIslandX = game.getNextIslandPosX();
                nextIslandY = game.getNextIslandPosY();
                game.moveForwardShip();
            } else {
                if (game.getCurrentIsland() == 0) {
                    step = 0;
                    isMoving = false;
                    return;
                }
                nextIslandX = game.getPrevIslandPosX();
                nextIslandY = game.getPrevIslandPosY();
                game.moveBackShip();
            }

            animationShipMoving(nextIslandX, nextIslandY, () -> {
                if (step > 0) {
                    step--;
                } else {
                    step++;
                }
                loopAnimationShipMoving();
            });
        }
        if (step == 0) {
            isMoving = false;
        }
    }

    public void animationShipMoving(int nextIslandX, int nextIslandY, Runnable onFinish) {
        tt.setByX(nextIslandX - game.getCurrentPosX());
        tt.setByY(nextIslandY - game.getCurrentPosY());
        game.setCurrentPos(nextIslandX, nextIslandY);
        tt.play();
        tt.setOnFinished(event -> {
            // Gọi callback khi di chuyển hoàn tất
            if (onFinish != null) {
                onFinish.run();
            }
        });
    }

    public void animationDiceRolling(int index) {
        diceButton.setDisable(true);
        RotateTransition rt = new RotateTransition();
        rt.setByAngle(360);
        rt.setNode(diceImage);
        rt.setDuration(Duration.millis(600));
        rt.play();
        rt.setOnFinished(event -> {
            diceButton.setDisable(false);
        });
    }

    public boolean isSpecialIsland() {
        if (!isMoving) {
            //pirate
            if (game.getCurrentIsland() == 7 || game.getCurrentIsland() == 18 || game.getCurrentIsland() == 21) {
                //System.out.println("cuop bien");
                step = -2;
                isMoving = false;
                createAndPlayMediaPlayer(PIRATE_SOUND_PATH, 0.5);
                return true;
            } else if (game.getCurrentIsland() == 10) {
                //System.out.println("nui lua");
                createAndPlayMediaPlayer(VOLCANO_SOUND_PATH, 0.5);
                isStartAgain = true;
                return true;
            } else if (game.getCurrentIsland() == 4 || game.getCurrentIsland() == 15) {
                //System.out.println("ca heo");
                createAndPlayMediaPlayer(DOLPHIN_SOUND_PATH, 0.5);
                step = 2;
                isMoving = false;
                return true;
            }
        }
        return false;
    }

    public void showQuiz() {
        darkPane.setVisible(true);
        quizPane.setVisible(true);

        createAndPlayMediaPlayer(QUIZ_SOUND_PATH, 0.6);
    }

    public void closeQuiz() {
        darkPane.setVisible(false);
        quizPane.setVisible(false);
        answer1Content.setStyle(STYLE_NO_COLOR_ANSWER_BUTTON);
        answer2Content.setStyle(STYLE_NO_COLOR_ANSWER_BUTTON);
        answer3Content.setStyle(STYLE_NO_COLOR_ANSWER_BUTTON);
        answer4Content.setStyle(STYLE_NO_COLOR_ANSWER_BUTTON);
    }

    public void checkAnswer() {
        if (userAnswer == quiz.getCorrectAnswer()) {
            game.plusScore();
            createAndPlayMediaPlayer(CORRECT_ANSWER_SOUND_PATH, 0.5);
        } else {
            game.minusScore();
            needAnswerQuizz = false;
            step = -2;
            createAndPlayMediaPlayer(GUNSHOT_SOUND_PATH, 0.5);
        }
        game.increaseNumberAnswer();
    }

    private void createAndPlayMediaPlayer(String mediaPath, double volume) {
        if (mediaPlayer != null) {
            //mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        mediaPlayer = new MediaPlayer(new Media(mediaPath));
        mediaPlayer.setCycleCount(1);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });
        }
    }

    public static void clear() {
        if (tt != null) {
            tt.stop();
            tt.setOnFinished(null);
        }
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
}

