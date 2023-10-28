package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;

import java.util.Scanner;

public class ServerConsole implements ChatIF {

    EchoServer echoServer;
    Scanner fromConsole;

    final public static int DEFAULT_PORT = 5555;
    public ServerConsole(int port){
        echoServer = new EchoServer(port);
        try{
            echoServer.listen();
        }catch (Exception ex){
            System.out.println("Problem Connecting to Server");
        }
        fromConsole = new Scanner(System.in);
    }


    @Override
    public void display(String message)
    {
        System.out.println("SERVER MSG> " + message);
    }

    public void accept(){
        String message;

        try{
            while(true){

                message = fromConsole.nextLine();
                this.display(message);
                echoServer.handleMessageFromServerUI(message);

            }

        }catch(Exception ex){
            System.out.println("Unexpected error while reading from console!");
        }
    }
    public static void main(String[] args){
        int port = 0;
        try{
            port = Integer.parseInt(args[0]);
        }catch(Exception ex){
            port = DEFAULT_PORT;
        }
        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }
}
