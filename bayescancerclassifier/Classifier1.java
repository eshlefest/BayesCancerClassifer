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
public class Classifier1 {
    //ArrayList<ArrayList<Integer>> frequencyCount = new ArrayList<ArrayList<Integer>>();
    //ArrayList<ArrayList<Integer>> conditionalFrequencyCount = new ArrayList<ArrayList<Integer>>();
    int[][] frequencyCount = new int[30][10];
    int[][][] conditionalFrequencyCount = new int[2][30][10];
    int hasCancer,
            numPos=0,
            numNeg=0;
    
    FeatureProbability probabilities;
    
    public Classifier1()
    {
        init();
    }        
    
    
    public Classifier1(int[][] binnedData) {
        init();
             
        
        //populate frequency arrays
        for(int[] data : binnedData)
            newData(data);
        
    }
    
    /**
     * This method initializes internal frequency arrays;
     */
    public void init()
    {
    for(int i = 0; i<30;i++)
            for(int j = 0; j<10; j++)
            {    
                frequencyCount[i][j]=1;
                conditionalFrequencyCount[0][i][j] = 1;
                conditionalFrequencyCount[1][i][j] = 1;
            }
    }        
    
    /**
     * This method updates current frequency values given new data
     * @param data 
     */
    public void newData(int[] data)
    {
        hasCancer= data[1];
            if(hasCancer==1)numPos++;
            else numNeg++;
                    
            for(int i = 2; i<32; i++)
            {    
                frequencyCount[i-2][data[i]]++;
                conditionalFrequencyCount[hasCancer][i-2][data[i]]++;
            }
    }        
    
    /**
     * This method makes the classification prediction
     * @param data
     * @return 
     */
    public int predict(int[] data)
    {
        double pPos = (double)numPos/((double)numPos + (double)numNeg),
                pNeg= (double)numNeg/((double)numPos + (double)numNeg);
        int dataFeature;
        
        //probability positive
        for(int i = 2; i<32; i++)
        {    
            dataFeature = data[i];
            pPos *= (conditionalFrequencyCount[1][i-2][dataFeature]/(double)numPos);
        }
        
        //probability  negative
        for(int i = 2; i<32; i++)
        {    
            dataFeature = data[i];
            pNeg *= (conditionalFrequencyCount[0][i-2][dataFeature]/(double)numNeg);
        }
        //System.out.println("" + pPos + " " + pNeg);
        return (pPos>=pNeg ? 1 : 0);
    }
    //compute probablility given a gaussian distribution
            

    
    
}
