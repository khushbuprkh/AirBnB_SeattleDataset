/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_10;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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
public class AirBnB_10 {

    public static class MapperClass extends Mapper<Object, Text, Text, CustomWritableClass> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            Text host_neighbourhood = new Text();
            Integer min_Bedrooms = 0;
            Integer max_Bedrooms = 0;
            Float average_Price = 0.F;
            CustomWritableClass cw = new CustomWritableClass();

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {
                    String str = itr.nextToken();
                    if (i == 1) {
                        if (!str.equals("host_neighbourhood")) {
                            host_neighbourhood.set(str);

                        }
                    }

                    if (i == 26) {
                        if (!str.equals("bedrooms")) {
                            min_Bedrooms = Integer.parseInt(str);
                            max_Bedrooms = Integer.parseInt(str);

                        }
                    }

                    if (i == 2) {
                        if (!str.equals("price")) {
                            average_Price = Float.parseFloat(str);
                        }
                    }
                i++;
                }
              
            }
            cw.setMax_Bedrooms(min_Bedrooms);
            cw.setMin_Bedrooms(max_Bedrooms);
            cw.setAverage_Price(average_Price);

            context.write(host_neighbourhood, cw);
            
        }

    }

    public static class ReducerClass extends Reducer<Text, CustomWritableClass, Text, CustomWritableClass> {

        @Override
        public void reduce(Text key, Iterable<CustomWritableClass> values, Context context) throws IOException, InterruptedException {
            Float averagePrice = 0F;
            Long count = 0L;
            Integer min_Bedrooms = 0;
            Integer max_Bedrooms = 0;

            Integer max = Integer.MIN_VALUE;
            Integer min = Integer.MAX_VALUE;

            CustomWritableClass cw = new CustomWritableClass();
            for (CustomWritableClass val : values) {
                if (val.getMin_Bedrooms() > max) {
                    max=val.getMin_Bedrooms();
                    max_Bedrooms = val.getMin_Bedrooms();
                }
                if (val.getMin_Bedrooms() < min) {
                    min=val.getMin_Bedrooms();
                    min_Bedrooms = val.getMin_Bedrooms();
                }

                averagePrice += val.getAverage_Price();
                count++;
            }
            averagePrice = (Float) averagePrice / count;
            cw.setMin_Bedrooms(min_Bedrooms);
            cw.setMax_Bedrooms(max_Bedrooms);
            cw.setAverage_Price(averagePrice);

            context.write(key, cw);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "MinMax");
            job.setJarByClass(AirBnB_10.class);

            job.setMapperClass(MapperClass.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(CustomWritableClass.class);
            job.setReducerClass(ReducerClass.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(CustomWritableClass.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            System.exit(job.waitForCompletion(true) ? 0 : 1);

        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            System.out.println("Erorr Message" + ex.getMessage());
        }
    }

}
