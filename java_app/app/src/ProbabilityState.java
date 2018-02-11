import java.util.HashMap;

public class ProbabilityState {
    public final HashMap<Integer,Double> probMap;
    public final int maxState;

    public ProbabilityState(double[] dataVector){
        probMap = new HashMap<Integer,Double>();
        int vectorLength = dataVector.length;
        double doubleLength = dataVector.length;

        //round input to integers
        int[] normalisedVector = new int[vectorLength];
        maxState = normaliseArray(dataVector,normalisedVector);

        HashMap<Integer,Integer> countMap = new HashMap<Integer,Integer>();

        for (int value : normalisedVector) {
            Integer tmpKey = value;
            Integer tmpValue = countMap.remove(tmpKey);
            if (tmpValue == null)
            {
                countMap.put(tmpKey,1);
            }
            else
            {
                countMap.put(tmpKey,tmpValue + 1);
            }
        }

        for (Integer key : countMap.keySet()){
            probMap.put(key,countMap.get(key) / doubleLength);
        }
    }//constructor(double[])

    public static final int normaliseArray(double[] inputVector, int[] outputVector){
        int minVal = 0;
        int maxVal = 0;
        int currentValue;
        int i;
        int vectorLength = inputVector.length;

        if (vectorLength > 0){
            minVal = (int) Math.floor(inputVector[0]);
            maxVal = (int) Math.floor(inputVector[0]);

            for (i = 0; i < vectorLength; i++){
                currentValue = (int) Math.floor(inputVector[i]);
                outputVector[i] = currentValue;

                if (currentValue < minVal)
                {
                    minVal = currentValue;
                }

                if (currentValue > maxVal)
                {
                    maxVal = currentValue;
                }
            }/*for loop over vector*/

            for (i = 0; i < vectorLength; i++){
                outputVector[i] = outputVector[i] - minVal;
            }

            maxVal = (maxVal - minVal) + 1;
        }

        return maxVal;
    }//normaliseArray(double[],double[]

    public static final int mergeArrays(double[] firstVector, double[] secondVector, double[] outputVector){
        int[] firstNormalisedVector;
        int[] secondNormalisedVector;
        int firstNumStates;
        int secondNumStates;
        int i;
        int[] stateMap;
        int stateCount;
        int curIndex;
        int vectorLength = firstVector.length;

        firstNormalisedVector = new int[vectorLength];
        secondNormalisedVector = new int[vectorLength];

        firstNumStates = normaliseArray(firstVector,firstNormalisedVector);
        secondNumStates = normaliseArray(secondVector,secondNormalisedVector);

        stateMap = new int[firstNumStates*secondNumStates];
        stateCount = 1;
        for (i = 0; i < vectorLength; i++){
            curIndex = firstNormalisedVector[i] + (secondNormalisedVector[i] * firstNumStates);
            if (stateMap[curIndex] == 0)
            {
                stateMap[curIndex] = stateCount;
                stateCount++;
            }
            outputVector[i] = stateMap[curIndex];
        }

        return stateCount;
    }//mergeArrays(double[],double[],double[])


    public static void printIntVector(int[] vector){
        for (int i = 0; i < vector.length; i++){
            if (vector[i] > 0)
                System.out.println("Val at i=" + i + ", is " + vector[i]);
        }//for number of items in vector
    }//printIntVector(doublei[])

    public static void printDoubleVector(double[] vector){
        for (int i = 0; i < vector.length; i++){
            if (vector[i] > 0)
                System.out.println("Val at i=" + i + ", is " + vector[i]);
        }//for number of items in vector
    }//printDoubleVector(doublei[])
}

