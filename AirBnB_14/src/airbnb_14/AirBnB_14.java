/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_14;

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
public class AirBnB_14 {

    public static class MapperClass extends Mapper<Object, Text, Text, CustomWritableClass> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            Text listing_Id = new Text();
            Integer minimum_nights = 0;
            Integer number_of_reviews = 0;
            Float price = 0.F;
            CustomWritableClass cw = new CustomWritableClass();

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

                    if (i == 37) {
                        if (!str.equals("minimum_nights")) {
                            minimum_nights = Integer.parseInt(str);
                        }
                    }

                    if (i == 45) {
                        if (!str.equals("number_of_reviews")) {
                            number_of_reviews = Integer.parseInt(str);
                        }
                    }
                    if (i == 2) {
                        if (!str.equals("price")) {
                            price = Float.parseFloat(str);
                        }
                    }
                    i++;
                }

            }
            cw.setMinimum_nights(minimum_nights);
            cw.setNumber_of_reviews(number_of_reviews);
            cw.setPrice(price);

            context.write(listing_Id, cw);

        }

    }

    public static class ReducerClass extends Reducer<Text, CustomWritableClass, Text, CustomWritableOutputClass> {

        @Override
        public void reduce(Text key, Iterable<CustomWritableClass> values, Context context) throws IOException, InterruptedException {
            Float costBasedOnMinDays = 0F;
            Float incomePerYear = 0F;
            Float incomePerMonth = 0F;
            Integer nightsPerYear = 0;
            Long count = 0L;
            Integer minimum_nights = 0;
            Float occupancyRate = 0F;

            CustomWritableOutputClass cow = new CustomWritableOutputClass();
            for (CustomWritableClass val : values) {
                costBasedOnMinDays = val.getMinimum_nights() * val.getPrice();
                incomePerYear = val.getNumber_of_reviews() * costBasedOnMinDays;
                incomePerMonth = incomePerYear / 12;
                nightsPerYear = val.getNumber_of_reviews() * val.getMinimum_nights();

                occupancyRate = (nightsPerYear / 365.0F) * 100;

            }

            cow.setIncomePerMonth(incomePerMonth);
            cow.setNightsPerYear(nightsPerYear);
            cow.setOccupancyRate(occupancyRate);

            context.write(key, cow);
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
            job.setJarByClass(AirBnB_14.class);

            job.setMapperClass(MapperClass.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(CustomWritableClass.class);
            job.setReducerClass(ReducerClass.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(CustomWritableOutputClass.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            System.exit(job.waitForCompletion(true) ? 0 : 1);

        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            System.out.println("Erorr Message" + ex.getMessage());
        }
    }

}
