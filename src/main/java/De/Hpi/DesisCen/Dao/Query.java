package De.Hpi.DesisCen.Dao;

import org.msgpack.annotation.Message;

@Message
public class Query {

    //if there is original query semantic
    private String entireQuery;

    //basic attribute
    private int queryId;
    private int key;
    private int function;
    private double functionAddition;
    private int windowType;
    private int scenario;

    //window punctuation
    private int range;
    private int slide;
    private int waterMark;
    private int startPunctuation;
    private int endPunctuation;
    private int batchSize;




    public String getEntireQuery() {
        return entireQuery;
    }

    public void setEntireQuery(String entireQuery) {
        this.entireQuery = entireQuery;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int object) {
        this.key = object;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public int getWindowType() {
        return windowType;
    }

    public void setWindowType(int upperBound) {
        this.windowType = upperBound;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public int getStartPunctuation() {
        return startPunctuation;
    }

    public void setStartPunctuation(int startPunctuation) {
        this.startPunctuation = startPunctuation;
    }

    public int getScenario() {
        return scenario;
    }

    public void setScenario(int scenario) {
        this.scenario = scenario;
    }

    public int getEndPunctuation() {
        return endPunctuation;
    }

    public void setEndPunctuation(int endPunctuation) {
        this.endPunctuation = endPunctuation;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getSlide() {
        return slide;
    }

    public void setSlide(int slide) {
        this.slide = slide;
    }

    public int getWaterMark() {
        return waterMark;
    }

    public void setWaterMark(int waterMark) {
        this.waterMark = waterMark;
    }

    public double getFunctionAddition() {
        return functionAddition;
    }

    public void setFunctionAddition(double functionAddition) {
        this.functionAddition = functionAddition;
    }

}
