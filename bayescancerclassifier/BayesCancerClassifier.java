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
 * This holds the main method used to execute the program which sets up a classifer
 * and runs k-fold validation on it
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
        int arg;
        double fScore;
        Scanner sc = new Scanner(System.in);
        
        if(args.length == 1) fileAddress = args[0];
 
        System.out.println("Choose your distribution model:");
        System.out.println("Enter 1 for all binned");
        System.out.println("Enter 2 for Gaussian Distribution");
        System.out.println("Enter 3 for Chi Squared Distribution");
        System.out.println("Enter 4 for a combination of Gaussian and Binned (optimum)");
        System.out.println("Enter 5 for a combination of Gaussian, Binned, and Chi Squared");
        System.out.println("Selection: ");
        
        arg = sc.nextInt();
        
        double data[][] = new double[NUM_DATA_POINTS][NUM_FEATURES];
        readData(data,fileAddress);
                   

        /* this array is used to choose distribution models for each attribute
         * if customDistributionModels[10] = true, then the 10th attribute will be treated as
         * a gaussian attribute, false means the values will be put into equal sized bins
         * bin size can be set using the options object
         * 
         */
        int allBinned[] = new int[NUM_FEATURES];
        int allGaussian[] = new int[NUM_FEATURES];
        int mixedBinnedGaussian[] = new int[NUM_FEATURES];
        int allChiSquare[] = new int[NUM_FEATURES];
        int mixedAll[] = new int[NUM_FEATURES];
        
        /*
         * this array is used to indicate whether or not the specific attribute
         * will be used in the classifier.  selected[10] = false means attribute 10
         * will not be used in classification, true means it will
         * 
         */
        boolean selected[] = new boolean[NUM_FEATURES];
        
        int customBinSizes[] = new int[NUM_FEATURES];
        
        
        for(int i = 0; i < NUM_FEATURES; i++)
        {
            selected[i] = true;
            allBinned[i] = 0;           //this array indicates all attributes should be binned
            allGaussian[i] = 1;         //this array is now all Gaussian
            mixedBinnedGaussian[i] = 1; //all Gaussian now, will be updated further down
            allChiSquare[i] = 2;        //all attributes will be treated as a chi squared distribution
            mixedAll[i] = 1;            //initialized to Gaussian, will be customized further down
            customBinSizes[i] = 25;     //initialize bin sizes
        }    
        
        //Set the Custom Bin Sizes
        customBinSizes[5]=4;
        customBinSizes[29]=4;
        customBinSizes[15]=4;        
        customBinSizes[24]=2;
        customBinSizes[9]=2;
        
        //selected attributes, false means they are ignored
        selected[5] = false;
        selected[10] = false;
        selected[6] = false;
        selected[26] = false;
        
        ProbabilityOptions options1 = new ProbabilityOptions();
        ProbabilityOptions options2 = new ProbabilityOptions();
        ProbabilityOptions options3 = new ProbabilityOptions();
        ProbabilityOptions options4 = new ProbabilityOptions();
        ProbabilityOptions options5 = new ProbabilityOptions();

        switch(arg){
            case 1:
                options1.setBinSize(customBinSizes);
                options1.setCustomDistributionModel(allBinned);
                kFoldValidation(data,DEGREE_OF_VALIDATION,options1);
                break;
            case 2:
                options2.setSelected(selected);
                options2.setCustomDistributionModel(allGaussian);
                kFoldValidation(data,DEGREE_OF_VALIDATION,options2);
                break;
            case 3:
                options3.setSelected(selected);
                options3.setCustomDistributionModel(allChiSquare);
                kFoldValidation(data,DEGREE_OF_VALIDATION,options3);
                break;
            case 4:
                mixedBinnedGaussian[9] = 0;
                mixedBinnedGaussian[16] = 0;
                options4.setSelected(selected);
                options4.setCustomDistributionModel(mixedBinnedGaussian);
                kFoldValidation(data,DEGREE_OF_VALIDATION,options4);
                break;
            case 5:
                mixedAll[13] = 2;   //set 13,18,19 to Chi Squared
                mixedAll[18] = 2;
                mixedAll[19] = 2;
                mixedAll[9] = 0;    //  set 9 and 16 to binned
                mixedAll[16] = 0;
                options5.setSelected(selected);
                options5.setCustomDistributionModel(mixedAll);
                kFoldValidation(data,DEGREE_OF_VALIDATION,options5);
                break;       
        }

           
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
        /**
         * This method performs the k-fold validation of the classifier settings
         * indicated in the options object.  It first partitions the data into
         * k partitions then trains the classifier using k-1 partitions and validates
         * the classifier using the remaining 1 partition
         * 
         * @param data
         * @param k
         * @param options 
         */
    
        public static void kFoldValidation(double data[][],int k,ProbabilityOptions options)
    {
        int interval = data.length / k;
        double[][] results = new double[k][4];
        ArrayList<ArrayList<double[]>> partitionedData = new ArrayList<ArrayList<double[]>>();
        Classifier cl;
        double fScore = 0;
        
        for(int i = 0; i<k; i++)
            partitionedData.add(new ArrayList<double[]>());
        
        //partition the data into k array lists
        //by adding the ith element in data to the i%kth array list
        for(int i = 0; i < data.length; i++)
            partitionedData.get(i%k).add(data[i]);
        
        
        //  perform validations
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
        
        
        //Print out results
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
        
        //calculate standard deviation of accuracy:
        for(int i = 0; i < k; i++)
        {
            standardError += (results[i][0] - meanAccuracy)*(results[i][0] - meanAccuracy);
        }
        standardError = Math.sqrt(standardError/(double)k);
        //calculate standard error
        standardError = standardError / Math.sqrt(k);
        
        
        System.out.println("Mean Accuracy : " + meanAccuracy);
        System.out.println("Standard Error: " + standardError);
        System.out.println("Mean F1 Score : " + meanFScore);

        return;
            
    }
    
    
    
    /**
     * this method tests the pretrained classifier against the test data
     * then prints a confusion matrix.  it returns an array of the results
     * in for form of {accuracy,precision,recall,f-1score}
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
        
        //make predictions on each data point and record results
        for(double[] d : data)
        {
            prediction = cl.predict(d);
            actualDiagnosis = d[1];
            if(prediction == 1 && actualDiagnosis == 1) truePositive++;
            else if(prediction == 1 && actualDiagnosis == 0) falsePositive++;
            else if(prediction == 0 && actualDiagnosis == 0) trueNegative++;
            else if(prediction == 0 && actualDiagnosis == 1) falseNegative++;
        }
        
        printConfusionMatrix(truePositive,falsePositive,trueNegative,falseNegative);
        
        precision = (double)truePositive/(double)(truePositive + falsePositive);
        recall = (double)truePositive/(double)(truePositive + falseNegative);
        accuracy = (double)(truePositive + trueNegative)/(double)(truePositive + trueNegative + falseNegative + falsePositive);
        f1score = 2.*(precision * recall)/(precision + recall);
        System.out.printf("Precision: %f    Recall:    %f  \nAccuracy:  %f    f-1 score: %f \n\n\n",precision,recall,accuracy,f1score);

        
        return new double[]{accuracy,precision,recall,f1score};
    }

    /**
     * This is a helper method that prints the confusion matrix based on input
     * 
     * @param truePositive
     * @param falsePositive
     * @param trueNegative
     * @param falseNegative 
     */
    private static void printConfusionMatrix(int truePositive, int falsePositive, int trueNegative, int falseNegative) {
        System.out.println("             | + Diagnosis   | - Diagnosis    ");
        System.out.println("--------------------------------------------------");
        System.out.printf("+ Prediction |  %10d   |  %10d\n",truePositive,falsePositive);
        System.out.printf("- Prediction |  %10d   |  %10d\n",falseNegative,trueNegative);
        System.out.println();
                
    }
}
