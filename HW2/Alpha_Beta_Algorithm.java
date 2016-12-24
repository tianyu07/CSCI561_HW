import java.util.ArrayList;

public class Alpha_Beta_Algorithm{
	private ArrayList<String> arrList;
	private int[][] valueGraph;
	private char[][] borderGraph;
	private ArrayList<String> returnList;
		
	public Alpha_Beta_Algorithm(ArrayList<String> arrList, int[][] valueGraph, char[][] borderGraph) {
		this.arrList = arrList;
		this.valueGraph = valueGraph;
		this.borderGraph = borderGraph;
		this.returnList = new ArrayList<String>();
	}
	
	public ArrayList<String> Alpha_Beta_Search() {
		int n = Integer.parseInt(arrList.get(0));
		char player = arrList.get(2).charAt(0);
		int depth = Integer.parseInt(arrList.get(3));
		int max = Integer.MIN_VALUE;
		int row = -1, col = -1;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		boolean ifRaid = false;
		boolean isRaid = false;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O') {
					borderGraph[i][j] = player;
					int value = MinValue(player, valueGraph, borderGraph, depth, i, j, n, alpha, beta, ifRaid); 
					borderGraph[i][j] = '.';
					if (value > max) {
						max = value;
						row = i;
						col = j;
					}
				}
			}
		}
		
		ifRaid = true;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O' && ToolClass.canRaid(player, n, i, j, borderGraph)) {
					borderGraph[i][j] = player;
					ArrayList<String> adjacent = ToolClass.Raid(player, n, i, j, borderGraph);
					if (adjacent.size() != 0) {
						for (int k = 0; k < adjacent.size(); k++) {
							String[] axis = adjacent.get(k).split(" ");
							borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player;
						}
					}
					int value = MinValue(player, valueGraph, borderGraph, depth, i, j, n, alpha, beta, ifRaid);
					if (adjacent.size() != 0) {
						for (int k = 0; k < adjacent.size(); k++) {
							String[] axis = adjacent.get(k).split(" ");
							borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player == 'X' ? 'O' : 'X';
						}
					}
					borderGraph[i][j] = '.';
					if (value > max) {
						max = value;
						row = i;
						col = j;
						isRaid = true;
					}
				}
			}
		}
		
		borderGraph[row][col] = player;
		ArrayList<String> adjacent = ToolClass.Raid(player, n, row, col, borderGraph);
		char column = (char) ('A' + col);
		if (adjacent.size() == 0 || !isRaid) {	
			returnList.add("" + column + (row+1) + " " + "Stake");
		}
		else {
			returnList.add("" + column + (row+1) + " " + "Raid");
			for (int i = 0; i < adjacent.size(); i++) {
				String[] axis = adjacent.get(i).split(" ");
				borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player;
			}
		}
		String graph = "";
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				graph += borderGraph[i][j];
			}
			returnList.add(graph);
			graph = "";
		}
		return returnList;
	}
	
	private static int MinValue(char player, int[][] valueGraph, char[][] borderGraph, int depth, int row, int col, int n, int alpha, int beta, boolean ifRaid) {
		if (depth == 1) {
			if (ifRaid) {
				ArrayList<String> adjacent = ToolClass.Raid(player, n, row, col, borderGraph);
				if (adjacent.size() != 0) {
					for (int i = 0; i < adjacent.size(); i++) {
						String[] axis = adjacent.get(i).split(" ");
						borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player;
					}
				}
				int score = 0;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (borderGraph[i][j] == player) score += valueGraph[i][j];
						else if (borderGraph[i][j] == '.') ;
						else score -= valueGraph[i][j];
					}
				}
				if (adjacent.size() != 0) {
					for (int i = 0; i < adjacent.size(); i++) {
						String[] axis = adjacent.get(i).split(" ");
						borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player == 'X' ? 'O' : 'X';
					}
				}
				return score;
			}
			else {
				int score = 0;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (borderGraph[i][j] == player) score += valueGraph[i][j];
						else if (borderGraph[i][j] == '.') ;
						else score -= valueGraph[i][j];
					}
				}
				return score;
			}			
		}
		boolean takeSteps = false;
		int v = Integer.MAX_VALUE;
		for (int i = 0; i < borderGraph.length; i++) {
			for (int j = 0; j < borderGraph[0].length; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O') {
					takeSteps = true;
					char thisTurnPlayer = player == 'X' ? 'O' : 'X';
					borderGraph[i][j] = thisTurnPlayer;
					v = ToolClass.Min(v, MaxValue(thisTurnPlayer, valueGraph, borderGraph, depth - 1, i, j, n, alpha, beta, ifRaid));					
					borderGraph[i][j] = '.';
					if (v <= alpha) return v;
					beta = ToolClass.Min(beta, v);
				}
			}
		}
		if (!takeSteps) {
			return MinValue(player, valueGraph, borderGraph, 1, row, col, n, alpha, beta, ifRaid);
		}
		ifRaid = true;
		for (int i = 0; i < borderGraph.length; i++) {
			for (int j = 0; j < borderGraph[0].length; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O') {
					char thisTurnPlayer = player == 'X' ? 'O' : 'X';
					if (ToolClass.canRaid(thisTurnPlayer, n, i, j, borderGraph)) {
						borderGraph[i][j] = thisTurnPlayer;
						ArrayList<String> adjacent = ToolClass.Raid(thisTurnPlayer, n, i, j, borderGraph);
						if (adjacent.size() != 0) {
							for (int k = 0; k < adjacent.size(); k++) {
								String[] axis = adjacent.get(k).split(" ");
								borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = thisTurnPlayer;
							}
						}
						v = ToolClass.Min(v, MaxValue(thisTurnPlayer, valueGraph, borderGraph, depth - 1, i, j, n, alpha, beta, ifRaid));
						if (adjacent.size() != 0) {
							for (int k = 0; k < adjacent.size(); k++) {
								String[] axis = adjacent.get(k).split(" ");
								borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = thisTurnPlayer == 'X' ? 'O' : 'X';
							}
						}
						borderGraph[i][j] = '.';						
						if (v <= alpha) return v;
						beta = ToolClass.Min(beta, v);
					}
				}
			}
		}
		
		return v;
	}


	private static int MaxValue(char player, int[][] valueGraph, char[][] borderGraph, int depth, int row, int col, int n, int alpha, int beta, boolean ifRaid) {
		if (depth == 1) {
			if (ifRaid) {
				ArrayList<String> adjacent = ToolClass.Raid(player, n, row, col, borderGraph);
				if (adjacent.size() != 0) {
					for (int i = 0; i < adjacent.size(); i++) {
						String[] axis = adjacent.get(i).split(" ");
						borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player;
					}
				}
				int score = 0;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (borderGraph[i][j] == player) score -= valueGraph[i][j];
						else if (borderGraph[i][j] == '.') ;
						else score += valueGraph[i][j];
					}
				}				
				if (adjacent.size() != 0) {
					for (int i = 0; i < adjacent.size(); i++) {
						String[] axis = adjacent.get(i).split(" ");
						borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = player == 'X' ? 'O' : 'X';
					}
				}
				return score;
			}
			else {
				int score = 0;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (borderGraph[i][j] == player) score -= valueGraph[i][j];
						else if (borderGraph[i][j] == '.') ;
						else score += valueGraph[i][j];
					}
				}
				return score;
			}
		}
		boolean takeSteps = false;
		int v = Integer.MIN_VALUE;
		for (int i = 0; i < borderGraph.length; i++) {
			for (int j = 0; j < borderGraph[0].length; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O') {
					takeSteps = true;
					char thisTurnPlayer = player == 'X' ? 'O' : 'X';
					borderGraph[i][j] = thisTurnPlayer;							
					v = ToolClass.Max(v, MinValue(thisTurnPlayer, valueGraph, borderGraph, depth - 1, i, j, n, alpha, beta, ifRaid));
					borderGraph[i][j] = '.';				
					if (v >= beta) return v;
					alpha = ToolClass.Max(v, alpha);
				}
			}
		}
		if (!takeSteps) {
			return MaxValue(player, valueGraph, borderGraph, 1, row, col, n, alpha, beta, ifRaid);
		}
		ifRaid = true;
		for (int i = 0; i < borderGraph.length; i++) {
			for (int j = 0; j < borderGraph[0].length; j++) {
				if (borderGraph[i][j] != 'X' && borderGraph[i][j] != 'O') {
					char thisTurnPlayer = player == 'X' ? 'O' : 'X';
					if (ToolClass.canRaid(thisTurnPlayer, n, i, j, borderGraph)) {
						borderGraph[i][j] = thisTurnPlayer;	
						ArrayList<String> adjacent = ToolClass.Raid(thisTurnPlayer, n, i, j, borderGraph);
						if (adjacent.size() != 0) {
							for (int k = 0; k < adjacent.size(); k++) {
								String[] axis = adjacent.get(k).split(" ");
								borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = thisTurnPlayer;
							}
						}
						v = ToolClass.Max(v, MinValue(thisTurnPlayer, valueGraph, borderGraph, depth - 1, i, j, n, alpha, beta, ifRaid));
						if (adjacent.size() != 0) {
							for (int k = 0; k < adjacent.size(); k++) {
								String[] axis = adjacent.get(k).split(" ");
								borderGraph[Integer.parseInt(axis[0])][Integer.parseInt(axis[1])] = thisTurnPlayer == 'X' ? 'O' : 'X';
							}
						}
						borderGraph[i][j] = '.';						
						if (v >= beta) return v;
						alpha = ToolClass.Max(v, alpha);
					}
				}
			}
		}
		
		return v;
	}
}