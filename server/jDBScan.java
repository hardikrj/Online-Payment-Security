import java.util.*;
import java.io.*;

class raw_point_data{
	int cluster=0;
	float x,y;
}

class cluster_centers{
	float x,y;
}

public class jDBScan{
	static String check="0";
	static ArrayList<Float> ar = new ArrayList<Float>(); 
	static ArrayList<Float> ar_2 = new ArrayList<Float>(); 
	
	static int width=800, height=800;
	
	static ArrayList<raw_point_data> raw_point = new ArrayList<raw_point_data>();;
	static ArrayList<cluster_centers> cluster_cent = new ArrayList<cluster_centers>();
	
	public static void main(String args[]){
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));		
			String line = "";
			while((line = br.readLine())!=null){
				ar.add(Float.parseFloat(line));
			}
			br.close();
			br = new BufferedReader(new FileReader(args[1]));		
			line = "";
			while((line = br.readLine())!=null){
				ar_2.add(Float.parseFloat(line));
			}
			br.close();
		}
		catch(Exception e){
			System.out.println("File Missing!");
		}
		int len = ar.size();
		generate_cluster_data();
		
		DB db = new DB();
		db.data(raw_point);
		db.dbscan();
		
		for(int i=0; i<raw_point.size();i++){
			raw_point.get(i).cluster = db.status.get(i);
			if(i==raw_point.size()-1){
				check=""+db.status.get(i);
			}
		}
		
		if(check.equals("0")){
			int result=0;
			System.out.println(result);
		}
		else{
			int result=1;
			System.out.println(result);
		}
		
	}
	
	static void generate_cluster_data(){ 
		int num_clusters = 0;
		int cluster_size = 0;
		int len = ar.size();
		int j=0;
		
		if(len<=100 && len>=20){
			cluster_size=20;
			num_clusters=len/20;
			j=10;
		}
		else if(len>100 && len<=300){
			cluster_size=30;
			num_clusters=len/30;
			j=15;
		}
		else if(len>300){
			cluster_size=50;
			num_clusters=len/50;
			j=25;
		}
			
		int b=0;
		
		for (int i = 0; i < num_clusters; i++) {
			cluster_centers clust = new cluster_centers();
			clust.x = ar.get(j);
			clust.y = ar_2.get(j);
			cluster_cent.add(clust);
			j+=cluster_size;
		}
		
		for(int i = 0; i < ar.size(); i++) {
			raw_point_data r = new raw_point_data();
			r.x=((ar.get(b)-10) * (width - 30))/20 ;
			r.y=((ar_2.get(b)-65) * (height - 30))/20 ;
			raw_point.add(r);
			b++;
		}
	}
}

class DB{

	int eps=35, minPts=5;
	
	ArrayList<raw_point_data> data = new ArrayList<raw_point_data>();
	
	ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(10);
	ArrayList<Integer> status = new ArrayList<Integer>();
	ArrayList<Integer> neighbours = new ArrayList<Integer>();
	ArrayList<Integer> curr_neighbours = new ArrayList<Integer>();
	
	void data(ArrayList<raw_point_data> raw){
		data = raw;
	}
	
	double euclidean_distance(raw_point_data point1, raw_point_data point2) {
		return Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
	}
	
	void get_region_neighbours(int point_idx, int chck) {
		neighbours = new ArrayList<Integer>();
		curr_neighbours = new ArrayList<Integer>();
		if(chck==0){
			raw_point_data d = data.get(point_idx);
			for (int i = 0; i < data.size(); i++) {
				if (point_idx == i) {
					continue;
				}
				if (euclidean_distance(data.get(i), d) <= eps) {
					neighbours.add(i);
				}
			}
		}
		else if(chck==1){
			raw_point_data d = data.get(point_idx);
			for (int i = 0; i < data.size(); i++) {
				if (point_idx == i) {
					continue;
				}
				if (euclidean_distance(data.get(i), d) <= eps) {
					curr_neighbours.add(i);
				}
			}
		}
		
	}
	
	void expand_cluster(int point_idx, ArrayList<Integer> neighbours, int cluster_idx) {
		clusters.get(cluster_idx - 1).add(point_idx); //add point to cluster
		status.set(point_idx,cluster_idx);	//assign cluster id

		for (int i = 0; i < neighbours.size(); i++) {
			int curr_point_idx = neighbours.get(i);
			if (status.get(curr_point_idx) == -1) {
				status.set(curr_point_idx,0); //visited and marked as noise by default
				get_region_neighbours(curr_point_idx,1);
				int curr_num_neighbours = curr_neighbours.size();
				if (curr_num_neighbours >= minPts) {
					expand_cluster(curr_point_idx, curr_neighbours, cluster_idx);
				}
			}

			if (status.get(curr_point_idx) < 1) { // not assigned to a cluster but visited (= 0)
				status.set(curr_point_idx,cluster_idx);
				clusters.get(cluster_idx - 1).add(curr_point_idx);
			}
		}
	}
	
	void dbscan(){
		for(int i = 0; i < data.size(); i++)
			status.add(-1);
		for (int i = 0; i < data.size(); i++) {
			if (status.get(i) == -1) {
				status.set(i,0); //visited and marked as noise by default
				get_region_neighbours(i,0);
				int num_neighbours = neighbours.size();
				if (num_neighbours < minPts) {
					status.set(i,0); //noise
				} else {
					//clusters.add([]); //empty new cluster
					clusters.add(new ArrayList<Integer>());
					int cluster_idx = clusters.size();
					expand_cluster(i, neighbours, cluster_idx);
				}
			}
		}
		//return status;
	}

}