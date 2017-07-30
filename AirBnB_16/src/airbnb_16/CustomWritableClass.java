/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_16;

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

    private float min_Security_Deposit;
    private float max_Security_Deposit;
    private float average_Security_Deposit;

    public CustomWritableClass() {

    }

    public float getMin_Security_Deposit() {
        return min_Security_Deposit;
    }

    public void setMin_Security_Deposit(float min_Security_Deposit) {
        this.min_Security_Deposit = min_Security_Deposit;
    }

    public float getMax_Security_Deposit() {
        return max_Security_Deposit;
    }

    public void setMax_Security_Deposit(float max_Security_Deposit) {
        this.max_Security_Deposit = max_Security_Deposit;
    }

    public float getAverage_Security_Deposit() {
        return average_Security_Deposit;
    }

    public void setAverage_Security_Deposit(float average_Security_Deposit) {
        this.average_Security_Deposit = average_Security_Deposit;
    }

    @Override
    public void write(DataOutput d) throws IOException {
        d.writeFloat(min_Security_Deposit);
        d.writeFloat(max_Security_Deposit);
        d.writeFloat(average_Security_Deposit);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        min_Security_Deposit = di.readFloat();
        max_Security_Deposit = di.readFloat();
        average_Security_Deposit = di.readFloat();

    }

    public String toString() {
        return (new StringBuilder().append(min_Security_Deposit).append("\t").append(max_Security_Deposit).append("\t").append(average_Security_Deposit)).toString();
    }
}
