/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

/**
 *
 * @author ryaneshleman
 */
public class ProbabilityOptions {
    boolean useGaussian = false;
    boolean isCustom = false;
    boolean selectedAttributes = false;
    boolean custom[];
    boolean selectAttributes[];
    int binSize = 10;
    
    public void reset()
    {
        useGaussian = isCustom = selectedAttributes = false;
        custom = null;
        selectAttributes = null;
    }
    
    public void setCustom(boolean[] custom)
    {
        this.custom = custom;
        isCustom = true;
    }
    
    public void setSelected(boolean[] selected)
    {
        this.selectAttributes = selected;
        selectedAttributes = true;
    }
    
    public void setBinSize(int size)
    {
        binSize = size;
    }        
    
    
}
