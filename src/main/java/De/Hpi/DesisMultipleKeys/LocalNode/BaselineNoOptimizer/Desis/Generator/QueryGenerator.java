package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Generator;

import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Configure.Configuration;
import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.Query;

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

                initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                        10000*conf.queryModes, 1000, 0, 1000, 0, 0,0.5);
                initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                        10000, 1000, 0, 1000, 0, 0,0.5);

//            initializeQuery(Configuration.SPEED,Configuration.AVERAGE,Configuration.TUMBING,
//                    1000, 1000, 0, 1000, 0, 0,0.5);
//            initializeQuery(Configuration.SPEED,Configuration.MEDIAN,Configuration.TUMBING,
//                    conf.queryModes*1000, 1000, 0, 1000, 0, 0,0.5);
            switch (conf.queryModes*100000){
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

                //for single query
                case 11:{
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 12:{
                    initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.SLIDING,
                            3000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 13:{
                    initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.PUNCTUATION,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 14:{
                    initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.PUNCTUATION,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 15:{
                    initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                            10000000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }

                case 21:{
                    initializeQuery(Configuration.SPEED, Configuration.MEDIAN, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 22:{
                    initializeQuery(Configuration.SPEED, Configuration.QUANTILE, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 23:{
                    initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 24:{
                    initializeQuery(Configuration.SPEED, Configuration.SUM, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 25:{
                    initializeQuery(Configuration.SPEED, Configuration.COUNT, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 26:{
                    initializeQuery(Configuration.SPEED, Configuration.MAX, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }
                case 27:{
                    initializeQuery(Configuration.SPEED, Configuration.MIN, Configuration.TUMBING,
                            1000, 1000, 0, 1000, 0, 0,0.5);
                    break;
                }

                case 31:{
                    for(int i = 0; i <= 1000; i++){
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.TUMBING,
                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                                10000000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }
                case 32:{
                    for(int i = 0; i <= 1000; i++){
//                        initializeQuery(Configuration.SPEED,Configuration.QUANTILE,Configuration.COUNTBASED,
//                                1000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                        initializeQuery(Configuration.SPEED, Configuration.AVERAGE, Configuration.COUNTBASED,
                                10000000, 1000, 0, 1000, 0, 0,(i%999 + 1) / 1000.0);
                    }
                    break;
                }

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
