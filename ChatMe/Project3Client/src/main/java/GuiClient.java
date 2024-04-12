
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class GuiClient extends Application{


	TextField textBox;
	Button sendButton, joinChat;
	TextField textFieldID, currentUser;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;

	ListView<String> screenView;
	ComboBox<String> usersOnline;

	ComboBox<String> recipients, groups;
	String lastChatScreen, lastRecipient;
	HashMap<String, ArrayList<String>> Chat;



	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					if(data instanceof Message){
						Message message = (Message) data;

						// update userList
						if(message.userNames != null){  //todo take this off
							updateUsersOnline(message);
							updateRecipientBox(message);

							// if broadcast message add to everyone chat
							if(!message.secreteMessage){
								Chat.get("Everyone online").add(message.MessageInfo);
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
						}




//						for(String user : message.recipients){
//							recipients.getItems().add(user);
//						}
					}
				});
		});

		clientConnection.start();

		screenView = new ListView<String>();
		Chat =  new HashMap<>();
		textBox = new TextField();
		sendButton = new Button("Send");
		joinChat = new Button("Join chat");

		Chat.put("Everyone online", new ArrayList<>());
		//todo: update clients sends info
		sendButton.setOnAction(e->{
			SendMessage();
		});

		textBox.setOnAction(e->{
			SendMessage();
		});

		joinChat.setOnAction(e -> {
			String userID = textFieldID.getText();
			if(userID != null) {
				boolean joined = clientConnection.joinChat(userID); //todo: can this help????
				Platform.runLater(() -> {
					if (joined) {
						System.out.println("Username good... adding to chat");
						currentUser.setText("User: " + clientConnection.clientID);
						// todo: can this help?
//						Message welcome = new Message();
//						welcome.MessageInfo = "joined";
						//clientConnection.send(welcome);
						primaryStage.setScene(sceneMap.get("client"));		//todo: send welcome to notify all users client is online?

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
		usersOnline.getItems().add("Everyone online");
		for(String user : message.userNames){
			//if(user.equals(clientConnection.clientID)){continue;}
			usersOnline.getItems().add(user);
		}
		usersOnline.setValue("Everyone online");
		// todo: is this needed?
		//usersOnline.getItems().add(clientConnection.clientID);
		//usersOnline.setValue("Everyone online");
	}

	private void updateRecipientBox(Message message){
		recipients.getItems().clear();
		recipients.getItems().add("Send to: Everyone");
		for(String user : message.userNames){
			//if(user.equals(clientConnection.clientID)){continue;}
			recipients.getItems().add("Send to: " + user);
		}
		recipients.setValue("Send to: ");
		// todo: is this needed?
		//recipients.getItems().add(clientConnection.clientID);
//		recipients.setValue("Send to: ");
//		usersOnline.setValue("Everyone online");
	}
	private Button createButton(String name){
		Button newButton = new Button(name);
		newButton.setAlignment(Pos.CENTER);

		return newButton;
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
		textField.setPrefSize(400,20);
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
		lastRecipient = recipients.getValue();

		// todo: prob here
		//newMessage.userNames = clientConnection.localList;
		newMessage.recipient = lastChatScreen;
		if(!newMessage.recipient.isEmpty() && !Objects.equals(newMessage.recipient,"Everyone online")){
			newMessage.secreteMessage = true;
			newMessage.userID = clientConnection.clientID;
		}
		System.out.println("Sending message to:"+ newMessage.recipient);


		clientConnection.send(newMessage);

		System.out.println(usersOnline.getValue());
		System.out.println(recipients.getValue());
	}
	public Scene createClientGui() {

		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		currentUser = new TextField();
		currentUser.setPrefSize(90,20);
		currentUser.setEditable(false);
		currentUser.setAlignment(Pos.TOP_LEFT);

		usersOnline = new ComboBox<>();
		usersOnline.setValue("Everyone online");
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

		screenView.setPrefHeight(250);

		textBox = createTextBox();
		HBox bottom1 = new HBox(textBox);
		bottom1.setAlignment(Pos.CENTER);
		bottom1.setPadding(new Insets(50,20,0,20));

		recipients = new ComboBox<>();
		recipients.setValue("Send to: Everyone");


		groups = new ComboBox<>();
		groups.setValue("Groups");

		HBox bottom2 = new HBox(50, sendButton, recipients, groups);
		bottom2.setAlignment(Pos.CENTER);
		bottom2.setPadding(new Insets(0,20,0,20));

		root.setPadding(new Insets(10));
		root.getChildren().addAll(topArea, screenView, bottom1,bottom2);
		root.setStyle("-fx-background-color: orange;"+"-fx-font-family: 'serif';");


		screenView.requestFocus();
		currentUser.setFocusTraversable(false);
		return new Scene(root,500,400);
	}

	public Scene createLoginGui(Stage primaryStage) {

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
		return new Scene(vbox, 500, 400);
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
		if(lastChatScreen != null && lastRecipient != null){
			usersOnline.setValue(lastChatScreen);
			recipients.setValue(lastRecipient);
		}
	}

}
