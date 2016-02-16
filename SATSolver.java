/* *****************************************************
Description: SAT Solver for CNF sentences
Author: Poushali Banerjee
Last Update: May 2014
/*******************************************************/


import java.awt.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;


public class SATSolver {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static String emptyString = "";
	static int chemicals;
	static int containers;
	static int countConstraints;
	static ArrayList<String> falseClauses= new ArrayList<String>();
	static int[][] model;
	static float p;
	static int max_flips;
	static String newLine = System.getProperty("line.separator");
	static Boolean isPLTrue=false;
	static Boolean isWalkSatTrue=false;
	
	
	public static void main(String[] args) throws IOException {
		
		 int [][] cTable = readcTable(args);
		 String constraintsPLogic = generatePLogic(cTable);
		 System.out.println("pLogic " + constraintsPLogic);
		 String CNFStatement = convertToCNF(constraintsPLogic);

		 System.out.println("CNFStatement " + CNFStatement);
		 String inputCNF=CNFStatement;

			ArrayList<String> clauses = new ArrayList<String>();
			ArrayList<String> newClauses1 = new ArrayList<String>();
			StringTokenizer stringtokenizer = new StringTokenizer(inputCNF, "^");
			while (stringtokenizer.hasMoreElements()) {
			clauses.add(stringtokenizer.nextToken().trim());
			}

			Boolean value = plResolution(clauses,newClauses1);
			isPLTrue = value;
			System.out.println("PL value " + value);

		 if(value == true){
			 Random generator = new Random();
			 Float randomNumberInput = generator.nextFloat();

			 Boolean walkSATVal = walkSAT(clauses);
			 if(walkSATVal == true){
				 isWalkSatTrue = true;
				System.out.println("Found Sol"); 
				writecTable(model, args[1],1);	
			 }
			 else{
				 System.out.println("No Solution");
				 writecTable(model, args[1],0);	
			 }
		 }
		
	}


	 
//select 1 clause(CLAUSE) randomly from the set of false clauses
//generate random number btw 0 and 1 and if that is greater than p then, flip the value of
//one randomly selected variable from the CLAUSE	 
static Boolean walkSAT(ArrayList<String> inputClausesWalkSAT){


  Vector<String> v = new Vector<String>();

  System.out.println("Model Vals ");
  for(int i =0;i<chemicals;i++){
	  for(int j =0;j<containers;j++){
		  Random rand=new Random();
		  int randomIndex=rand.nextInt(2); 
		  model[i][j] = randomIndex;
		System.out.print(randomIndex + " ");
	  }
	  System.out.println();
  }

  model[4][1]=1;*/
  	for(int i =1;i<=max_flips;i++){
	int value =  isSatisfiable(inputClausesWalkSAT);

	 if(value == 1){

		 System.out.println("Satisfiable true");
		 return true;
		 }
	 else{
		 Random rand=new Random();
		  int randomIndexFalseClauses=rand.nextInt(falseClauses.size()); 
		  String randomClause = falseClauses.get(randomIndexFalseClauses);		  
		  float randomIndexP=rand.nextFloat();
		  randomClause = randomClause.replace("(", "");
			randomClause = randomClause.replace(")", "");
			System.out.println("rand_val "+ randomIndexP);
		  if(randomIndexP<p){
				StringTokenizer stringtokenizer = new StringTokenizer(randomClause, " V ");	
				int randomClauseToFlipIndex = rand.nextInt(stringtokenizer.countTokens());
				for(int m =0;m<=randomClauseToFlipIndex;m++){
					String token = stringtokenizer.nextToken();
					System.out.println("token " + token);

					if(token.contains("X")){token = token.replace("X", "");}
					if(token.contains("~")){token = token.replace("~", "");}
					StringTokenizer commatokenizer = new StringTokenizer(token, ",");
					int index1 = Integer.parseInt(commatokenizer.nextToken())-1;
					int index2 =  Integer.parseInt(commatokenizer.nextToken())-1;

					if(model[index1][index2] == 0 ){
						model[index1][index2] = 1;
					}
					else if (model[index1][index2]==1){
						model[index1][index2] = 0;
						
					}
				}
			}
		  else{
			  StringTokenizer stringtokenizer = new StringTokenizer(randomClause, " V ");	
				int randomClauseToFlipIndex = rand.nextInt(stringtokenizer.countTokens());
				for(int m =0;m<=randomClauseToFlipIndex;m++){
					String token = stringtokenizer.nextToken();

					v.add(token);
					
			  int []Arr = new int[v.size()];
			  
			  for(int k=0;k<v.size();k++){
				  int[][] tempModel =model;			  
					String considerLiteral = v.elementAt(k);
					if(considerLiteral.contains("X")){considerLiteral =considerLiteral.replace("X", "");}
					if(considerLiteral.contains("~")){considerLiteral = considerLiteral.replace("~", "");}
					StringTokenizer commatokenizer = new StringTokenizer(considerLiteral, ",");
					int index1 = Integer.parseInt(commatokenizer.nextToken())-1;
					int index2 =  Integer.parseInt(commatokenizer.nextToken())-1;
					
					if(model[index1][index2] == 0 ){
						tempModel[index1][index2] = 1;
					}
					else if (model[index1][index2]==1){
						tempModel[index1][index2] = 0;
						
					}	
					Arr[k]= clauseSatisfiable(inputClausesWalkSAT,tempModel);
					
			  }
			  int maxIndex = 0;
			  int maxNumber=Arr[0];
			  for (int b = 1; b < Arr.length; b++){
			     if ((Arr[b] > maxNumber)){
			     maxIndex = b;maxNumber=Arr[b];
			    }
			  }
			  
			  String holder = v.get(maxIndex);
			  if(holder.contains("X")){holder =holder.replace("X", "");}
				if(holder.contains("~")){holder = holder.replace("~", "");}
				StringTokenizer commatokenizer = new StringTokenizer(holder, ",");
				int index1 = Integer.parseInt(commatokenizer.nextToken())-1;
				int index2 =  Integer.parseInt(commatokenizer.nextToken())-1;
				if(model[index1][index2] == 0 ){
					model[index1][index2] = 1;
				}
				else if (model[index1][index2]==1){
					model[index1][index2] = 0;
					
				}
			  

		  }
		  		  
	 }
		  System.out.println("Printing model ");
			 for (int c = 0; c < chemicals; c++) {
					for (int d = 0; d < containers; d++) {				
						System.out.print(Integer.toString(model[c][d]));
					}
					System.out.println();
				}
	 }
	 
	
 }	
  	
  	return false;
	
}

	
//evaluate value for clauses based on assignment in model
//parse into set of clauses
//return falseClauses
static int clauseSatisfiable(ArrayList<String> clausesWalkSAT, int[][] inputTempModel){
	
	int numClauseTrue=0;
	for (int i =0;i<clausesWalkSAT.size();i++){
		int clauseVal=0;
		String clauseString = clausesWalkSAT.get(i);
		//String copyofClauseString = clauseString;
		clauseString = clauseString.replace("(", "");
		clauseString = clauseString.replace(")", "");	
		StringTokenizer stringtokenizer = new StringTokenizer(clauseString, " V ");	
		int numTokens1 = stringtokenizer.countTokens();
		for(int j =0;j<numTokens1;j++){
			Boolean notFlag = false;
			String literal = stringtokenizer.nextToken();
			if(literal.contains("X")){literal = literal.replace("X", "");}
			if(literal.contains("~")){literal = literal.replace("~", "");notFlag=true;}

			StringTokenizer commatokenizer = new StringTokenizer(literal, ",");
			int index1 = Integer.parseInt(commatokenizer.nextToken());
			int index2 =  Integer.parseInt(commatokenizer.nextToken());
			int modelVal = inputTempModel[index1-1][index2-1];
			if(notFlag == true){
				if(modelVal == 0){modelVal=1;}
				else{modelVal = 0;}
			}
			if(j==0){clauseVal = modelVal;}
			else{
				if(clauseVal == 0 && modelVal == 0)
				{clauseVal=0;}
				else{clauseVal=1;}
				
			}
			
			notFlag=false;
		}
		
		if(clauseVal == 1){
			numClauseTrue++;
		}
		
		}

	return numClauseTrue;

	
}

