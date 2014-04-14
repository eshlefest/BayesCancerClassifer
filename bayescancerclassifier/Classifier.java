/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;

/**
 *  This Class manages the arrays of abstract feature probability estimators that are used to
 *  cary out the Naive Bayes Classification Algorithm.  In order to allow for
 *  maximum flexibility, each attribute is represented as a class that maintains
 *  statistics about all instances of this attribute in the data set.  These classes
 *  subclass the 'FeatureProbability' class.  GaussianProbability class treats the data
 *  as a Gaussian distribution and BinnedProbability treats it is a bunch of bins.
 *  
 * The constructor is passed a ProbabilityOptions object that provides information
 * about how the attributes should be modeled, ie Gaussian distribution or discretized
 * into bins.
 * 
 * 
 * 
 * @author ryaneshleman
 */
public class Classifier 
{
    ArrayList<double[]> data = new ArrayList<double[]>();
    FeatureProbability posProbs[];
    FeatureProbability negProbs[];

    boolean cleanData,initializedArrays;
    boolean useGaussian;
    int numPos=0,numNeg=0, binSize;
    ProbabilityOptions options;

    
    public Classifier(ProbabilityOptions options)
    {
        this.options = options;
        binSize = options.binSize;
        this.useGaussian = options.useGaussian;
        initalizeArrays(this.options);
        if(options.selectedAttributes)selectAttributes(options.selectAttributes);
        if(options.useCustomBinSizes)setCustomBinSizes(options.customBinSizes);
        
        
    }        


    /**
     * This method takes a boolean array indicating whether or not the attribute
     * should be considered in the classifier  
     * @param selected 
     */
    
    public void selectAttributes(boolean selected[])
    {

        for(int i = 0; i < selected.length;i++)
        {
            if(!selected[i])
            {
                posProbs[i].deactivate();
                negProbs[i].deactivate();
            }            
        }    
    }        
    
    /**
     * This method adds training data to the classifier
     * @param d 
     */
    
    public void addData(double[] d)
    {
        
        
        if(d[1]==1)
        {    
            numPos++;
            for(int i = 2; i < d.length; i++)
                    posProbs[i].addData(d[i]);
        }    
        else
        {    
            numNeg++;
            for(int i = 2; i<d.length; i++)
                    negProbs[i].addData(d[i]);
        }

    }


    /**
     * This method performs the prediction.  it uses the Gaussian formula
     * P(c|X) = P(X|c)P(c) / P(X) to predict the probability that the given data
     * is positive and the probability that it is negative and predicts the class
     * with the highest probability
     * 
     * @param d
     * @return 
     */
    public int predict(double[] d)
    {
        //initialize probability to P(c)
        double pPos = (double)numPos/((double)numPos + (double)numNeg),
                pNeg= (double)numNeg/((double)numPos + (double)numNeg);
        
        //probability positive
        for(int i = 2; i<32; i++)   
            pPos *= posProbs[i].getProbability(d[i]);

        
        //probability  negative
        for(int i = 2; i<32; i++)
            pNeg *= negProbs[i].getProbability(d[i]);
        
        return (pPos>=pNeg ? 1 : 0);
            
    }

    private void setCustomBinSizes(int customBinSizes[]) {

        for(int i = 0; i<customBinSizes.length; i++)
        {
        if(posProbs[i] instanceof BinnedProbability)
        {
            ((BinnedProbability)posProbs[i]).setCustomBinSize(customBinSizes[i]);
            ((BinnedProbability)negProbs[i]).setCustomBinSize(customBinSizes[i]);
        }    

        }    
        initializedArrays = true;
    }

    public void initalizeArrays(ProbabilityOptions options) 
    {
        posProbs = new FeatureProbability[options.customDistributionModels.length];
        negProbs = new FeatureProbability[options.customDistributionModels.length];
        
        for(int i = 0; i < options.customDistributionModels.length;i++)
        {
            switch (options.customDistributionModels[i]){
                    case 0: 
                        posProbs[i] = new BinnedProbability(binSize);
                        negProbs[i] = new BinnedProbability(binSize);
                        break;
                    case 1:
                        posProbs[i] = new GaussianProbability();
                        negProbs[i] = new GaussianProbability();
                        break;
                    case 2:
                        posProbs[i] = new ChiSquareProbability();
                        negProbs[i] = new ChiSquareProbability();
                        break;         
            }
        }
        initializedArrays = true;
    }


    
    
    
}
