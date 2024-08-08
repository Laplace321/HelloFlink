package com.laplace.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 *  TODO DataStream 实现 wordCount：读文件（有界流）
 */
public class WorldCountStreamDemo {
    public static void main(String[] args) throws Exception {
        // TODO 1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // TODO 2.读取数据
        DataStreamSource<String> lineDS = env.readTextFile("input/word.txt");
        // TODO 3.处理数据
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAneOne = lineDS.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] words = s.split(" ");
                for (String word : words) {
                    Tuple2<String, Integer> wordAndOne = Tuple2.of(word, 1);
                    collector.collect(wordAndOne);
                }
            }
        });
        KeyedStream<Tuple2<String, Integer>, String> wordAndOneKS = wordAneOne.keyBy(
                new KeySelector<Tuple2<String, Integer>, String>() {
                    @Override
                    public String getKey(Tuple2<String, Integer> value) throws Exception {
                        return value.f0;
                    }
                }
        );
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = wordAndOneKS.sum(1);

        // TODO 4.输出数据
        sum.print();
        // TODO 5.执行：类似sparkstreaming 最后 ssc.start()
        env.execute();
    }
}

/**
 * 接口 A ，里面有一个方法 a()
 * 正常写法，定义一个 class B，需要实现接口 A，方法 a()
 * B b = new B()
 * 另一种写法匿名实现类
 * new A() {
 *     a(){
 *
 *     }
 * }
 *
 */