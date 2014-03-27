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
    
}
