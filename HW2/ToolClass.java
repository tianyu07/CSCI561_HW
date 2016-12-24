import java.util.ArrayList;

public class ToolClass {
	
	public static ArrayList<String> Raid(char player, int n, int row, int col, char[][] borderGraph) {
		ArrayList<String> adjacent = new ArrayList<String>();
		char adversary = player == 'X'?'O':'X';	
		boolean canRaid = canRaid(player, n, row, col, borderGraph);
		if (!canRaid)	return adjacent;
		if (row > 0 && borderGraph[row - 1][col] == adversary) {
			adjacent.add(row - 1 + " " + col);
		}
		if (row + 1 < n && borderGraph[row + 1][col] == adversary) {
			adjacent.add(row + 1 + " " + col);
		}
		if (col > 0 && borderGraph[row][col - 1] == adversary) {
			adjacent.add(row + " " + (col - 1));
		}
		if (col + 1 < n && borderGraph[row][col + 1] == adversary) {
			adjacent.add(row + " " + (col + 1));
		}
		return adjacent;
	}


	public static boolean canRaid(char player, int n, int row, int col, char[][] borderGraph) {
		if (row > 0 && borderGraph[row - 1][col] == player) {
			return true;
		}
		else if (row + 1 < n && borderGraph[row + 1][col] == player) {
			return true;
		}
		else if (col > 0 && borderGraph[row][col - 1] == player) {
			return true;
		}
		else if (col + 1 < n && borderGraph[row][col + 1] == player) {
			return true;
		}
		return false;
	}
	
	public static int Min(int v, int s) {
		return v > s ? s : v;
	}
	
	public static int Max(int v, int s) {
		return v < s ? s : v;
	}
}