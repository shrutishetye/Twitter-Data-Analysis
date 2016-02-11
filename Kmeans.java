
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Shruti
 */
public class Kmeans {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    static List<Tweet> tweetlist;
    static List<Tweet> lastcentroidlist;
    static List<Cluster> clusterlist;
    static int K = 25;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        tweetlist = new ArrayList<Tweet>();
        K = Integer.parseInt(args[0]);
        String initialseed = args[1];
        String inputjson = args[2];
        String outputfile = args[3];
        //readJson("C:\\Users\\Shruti\\Desktop\\ML\\Assignment5\\tweets.json");
        //getInitialSeed("C:\\Users\\Shruti\\Desktop\\ML\\Assignment5\\InitialSeeds.txt");
        readJson(inputjson);
        getInitialSeed(initialseed);
        for (int j = 0; j < 25; j++) {
            clusterTweets();
            updateCentroids();
            List<Tweet> currcentroidlist = new ArrayList<>();
            double change = 0;

            for (int i = 0; i < clusterlist.size(); i++) {

                currcentroidlist.add(clusterlist.get(i).centroid);//new centroids
                change += calJaccardDist(lastcentroidlist.get(i).text, currcentroidlist.get(i).text);
            }

            if (change == 0) {
                break;
            }
        }
        printCluster(outputfile);
    }

    public static void readJson(String path) throws IOException {
        FileInputStream fin = new FileInputStream(path);
        BufferedReader input = new BufferedReader(new InputStreamReader(fin));
        String line;
        ArrayList<String> s = new ArrayList();
        while ((line = input.readLine()) != null) {
            s.add(line);
        }
        for (String item : s) {
            Tweet t = new Tweet();
            String[] l = item.split("\"text\":");
            String[] m = item.split("\"id\":");
            t.text = l[1].substring(1, l[1].indexOf(',')).replace("\"", "");
            t.id = Long.parseLong(m[1].substring(1, m[1].indexOf(',')).replace("\"", ""));
            tweetlist.add(t);
        }
        int j = 0;
    }

    public static void getInitialSeed(String path) throws IOException {
        FileInputStream fin = new FileInputStream(path);
        BufferedReader input = new BufferedReader(new InputStreamReader(fin));
        String line;
        ArrayList<String> s = new ArrayList();
        while ((line = input.readLine()) != null) {
            s.add(line.replace(",", ""));
        }
        clusterlist = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < tweetlist.size(); j++) {
                if (Long.parseLong(s.get(i)) == tweetlist.get(j).id) {
                    Cluster c = new Cluster(i);
                    c.centroid = tweetlist.get(j);
                    clusterlist.add(c);

                }
            }
        }

    }

    public static double calJaccardDist(String centroid, String tweet) {
        List<String> a = Arrays.asList(centroid.toLowerCase().split(" "));
        List<String> b = Arrays.asList(tweet.toLowerCase().split(" "));

        Set<String> union = new HashSet<String>(a);
        union.addAll(b);

        Set<String> intersection = new HashSet<String>(a);
        intersection.retainAll(b);

        return (double) (1 - (intersection.size() / (double) union.size()));

    }

    public static void clusterTweets() {

        flushClusters();
        ArrayList<Double[]> distancelist = new ArrayList();

        for (int i = 0; i < K; i++) {

            Double[] distance = new Double[tweetlist.size()];

            for (int j = 0; j < tweetlist.size(); j++) {

                distance[j] = calJaccardDist(clusterlist.get(i).centroid.text, tweetlist.get(j).text);
            }

            distancelist.add(distance);
        }

        for (int i = 0; i < tweetlist.size(); i++) {
            Double min = Double.MAX_VALUE;
            int index = 0;
            for (int k = 0; k < K; k++) {
                if (distancelist.get(k)[i] < min) {
                    min = distancelist.get(k)[i];
                    index = k;
                }

            }
            clusterlist.get(index).tweets.add(tweetlist.get(i));

        }
    }

    public static void updateCentroids() {

        for (int i = 0; i < K; i++) {
            int index = 0;
            double min = Double.MAX_VALUE;
            for (int j = 0; j < clusterlist.get(i).tweets.size(); j++) {

                double distance = 0;

                for (int k = 0; k < clusterlist.get(i).tweets.size(); k++) {
                    distance += calJaccardDist(clusterlist.get(i).tweets.get(j).getText(), clusterlist.get(i).tweets.get(k).getText());
                }

                if (distance < min) {
                    min = distance;
                    index = j;
                }
            }
            clusterlist.get(i).centroid = clusterlist.get(i).tweets.get(index);

        }

    }

    public static void flushClusters() {
        lastcentroidlist = new ArrayList<>();
        for (int i = 0; i < clusterlist.size(); i++) {
            clusterlist.get(i).tweets.clear();
            lastcentroidlist.add(clusterlist.get(i).centroid);//preserve older centroids
        }
    }

    public static void printCluster(String outputfilepath) throws IOException {
        for (int i = 0; i < clusterlist.size(); i++) {
//            System.out.print(i + 1 + "\t"+ clusterlist.get(i).centroid.id+"\t");
//            String s = "";
//            for (int j = 0; j < clusterlist.get(i).tweets.size(); j++) {
//                s += clusterlist.get(i).tweets.get(j).id.toString() + ",";
//            }
//            System.out.println(s);

            try (PrintWriter out = new PrintWriter(new FileWriter(outputfilepath, true))) {
                out.print(i + 1 + "\t");
                String s = "";
                for (int j = 0; j < clusterlist.get(i).tweets.size(); j++) {
                    s += clusterlist.get(i).tweets.get(j).id.toString() + ",";
                }
                out.println(s);
            }
        }
    }

    public static double findSSE() {
        double sse = 0;
        for (Cluster c : clusterlist) {
            double distance = 0;
            double d = 0;
            for (int k = 0; k < c.tweets.size(); k++) {
                d = calJaccardDist(c.centroid.text, c.tweets.get(k).text);
                distance += (d * d);
            }
            sse += distance;
        }
        return sse;
    }
}
