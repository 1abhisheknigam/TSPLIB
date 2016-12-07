
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;


public class ls1 {
	
	
	
	DecimalFormat twos_precision =  new DecimalFormat("#0.00");
	public double [][] distance;
	public int dimension;
	public double best=0.00;
	public double tmpBest = 1.00;
	public double totaltime=0;
	public double cutoff_time=0;
	public int[] bestcitynum;
	public int seed;
	PrintWriter output_trace;
	
	
	public ls1(double[][] distance, int dimension, int randseed, double cutofftime, PrintWriter output_trace ){
	

		
	this.output_trace = output_trace;	
	this.dimension=dimension;
	this.distance=distance;
	this.seed=randseed;
	this.cutoff_time=cutofftime;
	}
	
	public double getBestCost(){
	int fir=0;
	int sec=0;
	//get the initial solution by random
	ArrayList<Integer> citynumr=new ArrayList<Integer>(dimension);
    for(int i = 1; i <= dimension; i++) citynumr.add(i);

    Random rand = new Random(seed);
    Collections.shuffle(citynumr, rand);

    int [] citynum =new int[dimension];
    for (int i=0;i<citynum.length;i++)
    	citynum[i]=citynumr.get(i);
	Random randforpos = new Random(seed);	
	double startTime = System.nanoTime();
    while(totaltime<cutoff_time){
    fir = 1 + (int)(randforpos.nextInt(dimension));
    sec = 1 + (int)(randforpos.nextInt(dimension));
    while (Math.abs(fir-sec)<2)      sec = 1 + (int)(randforpos.nextInt(dimension));
    
    if (fir>sec){
    	int temp=0;
        temp=sec;
        sec=fir;
        fir=temp;
    	}
    int[] citynum1 =new int [dimension];
    int[] citynum2 =new int [dimension];
    int[] citynum3 =new int [dimension];
    int[] citynum4 =new int [dimension];
    int[] citynum5 =new int [dimension];
    //swap the point
    for(int i=0;i<fir;i++){
    	citynum1[i]=citynum[i];
    	citynum2[i+sec-fir]=citynum[i];
    	citynum3[i+citynum.length-fir]=citynum[i];
    	citynum4[i+citynum.length-sec]=citynum[i];
    	citynum5[i+citynum.length-fir]=citynum[i];
    }
    for(int i=fir;i<sec;i++){
    	citynum1[i+citynum.length-sec]=citynum[i];
    	citynum2[i-fir]=citynum[i];
    	citynum3[i-fir]=citynum[i];
    	citynum4[i+citynum.length-sec]=citynum[i];
    	citynum5[i+citynum.length-sec-fir]=citynum[i];
    }
    for(int i=sec;i<citynum.length;i++){
    	citynum1[fir+i-sec]=citynum[i];
    	citynum2[i]=citynum[i];
    	citynum3[i-fir]=citynum[i];
    	citynum4[i-sec]=citynum[i];
    	citynum5[i-sec]=citynum[i];
    }
    //compare different distance
    double [] totaldistance ={0.00,0.00,0.00,0.00,0.00,0.00};
    for (int i=0;i<citynum.length-1;i++){
    	totaldistance[0]+= distance[citynum[i]][citynum[i+1]];
    	totaldistance[1]+= distance[citynum1[i]][citynum1[i+1]];
    	totaldistance[2]+= distance[citynum2[i]][citynum2[i+1]];
    	totaldistance[3]+= distance[citynum3[i]][citynum3[i+1]];
    	totaldistance[4]+= distance[citynum4[i]][citynum4[i+1]];
    	totaldistance[5]+= distance[citynum5[i]][citynum5[i+1]];
    }
    double a,b,c,d,e,f;
    totaldistance[0]=totaldistance[0]+ distance[citynum[citynum.length-1]][citynum[0]];
    a=totaldistance[0];
    totaldistance[1]=totaldistance[1]+ distance[citynum1[citynum.length-1]][citynum1[0]];
	b=totaldistance[1];
	totaldistance[2]=totaldistance[2]+ distance[citynum2[citynum.length-1]][citynum2[0]];
	c=totaldistance[2];
	totaldistance[3]=totaldistance[3]+ distance[citynum3[citynum.length-1]][citynum3[0]];
	d=totaldistance[3];
	totaldistance[4]=totaldistance[4]+ distance[citynum4[citynum.length-1]][citynum4[0]];
	e=totaldistance[4];
	totaldistance[5]=totaldistance[5]+ distance[citynum5[citynum.length-1]][citynum5[0]];
	f=totaldistance[5];
	Arrays.sort(totaldistance);
	 this.best=totaldistance[0];
		    if (best==a){
		        citynum=citynum;
		        }
		    else if (best==b){
		        citynum=citynum1;
		        }
		    else if (best==c){
		        citynum=citynum2;
		        }
		    else if (best==d){
		        citynum=citynum3;
		        }
		    else if (best==e){
		        citynum=citynum4;
		        }
		    else if (best==f){
		        citynum=citynum5;
		        }
		   
		    double endTime = System.nanoTime();
		    totaltime= (endTime-startTime)/1000000000;
	//output the best one
			    double betterRouteFoundTime = (System.nanoTime() - startTime)/(double)1000000;
				output_trace.println(twos_precision.format(betterRouteFoundTime/1000) + ", " + (int)best);
				tmpBest = best;
		    
	}	    
    		this.bestcitynum=citynum;
			return best;
	}

	public int[] getRoute(){
		return bestcitynum;
	}
}
