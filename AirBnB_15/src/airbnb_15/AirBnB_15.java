/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_15;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author khushbuprkh
 */
public class AirBnB_15 {

    public static class MapperClass extends Mapper<Object, Text, CompositeKeyWritable, NullWritable> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            Text listing_Id = new Text();
            Integer review_scores_rating = 0;
            Integer review_scores_accuracy = 0;
            Integer review_scores_cleanliness = 0;
            Integer review_scores_checkin = 0;
            Integer review_scores_communication = 0;
            Integer review_scores_location = 0;
            Integer review_scores_value = 0;
            Integer totalRating = 0;
            Float percentage = 0F;

            CompositeKeyWritable ckw = new CompositeKeyWritable();

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            while (itr.hasMoreTokens()) {
                int i = 0;
                while (itr.hasMoreTokens()) {
                    String str = itr.nextToken();
                    if (i == 0) {
                        if (!str.equals("id")) {
                            listing_Id.set(str);

                        }
                    }

                    if (i == 48) {
                        if (!str.equals("review_scores_rating")) {
                            review_scores_rating = Integer.parseInt(str);
                        }
                    }

                    if (i == 49) {
                        if (!str.equals("review_scores_accuracy")) {
                            review_scores_accuracy = Integer.parseInt(str);
                        }
                    }
                    if (i == 50) {
                        if (!str.equals("review_scores_cleanliness")) {
                            review_scores_cleanliness = Integer.parseInt(str);
                        }
                    }
                    if (i == 51) {
                        if (!str.equals("review_scores_checkin")) {
                            review_scores_checkin = Integer.parseInt(str);
                        }
                    }
                    if (i == 52) {
                        if (!str.equals("review_scores_communication")) {
                            review_scores_communication = Integer.parseInt(str);
                        }
                    }
                    if (i == 53) {
                        if (!str.equals("review_scores_location")) {
                            review_scores_location = Integer.parseInt(str);
                        }
                    }
                    if (i == 54) {
                        if (!str.equals("review_scores_value")) {
                            review_scores_value = Integer.parseInt(str);
                        }
                    }

                    i++;
                }

            }

            totalRating = review_scores_rating + review_scores_accuracy + review_scores_cleanliness + review_scores_checkin + review_scores_communication + review_scores_location
                    + review_scores_value;
            percentage = (totalRating / 160.0F) * 100;
            ckw.setListing_Id(listing_Id.toString());
            ckw.setPercentage(percentage);
            context.write(ckw, NullWritable.get());
        }

    }

    public static class ReducerClass extends Reducer<CompositeKeyWritable, NullWritable, CompositeKeyWritable, NullWritable> {

        public static int a = 0;

        @Override
        public void reduce(CompositeKeyWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            for (NullWritable val : values) {
                if (a == 50) {
                    break;
                } else {
                    context.write(key, NullWritable.get());
                    a++;
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "Occupancy Rate");
            job.setJarByClass(AirBnB_15.class);

            job.setMapperClass(MapperClass.class);
            job.setMapOutputKeyClass(CompositeKeyWritable.class);
            job.setMapOutputValueClass(NullWritable.class);
            job.setReducerClass(ReducerClass.class);
            job.setGroupingComparatorClass(GroupComparator.class);
            job.setOutputKeyClass(CompositeKeyWritable.class);
            job.setOutputValueClass(NullWritable.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

           System.exit(job.waitForCompletion(true) ? 0 : 1);
          

        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            System.out.println("Erorr Message" + ex.getMessage());
        }
    }

}