	//evaluate value for clauses based on assignment in model
	//parse into set of clauses
	//return falseClauses
	
static int isSatisfiable(ArrayList<String> clausesWalkSAT){
	
	int cnfVal = 0;
	int clauseVal=0;

	for (int i =0;i<clausesWalkSAT.size();i++){
		String clauseString = clausesWalkSAT.get(i);
		String copyofClauseString = clauseString;
		clauseString = clauseString.replace("(", "");
		clauseString = clauseString.replace(")", "");	
		StringTokenizer stringtokenizer = new StringTokenizer(clauseString, " V ");	
		int numTokens = stringtokenizer.countTokens();

		for(int j=0;j<numTokens;j++){
			Boolean notFlag = false;
			String literal = stringtokenizer.nextToken();
			if(literal.contains("X")){literal = literal.replace("X", "");}
			if(literal.contains("~")){literal = literal.replace("~", "");notFlag=true;}
	
			StringTokenizer commatokenizer = new StringTokenizer(literal, ",");
			String val1 = commatokenizer.nextToken();
			String val2 = commatokenizer.nextToken();
			int index1 = Integer.parseInt(val1);
			int index2 =  Integer.parseInt(val2);
			int modelVal = model[index1-1][index2-1];

			if(notFlag == true){

				if(modelVal == 0){modelVal=1;}
				else{modelVal = 0;}
			}
			
			if(j==0){
				clauseVal = modelVal;
				}
			else{

				if(clauseVal == 0 && modelVal == 0)
				{clauseVal=0;}
				else{clauseVal=1;}				
			}			

		}

		if(clauseVal == 0){
			falseClauses.add(copyofClauseString);
		}
		if(i==0){
			cnfVal = clauseVal;
		}
		else{
			if(cnfVal == 1 && clauseVal == 1)
			{cnfVal=1;}
			else{cnfVal=0;}
		}

		}

	return cnfVal;
	

}
	
