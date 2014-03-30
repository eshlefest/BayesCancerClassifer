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
abstract class FeatureProbability 
{            
    public abstract double getProbability(double d);
    public abstract void addData(double d);
    boolean active = true;

    public void deactivate() {
        active = false;
    }
    
    public void activate(){
        active = true;
    }
            
    
}
