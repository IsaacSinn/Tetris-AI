package src.labs.zombayes.agents;


// SYSTEM IMPORTS
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// JAVA PROJECT IMPORTS
import edu.bu.labs.zombayes.agents.SurvivalAgent;
import edu.bu.labs.zombayes.features.Features.FeatureType;
import edu.bu.labs.zombayes.linalg.Matrix;
import edu.bu.labs.zombayes.utils.Pair;



public class NaiveBayesAgent
    extends SurvivalAgent
{

    public static class NaiveBayes
        extends Object
    {

        public static final FeatureType[] FEATURE_HEADER = {FeatureType.CONTINUOUS,
                                                            FeatureType.CONTINUOUS,
                                                            FeatureType.DISCRETE,
                                                            FeatureType.DISCRETE};
        
        public Map<Double, Double> probDiscreteHuman1; // key is the feature value, value is the probability
        public Map<Double, Double> probDiscreteHuman2;
        public Map<Double, Double> probDiscreteZombie1;
        public Map<Double, Double> probDiscreteZombie2;

        public double probHuman;
        public double probZombie;

        public Map<String, Double> meanStdContinuousHumans1;
        public Map<String, Double> meanStdContinuousHumans2;
        public Map<String, Double> meanStdContinuousZombies1;
        public Map<String, Double> meanStdContinuousZombies2;

        // TODO: complete me!
        public NaiveBayes()
        {
            this.probDiscreteHuman1 = new HashMap<>();
            this.probDiscreteHuman2 = new HashMap<>();
            this.probDiscreteZombie1 = new HashMap<>();
            this.probDiscreteZombie2 = new HashMap<>();

            this.probHuman = 0.0;
            this.probZombie = 0.0;

            this.meanStdContinuousHumans1 = new HashMap<>();
            this.meanStdContinuousHumans2 = new HashMap<>();
            this.meanStdContinuousZombies1 = new HashMap<>();
            this.meanStdContinuousZombies2 = new HashMap<>();

        }

        // TODO: complete me!
        public void fit(Matrix X, Matrix y_gt)
        {
            // X is a matrix with every row being a feature vector, first 2 columns are continuous, next 2 are discrete
            // y is a matrix with every row being a label vector. 1 being a zombie, 0 being a human

            // for the discrete features, keep a count of the amount of zombies and humans for each value
            // for the continuous features, keep a count of the mean and standard deviation for each feature for zombies and humans
            // System.out.println(X);

            int zombies = 0;
            int humans = 0;

            // store discrete features counts for zombies and humans. // key is the feature value, value is the count
            Map<Double, Integer> discreteHuman1 = new HashMap<>();
            Map<Double, Integer> discreteHuman2 = new HashMap<>();
            Map<Double, Integer> discreteZombie1 = new HashMap<>();
            Map<Double, Integer> discreteZombie2 = new HashMap<>();

            ArrayList<Double> continuousHuman1Array = new ArrayList<>();
            ArrayList<Double> continuousHuman2Array = new ArrayList<>();
            ArrayList<Double> continuousZombie1Array = new ArrayList<>();
            ArrayList<Double> continuousZombie2Array = new ArrayList<>();


            for (int i = 0; i < X.getShape().getNumRows(); i++)
            {
                for (int j = 0; j < X.getShape().getNumCols(); j++)
                {
                    if (FEATURE_HEADER[j] == FeatureType.DISCRETE)
                    {
                        if (y_gt.get(i, 0) == 1)
                        {
                            // is zombie
                            if (j == 2)
                            {
                                // first discrete feature
                                if (discreteZombie1.containsKey(X.get(i,j)))
                                {
                                    discreteZombie1.put(X.get(i,j), discreteZombie1.get(X.get(i,j)) + 1);
                                }
                                else
                                {
                                    discreteZombie1.put(X.get(i,j), 1);
                                }
                            }
                            else 
                            {
                                // second discrete feature
                                if (discreteZombie2.containsKey(X.get(i,j)))
                                {
                                    discreteZombie2.put(X.get(i,j), discreteZombie2.get(X.get(i,j)) + 1);
                                }
                                else
                                {
                                    discreteZombie2.put(X.get(i,j), 1);
                                }
                            }
                        }
                        
                        else 
                        {
                            // is human
                            if (j == 2)
                            {
                                // first discrete feature
                                if (discreteHuman1.containsKey(X.get(i,j)))
                                {
                                    discreteHuman1.put(X.get(i,j), discreteHuman1.get(X.get(i,j)) + 1);
                                }
                                else
                                {
                                    discreteHuman1.put(X.get(i,j), 1);
                                }
                            }
                            else 
                            {
                                // second discrete feature
                                if (discreteHuman2.containsKey(X.get(i,j)))
                                {
                                    discreteHuman2.put(X.get(i,j), discreteHuman2.get(X.get(i,j)) + 1);
                                }
                                else
                                {
                                    discreteHuman2.put(X.get(i,j), 1);
                                }
                            }
                        }
                    } 
                    else
                    {
                        // continuous feature calculate the mean and std for every feature for zombies and humans
                        if (y_gt.get(i, 0) == 1)
                        {
                            // is zombie
                            if (j == 0)
                            {
                                // first continuous feature
                                continuousZombie1Array.add(X.get(i,j));
                            }
                            else
                            {
                                // second continuous feature
                                continuousZombie2Array.add(X.get(i,j));
                            }
                        }
                        else
                        {
                            // is human
                            if (j == 0)
                            {
                                // first continuous feature
                                continuousHuman1Array.add(X.get(i,j));
                            }
                            else
                            {
                                // second continuous feature
                                continuousHuman2Array.add(X.get(i,j));
                            }
                        }

                    }
                }

                if (y_gt.get(i, 0) == 1)
                {
                    zombies++;
                }
                else
                {
                    humans++;
                }
            }

            // calculate the mean and std for the continuous features
            this.meanStdContinuousHumans1.put("mean", continuousHuman1Array.stream().mapToDouble(val -> val).average().orElse(0.0));
            this.meanStdContinuousHumans2.put("mean", continuousHuman2Array.stream().mapToDouble(val -> val).average().orElse(0.0));
            this.meanStdContinuousZombies1.put("mean", continuousZombie1Array.stream().mapToDouble(val -> val).average().orElse(0.0));
            this.meanStdContinuousZombies2.put("mean", continuousZombie2Array.stream().mapToDouble(val -> val).average().orElse(0.0));

            double stdContinuousHumans1 = Math.sqrt(continuousHuman1Array.stream().mapToDouble(val -> Math.pow(val - this.meanStdContinuousHumans1.get("mean"), 2)).sum() / continuousHuman1Array.size());
            this.meanStdContinuousHumans1.put("std", stdContinuousHumans1);
            
            double stdContinuousHumans2 = Math.sqrt(continuousHuman2Array.stream().mapToDouble(val -> Math.pow(val - this.meanStdContinuousHumans2.get("mean"), 2)).sum() / continuousHuman2Array.size());
            this.meanStdContinuousHumans2.put("std", stdContinuousHumans2);

            double stdContinuousZombies1 = Math.sqrt(continuousZombie1Array.stream().mapToDouble(val -> Math.pow(val - this.meanStdContinuousZombies1.get("mean"), 2)).sum() / continuousZombie1Array.size());
            this.meanStdContinuousZombies1.put("std", stdContinuousZombies1);

            double stdContinuousZombies2 = Math.sqrt(continuousZombie2Array.stream().mapToDouble(val -> Math.pow(val - this.meanStdContinuousZombies2.get("mean"), 2)).sum() / continuousZombie2Array.size());
            this.meanStdContinuousZombies2.put("std", stdContinuousZombies2);


            // loop through the keys for discreteHuman1 and calculate the probability
            for (Double key : discreteHuman1.keySet())
            {
                this.probDiscreteHuman1.put(key, (double)discreteHuman1.get(key) / humans);
            }

            for (Double key : discreteHuman2.keySet())
            {
                this.probDiscreteHuman2.put(key, (double)discreteHuman2.get(key) / humans);
            }

            for (Double key : discreteZombie1.keySet())
            {
                this.probDiscreteZombie1.put(key, (double)discreteZombie1.get(key) / zombies);
            }

            for (Double key : discreteZombie2.keySet())
            {
                this.probDiscreteZombie2.put(key, (double)discreteZombie2.get(key) / zombies);
            }

            // calculate the prob of human and zombie
            this.probHuman = (double)humans / (humans + zombies);
            this.probZombie = (double)zombies / (humans + zombies);

            System.out.println(this.meanStdContinuousHumans1);
            System.out.println(this.meanStdContinuousZombies1);
            System.out.println(this.meanStdContinuousHumans2);
            System.out.println(this.meanStdContinuousZombies2);

            return;
        }

        public double calculateProbability(double x, double mean, double std)
        {
            return (1 / (Math.sqrt(2 * Math.PI) * std)) * Math.exp(-Math.pow(x - mean, 2) / (2 * Math.pow(std, 2)));
        }

        // TODO: complete me!
        public int predict(Matrix x)
        {
            // x is a matrix with a single row of features

            double isHuman = 1.0;
            double isZombie = 1.0;
            for (int i = 0; i < x.getShape().getNumCols(); i++)
            {
                if (i == 0)
                {
                    // first continuous feature
                    if (calculateProbability(x.get(0, i), this.meanStdContinuousHumans1.get("mean"), this.meanStdContinuousHumans1.get("std")) < 0.001)
                    {
                        isHuman *= 0.001;
                    }
                    else
                    {
                        isHuman *= calculateProbability(x.get(0, i), this.meanStdContinuousHumans1.get("mean"), this.meanStdContinuousHumans1.get("std"));
                    }
                    
                    if (calculateProbability(x.get(0, i), this.meanStdContinuousZombies1.get("mean"), this.meanStdContinuousZombies1.get("std")) < 0.001)
                    {
                        isZombie *= 0.001;
                    }
                    else
                    {
                        isZombie *= calculateProbability(x.get(0, i), this.meanStdContinuousZombies1.get("mean"), this.meanStdContinuousZombies1.get("std"));
                    }
                }
                else if (i == 1)
                {
                    // second continuous feature
                    if (calculateProbability(x.get(0, i), this.meanStdContinuousHumans2.get("mean"), this.meanStdContinuousHumans2.get("std")) < 0.001)
                    {
                        isHuman *= 0.001;
                    }
                    else
                    {
                        isHuman *= calculateProbability(x.get(0, i), this.meanStdContinuousHumans2.get("mean"), this.meanStdContinuousHumans2.get("std"));
                    }

                    if (calculateProbability(x.get(0, i), this.meanStdContinuousZombies2.get("mean"), this.meanStdContinuousZombies2.get("std")) < 0.001)
                    {
                        isZombie *= 0.001;
                    }
                    else
                    {
                        isZombie *= calculateProbability(x.get(0, i), this.meanStdContinuousZombies2.get("mean"), this.meanStdContinuousZombies2.get("std"));
                    }
                }
                else if (i == 2)
                {
                    // first discrete feature
                    isHuman *= this.probDiscreteHuman1.getOrDefault(x.get(0, i), 0.001);
                    isZombie *= this.probDiscreteZombie1.getOrDefault(x.get(0, i), 0.001);
                }
                else if (i == 3)
                {
                    // second discrete feature
                    isHuman *= this.probDiscreteHuman2.getOrDefault(x.get(0, i), 0.001);
                    isZombie *= this.probDiscreteZombie2.getOrDefault(x.get(0, i), 0.001);
                }
                    
            }

            if (isHuman * this.probHuman > isZombie * this.probZombie)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

    }
    
    private NaiveBayes model;

    public NaiveBayesAgent(int playerNum, String[] args)
    {
        super(playerNum, args);
        this.model = new NaiveBayes();
    }

    public NaiveBayes getModel() { return this.model; }

    @Override
    public void train(Matrix X, Matrix y_gt)
    {
        this.getModel().fit(X, y_gt);
    }

    @Override
    public int predict(Matrix featureRowVector)
    {
        return this.getModel().predict(featureRowVector);
    }

}
