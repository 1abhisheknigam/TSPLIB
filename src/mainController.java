import java.awt.Toolkit;
import java.io.IOException;


/** mainController.java
* 
* The main file for TSPLIB
* 
* Checks all the arguments for commandline and then runs the proper algorithm that is desired.
* 
* Also checks if the arguments are valid. If not, sends a custom error message telling user 
* exactly what the mistake was.
*
* @author Abhishek Nigam
* @since  Dec 6, 2016
*/

public class mainController {
	
	private static final String MST_APPROX_MODE = "MSTApprox";
	private static final String HEURISTIC_MODE = "Heur";
	private static final String BNB_MODE = "BnB";
	private static final String LS1_MODE = "LS1";
	private static final String LS2_MODE = "LS2";
	
	private static final String INST_FLAG = "-inst";
	private static final String ALG_FLAG = "-alg";
	private static final String TIME_FLAG = "-time";
	private static final String SEED_FLAG = "-seed";
	
	private static final String INPUT_FILE_TYPE = "tsp";

	/*
	 * main class of TSPLIB, responsible for sending arguments to the right place
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		/* ARGUMENTS INFORMATION
		 * exec −inst < filename > −alg [BnB|MSTApprox|Heur|LS1|LS2] −time < cutoff in seconds > −seed < random seed >
		 * args[0] = "-inst"
		 * args[1] = "filename.tsp"
		 * args[2] = "-alg"
		 * args[3] = "METHOD [BnB|MSTApprox|Heur|LS1|LS2]"
		 * args[4] = "-time"
		 * args[5] = "CUTOFF (e.g. 10.01)"
		 * args[6] = "-seed"
		 * args[7] = "RANDOMSEED (e.g. 600)"
		 */
		try{
			checkArgs(args);
		}
		catch(InvalidArgsException e){
			System.err.println(e.toString());
			System.err.println("Usage: jar −inst <filename.tsp> −alg [BnB|MSTApprox|Heur|LS1|LS2] −time <cutoff in seconds> (−seed <random seed>)");
			return;
		}
		
		String input_file 		 = args[1];
		String TSP_mode   		 = args[3];
		Double cutoff_in_seconds = Double.valueOf(args[5]);
		
		System.out.println("Input:" + input_file + " " + "Mode:" + TSP_mode);
		
		switch(TSP_mode){
			case BNB_MODE:
				RunBnB.run(input_file, cutoff_in_seconds);
				break;
			case MST_APPROX_MODE:
				RunMSTApprox.run(input_file, cutoff_in_seconds);
				break;
			case HEURISTIC_MODE:
				RunHeuristicFurthestInsertion.run(input_file, cutoff_in_seconds);
				break;
			case LS1_MODE:
				int seed1 = Integer.valueOf(args[7]);
				RunLS1.run(input_file, cutoff_in_seconds, seed1);
			case LS2_MODE:
				int seed2 = Integer.valueOf(args[7]);
				RunLS2.run(input_file, cutoff_in_seconds, seed2);
		}
		
		Toolkit.getDefaultToolkit().beep();    
	}
	
	/*
	 * Does the meat of the argument checking
	 */
	private static void checkArgs(String[] args) throws InvalidArgsException{
		//not the final versions yet
		if(args.length<6) throw new InvalidArgsException("Too few arguments");
		if(!args[0].equals(INST_FLAG)) throw new InvalidArgsException("No -inst flag");
		if(!args[1].split("\\.")[1].equals(INPUT_FILE_TYPE)) throw new InvalidArgsException("Invalid Filetype:." + args[0].split("\\.")[1]);
		if(!args[2].equals(ALG_FLAG)) throw new InvalidArgsException("No -arg flag");
		if(!args[4].equals(TIME_FLAG)) throw new InvalidArgsException("No -time flag");
		if(Double.valueOf(args[5])<0) throw new InvalidArgsException("Cutoff must be in positive seconds");
		
		switch(args[3]){
			case MST_APPROX_MODE:break;
			case HEURISTIC_MODE: break;
			case BNB_MODE: break;
			case LS1_MODE: checkargs(args);break;
			case LS2_MODE: checkargs(args);break;
			default: throw new InvalidArgsException("Invalid TSPLIB Mode: "+args[3]);
		}
		
	}
	
	/*
	 * Checks for random seeds if needed for specific algorithms.
	 */
	private static void checkargs(String[] args) throws InvalidArgsException{
		if(args.length<7)throw new InvalidArgsException("No random seed provided for mode " + args[3]);
		if(!args[6].equals(SEED_FLAG)) throw new InvalidArgsException("No -seed flag");
		if(args.length<8)throw new InvalidArgsException("No random seed provided for mode " + args[3]);
	}
	
	
	/** InvalidArgsException.class
	* 
	* A custom exception built to tell what was wrong in the way the program was called.
	*
	* @author Abhishek Nigam
	* @since  Dec 6, 2016
	*/
	private static class InvalidArgsException extends Exception{
		private static final long serialVersionUID = -223663034384178530L;
		public InvalidArgsException(String message) {
			super(message);
		}	
	}
}
