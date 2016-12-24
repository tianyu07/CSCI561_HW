import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class homework{
	
	public static void main(String[] args) throws Exception {
		
		try(
				FileReader inputFile = new FileReader("input.txt");
				FileWriter outputFile = new FileWriter("output.txt");
				BufferedReader br = new BufferedReader(inputFile);
				BufferedWriter bw = new BufferedWriter(outputFile);
				) {
			
			String line;
			ArrayList<String> arrList = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				line = line.replace(" ", "");
				arrList.add(line);
		    }
			/*
			 * 主程序按照如下流程执行：
			 * 1.分类。将query、facts和需要转换为CNF的clauses分开，由initial class做，
			 * clauses结果写入output.txt。query、facts用set存储
			 * 2.convert CNF。由CNFconverter class做，结果用kb set存储
			 * 3.建立KB map，predicate unify standardize等
			 * 4.Resolution，结果写入output.txt，覆盖之前结果
			 */
			Initialization initial = new Initialization(arrList);
			ArrayList<Term> querySet = initial.getQuery();
			Set<Term> factsSet = initial.getFact();
			Set<String> sentenceSet = initial.getSentence();
			
			Set<String> CNFClause = CNFConverter.getCNFClause(sentenceSet);
			
			KnowledgeBase buildingKB = new KnowledgeBase(factsSet, CNFClause);
			Map<Integer, ArrayList<Term>> KB = buildingKB.getKB();
			Map<String, ArrayList<Integer>> predIndexMap = buildingKB.getPredIndex();
					
			FOLResolution resolution = new FOLResolution(querySet, KB, predIndexMap);
			ArrayList<String> returnList = resolution.execute();

			for (int i = 0; i < returnList.size() - 1; i++) {
				bw.write(returnList.get(i) + "\n");
			}	
			bw.write(returnList.get(returnList.size() - 1));
		}
		
		catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	
	}
}
	