	static String convertToCNF(String input){
		String CNF = "";
		String temp = input;
		String temp2 = "";
		int pos=0;
		int orClauses=0;
		int index =0;
		String word8 = "";
		String orCNF = "";
		while(!temp.isEmpty()){
			int occurrences = 0;
			if(temp.contains("(("))

			temp2 = temp.substring(0,pos+2);
			
			 if(temp2.contains("OR")){
				 word8=temp2;
				 index = word8.indexOf("OR");

				 while (index != -1) {
				        occurrences++;
				        word8 = word8.substring(index + 1);
				        index = word8.indexOf("OR");
				    }
				 orClauses = occurrences+1;

				temp2 = temp2.replaceAll("[\\(\\)\\^]", "");
				temp2=temp2.replace("OR","");
				System.out.println(temp2);
				orCNF = printTruthTable(orClauses,temp2);
				CNF = CNF.concat(orCNF);
				CNF = CNF.concat(" ^ ");
				
			}			 
			 else{

				 CNF = CNF.concat(temp2 + " AND ");
			 }

			temp = temp.substring(pos+2);
			if(temp.contains("AND")){//remove trailing AND
				
				temp = temp.replace("AND", "").trim();

			}

		}
	
		CNF=CNF.replaceAll("[(][(]", "(");
		CNF=CNF.replaceAll("[)][)]", ")"); 
		CNF=CNF.replaceAll("[\\s][\\s][\\s]", " "); 
		CNF=CNF.replaceAll("AND", "^"); 
		CNF=CNF.substring(0,CNF.lastIndexOf("^"));
		return CNF;
	}
	


