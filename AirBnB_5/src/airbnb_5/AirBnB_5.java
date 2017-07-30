/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_5;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB_5 {

    public static class InvertedMapper extends Mapper<Object, Text, Text, Text> {

        Text outKey = new Text();
        Text outValue = new Text();
        Float price = 0.F;

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {

                    String str = itr.nextToken();
                    if(i==0){
                        if(!str.equals("id")){
                        outValue.set(str);
                        }
                    }
                    if (i == 2) {
                        if(!str.equals("price")){
                        price = Float.parseFloat(str);
                        if (price >= 0.00 && price <= 50.00) {
                            outKey.set("0-50");
                            
                        }else if(price >= 51.00 && price <= 100.00) {
                            outKey.set("51-100");
                            
                        }else if(price >= 101.00 && price <= 150.00) {
                            outKey.set("101-150");
                            
                        }else if(price >= 151.00 && price <= 200.00) {
                            outKey.set("151-200");
                            
                        }else if(price >= 201.00 && price <= 250.00) {
                            outKey.set("201-250");
                           
                        }else if(price >= 251.00 && price <= 300.00) {
                            outKey.set("251-300");
                            
                        }else if(price >= 301.00 && price <= 350.00) {
                            outKey.set("301-350");
                           
                        }else if(price >= 351.00 && price <= 400.00) {
                            outKey.set("351-400");
                            
                        }else if(price >= 401.00 && price <= 450.00) {
                            outKey.set("401-450");
                           
                        }else if(price >= 451.00 && price <= 500.00) {
                            outKey.set("451-500");
                           
                        }else if(price >= 501.00){
                            outKey.set("Greater than 500");
                            
                        }
                        }

                    }

                    i++;
                }
                context.write(outKey, outValue);
            }

        }
    }

    public static class InvertedReducer extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            StringBuilder sb = new StringBuilder();
            boolean first = true;

            for (Text id : values) {
                if (first) {
                    first = false;
                } else {
                    sb.append("     ");
                }
                sb.append(id.toString());
            }
            result.set(sb.toString());
            context.write(key, result);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "Inverted Index");
            job.setJarByClass(AirBnB_5.class);
            job.setMapperClass(InvertedMapper.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setReducerClass(InvertedReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            TextOutputFormat.setOutputPath(job, new Path(args[1]));
            job.waitForCompletion(true);
        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            System.out.println("Error in Main" + ex.getMessage());
        }
    }

}
