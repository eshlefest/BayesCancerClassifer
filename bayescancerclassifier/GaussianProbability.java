/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;

/**
 * This method holds a list of double values assumed to follow a Gaussian Distribution
 * it computes the mean and standard deviation so that it can generate the probability
 * of a value using the getProbability method
 * 
 * @author ryaneshleman
 */
public class GaussianProbability extends FeatureProbability {
    ArrayList<Double> data = new ArrayList<Double>();
    private double mean,standardDeviation,sum=0;
    private boolean clean = true;
    
    //Default Constructor
    public GaussianProbability(){};
    //Constructor
    public GaussianProbability(double[] d)
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
        return computeProbability(d,mean,standardDeviation);
    }

    @Override
    public void addData(double d) {
        data.add(d);
        sum+=d;
        clean = false;
    }
    
    static double computeProbability(double x,double mean, double sDev)
    {
        return (1./Math.sqrt(2*Math.PI*sDev))*
                Math.pow(Math.E,-(Math.pow(x-mean,2))/(2*sDev*sDev));
    }

    private void computeValues() {
        //System.out.println("computeValues: sum " + sum);
        mean = sum / (double)data.size();
        standardDeviation = 0.;
        for(int i = 0; i< data.size(); i++)
            standardDeviation += ((mean - data.get(i))*(mean - data.get(i)));
            
        //System.out.println("variance mean" + standardDeviation + " " + mean);
        
        standardDeviation = Math.sqrt((standardDeviation / (double)data.size()));
        
        clean = true;
    }
    
    

    
    
}
