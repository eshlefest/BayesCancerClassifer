/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
 

/**
 * This class is used to determine probabilities of attributes assuming that they
 * follow a ChiSquared distribution.  Help from the Apache Commons Math library is used.
 * @author ryaneshleman
 */
public class ChiSquareProbability extends FeatureProbability {
    ArrayList<Double> data = new ArrayList<Double>();
    private double mean,sum=0;
    private boolean clean = true;
    double k = 2;
    ChiSquaredDistribution chiSquareDist = new ChiSquaredDistribution(k);
    
    //Default Constructor
    public ChiSquareProbability(){};
    //Constructor
    public ChiSquareProbability(double[] d)
    {
        for(double p : d)
        {    
            data.add(p);
            sum+= p;
        }
        clean = false;
    }
    /**
     * Returns the probability of value d, based on a Chi Squared distribution
     * @param d
     * @return 
     */
    @Override
    public double getProbability(double d) {
        if(!active)return 1;
        
        if(!clean)computeValues();
        return computeProbability(d,mean);
    }
    
    @Override
    public void addData(double d) {
        data.add(d);
        sum+=d;
        clean = false;
    }
    
    private double computeProbability(double d, double mean) {
        double chiSquare = (Math.pow((d - mean),2)) / mean;
        double prob =1 - chiSquareDist.cumulativeProbability(chiSquare);
        return prob;
        
    }
    
    private void computeValues() {
        mean = sum / (double)data.size();
        clean = true;
    }
    
}
