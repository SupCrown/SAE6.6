package androidgameserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Thread TCP du serveur : écoute sur un port fixe et crée un {@link ClientHandler}
 * dans un nouveau thread pour chaque client qui se connecte.
 * Affiche l'adresse IP locale au démarrage pour faciliter la connexion des bots.
 */
public class ServerThread extends Thread
{ 
	int port;
    private int connectedPlayerCount = 0;
	
	
	public ServerThread(int port) {
		this.port = port;
	}

    public int getConnectedPlayerCount() {
        return connectedPlayerCount;
    }

	public void run()
	{ 
		System.out.println("Server thread starting"); 
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Server address :");
			showLocalAddress();
			System.out.println("listening on port :" + port);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");
                connectedPlayerCount++;
                System.out.println("Nombre de joueurs connectés : " + connectedPlayerCount);
				BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter dos = new PrintWriter(socket.getOutputStream());
				System.out.println("Assigning new thread for this client : "
						+ socket.getInetAddress().getHostName());
				socket.setSoTimeout(AppDefines.connectionTimeoutDelay); // millisec
				//socket.setSoLinger(false, 5);
				Thread t = new Thread(new ClientHandler(socket, dis, dos));
				t.start();
			}
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	} 
	
	private void showLocalAddress()
	{
		try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        System.out.println(interfaceAddress.getAddress().getHostAddress());
                   // else
                   // 	System.out.println("not local : "+interfaceAddress.getAddress().getHostAddress());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
	}
} 
