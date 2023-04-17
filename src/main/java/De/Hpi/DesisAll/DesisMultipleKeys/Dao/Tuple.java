package De.Hpi.DesisAll.DesisMultipleKeys.Dao;

import org.msgpack.annotation.Message;

@Message
public class Tuple {

    public int TIME;
    public int KEY;
    public double DATA;
    public int EVENT;

}
