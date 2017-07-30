/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_6;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB_6 {

    public static class InvertedMapper extends Mapper<Object, Text, Text, Text> {

        Text outKey = new Text();
        Text outValue = new Text();
        Integer bedroom = 0;

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {

                    String str = itr.nextToken();
                    if (i == 0) {
                        if (!str.equals("id")) {
                            outValue.set(str);
                        }
                    }
                    if (i == 26) {
                        if (!str.equals("bedrooms")) {
                            bedroom = Integer.parseInt(str);
                            outKey.set(bedroom + "-Bedrooms");
                            
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
            job.setJarByClass(AirBnB_6.class);
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
