
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	
	ListView<String> listItems;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		serverConnection = new Server(data -> {
			if(serverConnection.count == 0){
				Platform.runLater(()->{
					primaryStage.setScene(sceneMap.get("Home"));
				});
			}
			else{
				Platform.runLater(()->{
					primaryStage.setScene(sceneMap.get("server"));
					listItems.getItems().add(data.toString());
				});
			}

		});

		
		listItems = new ListView<String>();

		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("Home",createHomeGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("Home"));
		primaryStage.setTitle("This is the Server");
		primaryStage.show();
		
	}

	Background createBackGroundImage(String src){

		Image backgroundImage = new Image(src);

		// create background with image
		BackgroundImage background = new BackgroundImage(backgroundImage,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER,
				new BackgroundSize(100,100,true,true,true,true));

		return new Background(background);
	}
	public Scene createHomeGui() {

		// Create a background with the background image
		Background backgroundWithImage = createBackGroundImage("images/chatPic.jpg");

		// Create a layout pane
		StackPane root = new StackPane();

		// Set the background to the layout pane
		root.setBackground(backgroundWithImage);
		root.setPadding(new Insets(20,20,300,20));


		TextArea textArea = new TextArea("********* Welcome to ChatMe **********\n" +
											"Server is waiting for client connection........");
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setStyle("-fx-font-family: 'serif'; -fx-font-size: 14px; -fx-font-weight: bold;");
		textArea.setPrefSize(300,50);


		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(10));


		HBox textAreaBox = new HBox(textArea);
		textAreaBox.setAlignment(Pos.CENTER);
		textAreaBox.setPadding(new Insets(10));

		// Add the TextArea
		root.getChildren().addAll(hBox, textAreaBox);
		return new Scene(root, 500, 400);


	}
	public Scene createServerGui() {

		// Create a background with the background image
		Background backgroundWithImage = createBackGroundImage("images/chatPic.jpg");

		// Create a layout pane
		StackPane root = new StackPane();

		// Set the background to the layout pane
		root.setBackground(backgroundWithImage);
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		
		pane.setCenter(listItems);
		pane.setStyle("-fx-font-family: 'serif'");

		root.getChildren().add(pane);
		return new Scene(root, 500, 400);
		
		
	}


}
