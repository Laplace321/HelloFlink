package com.laplace.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

// TODO ：DataSet API 实现 wordcount,已经过时，不推荐，官方推荐使用 datastream；

public class WordCountBatchDemo {
    public static void main(String[] args) throws Exception {
        // TODO 1. 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // TODO 2. 读取数据
        DataSource<String> lineDS = env.readTextFile("input/word.txt");
        // TODO 3. 按行切分、转换 -> (word,1)
        FlatMapOperator<String, Tuple2<String, Integer>> wordAndOne = lineDS.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                // TODO 3.1 按照空格切分单词
                String[] words = s.split(" ");
                //TODO 3.2 将单词转换为（word,1)
                for (String word : words) {
                    Tuple2<String, Integer> wordTuple2 = Tuple2.of(word, 1);
                    // TODO 3.3 使用 Collector 向下游发送数据
                    collector.collect(wordTuple2);
                }
            }
        });
        // TODO 4. 按照 word 分组
        UnsortedGrouping<Tuple2<String, Integer>> wordAndOneGroupBy = wordAndOne.groupBy(0);
        // TODO 5. 按各分组内聚合
        AggregateOperator<Tuple2<String, Integer>> sum = wordAndOneGroupBy.sum(1); //1是位置，表示第二个元素
        // TODO 6. 输出
        sum.print();
        // 其实就是 Mapreduce 的思维
    }
}