	private static String printTruthTable(int n, String literals) {
		String CNFBuilder = "";
		ArrayList<String> literalList = new ArrayList<String>();
		StringTokenizer stringtokenizer = new StringTokenizer(literals, " ");
		while (stringtokenizer.hasMoreElements()) {
			
		literalList.add(stringtokenizer.nextToken().trim());
		}
        int rows = (int) Math.pow(2,n);
        CNFBuilder = CNFBuilder.concat("(");
        
        for (int i=0; i<rows; i++) {
            for (int j=n-1; j>=0; j--) {
            	CNFBuilder = CNFBuilder.concat(literalList.get(((2*j) + (i/(int) Math.pow(2, j))%2)));
            	if(j!=0){CNFBuilder = CNFBuilder.concat(" V ");}
            }
            if(i!=(rows-1)){
            	CNFBuilder = CNFBuilder.concat(") ^ (");
            }
            
        }
        CNFBuilder = CNFBuilder.concat(")");
        System.out.println(CNFBuilder);
        return CNFBuilder;
    }
	

public static boolean plResolution(ArrayList<String> inputClauses, ArrayList<String> newClauses){
		
		for(int i =0;i<inputClauses.size()-1;i++){
			for(int j = i+1;j<inputClauses.size();j++){
				ArrayList<String> resolventSet = new ArrayList<String>();
				String clause1 = inputClauses.get(i);
				String clause2 = inputClauses.get(j);
				resolventSet = plResolve(clause1, clause2);	
				if (resolventSet.contains(emptyString)){
					return false;
				}	
				newClauses.addAll(resolventSet);
			}
		}
		int counter=0;
	
		for(int x=0;x<newClauses.size();x++){
			if(inputClauses.contains(newClauses.get(x))){
				counter++;	
			}			
		}
		if(counter == newClauses.size()){
			return true;
		}
	
		inputClauses.addAll(newClauses);
		return plResolution(inputClauses,newClauses);
		//return true;
	}
	
	
	static String generatePLogic(int[][] inputTable){
		
		ArrayList<String> visited = new ArrayList<String>();		
		String pLogic = "";
		Boolean updated1=false, updated2 = false,updated3=false;
		pLogic = pLogic.concat("(");
		int y=1;
		
		for (int i = 1; i < chemicals+1; i++) {
			for (int j = 1; j < chemicals+1; j++) {
				for(int x =1; x<containers+1; x++){
					
					String pair = Integer.toString(i)+Integer.toString(j);
					if((inputTable[i-1][j-1] == +1 || inputTable[i-1][j-1] == 1) && 
							visited.contains(pair) == false){
	 					pLogic = pLogic.concat("(" + "X" + i+ ","+x   + " ^ " + "X" + j + ","+x+")");
	 					if(x<containers){pLogic = pLogic.concat(" OR ");}
						updated1 = true;
						}
						else if(inputTable[i-1][j-1] == -1 && 
								visited.contains(pair) == false){
							pLogic = pLogic.concat("(" + "~X" + i+ ","+x  + " V " + "~X" + j + ","+x+")");
							if(x<containers) {pLogic = pLogic.concat(" ^ ");}	
							updated1=true;							
						}				
				}								
				String reversePair = Integer.toString(j)+Integer.toString(i);
				visited.add(reversePair);	
				if(updated1==true && y<(countConstraints/2)){
					pLogic = pLogic.concat(") AND (" );	
					updated1=false;
					y++;
				}
				
				}	
			}
		pLogic = pLogic.concat(")");
		pLogic = pLogic.concat(" AND (");

		for(int i=1;i<=chemicals;i++){

			pLogic = pLogic.concat("(");
			for(int j=1;j<=containers;j++){
				pLogic = pLogic.concat("X" + i+ ","+j);
				if(j<containers){
					pLogic = pLogic.concat(" V ");
				}
			}
			pLogic = pLogic.concat(")");
			if(i<chemicals){
				pLogic = pLogic.concat(" ^ ");	
			}
			
		}
		pLogic = pLogic.concat(")");
		
		pLogic = pLogic.concat(" AND (");
		for(int i=1;i<=chemicals;i++){

			for(int j=1;j<containers;j++){
				pLogic = pLogic.concat("(");
				pLogic = pLogic.concat("~X" + i+ ","+j);

					pLogic = pLogic.concat(" V ");

				for(int x=(j+1);x<=containers;x++){
					pLogic = pLogic.concat("~X" + i+ ","+x);
				}
				pLogic = pLogic.concat(")");
			
			}
			if(i<chemicals){
				pLogic = pLogic.concat(" ^ ");
			}
			
		}
		pLogic = pLogic.concat(")");
		return pLogic;	
	}
		

	
	static ArrayList<String> plResolve(String c1, String c2){
		
		String combine="";
		ArrayList<String> resolvents = new ArrayList<String>();
		StringTokenizer stringtokenizer1 = new StringTokenizer(c1, "OR");
		StringTokenizer stringtokenizer2 = new StringTokenizer(c2, "OR");
		ArrayList<String> symbols1 = new ArrayList<String>();
		ArrayList<String> symbols2 = new ArrayList<String>();
		while(stringtokenizer1.hasMoreTokens()){
			symbols1.add(stringtokenizer1.nextToken().trim().replaceAll("[()]", ""));
		}
		while(stringtokenizer2.hasMoreTokens()){
			symbols2.add(stringtokenizer2.nextToken().trim().replaceAll("[()]", ""));			
		}
				
		for(int i=0;i<symbols1.size();i++){
				if(symbols2.contains("~"+symbols1.get(i))){					
					int x = symbols2.indexOf("~"+symbols1.get(i));
					symbols1.remove(i);symbols2.remove(x);
					
					for(int y =0;y<symbols1.size();y++){
						combine = combine.concat(symbols1.get(y) + " OR ");
					}
					for(int y =0;y<symbols2.size();y++){
						combine = combine.concat(symbols2.get(y) + " OR ");
					}
					combine = combine.substring(0,combine.lastIndexOf("OR"));

					resolvents.add(combine);

				}
				
				
			}
		return resolvents;
	}
	
	
	
	
	static int[][] readcTable(String[] args) throws FileNotFoundException {
		String line;
		int k = 0;
		String temp,temp1;
		p=Float.parseFloat(args[2]);
		max_flips=Integer.parseInt(args[3]);
		System.out.println("p " + p + "max_flips " + max_flips);
	    	
	    	    if(args.length < 1) {
	    	    	System.out.println("Invalid input arguments");
	    			System.exit(1);
	    	    }
	    	   
	    	    Scanner reader = new Scanner(new FileInputStream(args[0]));
	    	    String firstLine = reader.nextLine();
	    	    temp = firstLine.substring(0, firstLine.indexOf(" ")).trim();
	    	    temp1 = firstLine.substring(firstLine.indexOf(" ")).trim();
	    	    int m = Integer.parseInt(temp);
	    	    chemicals = m;
	    	    containers = Integer.parseInt(temp1);
	    	    model = new int[chemicals][containers];

	    	    
	    	    int [][] inputcTable = new int[m][m];
	    	    int i = 0;
	    	    while(reader.hasNextLine()){
	    	    	
	    	    	line = reader.nextLine();
	    	    	for(int j=0; j<m; j++){  	    	    			    	    	
	    	        String[] ns = line.split(" ");
	    	        k = Integer.parseInt(ns[j]);
	    	        if(k == +1 || k == 1 | k==-1){countConstraints++;}
	    	        inputcTable[i][j] = k;    	    		
	    	    	}  	    	    		    	    
	    	    	i++;
	    	    }
	    	    reader.close();
	    	    
	    	    return inputcTable;    	     	    
	}


	
	static void writecTable(int[][] solution, String outputFile, int isFound) throws IOException {

		FileWriter writer = new FileWriter(outputFile);
		if(isFound == 0){
			System.out.println("Enter not found");
			writer.write(Integer.toString(isFound));
		}
		else if(isFound == 1){
			System.out.println("Enter found");
			writer.write(Integer.toString(isFound));
			writer.write(newLine);
			for (int j = 0; j < containers; j++) {
				for (int i = 0; i < chemicals; i++) {
				writer.write(Integer.toString(solution[i][j]));
			}
			writer.write(newLine);
		}
		}
				
			writer.close();
		}


}
