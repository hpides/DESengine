package De.Hpi.Desis.Test;

import java.io.IOException;

import De.Hpi.Desis.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

//  Pathological subscriber
//  Subscribes to one random topic and prints received messages
public class ZeroMqSub
{
    public static void main(String[] args) throws IOException {
        try (ZContext context = new ZContext()) {
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5501");
            MessagePack msgpack = new MessagePack();
            subscriber.subscribe("".getBytes());

            while (true) {
                byte[] raw = subscriber.recv(1);
                if(raw != null) {
                    MessageResult messageResult = msgpack.read(raw,
                            MessageResult.class);
//                if(data!=null) {
//                    System.out.println(byteArrayToInt(data));
//                    System.out.println(data.length);
//                }
//                String data = subscriber.recvStr();
//                    System.out.println(tuple.TIME);
                }
            }
        }
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }

}