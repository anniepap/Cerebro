import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

public class Entropy {
    public int numofSensors=14;
    public int size=1500;
    public int numberofFiles=264;

    public static double LOG_BASE = 2.0;

    public void calculations(){
        double[][] dataVector =new double[numofSensors][size];
        Vector []v = new Vector[numberofFiles];
        for(int i = 0; i < v.length; i++)
            v[i] = new Vector(15);

        String line = "";
        String csvFile;
        int cur_file;
        String cvsSplitBy = ",";
        double entr;

        File folder = new File("C:/Users/User/Desktop/Data Final for Software Development");
        File[] listOfFiles = folder.listFiles();
        cur_file=0;
        for (File file : listOfFiles){
            int cur_line=0;
            if (file.isFile()) {
                System.out.println(file.getName());
                //System.out.println(file.getPath());
            }
            csvFile = file.getPath();

                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    line = br.readLine();
                    while ((line = br.readLine()) != null) {

                        String[] line_sensors = line.split(cvsSplitBy);

                        for (int i = 0; i < numofSensors; i++) {
                            dataVector[i][cur_line]=Float.parseFloat(line_sensors[i]);
                            //System.out.println("line " + cur_line + " , value=" + line_sensors[i]);
                        }
                        cur_line++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            for (int i = 0; i < numofSensors; i++) {
                //dataVector[i][cur_line]=Float.parseFloat(line_sensors[i]);
                entr=calculateEntropy(dataVector[i]);

                System.out.println("entropy of sensor " + i + " is ="+entr);
                v[cur_file].add(entr);
                //calculateEntropy(dataVector[i]);
            }
            v[cur_file].add(file.getName());
            cur_file++;
        }

    }

    public static double calculateEntropy(double[] dataVector) {
        ProbabilityState state = new ProbabilityState(dataVector);

        double entropy = 0.0;
        for (Double prob : state.probMap.values())
        {
            if (prob > 0)
            {
                entropy -= prob * Math.log(prob);
            }
        }

        entropy /= Math.log(LOG_BASE);

        return entropy;
    }//calculateEntropy(double [])

    public static void main(String[] args) {
        Entropy en = new Entropy();
        en.calculations();
    }

}
