package org.shared.operators;

import org.shared.config.OperatorType;

import java.io.Serializable;
import java.util.function.Supplier;

public class OperatorFactory implements Supplier<MultiQueryAggregationOperator>, Serializable {
    private final OperatorType operatorType;
    private final int fieldCount;
    private final boolean compressBitmaps;

    public OperatorFactory(OperatorType operatorType, int fieldCount, boolean compressBitmaps) {
        this.operatorType = operatorType;
        this.fieldCount = fieldCount;
        this.compressBitmaps = compressBitmaps;
    }


    @Override
    public MultiQueryAggregationOperator get() {
        switch (operatorType) {
            case Optimized:
                return new OptimizedAggregationOperator(fieldCount);
            case AStream:
                return new AStreamAggregationOperator(compressBitmaps);
            default:
                return new BaselineAggregationOperator();
        }

    }
}
