package De.Hpi.Disco.Source.aggregation;

public interface AlgebraicPartial<PartialType, ResultType> {
    Double lower();
    PartialType merge(PartialType other);

    PartialType fromString(String s);
    String asString();
}
