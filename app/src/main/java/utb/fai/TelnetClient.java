package utb.fai;

import java.io.*;
import java.net.*;

import com.google.common.io.ByteArrayDataInput;

public class TelnetClient {

    private static final String QUIT_COMMAND = "/QUIT";

    private String serverIp;
    private int port;

    private volatile boolean running = true;

    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try (Socket socket = new Socket(serverIp, port);){
            InputStream socketIn = socket.getInputStream();
            OutputStream socketOut = socket.getOutputStream();
           
            Thread receiver = new Thread(() -> receiveLoop(socketIn), "receiver");
            receiver.setDaemon(true);

            Thread sender = new Thread(() -> sendLoop(socketOut), "sender");
            sender.setDaemon(true);

            receiver.start();
            sender.start();

            while(running){
                Thread.sleep(20);
            }

            receiver.join(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            running = false;
            System.out.flush();
        }
    }

    private void receiveLoop(InputStream socketIn){
        byte[] buffer = new byte[4090];
        try{
            while(running){
                int available = socketIn.available();
                if(available > 0){
                    int read = socketIn.read(buffer, 0, Math.min(available, buffer.length));
                    if(read == -1){
                        break;
                    }
                    System.out.write(buffer, 0, read);
                    System.out.flush();
                }else{
                    Thread.sleep(20);
                }
            }
        }catch(IOException e){

        }catch(InterruptedException e ){
            Thread.currentThread().interrupt();
        }finally{
            running = false;
        }
    }

    void sendLoop(OutputStream socketOut){
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        try{
            while(running){
                int available = System.in.available();
                if(available > 0){
                    int b = System.in.read();
                    if(b== -1){
                        break;
                    }
                    if(b == '\n'){
                        String text = line.toString("UTF-8");
                        if(text.endsWith("\r")){
                            text = text.substring(0, text.length() - 1);
                        }
                        if(text.equals(QUIT_COMMAND)){
                            break;
                        }
                        socketOut.write(text.getBytes("UTF-8"));
                        socketOut.write('\n');
                        socketOut.flush();
                        line.reset();
                    }else{
                        line.write(b);
                    }
                }else{
                    Thread.sleep(20);
                }
            }
        }catch(IOException e){

        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }finally{
            running = false;
        }
    }
}
