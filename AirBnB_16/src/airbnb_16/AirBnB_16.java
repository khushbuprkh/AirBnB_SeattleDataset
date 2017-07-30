/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_16;

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
public class AirBnB_16 {

    public static class MapperClass extends Mapper<Object, Text, Text, CustomWritableClass> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            Text host_neighbourhood = new Text();
            Float min_Security_Deposit = 0F;
            Float max_Security_Deposit = 0F;
            Float average_Security_Deposit = 0.F;
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

                    if (i == 33) {
                        if (!str.equals("security_deposit")) {
                            min_Security_Deposit = Float.parseFloat(str);
                            max_Security_Deposit = Float.parseFloat(str);
                            average_Security_Deposit = Float.parseFloat(str);

                        }
                    }

                i++;
                }
              
            }
            cw.setMin_Security_Deposit(min_Security_Deposit);
            cw.setMax_Security_Deposit(max_Security_Deposit);
            cw.setAverage_Security_Deposit(average_Security_Deposit);

            context.write(host_neighbourhood, cw);
            
        }

    }

    public static class ReducerClass extends Reducer<Text, CustomWritableClass, Text, CustomWritableClass> {

        @Override
        public void reduce(Text key, Iterable<CustomWritableClass> values, Context context) throws IOException, InterruptedException {
            Float average_Security_Deposit = 0F;
            Long count = 0L;
            Float min_Security_Deposit = 0F;
            Float max_Security_Deposit = 0F;
            

            Float max = Float.MIN_VALUE;
            Float min = Float.MAX_VALUE;

            CustomWritableClass cw = new CustomWritableClass();
            for (CustomWritableClass val : values) {
                if (val.getMin_Security_Deposit()> max) {
                    max=val.getMin_Security_Deposit();
                    max_Security_Deposit = val.getMin_Security_Deposit();
                }
                if (val.getMin_Security_Deposit()< min) {
                    min=val.getMin_Security_Deposit();
                    min_Security_Deposit = val.getMin_Security_Deposit();
                }

                average_Security_Deposit += val.getAverage_Security_Deposit();
                count++;
            }
            average_Security_Deposit = (Float) average_Security_Deposit / count;
            cw.setMin_Security_Deposit(min_Security_Deposit);
            cw.setMax_Security_Deposit(max_Security_Deposit);
            cw.setAverage_Security_Deposit(average_Security_Deposit);

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
            job.setJarByClass(AirBnB_16.class);

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
