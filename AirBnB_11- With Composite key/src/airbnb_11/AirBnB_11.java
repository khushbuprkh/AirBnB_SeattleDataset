/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_11;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB_11 {

    public static class MapperClass extends Mapper<Object, Text, CompositeKeyWritable, CustomWritableClass> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text host_neighbourhood = new Text();
            Integer bedroom = 0;
            Float min_Price = 0F;
            Float max_Price = 0F;
            CompositeKeyWritable ckw = new CompositeKeyWritable();
            CustomWritableClass cw = new CustomWritableClass();

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {

                    String str = itr.nextToken();
                    if (i == 1) {
                        if (!str.equals("host_neighbourhood") && !str.equals(" ") && !str.equals("null") && !str.equals(null)) {
                            ckw.setHost_neighbourhood(str);
                            host_neighbourhood.set(str);
                        }
                    }

                    if (i == 2) {
                        if (!str.equals("price") && !str.equals(" ") && !str.equals("null") && !str.equals(null) && str != "0") {
                            min_Price = Float.parseFloat(str);
                            if (min_Price != 0.0) {
                                cw.setMin_Price(Float.parseFloat(str));
                                cw.setMax_Price(Float.parseFloat(str));
                            }
                        }
                    }

                    if (i == 26) {
                        if (!str.equals("bedrooms") && !str.equals("null")) {
                            ckw.setBedroom(str);
                        }
                    }

                    i++;
                }
            }
            context.write(ckw, cw);

        }
    }

    public static class ReducerClass extends Reducer<CompositeKeyWritable, CustomWritableClass, CompositeKeyWritable, CustomWritableClass> {

        @Override
        public void reduce(CompositeKeyWritable key, Iterable<CustomWritableClass> values, Context context) throws IOException, InterruptedException {

            Float min_Price = 0F;
            Float max_Price = 0F;

            Float max = Float.MIN_VALUE;
            Float min = Float.MAX_VALUE;

            CustomWritableClass cw = new CustomWritableClass();
            for (CustomWritableClass val : values) {
                if (val.getMin_Price() > max) {
                    max = val.getMin_Price();
                    max_Price = val.getMin_Price();
                }
                if (val.getMax_Price() < min) {
                    min = val.getMin_Price();
                    min_Price = val.getMin_Price();
                }

            }

            cw.setMin_Price(min_Price);
            cw.setMax_Price(max_Price);

            context.write(key, cw);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "SecondarySort");
            job.setJarByClass(AirBnB_11.class);

            job.setMapperClass(MapperClass.class);
            job.setMapOutputKeyClass(CompositeKeyWritable.class);
            job.setMapOutputValueClass(CustomWritableClass.class);

           // job.setGroupingComparatorClass(GroupComparator.class);
            //     job.setPartitionerClass(PartitionerClass.class);
            job.setReducerClass(ReducerClass.class);
            job.setOutputKeyClass(CompositeKeyWritable.class);
            job.setOutputValueClass(CustomWritableClass.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            System.exit(job.waitForCompletion(true) ? 0 : 1);

        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            System.out.println("Erorr Message" + ex.getMessage());
        }

    }

}
