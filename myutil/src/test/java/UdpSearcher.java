import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpSearcher {
    public static void main(String[] args) throws IOException {
        var ds = new DatagramSocket();

        var tmp = new DatagramPacket("hello".getBytes(StandardCharsets.UTF_8),"hello".length(), InetAddress.getLocalHost(),20000);
        ds.send(tmp);

        var buf = new byte[512];
        var pack = new DatagramPacket(buf, buf.length);
        ds.receive(pack);
        var ip = pack.getAddress().getHostAddress();
        var port = pack.getPort();
        var len = pack.getLength();
        var data = new String(pack.getData(), StandardCharsets.UTF_8);
        System.out.println(data);
        ds.close();
    }
}
