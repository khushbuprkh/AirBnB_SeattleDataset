/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_11;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

/**
 *
 * @author khushbu
 */
public class CompositeKeyWritable implements Writable, WritableComparable<CompositeKeyWritable> {

    private String host_neighbourhood;
    private String bedroom;

    public CompositeKeyWritable() {

    }

    public CompositeKeyWritable(String host_neighbourhood, String bedroom) {
        this.host_neighbourhood = host_neighbourhood;
        this.bedroom = bedroom;
    }

    public String getHost_neighbourhood() {
        return host_neighbourhood;
    }

    public void setHost_neighbourhood(String host_neighbourhood) {
        this.host_neighbourhood = host_neighbourhood;
    }

    public String getBedroom() {
        return bedroom;
    }

    public void setBedroom(String bedroom) {
        this.bedroom = bedroom;
    }

    @Override
    public void write(DataOutput d) throws IOException {
        WritableUtils.writeString(d, host_neighbourhood);
        WritableUtils.writeString(d, bedroom);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        host_neighbourhood = WritableUtils.readString(di);
        bedroom = WritableUtils.readString(di);
    }
    
    
    



    public String toString() {
        return (new StringBuilder().append(host_neighbourhood).append("\t").append(bedroom)).toString();
    }

    @Override
    public int compareTo(CompositeKeyWritable o) {
   
        int result = bedroom.compareTo(o.bedroom);
             if (result == 0) {
            result = host_neighbourhood.compareTo(o.host_neighbourhood);
        }
        return result;
    }

}
