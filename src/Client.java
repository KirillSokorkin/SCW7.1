import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.HashSet;

public class Client {
    public static void main(String[] args) throws IOException {

        SocketChannel outcoming = SocketChannel.open();
        try {
            outcoming.connect(new InetSocketAddress(InetAddress.getLocalHost() , 2605));
            System.out.println("Добро пожаловать)");
            outcoming.socket().setSoTimeout(10000);
            try (ObjectOutputStream SendtoServer = new ObjectOutputStream(outcoming.socket().getOutputStream());
                 ObjectInputStream GetfromServer = new ObjectInputStream(outcoming.socket().getInputStream())
            ) {
                CComandReader clientreader = new CComandReader(GetfromServer, SendtoServer);
                System.out.println((String) GetfromServer.readObject());
                System.out.println(">Начало работы. Введите help чтобы ознакомится со списком команд.");
                clientreader.start_reading(new HashSet<String>(), "");
                System.out.println(">Завершение работы, Берегите себя и своих близких");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }  catch (IOException e) {
            System.out.println(">Подключение не выполнено");
            System.out.println(e.getMessage());
        }
    }
}