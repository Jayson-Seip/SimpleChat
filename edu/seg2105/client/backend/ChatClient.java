// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String userLogin;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI,String userLogin)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.userLogin = userLogin;
  }

  
  //Instance methods ************************************************
  /**
   * Hook method called after a connection has been established. The default
   * implementation does nothing. It may be overridden by subclasses to do
   * anything they wish.
   */
  protected void connectionEstablished() {
    try{
      sendToServer("#login "+userLogin);

    }catch (Exception e){
    }

  }
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      // Check if message starts with # - symbol
      if(message.startsWith("#")){
        handleCommand(message);
      }
      else{
        sendToServer(message);
      }

    }
    catch(IllegalArgumentException il){
      System.out.println("Invalid command");
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  /**
   * Handles commands entered by the user
   * @param userCommand
   */
  private void handleCommand(String userCommand) throws IOException{
    // Exits the program
    String[] task = userCommand.split(" ");
    String command = task[0];
    if(command.equals("#quit")){
      quit();
    }
    // User logs out of chat client
    else if(command.equals("#logoff")){
      try {
        System.out.println("Logging off");
        closeConnection();
      } catch (IOException e) {
        //connectionClosed();
      }
    }
    // allows user to login
    else if(command.equals("#login")){
      System.out.println("Logging in");
      try{
        if(!isConnected()){
          openConnection();
        }
        else{
          System.out.println("You Are Already Connected to Server");
        }
      } catch (IOException e){
        System.out.println("ERROR - Can't setup connection! Terminating client.");
        quit();
      }


    }
    // allows user to set hostname
    else if (command.equals("#sethost")) {
      if(task[1] == null){
        throw new IllegalArgumentException();
      }
      else if(isConnected()){
        System.out.println("Connect change host name when logged in");
      }
      else{
        setHost(task[1]);
        System.out.println("New Host Name: "+getHost());

      }
      
    }
    else if (command.equals("#setport")) {
      if(task[1] == null){
        throw  new IllegalArgumentException();
      }
      else if(isConnected()){
        System.out.println("Connect change port number when logged in");
      }
      else{
        try{
          int port = Integer.parseInt(task[1]);
          setPort(port);
          System.out.println("New port: "+port);
        }
        // Throws exception if port is not an integer type
        catch (NumberFormatException nf){
          System.out.println("Invalid port");
        }
      }
      
    }
    else if (command.equals("#gethost")) {
      System.out.println("Host name "+getHost());

    }
    else if(command.equals("#getport")){
      System.out.println("Port number: "+getPort());
    }
    else{
      throw new IllegalArgumentException();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }



  /**
   * Hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   *
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("Server has Shutdown");
    System.exit(0);
  }

  /**
   * Hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
    clientUI.display("Connection Closed");

  }
}

//End of ChatClient class
