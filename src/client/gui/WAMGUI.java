package client.gui;

import client.WAMBoard;
import client.WAMNetworkClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * WAMGUI represents the view of the MVC architecture.
 * Contains all graphics, text, and buttons.
 *
 * @author Albert Htun
 * @author Cheng Ye
 */
public class WAMGUI extends Application implements connectfour.client.Observer<WAMBoard> {

    /** The game board that contains all the stats and game core logic */
    private WAMBoard board;

    /** The network client */
    private WAMNetworkClient client;

    /** List of buttons */
    private ArrayList<Button> buttons;

    /** BorderPane of the gui */
    private BorderPane borderPane;

    /** Image for mole up */
    private Image moleUp =
            new Image(getClass().getResourceAsStream("moleup.jpg"));

    /** Image for mole down */
    private Image moleDown =
            new Image(getClass().getResourceAsStream("moledown.jpg"));

    /**
     * Initializes all the necessary fields and variables.
     * Gets the client and board objects.
     */
    @Override
    public void init() {

        List<String> args = getParameters().getRaw();

        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));

        try {
            this.client = new WAMNetworkClient(host, port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.board = client.getBoard();
        this.board.addObserver( this );

    }

    /**
     * Sets up the GUI and shows it to user.
     * The scene -- all text and buttons -- are created here.
     *
     * @param stage stage container that is rendered as the GUI
     * @throws Exception if there is a problem
     */
    public void start (Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        this.borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        int position = 0;
        this.buttons = new ArrayList<>();
        for (int row=0; row<this.board.getRows(); ++row) {
            Button[] buttrow = new Button[this.board.getCols()];
            for (int col=0; col<this.board.getCols(); ++col) {
                Button button = new Button();
                button.setGraphic(new ImageView(moleDown));
                button.setId(String.valueOf(position++));
                buttons.add(button);
                buttrow[col] = button;
                // TODO add event handler and graphics for each button
                button.setOnAction(e -> {
                    this.client.whack(Integer.parseInt(button.getId()));
                });
            }
            gridPane.addRow(row, buttrow);
        }
        borderPane.setTop(new Text("Player ID: #" + this.client.getID()));
        borderPane.setCenter(gridPane);
        borderPane.setBottom(this.borderPane);
        this.borderPane.setLeft(new Text("" + this.board.getScores(this.client.getID())));
        this.borderPane.setRight(new Text ("Game is running"));
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
        client.start();
    }

    /**
     * This method is the controller that modifies the view.
     * All GUI updates are done here.
     */
    public void refresh() {
        Integer moleId = this.board.getUnupdatedMole();
        if (moleId != null) {
            if ( board.isMoleUp(moleId) ) {
                this.buttons.get(moleId).setGraphic(new ImageView(moleUp));
            }
            else {
                this.buttons.get(moleId).setGraphic(new ImageView(moleDown));
            }
        }
        this.borderPane.setLeft(new Text("Score: " + this.board.getScores(this.client.getID())));
        switch (board.getStatus()) {
            case TIE:
                this.borderPane.setRight(new Text("Game is tied"));
                break;
            case ERROR:
                this.borderPane.setRight(new Text("ERROR!"));
                break;
            case I_WON:
                this.borderPane.setRight(new Text("You Won!!!"));
                break;
            case I_LOST:
                this.borderPane.setRight(new Text("You Lost"));
        }
    }

    /**
     * This method is called by other classes/threads
     * to ensure that the main application thread
     * does the GUI updates.
     */
    @Override
    public void update(WAMBoard wamBoard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    /**
     * Retrieves args and starts the GUI application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WAMGUI [host name] [port]");
            System.exit(-1);
        }
        else {
            Application.launch(args);
        }
    }
}
