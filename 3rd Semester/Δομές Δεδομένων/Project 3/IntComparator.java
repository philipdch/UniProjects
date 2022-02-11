import java.util.Comparator;

public class IntComparator implements Comparator<WordFreq> {

	public int compare(WordFreq a, WordFreq b) {
		if(a.getFrequency() == b.getFrequency()) 
			return a.key().compareToIgnoreCase(b.key());
		return (a.getFrequency() > b.getFrequency())? 1 : -1;
	}	
}
