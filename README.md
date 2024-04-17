### ChatMe

## Messaging App with TCP Protocol

## Description
This messaging app is built using the TCP protocol and allows users to communicate with each other via broadcast messages and private messages. 
The app runs locally on the user's machine and consists of two main components: a server application and a client application. 
Multiple threads are used to handle concurrent connections and messaging functionalities.

## Features
- Broadcast Messages: Users can send messages that will be broadcast to all online users.
- Private Messages: Users can send private messages to specific online users.
- Threaded Architecture: The app utilizes multiple threads to handle concurrent connections and messaging functionalities efficiently.
- Server Application: Handles incoming connections from clients, manages user sessions, and facilitates message broadcasting and private messaging.
- Client Application: Allows users to connect to the server, send messages, and receive messages from other users.
- Localhost: The app runs on localhost, enabling communication within the user's local network.


  ## Screenshots

  Screenshot1 

<img width="497" alt="Screenshot 2024-04-14 at 10 38 14 AM" src="https://github.com/M-i-c-a-h/ChatMeApp/assets/145408039/4b0728ec-3bd3-4e91-9880-e2fffd470b31">

  Screenshot2

<img width="1003" alt="Screenshot 2024-04-14 at 10 38 54 AM" src="https://github.com/M-i-c-a-h/ChatMeApp/assets/145408039/6004b0c9-22b5-4afd-95f6-3d8e7abfeb73">


  Screenshot3
<img width="995" alt="Screenshot 2024-04-14 at 11 03 58 AM" src="https://github.com/M-i-c-a-h/ChatMeApp/assets/145408039/fd4fb2ce-d42d-4f9f-b7d6-279b3e008500">


  Screenshot4
<img width="1464" alt="Screenshot 2024-04-14 at 11 09 43 AM" src="https://github.com/M-i-c-a-h/ChatMeApp/assets/145408039/aaccb525-439b-4734-8fcd-17a5d8167ae4">


## Installation
- Clone the repository to your local machine:
``` bash
git clone https://github.com/yourusername/messaging-app.git
```
- Navigate to the project directory:
- Compile and run the server application using maven config:
``` bash
clean compile exec : java
```
 - Compile and run the client application using maven config:
``` bash
clean compile exec : java
```
 - check allow multiple instances [modify options tab]
                                                    
This program requires Java and JavaFX to be installed on the local machine to run.
Follow the suggestions in the link to install packages.
https://docs.oracle.com/javafx/2/installation/jfxpub-installation.htm


## Contributing
Contributions are welcome! If you have any ideas for improvements or new features, feel free to submit a pull request or open an issue.

## Credits
This project was created as a CS342 coursework by Micah Olugbamila under the instructions of Prof Evan McCarty.

## License
This project is licensed under the MIT License.

