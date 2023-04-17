package De.Hpi.DesisAll.DesisCen.MessageManager;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.Dao.Tuple;
import De.Hpi.DesisAll.DesisCen.Dao.Window;
import De.Hpi.DesisAll.DesisCen.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RootSubscribeMassage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<Window> resultFromIntermediaDecentral;
    private ConcurrentLinkedQueue<Tuple> resultFromIntermediaCentral;
    private ZMQ.Socket socketSub;

    public RootSubscribeMassage(ConcurrentLinkedQueue<Window> resultFromIntermediaDecentral
            , ConcurrentLinkedQueue<Tuple> resultFromIntermediaCentral
            , Configuration conf, ZMQ.Socket socketSub) {
        this.resultFromIntermediaDecentral =resultFromIntermediaDecentral;
        this.resultFromIntermediaCentral =resultFromIntermediaCentral;
        this.socketSub = socketSub;
        this.conf = conf;
    }

    public void run() {
        MessagePack msgpack = new MessagePack();
        socketSub.subscribe("".getBytes());
        while (true) {
            byte[] raw = socketSub.recv(1);
            if(raw!=null) {
                try {
                    MessageResult messageResult = msgpack.read(raw,
                            MessageResult.class);
                    if(messageResult.window.getScenario() == conf.DeCentralizedAggregation){
                        resultFromIntermediaDecentral.offer(messageResult.window);
                    }else{
                        //needs to add buffer
                        resultFromIntermediaCentral.addAll(messageResult.window.tuples);
                    }
                    if(conf.DEBUGMODE) {
                    System.out.println("LowerResponseThread----intermediaNode-----"
                            + messageResult.getNodeId() + " "
                            + messageResult.window.getWindowId() + "    "
                            + messageResult.window.count + "    "
                            + messageResult.window.tuples.size());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
