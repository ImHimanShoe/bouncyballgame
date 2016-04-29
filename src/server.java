import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages.
 */
public class server {
	static Game g;

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 4;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) {
	   // new Thread(new Game(2)).start();
	    Game g2 = new Game(2);
	    g  = g2;
	    System.out.println("From The server just after game object");		              
	  //g.processdata();
    // The default port number.
    int portNumber = 3333;
    if (args.length < 1) {
      System.out.println("Usage: java server <portNumber>\n"
          + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }
    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
    while (true) {
      try {
    	  System.out.println("From The server in 50 th line");		      
        clientSocket = serverSocket.accept();
        int i = 0;


        DataInputStream is = new DataInputStream(clientSocket.getInputStream());
        // will connect the client to server or supress the computer player at that spot; 
        String output = is.readLine();
        System.out.println("the output fot the current player is " +output);
          g.connectclient(output);
          for (i = 0; i < maxClientsCount; i++) {
              if (threads[i] == null) {
                (threads[i] = new clientThread(clientSocket, threads)).start();
                
                 break;
              
              }
            }

          if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

  private String clientName = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      //String name = " ";
      /*while (true) {
        os.println("Enter your name.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          os.println("The name should not contain '@' character.");
        }
      }*/

      ///* Welcome the new the client. */
      /*os.println("Welcome " + name
          + " to our chat room.\nTo leave enter /quit in a new line.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = "@" + name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].os.println("*** A new user " + name
                + " entered the chat room !!! ***");
          }
        }
      }*/
      /* Start the conversation. */
      while (true) {
    	  Game g = Game.gameobject;     
    	  String line = is.readLine();
    	  System.out.println("the String send by client is "+ line );
    	  g.serverprocessor(line);
          if (line.startsWith("/quit")) {
          break;
          
          }

    	  System.out.println("From The server in 142 th line");
          boolean t = true;
        /* If the message is private sent it to the given client. */
        if (t) {
          //String[] words = line.split("\\s", 2);
          
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                	//<------------------>
                	String output = g.serversend();
                    if (threads[i] != null && threads[i] == this) {
                    threads[i].os.println(output);
                    /*
                     * Echo this message to let the client know the private
                     * message was sent.
                     */
                   // this.os.println(">" + name + "> " + words[1]);
                    //break;
                  
                    }
               
            }
          }
        } else {
          /* The message is public, broadcast it to all other clients. */
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
               // threads[i].os.println("<" + name + "> " + line);
              }
            }
          }
        }
      }
     /* synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
       //     threads[i].os.println("*** The user " + name
         //       + " is leaving the chat room !!! ***");
          }
        }
      }
     // os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}