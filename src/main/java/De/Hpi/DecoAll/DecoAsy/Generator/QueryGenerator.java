package De.Hpi.DecoAll.DecoAsy.Generator;

import De.Hpi.DecoAll.DecoAsy.Configure.Configuration;
import De.Hpi.DecoAll.DecoAsy.Dao.Query;
import De.Hpi.DecoAll.DecoAsy.Message.MessageToLocal;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueryGenerator {

    private ConcurrentLinkedQueue<MessageToLocal> messageToLocals;
    private ConcurrentLinkedQueue<Query> queryList;
    private Configuration conf;
    private int queryCounter;
    private boolean flag;

    public QueryGenerator(ConcurrentLinkedQueue<MessageToLocal> messageToLocals,
                          ConcurrentLinkedQueue<Query> queryList,
                          Configuration conf){
        this.conf = conf;
        this.messageToLocals = messageToLocals;
        this.queryList = queryList;
        this.queryCounter = 0;
    }

    public void generate(){

        try {
            //totally 20 queries
            //object, function, windowType, range, slide, startPunctuation, endPunctuation, warterMark, Batch size.

                initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                        10000, 0, 0, 0, 0, 0,0.5);


//            initializeQuery(Configuration.SPEED,Configuration.AVERAGE,Configuration.TUMBING,
//                    1000, 1000, 0, 1000, 0, 0,0.5);
//            initializeQuery(Configuration.SPEED,Configuration.MEDIAN,Configuration.TUMBING,
//                    conf.queryModes*1000, 1000, 0, 1000, 0, 0,0.5);
            switch (conf.queryModes*100000){
                 case 41:{
                    for(int i = 0; i < 210000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.SUM, Configuration.SLIDING,
                                2000*(i%10 + 1), 1000*(i%10 + 1), 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.COUNT, Configuration.PUNCTUATION,
                                1000, 1000, 0, 1000*(i%10 + 1), 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.MAX, Configuration.PUNCTUATION,
                                1000, 1000, 0, 1000*(i%10 + 1), 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.MIN, Configuration.TUMBING,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                default:{
                    break;
                }
            }

            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeQuery(int Object, int function, int windowType, int range, int slide
            , int startPunctuation, int endPunctuation, int waterMark, int batchSize, double functionAddition) {

        Query query = new Query();
        query.setQueryId(queryCounter++);
//        System.out.println(query.getQueryId());
        query.setKey(Object);
        query.setFunction(function);
        query.setFunctionAddition(functionAddition);
        query.setWindowType(windowType);
        query.setRange(range);
        query.setSlide(slide);
        query.setStartPunctuation(startPunctuation);
        query.setEndPunctuation(endPunctuation);
        query.setWaterMark(waterMark);
        query.setBatchSize(batchSize);
        query.setScenario(classifyQuery(query, conf));
        query.setEntireQuery(Object + "," + function
                + "," + windowType + "," + range + "," + slide
                + "," + startPunctuation + "," + endPunctuation
                + "," + waterMark + "," + batchSize
                );

        //to send the query
        MessageToLocal messageToLocal = new MessageToLocal();
        messageToLocal.setMessageType(1);
        messageToLocal.setNodeId(0);
        messageToLocal.query = query;
        messageToLocals.offer(messageToLocal);
        queryList.add(query);

//        if(query.getScenario() == conf.DeCentralizedAggregation){
//            queryListDecentral.offer(query);
//            conf.setQueryNumberDecentral(conf.getQueryNumberDecentral()+1);
//        } else{
//            queryListCenral.offer(query);
//            conf.setQueryNumberCentral(conf.getQueryNumberCentral()+1);
//        }
    }

    private int classifyQuery(Query query, Configuration conf){
        if(query.getFunction() == conf.MEDIAN || query.getFunction() == conf.QUANTILE
                || query.getWindowType() == conf.COUNTBASED  ){
            return conf.CentralizedAggregation;
        }else{
            return conf.DeCentralizedAggregation;
        }
    }

}
