/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airbnb_14;

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

    private int minimum_nights;
    private int number_of_reviews;
    private float price;

    public CustomWritableClass() {

    }

    public int getMinimum_nights() {
        return minimum_nights;
    }

    public void setMinimum_nights(int minimum_nights) {
        this.minimum_nights = minimum_nights;
    }

    public int getNumber_of_reviews() {
        return number_of_reviews;
    }

    public void setNumber_of_reviews(int number_of_reviews) {
        this.number_of_reviews = number_of_reviews;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    @Override
    public void write(DataOutput d) throws IOException {
        d.writeInt(minimum_nights);
        d.writeInt(number_of_reviews);
        d.writeFloat(price);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        minimum_nights = di.readInt();
        number_of_reviews = di.readInt();
        price = di.readFloat();

    }

    public String toString() {
        return (new StringBuilder().append(minimum_nights).append("\t").append(number_of_reviews).append("\t").append(price)).toString();
    }
}
