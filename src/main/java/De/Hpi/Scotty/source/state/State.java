package De.Hpi.Scotty.source.state;

import java.io.Serializable;

public interface State extends Serializable {

    void clean();

    boolean isEmpty();

}
