package org.shared.datagenerator;

import java.io.Serializable;
import java.util.List;

public interface DataSource extends Serializable {
    public Tuple nextTuple() throws Exception;
    public void initialize() throws Exception;
    public void close() throws Exception;
    public int getFieldCount();
    public List<Integer> getMinFieldValues();
    public List<Integer> getMaxFieldValues();
}
