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
class BinnedProbability extends FeatureProbability 
{
    ArrayList<Double> data = new ArrayList<Double>();
    int numBins = 10;
    int bins[]; 
    double interval;
    private boolean clean = true;
    
    //initialize bins to 0
    public BinnedProbability() {
        for(int i = 0; i < numBins; i++)
            bins[i] = 0;
        
    }
    
    public BinnedProbability(int numBins) {
        bins = new int[numBins];
        for(int i = 0; i < numBins; i++)
            bins[i] = 0;
        this.numBins = numBins;
        
    }

    @Override
    public double getProbability(double d) {
        if(!active)return 1;
        
        if(!clean)binData();
        int inputBin = (int)(d/interval);
        if(inputBin > numBins - 1) inputBin = numBins - 1;
        return ((double)bins[inputBin] / (double)data.size());
    }

    @Override
    public void addData(double d) {
        data.add(d);
        clean = false;   
    }

    private void binData() {
        double min,
               max;
        int bin;
        min = max = data.get(0);
        for(double d : data)
            if(d>max)max=d;
            else if(d<min)min=d;
        
        interval = (max - min)/((double)numBins - 1.);
        
        for(double d : data)
        {    
            bin = (int)(d / interval);
            if(bin > numBins - 1) bin = numBins - 1;
            bins[bin]++;
        }
        clean = true;
                    
    }
    
}
