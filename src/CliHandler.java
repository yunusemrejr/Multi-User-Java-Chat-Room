import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CliHandler implements Runnable{

    public static ArrayList<CliHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public CliHandler(Socket socket){
        try{
            this.socket=socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMSG("Server: "+clientUsername+"has entered the chat room.");

        }catch(IOException e){
            closeAll(socket,bufferedReader,bufferedWriter);

        }
    }

     @Override
     public void run() {

        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                broadcastMSG(messageFromClient);
            }catch(IOException e){
                closeAll(socket,bufferedReader,bufferedWriter);
                break;
            }
        }

     }

     public void broadcastMSG(String messageToSend){
        for(CliHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeAll(socket, bufferedReader,bufferedWriter);
            }
        }
     }
     public void removeCliHandler(){
        clientHandlers.remove(this);
        broadcastMSG("Server: "+clientUsername+" left the room.");
     }

     public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeCliHandler();
        try{
            if(bufferedReader !=null){
                bufferedReader.close();
            }
            if(bufferedWriter !=null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
     }
 }
