import java.util.Comparator;

public class nodeComparator implements Comparator<node>{

	@Override
	public int compare(node arg0, node arg1) {
		if(arg0.getDistance() > arg1.getDistance())return 1;
		else if(arg0.getDistance() == arg1.getDistance()){
			if(arg0.getFrom()<arg1.getFrom())return -1;
			else if(arg0.getFrom() > arg1.getFrom())return 1;
			else return 0;
		}
		else return -1;
	}

}
