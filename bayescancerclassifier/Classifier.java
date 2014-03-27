/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.util.ArrayList;

/**
 *  This Class manages the arrays of abstract probability estimators that are used to
 *  cary out the Naive Bayes Classification Algorithm.  In order to allow for
 *  maximum flexibility, each attribute is represented as a class that maintains
 *  statistics about all instances of this attribute in the data set.  These classes
 *  subclass the 'FeatureProbability' class.  GaussianProbability class treats the data
 *  as a Gaussian distribution and BinnedProbability treats it is a bunch of bins.
 *  
 *  The types of distributions to be used can be indicated by a boolean value 
 *  given to the constructor.  'true' means use Gaussian distributions, false or
 *  nothing at all defaults to using binned distribution.
 * 
 *  A third option is to use provide an array of boolean values, a true at index 1
 *  indicates that the first attribute should be treated as a Gaussian distribution,
 *  a false indicates that the values should be binned.
 * 
 * @author ryaneshleman
 */
class Classifier 
{
    ArrayList<double[]> data = new ArrayList<double[]>();
    //double[][] data;
    //double[] meansPos,meansNeg,sDeviationsPos,sDeviationsNeg;
    FeatureProbability posProbs[];
    FeatureProbability negProbs[];
    boolean cleanData,initializedArrays;
    boolean useGaussian;
    int numPos=0,numNeg=0;
    
    //default constructor uses bins
    public Classifier()
    {
        initializedArrays = false;
        useGaussian = false;
    }
    
    //user can specify gaussian distribution or bins
    public Classifier(boolean gaus)
    {
        initializedArrays = false;
        useGaussian = gaus;
    }
    
    //custom initialization
    public Classifier(boolean custom[])
    {
        posProbs = new FeatureProbability[custom.length];
        negProbs = new FeatureProbability[custom.length];
        
        for(int i = 0; i<custom.length; i++)
        {
        posProbs[i] = (custom[i]?new GaussianProbability():new BinnedProbability());
        negProbs[i] = (custom[i]?new GaussianProbability():new BinnedProbability());
        }    
        initializedArrays = true;    
    }        
    
    //again, defaults to bins
    public Classifier(double[][] data) 
    {
        
        useGaussian = false;
        initializeArrays(data[1]);
        
        
        for(double[] d : data)
            addData(d);
    }

    public void addData(double[] d)
    {
        if(!initializedArrays)
            initializeArrays(d);
        
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
    
    public void addData(double[][] d)
    {
        for(double[] data : d)
            addData(d);

    }
    
    public int predict(double[] d)
    {
        
        double pPos = (double)numPos/((double)numPos + (double)numNeg),
                pNeg= (double)numNeg/((double)numPos + (double)numNeg);
        
        //probability positive
        for(int i = 2; i<32; i++)   
            pPos *= posProbs[i].getProbability(d[i]);

        
        //probability  negative
        for(int i = 2; i<32; i++)
            pNeg *= negProbs[i].getProbability(d[i]);
        
        //System.out.println("" + d[1] + "  " + pPos + " " + pNeg);
        return (pPos>=pNeg ? 1 : 0);
            
    }

    private void initializeArrays(double[] d) 
    {
        posProbs = new FeatureProbability[d.length];
        negProbs = new FeatureProbability[d.length];
        
        for(int i = 0; i<d.length; i++)
        {
        posProbs[i] = (useGaussian?new GaussianProbability():new BinnedProbability());
        negProbs[i] = (useGaussian?new GaussianProbability():new BinnedProbability());
        }    
        initializedArrays = true;
    }
    
    
    
}
