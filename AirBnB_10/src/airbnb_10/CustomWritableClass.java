/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_10;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

/**
 *
 * @author khushbuprkh
 */
public class CustomWritableClass implements Writable {

    private int min_Bedrooms;
    private int max_Bedrooms;
    private float average_Price;

    public CustomWritableClass() {

    }

    public int getMin_Bedrooms() {
        return min_Bedrooms;
    }

    public void setMin_Bedrooms(int min_Bedrooms) {
        this.min_Bedrooms = min_Bedrooms;
    }

    public int getMax_Bedrooms() {
        return max_Bedrooms;
    }

    public void setMax_Bedrooms(int max_Bedrooms) {
        this.max_Bedrooms = max_Bedrooms;
    }

    public float getAverage_Price() {
        return average_Price;
    }

    public void setAverage_Price(float average_Price) {
        this.average_Price = average_Price;
    }

    @Override
    public void write(DataOutput d) throws IOException {
        d.writeInt(min_Bedrooms);
        d.writeInt(max_Bedrooms);
        d.writeFloat(average_Price);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        min_Bedrooms = di.readInt();
        max_Bedrooms = di.readInt();
        average_Price = di.readFloat();

    }

    public String toString() {
        return (new StringBuilder().append(min_Bedrooms).append("\t").append(max_Bedrooms).append("\t").append(average_Price)).toString();
    }
}
