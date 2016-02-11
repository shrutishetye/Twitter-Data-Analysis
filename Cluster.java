
/**
 *
 * @author Shruti
 */
import java.util.ArrayList;
import java.util.List;

public class Cluster {
	public List<Tweet> tweets;
	public Tweet centroid;
	public int id;
	public Cluster(int id) 
	{
		this.id = id;
		this.tweets = new ArrayList();
		this.centroid = null;
	}
	public void setCentroid(Tweet t)
	{
		this.centroid = t;
	}
	public Tweet getCentroid()
	{
		return this.centroid;
	}
	public void addPoint(Tweet t)
	{
		this.tweets.add(t);
	}
	public List<Tweet> getPoints()
	{
		return this.tweets;
	}
	public void clear()
	{
		tweets.clear();
	}
	public void printCluster()
	{
		System.out.println("[Cluster: " + this.id+"]");
		System.out.println("[Centroid: " + this.centroid.getText()+ "]");
		System.out.println("[Points:");
		for(Tweet t : tweets) {
			System.out.print(t.getId() +",");
		}
		System.out.println("]");
	}
	
}
