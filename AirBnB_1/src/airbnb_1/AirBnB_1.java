/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_1;

import java.io.IOException;
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
public class AirBnB_1 {

    public static class MapperClass extends Mapper<Object, Text, Text, IntWritable> {

        Text outKey = new Text();
        IntWritable outValue = new IntWritable();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String val[] = value.toString().split(",");
            if(!val[5].equals("host_id")){
               
                 outKey.set(val[5].toString());
                
                 outValue.set(1);
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
    
    public static class SecondMapperClass extends Mapper<Object, Text, Text, IntWritable> {

        Text outKey = new Text();
        Integer count=0;
        IntWritable outValue = new IntWritable();

        @Override
        protected void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {

            String val[] = value.toString().split("\t");
            if(val[1].equals("1")){
               
                 outKey.set("Single Listing");
                
                 outValue.set(1);
            }
            else {
            
                 outKey.set("Multiple Listing");
                 count = Integer.parseInt(val[1]);
                 outValue.set(count);
            }
            context.write(outKey, outValue);
            }
        }

            
    public static class SecondReducerClass extends Reducer<Text, IntWritable, Text, IntWritable> {

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
        job.setJarByClass(AirBnB_1.class);
        job.setMapperClass(MapperClass.class);
        job.setCombinerClass(ReducerClass.class);
        job.setReducerClass(ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        boolean complete =job.waitForCompletion(true);
        if(complete){
        Configuration conf2= new Configuration();
        Job job2= Job.getInstance(conf2,"chaining");
        job2.setJarByClass(AirBnB_1.class);
        job2.setMapperClass(SecondMapperClass.class);
        job2.setReducerClass(SecondReducerClass.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));
        System.exit(job2.waitForCompletion(true) ?0 :1);
    }

    }
    
}
