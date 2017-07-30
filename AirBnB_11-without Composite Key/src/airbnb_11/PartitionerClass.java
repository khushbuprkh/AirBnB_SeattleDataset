/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_11;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 *
 * @author pooja
 */
public class PartitionerClass extends Partitioner<CompositeKeyWritable, NullWritable>{

    @Override
    public int getPartition(CompositeKeyWritable key, NullWritable value, int numOfPartitions) {
        
        return (key.getBedroom().hashCode() % numOfPartitions);
    }
}
