package De.Hpi.Test;

import De.Hpi.DesisAll.Desis.Dao.Tuple;
import De.Hpi.DesisAll.Desis.Dao.WindowCollection;
import De.Hpi.DesisAll.Desis.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

//  Pathological publisher
//  Sends out 1,000 topics and then one random update per second
public class ZeroMqPub {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:5501");
            MessagePack msgpack = new MessagePack();
            //  Ensure subscriber connection has time to complete
            //  Send out all 1,000 topic messages
            int topicNbr;
            for (topicNbr = 0; topicNbr < 1000; topicNbr++) {
                Thread.sleep(1000);
                String content = "hallo" + topicNbr;
                //test objects
//                Tuple tuple = new Tuple();
//                tuple.TIME = (int) System.currentTimeMillis();
//                tuple.DATA = topicNbr;
//                tuple.EVENT = 1;
                MessageResult messageResult = new MessageResult();
                WindowCollection windowCollection = new WindowCollection();

//                window.tupleCounter =52;
                messageResult.windowCollection = windowCollection;
//                messageResult.window.tuples = new ArrayList<>();

                for(int i=0; i< 1000; i++){
                    Tuple tuple = new Tuple();
                    tuple.TIME = (int) System.currentTimeMillis();
                    tuple.DATA = topicNbr;
                    tuple.EVENT = 1;
//                    messageResult.window.tuples.add(tuple);
                }

                byte[] raw = msgpack.write(messageResult);
                publisher.send(raw);
//                System.out.println(" content: " + window.tupleCounter);
                System.out.println(" size: " + raw.length);
//                publisher.send(topicNbr+"");
//                System.out.println("Time:"+topicNbr);
            }
        }
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }


}
