package com.laplace.wc;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCountStreamUnboundedDemo {
    public static void main(String[] args) throws Exception {
        //TODO 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //TODO 2.读取数据 socket
        DataStreamSource<String> socketDS = env.socketTextStream("localhost", 9999);
        //TODO 3. 处理数据，输出
        socketDS.flatMap(
                (String s, Collector<Tuple2<String, Integer>> collector) -> {
                    String[] words = s.split(" ");
                    for (String word : words) {
                        collector.collect(Tuple2.of(word,1));
                    }
                }
        ).returns(Types.TUPLE(Types.STRING,Types.INT)).keyBy(
             //   (Tuple2<String, Integer> value) -> {return value.f0;}
             value -> value.f0
        ).sum(1).print();
        //TODO 5.执行
        env.execute();
    }
}
