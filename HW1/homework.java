import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

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
				String line1 = line.trim();
				arrList.add(line1);
		    }
			
			LinkedList<String[]> returnList = FindPath(arrList);
			for (int i = 0; i < returnList.size(); i++) {
				bw.write(returnList.get(i)[0] + " " + returnList.get(i)[1] + "\n");
			}		    
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	private static LinkedList<String[]> FindPath(ArrayList<String> arrList) {
		LinkedList<String[]> returnList = new LinkedList<String[]>();
		Map<String, Integer> matrixHead = new HashMap<String, Integer>();
		Map<Integer, String> anti_matrixHead = new HashMap<Integer, String>();
		Map<String, ArrayList<String>> edgeSequence = new HashMap<String, ArrayList<String>>();
		int[][] relationGraph = buildGraph(arrList, matrixHead, anti_matrixHead, edgeSequence);
		
		if (arrList.get(0).equals("BFS")) {
			returnList = BFSSearch(arrList, relationGraph, edgeSequence, matrixHead, anti_matrixHead);			
		} else if (arrList.get(0).equals("DFS")) {
			returnList = DFSSearch(arrList, relationGraph, edgeSequence, matrixHead, anti_matrixHead);		
		} else if (arrList.get(0).equals("UCS")) {
			returnList = UCSSearch(arrList, relationGraph, edgeSequence, matrixHead, anti_matrixHead);
		} else if (arrList.get(0).equals("A*")) {
			returnList = ASearch(arrList, relationGraph, edgeSequence, matrixHead, anti_matrixHead);
		}
		return returnList;
	}

	private static int[][] buildGraph(ArrayList<String> arrList, Map<String, Integer> matrixHead, 
			Map<Integer, String> anti_matrixHead, Map<String, ArrayList<String>> edgeSequence) {
		int num = Integer.parseInt(arrList.get(3));
		ArrayList<String[]> strStore = new ArrayList<String[]>();
		Set<String> vertexSet = new HashSet<String>();
		for (int i = 0; i < num; i++) {
			strStore.add(arrList.get(4 + i).split(" "));
			if (!vertexSet.contains(strStore.get(i)[0])) {
				vertexSet.add(strStore.get(i)[0]);
			}
			if (!vertexSet.contains(strStore.get(i)[1])) {
				vertexSet.add(strStore.get(i)[1]);
			}
		}

		int[][] relationMatrix = new int[vertexSet.size()][vertexSet.size()]; 
		String startPoint, endPoint, distance;
		for (int i = 0, count = 0; i < num; i++) {
			startPoint = strStore.get(i)[0];
			endPoint = strStore.get(i)[1];
			distance = strStore.get(i)[2];
			
			if (!matrixHead.containsKey(startPoint)) {
				matrixHead.put(startPoint, count);
				anti_matrixHead.put(count, startPoint);
				count++;
			}
			if (!matrixHead.containsKey(endPoint)) {
				matrixHead.put(endPoint, count);
				anti_matrixHead.put(count, endPoint);
				count++;
			}
			storeValue(relationMatrix, matrixHead, startPoint, endPoint, distance);
		}
		
		for (int i = 0; i < num; i++) {
			startPoint = strStore.get(i)[0];
			endPoint = strStore.get(i)[1];
			ArrayList<String> destination = new ArrayList<String>();
			if (!edgeSequence.containsKey(startPoint)) {
				destination.add(endPoint);
				edgeSequence.put(startPoint, destination);
			} else {
				destination.addAll(edgeSequence.get(startPoint));
				destination.add(endPoint);
				edgeSequence.put(startPoint, destination);
			}
		}

		return relationMatrix;
		
	}

	private static void storeValue(int[][] relationMatrix, Map<String, Integer> matrixHead, 
			String startPoint, String endPoint, String distance) {
		int row = matrixHead.get(startPoint);
		int col = matrixHead.get(endPoint);
		relationMatrix[row][col] = Integer.parseInt(distance);
		
	}
	
	private static LinkedList<String[]> BFSSearch(ArrayList<String> arrList, int[][] relationGraph,
			Map<String, ArrayList<String>> edgeSequence, Map<String, Integer> matrixHead, 
			Map<Integer, String> anti_matrixHead) {
		
		LinkedList<String[]> returnList = new LinkedList<String[]>();
		Map<String, Path> exploreSet = new HashMap<String, Path>();
		Queue<Path> bfsQueue = new LinkedList<Path>();
		String start = arrList.get(1);
		String end = arrList.get(2);
		String path = "";
		
		if (matrixHead.containsKey(start)) {
			bfsQueue.add(new Path(start, 0, 0));
			exploreSet.put(start, new Path(start, 0, 0));
			while (!bfsQueue.isEmpty()) {				
				Path top = bfsQueue.poll();
				exploreSet.put(top.getLastPoint(), top);
				if (exploreSet.containsKey(end)) {
					path = exploreSet.get(end).get();
					break;
				}
				ArrayList<String> successor = edgeSequence.get(top.getLastPoint());
				if (successor != null) {
					for (int i = 0; i < successor.size(); i++) {
						if (!exploreSet.containsKey(successor.get(i))) {
							Path temp = new Path(top.get(), top.pathCost + 1, 0);
							temp.add(successor.get(i));
							bfsQueue.add(temp);
							exploreSet.put(temp.getLastPoint(), temp);
						}  
					}
				}
			}
			
			String[] pathArr = path.split(" ");
			for (int i = 0; i < pathArr.length; i++) {
				returnList.add(new String[]{pathArr[i], i + ""});
			}
			
		}
					
		return returnList;
	}
	
	
	private static LinkedList<String[]> DFSSearch(ArrayList<String> arrList, int[][] relationGraph,
			Map<String, ArrayList<String>> edgeSequence, Map<String, Integer> matrixHead, 
			Map<Integer, String> anti_matrixHead) {
		
		LinkedList<String[]> returnList = new LinkedList<String[]>();
		Map<String, Path> exploreSet = new HashMap<String, Path>();
		Stack<Path> dfsStack = new Stack<Path>();
		String start = arrList.get(1);
		String end = arrList.get(2);
		String path = "";
		
		if (matrixHead.containsKey(start)) {
			dfsStack.add(new Path(start, 0, 0));
			exploreSet.put(start, new Path(start, 0, 0));
			while (!dfsStack.isEmpty()) {
				Path top = dfsStack.pop();
				exploreSet.put(top.getLastPoint(), top);
				if (exploreSet.containsKey(end)) {
					path = exploreSet.get(end).get();
					break;
				}
				ArrayList<String> successor = edgeSequence.get(top.getLastPoint());
				if (successor != null) {
					for (int i = successor.size() - 1; i >= 0; i--) {
						if (!exploreSet.containsKey(successor.get(i))) {
							Path temp = new Path(top.get(), top.pathCost + 1, 0);
							temp.add(successor.get(i));
							dfsStack.add(temp);
							exploreSet.put(temp.getLastPoint(), temp);
						}  
					}
				}		
			}
			
			String[] pathArr = path.split(" ");
			for (int i = 0; i < pathArr.length; i++) {
				returnList.add(new String[]{pathArr[i], i + ""});
			}
			
		}
					
		return returnList;
	}

	private static LinkedList<String[]> UCSSearch(ArrayList<String> arrList, int[][] relationGraph,
			Map<String, ArrayList<String>> edgeSequence, Map<String, Integer> matrixHead, 
			Map<Integer, String> anti_matrixHead) {
		
		LinkedList<String[]> returnList = new LinkedList<String[]>();
		Comparator<Path> comparator = new PathComparator();
		PriorityQueue<Path> ucsPriorityQueue = new PriorityQueue<Path>(comparator);
		Map<String, Path> frontierMap = new HashMap<String, Path>();
		Set<String> exploreSet = new HashSet<String>();
		String start = arrList.get(1);
		String end = arrList.get(2);
		int tag = 1;
		
		if (matrixHead.containsKey(start)) {
			ucsPriorityQueue.add(new Path(start, 0, 0));
			frontierMap.put(start, new Path(start, 0, 0));
			int row = matrixHead.get(start);
			while (!ucsPriorityQueue.isEmpty()) {
				Path top = ucsPriorityQueue.poll();
				exploreSet.add(top.getLastPoint());
				if (exploreSet.contains(end)) {
					ucsPriorityQueue.clear();
					ucsPriorityQueue.add(top);
					break;
				}
				ArrayList<String> successor = edgeSequence.get(top.getLastPoint());
				row = matrixHead.get(top.getLastPoint());
				if (successor != null) {
					for (int i = 0; i < successor.size(); i++) {
						if (!exploreSet.contains(successor.get(i))) {
							Path temp = new Path(top.get(), top.pathCost, tag);
							int cost = relationGraph[row][matrixHead.get(successor.get(i))];
							temp.add(successor.get(i));
							temp.pathCost += cost;
							if (frontierMap.containsKey(temp.getLastPoint()) && 
									temp.pathCost < frontierMap.get(temp.getLastPoint()).pathCost) {
								ucsPriorityQueue.remove(frontierMap.get(temp.getLastPoint()));
							}
							ucsPriorityQueue.add(temp);
							frontierMap.put(temp.getLastPoint(), temp);
							tag++;
						}
					}
				}
			}
			Path path = ucsPriorityQueue.peek();
			String pathStr = path.get();
			String[] strPath = pathStr.split(" ");
			int pathCount = 0;
			returnList.add(new String[]{start, "0"});

			for (int i = 0; i < strPath.length - 1; i++) {
				String startPoint = strPath[i];
				String endPoint = strPath[i + 1];
				row = matrixHead.get(startPoint);
				int col = matrixHead.get(endPoint);
				pathCount += relationGraph[row][col];
				returnList.add(new String[]{endPoint, pathCount+""});
			}			
		}				
		return returnList;
	}


	private static LinkedList<String[]> ASearch(ArrayList<String> arrList, int[][] relationGraph,
			Map<String, ArrayList<String>> edgeSequence, Map<String, Integer> matrixHead, 
			Map<Integer, String> anti_matrixHead) {
		
		LinkedList<String[]> returnList = new LinkedList<String[]>();
		Comparator<Path> comparator = new PathComparator();
		PriorityQueue<Path> A_PriorityQueue = new PriorityQueue<Path>(comparator);
		Map<String, Path> frontierMap = new HashMap<String, Path>();
		Set<String> exploreSet = new HashSet<String>();
		String start = arrList.get(1);
		String end = arrList.get(2);
		int tag = 1;
		
		Map<String, Integer> A_costGraph = new HashMap<String, Integer>();
		A_costGraph = buildA_Graph(arrList);
		
		if (matrixHead.containsKey(start)) {
			Path startNode = new Path(start, 0, 0);
			A_PriorityQueue.add(startNode);
			frontierMap.put(start, new Path(start, 0, 0));
			int row = matrixHead.get(start);
			while (!A_PriorityQueue.isEmpty()) {				
				Path top = A_PriorityQueue.poll();
				exploreSet.add(top.getLastPoint());
				if (exploreSet.contains(end)) {
					A_PriorityQueue.clear();
					A_PriorityQueue.add(top);
					break;
				}
				ArrayList<String> successor = edgeSequence.get(top.getLastPoint());
				row = matrixHead.get(top.getLastPoint());
				if (successor != null) {
					for (int i = 0; i < successor.size(); i++) {
						Path temp = new Path(top.get(), top.pathCost - top.estimatedCost, tag);
						int cost = relationGraph[row][matrixHead.get(successor.get(i))];
						temp.add(successor.get(i));
						temp.estimatedCost = A_costGraph.get(successor.get(i));
						temp.pathCost = temp.pathCost + cost + temp.estimatedCost;
						if (!exploreSet.contains(successor.get(i))) {				
							if (frontierMap.containsKey(temp.getLastPoint()) && 
									temp.pathCost < frontierMap.get(temp.getLastPoint()).pathCost) {
								A_PriorityQueue.remove(frontierMap.get(temp.getLastPoint()));
							}
							A_PriorityQueue.add(temp);
							frontierMap.put(temp.getLastPoint(), temp);
							tag++;
						}
						else if (temp.pathCost < frontierMap.get(temp.getLastPoint()).pathCost){
							A_PriorityQueue.remove(frontierMap.get(temp.getLastPoint()));
							A_PriorityQueue.add(temp);
							frontierMap.put(temp.getLastPoint(), temp);
							tag++;
						}
					}
				}
			}
			Path path = A_PriorityQueue.peek();
			String pathStr = path.get();
			String[] strPath = pathStr.split(" ");
			int pathCount = 0;
			returnList.add(new String[]{start, "0"});
			for (int i = 0; i < strPath.length - 1; i++) {
				String startPoint = strPath[i];
				String endPoint = strPath[i + 1];
				row = matrixHead.get(startPoint);
				int col = matrixHead.get(endPoint);
				pathCount += relationGraph[row][col];
				returnList.add(new String[]{endPoint, pathCount+""});
			}	
			
		}					
		return returnList;
	}

	private static Map<String, Integer> buildA_Graph(ArrayList<String> arrList) {
		Map<String, Integer> A_costGraph = new HashMap<String, Integer>();
		int num = Integer.parseInt(arrList.get(3));
		int totalNum = Integer.parseInt(arrList.get(num + 4));
		for (int i = 0; i < totalNum; i++) {
			String[] strArr = arrList.get(num + 5 + i).split(" ");
			A_costGraph.put(strArr[0], Integer.parseInt(strArr[1]));
		}
		return A_costGraph;
	}
	
}