import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class homework {
	
	public static void main(String[] args) {
		
		try(
				FileReader inputFile = new FileReader("input.txt");
				FileWriter outputFile = new FileWriter("output.txt");
				BufferedReader br = new BufferedReader(inputFile);
				BufferedWriter bw = new BufferedWriter(outputFile);
				) {
			
			String line;
			ArrayList<String> arrList = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				line = line.trim();
				arrList.add(line);
		    }
			
			int[][] valueGraph = BuildValueGraph(arrList);
			char[][] borderGraph = BuildBorderGraph(arrList);
			ArrayList<String> returnList = FindPath(arrList, valueGraph, borderGraph);
			
            for (int i = 0; i < returnList.size() - 1; i++) {
                bw.write(returnList.get(i) + "\n");
            }
            bw.write(returnList.get(returnList.size() - 1));
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	
	}


	private static int[][] BuildValueGraph(ArrayList<String> arrList) {
		int n = Integer.parseInt(arrList.get(0));
		int[][] valueGraph = new int[n][n];
		for (int i = 0; i < n; i++) {
			String[] values = arrList.get(4 + i).split(" ");
			for (int j = 0; j < n; j++) {
				valueGraph[i][j] = Integer.parseInt(values[j]);
			}
		}
		return valueGraph;
	}

	private static char[][] BuildBorderGraph(ArrayList<String> arrList) {
		int n = Integer.parseInt(arrList.get(0));
		char[][] borderGraph = new char[n][n];
		for (int i = 0; i < n; i++) {
			borderGraph[i] = arrList.get(4 + n + i).toCharArray();
		}
		return borderGraph;
	}
	
	private static ArrayList<String> FindPath(ArrayList<String> arrList, 
			int[][] valueGraph, char[][] borderGraph) {
		ArrayList<String> returnList = new ArrayList<String>();
		if (arrList.get(1).equals("MINIMAX")) {
			MinimaxAlgorithm minimaxObj = new MinimaxAlgorithm(arrList, valueGraph, borderGraph);
			returnList = minimaxObj.MinimaxSearch();
		}
		else if (arrList.get(1).equals("ALPHABETA")) {
			Alpha_Beta_Algorithm alphaBetaObj = new Alpha_Beta_Algorithm(arrList, valueGraph, borderGraph);
			returnList = alphaBetaObj.Alpha_Beta_Search();
		}
		return returnList;
	}

}
