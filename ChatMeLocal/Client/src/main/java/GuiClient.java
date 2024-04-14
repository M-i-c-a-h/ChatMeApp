
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{


	TextField textBox;
	Button sendButton, joinChat;
	TextField textFieldID, currentUser;
	HashMap<String, Scene> sceneMap;
	Client clientConnection;

	ListView<String> screenView;
	ComboBox<String> usersOnline;

	String lastChatScreen = "";
	HashMap<String, ArrayList<String>> Chat;
	Button notificationBar;
	boolean newChat, isBroadcast;
	String sender;



	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					if(data instanceof Message){
						Message message = (Message) data;
						lastChatScreen = usersOnline.getValue();
						updateUsersOnline(message);
						isBroadcast = false;


						// update chat
						if(message.userNames != null){  //todo take this off
							if(!Objects.equals(message.userID, clientConnection.clientID) && !message.newUser){
								newChat = true;
								sender = message.userID;
							}
							// if broadcast message add to everyone chat
							if(!message.secreteMessage){
								Chat.get("Users Online").add(message.MessageInfo);
								isBroadcast = true;
							}
							// if user has existing private message with client, add to chat
							else if(Chat.containsKey(message.userID)){
								Chat.get(message.userID).add(message.MessageInfo);
							}
							// create new private message chat
							else{
								Chat.put(message.userID,new ArrayList<>());
								Chat.get(message.userID).add(message.MessageInfo);
							}
							// update chat
							updateChat();

							// update screen
							updateScreen();

							if(newChat){
								notificationBar.setText("New message from: " + sender);
								notificationBar.setDisable(false);
								newChat = false;
							}

							if(message.userNames.size() == 1 || usersOnline.getValue() == null){
								usersOnline.setValue("Users Online");
							}
						}

					}
				});
		});

		clientConnection.start();

		screenView = new ListView<String>();
		Chat =  new HashMap<>();
		textBox = new TextField();
		sendButton = new Button("Send");
		joinChat = new Button("Join chat");

		Chat.put("Users Online", new ArrayList<>());
		// send info to server
		sendButton.setOnAction(e->{
			SendMessage();
		});
		// send info to server
		textBox.setOnAction(e->{
			SendMessage();
		});

		joinChat.setOnAction(e -> {
			String userID = textFieldID.getText();
			if(userID != null) {
				boolean joined = clientConnection.joinChat(userID);
				Platform.runLater(() -> {
					if (joined) {
						System.out.println("Username good... adding to chat");
						currentUser.setText("User: " + clientConnection.clientID);
						primaryStage.setScene(sceneMap.get("client"));

					} else {
						System.out.println("username already exists");

						Alert alert = createAlert();
						alert.showAndWait();
						textFieldID.setPromptText("username already exists");
					}
				});
			}
		});
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("login", createLoginGui(primaryStage));
		sceneMap.put("client",  createClientGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


		primaryStage.setScene(sceneMap.get("login"));
		primaryStage.setTitle("Client");
		primaryStage.show();

	}

	private void updateUsersOnline(Message message){
		usersOnline.getItems().clear();
		usersOnline.getItems().add("Users Online");
		for(String user : message.userNames){
			usersOnline.getItems().add(user);
		}
		usersOnline.setValue("Users Online");
	}
	private Button createNotificationBar(){
		Button newButton = new Button();
		newButton.setAlignment(Pos.TOP_LEFT);
		newButton.setPrefSize(380,20);
		newButton.setText("No new message");
		newButton.setDisable(true);
		newButton.setOnAction(e->{
			updateNotification(isBroadcast);
			newButton.setDisable(true);
			newButton.setText("No new message");
			sender = "";
		});

		return newButton;
	}
	private void updateNotification(boolean isBroadcast){
		if(!Objects.equals(sender,"")) {
			if(isBroadcast){
				sender = "Users Online";
			}
			usersOnline.setValue(sender);
			updateChat();
		}

	}
	private void SendMessage(){
		Message newMessage = new Message();
		newMessage.MessageInfo = textBox.getText();
		if(!Objects.equals(newMessage.MessageInfo, "")){
			processMessage(newMessage);
		}
	}
	private TextField createTextBox(){
		TextField textField = new TextField();
		textField.setEditable(true);
		textField.setPromptText("type your message here");
		textField.setPrefSize(320,20);
		textField.setOnAction( e->{
			Message newMessage = new Message();
			newMessage.MessageInfo = textField.getText();
			if(!Objects.equals(newMessage.MessageInfo, "")){
				processMessage(newMessage);
			}

		});

		return textField;
	}
	private void processMessage(Message newMessage){
		textBox.clear();
		lastChatScreen = usersOnline.getValue();

		newMessage.recipient = lastChatScreen;
		if(!newMessage.recipient.isEmpty() && !Objects.equals(newMessage.recipient,"Users Online")){
			newMessage.secreteMessage = true;
			newMessage.userID = clientConnection.clientID;
		}
		System.out.println("Sending message to:"+ newMessage.recipient);

		clientConnection.send(newMessage);
	}
	public Scene createClientGui() {

		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		notificationBar = createNotificationBar();
		HBox top = new HBox(notificationBar);
		top.setPadding(new Insets(10,10,10,10));
		top.setAlignment(Pos.TOP_LEFT);


		currentUser = new TextField();
		currentUser.setPrefSize(90,20);
		currentUser.setEditable(false);
		currentUser.setAlignment(Pos.TOP_LEFT);

		usersOnline = new ComboBox<>();
		usersOnline.setValue("Users Online");
		usersOnline.setEditable(false);
		usersOnline.setOnAction(e->{
			updateChat();
		});


		HBox top1 = new HBox();
		top1.getChildren().add(usersOnline);
		top1.setAlignment(Pos.TOP_RIGHT);

		HBox topArea = new HBox();
		topArea.getChildren().addAll(currentUser,usersOnline);
		topArea.setSpacing(200);
		topArea.setPadding(new Insets(0,10,0,10));

		HBox screenBox = new HBox(screenView);
		screenBox.setPadding(new Insets(0,20,0,0));
		screenView.setPrefHeight(250);

		textBox = createTextBox();
		HBox bottom1 = new HBox();
		bottom1.setAlignment(Pos.CENTER);
		bottom1.setPadding(new Insets(10,0,0,10));
		bottom1.setSpacing(10);
		bottom1.getChildren().addAll(textBox,sendButton);


		root.setPadding(new Insets(10));
		root.getChildren().addAll(top, topArea, screenView, bottom1);
		root.setStyle("-fx-background-color: orange;"+"-fx-font-family: 'serif'; -fx-font-weight: bold;");


		screenView.requestFocus();
		currentUser.setFocusTraversable(false);
		return new Scene(root,500,400);
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
	public Scene createLoginGui(Stage primaryStage) {

		// Create a background with the background image
		Background backgroundWithImage = createBackGroundImage("images/chatPic.jpg");

		// Create a layout pane
		StackPane root = new StackPane();

		// Set the background to the layout pane
		root.setBackground(backgroundWithImage);

		textFieldID = new TextField();
		textFieldID.setPromptText("Enter a unique username");
		textFieldID.setAlignment(Pos.CENTER);
		textFieldID.setOnAction(e-> joinChat.fire());

		joinChat.setAlignment(Pos.CENTER);

		VBox vbox = new VBox();
		vbox.setSpacing(30);
		vbox.setPadding(new Insets(0,20,0,20));
		vbox.getChildren().addAll(textFieldID,joinChat);
		vbox.setAlignment(Pos.CENTER);

		textFieldID.setFocusTraversable(false);

		root.getChildren().add(vbox);
		return new Scene(root, 500, 400);
	}

	private Alert createAlert(){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(null);
		alert.setHeaderText(null);
		alert.setContentText(("username already exists\npls choose another username"));

		ButtonType buttonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(buttonType);

		return alert;
	}
	private void updateChat(){

		screenView.getItems().clear();
		if(Chat.get(usersOnline.getValue()) != null) {
			for (String string : Chat.get(usersOnline.getValue())) {
				screenView.getItems().add(string);
			}
		}
	}
	private void updateScreen(){
		if(Objects.equals(lastChatScreen, "")){
			usersOnline.setValue("Users Online");
			return;
		}
		usersOnline.setValue(lastChatScreen);
	}

}
