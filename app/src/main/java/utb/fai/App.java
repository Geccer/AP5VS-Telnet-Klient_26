package utb.fai;

public class App {

    public static void main(String[] args) {
        if(args.length < 2){
            System.err.println("Pouziti: <IP adresa> <port>");
            System.exit(1);
        }
        
        String ip = args[0];
        int port;
        try{
            port = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            System.err.println("Neplatne cislo portu: " + args[1]);
            System.exit(1);
            return;
        }

        if(port < 1 || port > 65535){
            System.err.println("Port musi byt v rozsahu 1-65535.");
            System.exit(1);
        }

        TelnetClient telnetClient = new TelnetClient(ip, port);
        telnetClient.run(); // run telnet client
    }
}
