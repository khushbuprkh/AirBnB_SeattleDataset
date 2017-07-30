/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB {

    /**
     * @param args the command line arguments
     */
    public static class MapperClass extends Mapper<Object, Text, Text, IntWritable> {

        Text outKey = new Text();
        IntWritable outValue = new IntWritable();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {

                    String str = itr.nextToken();
                    if (i == 5) {
                        outKey.set(str);
                        outValue.set(1);
                    }

                    i++;
                }
                context.write(outKey, outValue);
            }

        }

    }

    public static class ReducerClass extends Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                Context context) throws IOException, InterruptedException {
            Integer count = 0;
            IntWritable outValue = new IntWritable();

            for (IntWritable val : values) {
                count += val.get();
            }

            outValue.set(count);
            context.write(key, outValue);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        // TODO code application logic here

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Green Taxi");
        job.setJarByClass(AirBnB.class);
        job.setMapperClass(MapperClass.class);
        job.setCombinerClass(ReducerClass.class);
        job.setReducerClass(ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
