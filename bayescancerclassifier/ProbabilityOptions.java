/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

/**
 *  This class is used to set various options in the classifier, such as bin size
 *  and distribution models
 * @author ryaneshleman
 */
public class ProbabilityOptions {
    boolean useGaussian = false;
    boolean isCustom = false;
    boolean selectedAttributes = false;
    boolean customDistributionModels[];
    boolean selectAttributes[];
    boolean useCustomBinSizes = false;
    int binSize = 10;
    int customBinSizes[];
    
    /**
     * Resets all values to default values
     */
    public void reset()
    {
        useGaussian = isCustom = selectedAttributes = false;
        customDistributionModels = null;
        selectAttributes = null;
        binSize = 10;
    }
    /**
     * accepts a boolean array, each index in the array will determine the 
     * distribution of the feature, true is Gaussian, false is binned
     * @param custom 
     */
    public void setCustomDistributionModel(boolean[] custom)
    {
        this.customDistributionModels = custom;
        isCustom = true;
    }
    /**
     * takes a boolean array that represents whether or not the feature 
     * will be used in the model
     * @param selected 
     */
    public void setSelected(boolean[] selected)
    {
        this.selectAttributes = selected;
        selectedAttributes = true;
    }
    
    public void setBinSize(int size)
    {
        binSize = size;
    }
    
    public void setBinSize(int sizes[])
    {
        useCustomBinSizes = true;
        customBinSizes = sizes;
    }        
    
    
}
