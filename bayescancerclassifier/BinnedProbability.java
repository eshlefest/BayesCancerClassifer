/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;

/**
 * this object accepts data and then discretizes it based on the number of bins
 * designated.  
 * @author ryaneshleman
 */
public class BinnedProbability extends FeatureProbability 
{
    ArrayList<Double> data = new ArrayList<Double>();
    int numBins = 10;
    int bins[]; 
    double interval;
    private boolean clean = true;
    boolean laplacePerformed = false;
    double min;
    
    //initialize bins to 0
    public BinnedProbability() {
        for(int i = 0; i < numBins; i++)
            bins[i] = 0;
        
    }
    /**
     * Constructor initializses bins
     * @param numBins 
     */
    public BinnedProbability(int numBins) {
        this.numBins = numBins;
        bins = new int[numBins];
        for(int i = 0; i < numBins; i++)
            bins[i] = 0;

    }
    
    public void setCustomBinSize(int newSize)
    {
        this.numBins = newSize;
        bins = new int[numBins];
        for(int i = 0; i < numBins; i++)
            bins[i] = 0;
    }        
    /**
     * Returns the probability of the value d based on the probability of the bin
     * it falls into
     * @param d
     * @return 
     */
    @Override
    public double getProbability(double d) {
        if(!active)return 1;
        
        if(!clean)binData(false);
        int inputBin = (int)((d - min)/interval);
        if(inputBin > numBins - 1) inputBin = numBins -1;
        else if(inputBin < 0) inputBin = 0;
        return ((double)bins[inputBin] / (double)data.size());
    }

    @Override
    public void addData(double d) {
        data.add(d);
        clean = false;   
    }
    /**
     * This method bins the data.  The number of bins is determined by the value 
     * of the numBins variable
     * @param lPerformed 
     */
    private void binData(boolean lPerformed) {
        double max;
        int bin;
        boolean performLaplace = false;
        min = max = data.get(0);
        for(double d : data)
            if(d>max)max=d;
            else if(d<min)min=d;
        
        interval = (max - min)/((double)numBins - 1.);
        
        for(double d : data)
        {    
            bin = (int)((d - min) / interval);
            if(bin > numBins - 1) bin = numBins - 1;
            bins[bin]++;
        }
        for(int i = 0; i< bins.length; i++)
            if (bins[i]==0)
                if(lPerformed)
                {
                    bins[i] = 1;
                    //System.out.println("Fixing the fix");
                }    
                else performLaplace = true;
        
        if(performLaplace)laplacianCorrection();
        
        clean = true;
                    
    }
    /**
     * Performs the Laplacian Correction
     */
    private void laplacianCorrection() {
        
        for(int i = 0; i<bins.length; i++)
        {
            addData(min + .001 + (interval * i));
        }
        binData(true);
        laplacePerformed = true;
    }
    
}
