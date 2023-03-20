package De.Hpi.DesisQuantile.Generator;

import De.Hpi.DesisQuantile.Configure.Configuration;
import De.Hpi.DesisQuantile.Dao.Query;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueryGenerator {

    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<Query> queryList;
    private Configuration conf;
    private int queryCounter;
    private boolean flag;

    public QueryGenerator(ConcurrentLinkedQueue<Query> queryQueue,
                          ConcurrentLinkedQueue<Query> queryList,
                          Configuration conf){
        this.conf = conf;
        this.queryQueue = queryQueue;
        this.queryList = queryList;
        this.queryCounter = 0;
    }

    public void generate(){

        try {
            //totally 20 queries
            //object, function, windowType, range, slide, startPunctuation, endPunctuation, warterMark, Batch size.
//            initializeQuery(Configuration.SPEED,Configuration.MEDIAN,Configuration.TUMBING,
//                    1000, 1000, 0, 1000, 0, 0,0.1);
            switch (conf.queryModes){
                case 0:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.QUANTILE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
//                        initializeQuery(Configuration.SPEED,Configuration.QUANTILE,Configuration.PUNCTUATION,
//                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 1:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.QUANTILE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
//                        initializeQuery(Configuration.SPEED,Configuration.MAX,Configuration.TUMBING,
//                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 2:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.QUANTILE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.MAX, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 3:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.QUANTILE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 4:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.MAX, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 5:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.SUM, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 6:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 7:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.PUNCTUATION,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 8:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.PUNCTUATION,
                                1000*(i%10 + 1), 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
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
        queryQueue.offer(query);
        queryList.offer(query);


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
