import java.io.File;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.StringTokenizer;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }
    public String[] process() throws Exception {
        String[] ret = new String[20];
       
        //TODO
	// Read every line of the file
	/**
		Problem statement says that the titles to be processed are 
		those in the indexes array.
	*/
	Integer[] indexes = getIndexes();
	Arrays.sort(indexes);

	InputStream fis = new FileInputStream(inputFileName);
	InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
	BufferedReader br = new BufferedReader(isr);
	String line;
	List<String> l = Arrays.asList(stopWordsArray);
	HashMap<String, Integer> hm = new HashMap<String, Integer>();
	int index = 0;
	// This variable indicates the real index
	int j = 0;
	while ( (line = br.readLine()) != null && index < indexes.length ) {
		//System.out.println("indexes["+index+"] = " + indexes[index] + ", j = "+ j);
		if (indexes[index] != j) {
			// if "j" is not equal to current index in
			// indexes[index] then skip
			//System.out.println("\tSkipping");
			j++;
			continue;
		}
		int jtemp = j;
		int reps = 0;
		index++; j++;
		// Because the indexes are randomly generated, copies of one
		// index could repeated more than once. If so then the 
		// repeated index indicate to process the line (then the tokens
		// in the line) the number of times that such index is 
		// repeated. The line with its corresponding tokens.
		while (index < indexes.length && indexes[index] == jtemp) {
			//System.out.println("\tskipping");
			index++;
			reps++;
		}
		StringTokenizer stt = new StringTokenizer(line, delimiters);
		// Change to lower case and remove white spaces at beginning
		// and end of String
		String[] tokens = toLowerRemoveTailing(stt);
		String[] newTokens = removeStopWords(tokens, l);
		Set<String> set = hm.keySet();
		for (int i = 0; i < newTokens.length; i++) {
			if (hm.containsKey(newTokens[i])) {
				hm.put(newTokens[i], hm.get(newTokens[i]) + 1 + reps);
			} else {
				hm.put(newTokens[i],1 + reps);
			}
		}
	}
	//System.out.println("Index [" + index + "] j [" + j + "]");
	HashMap<String, Integer> m1 = sortByValue(hm);

	Iterator<String> iter = m1.keySet().iterator();
	j = 0;
	for (; iter.hasNext(); ) {
		String n = iter.next();
		System.out.println("[" + n + "] " + m1.get(n));
		if (j < 20) 
			ret[j++] = n;
	}

        return ret;
    }
/**
           My auxiliary functions 
**/
String[] toLowerRemoveTailing(StringTokenizer stt) {
	String[] result = null;
	int totalTokens = stt.countTokens();
	result = new String[totalTokens];
	for (int i = 0; stt.hasMoreElements(); i++) {
		result[i] = stt.nextToken().toLowerCase().trim();
		//System.out.println("[" + result[i] + "]");
	}
	//System.out.println(totalTokens +  "\n");
	return result;
}

/*
ArrayList<String> removeStopWords(String[] sta, List l) {
	ArrayList<String> result = new ArrayList();
	for (int i = 0; i < sta.length; i++) {
		if (!l.contains(sta[i])) {
			result.add(sta[i]);
		}
	}
	return result;
}
*/
String[] removeStopWords(String[] sta, List l) {
	int nonStopWords = 0;
	int i;
	String result[] = null;
	for (i = 0;  i < sta.length; i++) 
		if (!l.contains(sta[i])) nonStopWords++;
	result = new String[nonStopWords];
	int j = 0;
	for (i = 0;  i < sta.length; i++) 
		if (!l.contains(sta[i])) {
			result[j++] = sta[i];
		}
	return result;
}

// http://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/
// http://stackoverflow.com/questions/3074154/sorting-a-hashmap-based-on-value-then-key
private static HashMap sortByValue(HashMap map) {
	List list = new LinkedList(map.entrySet());
	Collections.sort(list, new Comparator() {
		public int compare(Object o1, Object o2) { 
			int cmp1 = ((Comparable) ((Map.Entry)(o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			if (cmp1 == 0)
				return ((Comparable) ((Map.Entry)(o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());
			return cmp1;
		} 
	});
	HashMap sortedHashMap = new LinkedHashMap();
	for (Iterator it = list.iterator(); it.hasNext();) { 
		Map.Entry entry = (Map.Entry) it.next();
		sortedHashMap.put(entry.getKey(), entry.getValue());
	}
	return sortedHashMap;
}

/**************/


    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
