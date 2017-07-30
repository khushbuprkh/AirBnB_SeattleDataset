/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_4;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
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
public class AirBnb_4 {

    public static class MapperClass extends Mapper<Object, Text, Text, FloatWritable> {

        Text outKey = new Text();
        FloatWritable outValue = new FloatWritable();
        Float price = 0.F;

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {

                    String str = itr.nextToken();
                    if (i == 1) {
                        outKey.set(str);

                    } else if (i == 2) {
                        if(!str.equals("price")){
                        price = Float.parseFloat(str);
                        outValue.set(price);
                        }
                    }

                    i++;
                }
                context.write(outKey, outValue);
            }

        }

    }

    public static class ReducerClass extends Reducer<Text, FloatWritable, Text, FloatWritable> {
        Float avg=0.F;
        Float sum = 0.F;
        Long count = 0L;

        @Override
        protected void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {

            for (FloatWritable val : values) {
                sum += val.get();
                count++;
                 
            }
            avg = (Float)sum/count;
            context.write(key, new FloatWritable(avg));
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        // TODO code application logic here
                Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Green Taxi");
        job.setJarByClass(AirBnb_4.class);
        job.setMapperClass(MapperClass.class);
        job.setCombinerClass(ReducerClass.class);
        job.setReducerClass(ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}
