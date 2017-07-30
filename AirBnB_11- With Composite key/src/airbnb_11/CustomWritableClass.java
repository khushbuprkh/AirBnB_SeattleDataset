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
import org.apache.hadoop.io.WritableUtils;

/**
 *
 * @author khushbuprkh
 */
public class CustomWritableClass implements Writable {

    private float min_Price;
    private float max_Price;

    public CustomWritableClass() {

    }

    public float getMin_Price() {
        return min_Price;
    }

    public void setMin_Price(float min_Price) {
        this.min_Price = min_Price;
    }

    public float getMax_Price() {
        return max_Price;
    }

    public void setMax_Price(float max_Price) {
        this.max_Price = max_Price;
    }

  

    @Override
    public void write(DataOutput d) throws IOException {
        d.writeFloat(min_Price);
        d.writeFloat(max_Price);
        
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        min_Price = di.readFloat();
        max_Price = di.readFloat();

    }

    public String toString() {
        return (new StringBuilder().append(min_Price).append("\t").append(max_Price)).toString();
    }
}
