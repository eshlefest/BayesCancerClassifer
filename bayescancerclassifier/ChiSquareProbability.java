/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;

/**
 *
 * @author ryaneshleman
 */
public class ChiSquareProbability extends FeatureProbability {
    ArrayList<Double> data = new ArrayList<Double>();
    private double mean,sum=0;
    private boolean clean = true;
    double k = 2;
    
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
    
    @Override
    public double getProbability(double d) {
        if(!active)return 1;
        
        if(!clean)computeValues();
        //System.out.println("standardDEviation: " + standardDeviation);
        return computeProbability(d,mean);
    }
    
    @Override
    public void addData(double d) {
        data.add(d);
        sum+=d;
        clean = false;
    }

    private double computeProbability(double d, double mean) {
        double chiSquare = Math.pow((d - mean),2);
        
        return chiSquareProbabilityDensityFunction(chiSquare);
    }
    
    private void computeValues() {
        //System.out.println("computeValues: sum " + sum);
        mean = sum / (double)data.size();

        clean = true;
    }

    private double chiSquareProbabilityDensityFunction(double chiSquare) {
        double numerator = Math.pow(chiSquare,(k/2.) - 1) * Math.pow(Math.E,-(chiSquare / 2.));
        double denominator = Math.pow(2,(k/2.)) * gamma(k/2.);
        return numerator/denominator;
    }

    /**
     * instead of actually implementing the gamma function, I just hard coded gamma(x) values from
     * Wolfram for the possible inputs of my gamma function.
     * @param d
     * @return 
     */
    private double gamma(double d) {
        if(d == .5) return 1.77245;
        if(d == 1.) return 1.;
        if(d == 1.5) return .886227;
        if(d == 2.) return 1.;
        
        //return 0 otherwise, causing a divide by zero error later on
        return 0.;
        
        
    }
    
    
}
