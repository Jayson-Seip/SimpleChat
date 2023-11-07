package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port)
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String command = msg.toString();
    System.out.println("Message received: " + msg + " from " + client.getInfo("userLogin"));

    if(command.startsWith("#login")){
      // Sets up user if it is the first time connecting to the server
      if(client.getInfo("userLogin") != null){
        try{
          client.close();
        }catch(IOException e){
        }

      }
      String[] information = command.split(" ");
      client.setInfo("userLogin",information[1]);
      System.out.println(client.getInfo("userLogin")+" has logged on");


    }
    else{
      // Sends message if login is already established
      this.sendToAllClients(client.getInfo("userLogin")+"> "+msg);
    }

  }

  /**
   *Gets the message from the server UI
   * @param msg
   */
  public void handleMessageFromServerUI(String msg){
    if(msg.startsWith("#")){
        handleCommand(msg);
    }
    this.sendToAllClients("SERVER MSG> " + msg);
  }

  /**
   *Handles the commands from the server UI
   * @param userCommand
   */
  private void handleCommand(String userCommand){
    String[] task = userCommand.split(" ");
    String command = task[0];

    // quits out of the program
    if(command.equals("#quit")){
      sendToAllClients("Server has shutdown");
      System.exit(0);
    }
    // Stops listening for connections but existing clients
    else if(command.equals("#stop")){
      serverStopped();
    }
    // Closes the server and disconnects all connects
    else if(command.equals("#close")){
      try{
        close();
      }
      catch (IOException e) {

      }
    }
    // Sets the port
    else if(command.equals("#setport")){
      try{
        int port = Integer.parseInt(task[1]);
        setPort(port);

      }
      // Sets port to 5555 if port is entered incorrectly
      catch(NumberFormatException nf){
        setPort(DEFAULT_PORT);
      }catch (ArrayIndexOutOfBoundsException e){
        setPort(DEFAULT_PORT);
      }
    }
    //Returns the port of the server
    else if(command.equals("#getport")){
      System.out.println(getPort());
    }

    // Starts the server if it is closed or stopped
    else if(command.equals("#start")){
      try{
        listen();
      }catch (IOException e){
        System.out.println("Server error");
      }
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client has connected to the server");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    //System.out.println(client + " Disconnected");
  }

  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    System.out.println(client.getInfo("userLogin") + "has logged off");
  }

  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections

    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}

//End of EchoServer class
