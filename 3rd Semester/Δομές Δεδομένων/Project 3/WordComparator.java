import java.util.Comparator;

public class WordComparator implements Comparator<WordFreq> {

	public int compare(WordFreq a, WordFreq b) {
		if(a == b)
			return 0;
		return (a.key().compareToIgnoreCase(b.key()) < 0)? -1 : 1;
	}	
}