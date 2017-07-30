/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_15;

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

    private String listing_Id;
    private Float percentage;

    public CompositeKeyWritable() {

    }

    public CompositeKeyWritable(String listing_Id, Float percentage) {
        this.listing_Id = listing_Id;
        this.percentage = percentage;
    }

    public String getListing_Id() {
        return listing_Id;
    }

    public void setListing_Id(String listing_Id) {
        this.listing_Id = listing_Id;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    @Override
    public void write(DataOutput d) throws IOException {
        WritableUtils.writeString(d, listing_Id);
        WritableUtils.writeString(d, String.valueOf(percentage));
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        listing_Id = WritableUtils.readString(di);
        percentage =Float.parseFloat(WritableUtils.readString(di));
    }

    public String toString() {
        return (new StringBuilder().append(listing_Id).append("\t").append(percentage)).toString();
    }

    @Override
    public int compareTo(CompositeKeyWritable o) {

        int result = percentage.compareTo(o.percentage);
        if (result == 0) {
            result = listing_Id.compareTo(o.listing_Id);
        }
        return -result;
    }

}
