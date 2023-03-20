package De.Hpi.DesisQuantile.Dao;

import org.msgpack.annotation.Message;

@Message
public class Tuple {

    public int TIME;
    public double DATA;
    public int EVENT;

}
