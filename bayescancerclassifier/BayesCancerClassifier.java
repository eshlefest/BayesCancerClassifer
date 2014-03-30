/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayescancerclassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 *
 * @author ryaneshleman
 */
public class BayesCancerClassifier {

    /**
     * @param args the command line arguments
     * 
     */
    public static void main(String[] args) throws FileNotFoundException {
        String fileAddress = "wdbc.data";
        int NUM_DATA_POINTS = 569;
        int NUM_FEATURES = 32;
        int DEGREE_OF_VALIDATION = 10;
        Classifier classifier;
        double fScore;
        
        double data[][] = new double[NUM_DATA_POINTS][NUM_FEATURES];
        int binnedData[][] = new int[NUM_DATA_POINTS][NUM_FEATURES];
        
        
        readData(data,fileAddress);
                    
        classifier = new Classifier(data);

        boolean custom[] = new boolean[32];
        boolean selected[] = new boolean[32];
        
        for(int i = 0; i < custom.length; i++)
        {
            selected[i] = custom[i] = true;
        }    
            
        
        
        
        custom[8] = false;
        custom[15] = false;
       // custom[12] = false;
        //custom[14] = false;
        //custom[9] = false;
        //custom[10] = false;
        ProbabilityOptions options = new ProbabilityOptions();
        options.setCustom(custom);
        options.setBinSize(7);
        kFoldValidation(data,DEGREE_OF_VALIDATION,options);
        //System.out.printf("After %d fold validation, f1-score=%f\n\n\n",DEGREE_OF_VALIDATION,fScore);
        
        options.reset();
        options.useGaussian = true;
        selected[5] = false;
        selected[10] = false;
        options.setSelected(selected);
        //kFoldValidation(data,DEGREE_OF_VALIDATION,options);
        
        //System.out.printf("After %d fold validation, f1-score=%f\n",DEGREE_OF_VALIDATION,fScore);
            
        
    }

    /**
     * This method reads in data from file and puts it in the provided 2d array.
     * M for Malignant is translated to 1 and B for benign is 0
     * 
     * @param data
     * @param fileAddress
     * @throws FileNotFoundException 
     */
    private static void readData(double[][] data, String fileAddress) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileAddress));

        String line;
        StringTokenizer st;
        int i=0,j;
        while(sc.hasNext())
        {
            j=2;
            line = sc.nextLine();
            st = new StringTokenizer(line,",");
            data[i][0] = Double.parseDouble(st.nextToken());
            data[i][1] = (st.nextToken().equals("M") ? 1 : 0);
            while(st.hasMoreTokens())
            {
                data[i][j] = Double.parseDouble(st.nextToken());
                j++;
            }
            i++;
        }
        
    }

    
        public static void kFoldValidation(double data[][],int k,ProbabilityOptions options)
    {
        int interval = data.length / k;
        double[][] results = new double[k][4];
        //System.out.println(interval);
        ArrayList<ArrayList<double[]>> partitionedData = new ArrayList<ArrayList<double[]>>();
        Classifier cl;
        double fScore = 0;
        
        for(int i = 0; i<k; i++)
            partitionedData.add(new ArrayList<double[]>());
        
        //partition the data into k array lists
        //by adding the ith element in data to the i%kth array list
        for(int i = 0; i < data.length; i++)
            partitionedData.get(i%k).add(data[i]);
        
        for(int i = 0; i<k; i++)
        {
            System.out.println("VALIDATION TEST NUMBER: " + (i+1));
            cl = new Classifier(options);
            //load training data into classifier
            for(int j = 0; j<k; j++)
            {
                if(i!=j)
                    for(double[] d : partitionedData.get(j))
                        cl.addData(d);
            }
            results[i] = validate(cl,partitionedData.get(i));
        }    
        
        double meanAccuracy = 0;
        double meanFScore = 0;
        double standardError=0;
        System.out.println("aggregate results:\n      Accuracy   precision  recall     f-1 score");
        for(int i = 0; i < k; i++)
        {
            System.out.printf("%3d %10f %10f %10f %10f\n",i,results[i][0],results[i][1],results[i][2],results[i][3]);
            meanAccuracy += results[i][0];
            meanFScore+=results[i][3];
        }
        
        meanAccuracy /= (double)k;
        meanFScore /= (double)k;
        
        //calculate standard eviation of accuracy:
        for(int i = 0; i < k; i++)
        {
            standardError += (results[i][0] - meanAccuracy)*(results[i][0] - meanAccuracy);
        }
        standardError = Math.sqrt(standardError/(double)k);
        standardError = standardError / Math.sqrt(k);
        
        
        System.out.println("Mean Accuracy : " + meanAccuracy);
        System.out.println("Standard Error: " + standardError);
        System.out.println("Mean F1 Score : " + meanFScore);
        //
         
        //
        
        return;
            
    }
    
    
    
    /**
     * this method tests the pretrained classifier against the test data
     * then prints a confusion matrix.  it returns the f-1 score
     * @param cl
     * @param data
     * @return 
     */
    public static double[] validate(Classifier cl, ArrayList<double[]> data)
    {
        int truePositive = 0,
            falsePositive = 0,
            trueNegative = 0,
            falseNegative = 0,
            prediction;
        double precision,recall, actualDiagnosis, accuracy,f1score;
        
        for(double[] d : data)
        {
            prediction = cl.predict(d);
            actualDiagnosis = d[1];
            if(prediction == 1 && actualDiagnosis == 1) truePositive++;
            else if(prediction == 1 && actualDiagnosis == 0) falsePositive++;
            else if(prediction == 0 && actualDiagnosis == 0) trueNegative++;
            else if(prediction == 0 && actualDiagnosis == 1) falseNegative++;
        }
        
        printConfusionMatric(truePositive,falsePositive,trueNegative,falseNegative);
        precision = (double)truePositive/(double)(truePositive + falsePositive);
        recall = (double)truePositive/(double)(truePositive + falseNegative);
        accuracy = (double)(truePositive + trueNegative)/(double)(truePositive + trueNegative + falseNegative + falsePositive);
        f1score = 2.*(precision * recall)/(precision + recall);
        System.out.printf("Precision: %f    Recall: %f  \nAccuracy: %f     f-1 score: %f \n\n\n",precision,recall,accuracy,f1score);

        
        return new double[]{accuracy,precision,recall,f1score};
    }

    private static void printConfusionMatric(int truePositive, int falsePositive, int trueNegative, int falseNegative) {
        System.out.println("             | + Diagnosis   | - Diagnosis    ");
        System.out.println("--------------------------------------------------");
        System.out.printf("+ Prediction |  %10d   |  %10d\n",truePositive,falsePositive);
        System.out.printf("- Prediction |  %10d   |  %10d\n",falseNegative,trueNegative);
        System.out.println();
                
    }
}
