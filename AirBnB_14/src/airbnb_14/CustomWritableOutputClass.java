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

/**
 *
 * @author khushbuprkh
 */
public class CustomWritableOutputClass implements Writable {

    private float incomePerMonth;
    private int nightsPerYear;
    private float occupancyRate;

    public CustomWritableOutputClass() {

    }

    public float getIncomePerMonth() {
        return incomePerMonth;
    }

    public void setIncomePerMonth(float incomePerMonth) {
        this.incomePerMonth = incomePerMonth;
    }

    public int getNightsPerYear() {
        return nightsPerYear;
    }

    public void setNightsPerYear(int nightsPerYear) {
        this.nightsPerYear = nightsPerYear;
    }

    public float getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(float occupancyRate) {
        this.occupancyRate = occupancyRate;
    }


    @Override
    public void write(DataOutput d) throws IOException {
        d.writeFloat(incomePerMonth);
        d.writeInt(nightsPerYear);
        d.writeFloat(occupancyRate);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        incomePerMonth = di.readFloat();
        nightsPerYear = di.readInt();
        occupancyRate = di.readFloat();

    }

    public String toString() {
        return (new StringBuilder().append(incomePerMonth).append("\t").append(nightsPerYear).append("\t").append(occupancyRate)).toString();
    }
}
