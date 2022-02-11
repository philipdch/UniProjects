import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	public int compare(String a, String b) {
		if(a == b)
			return 0;
		return (a.compareTo(b) < 0)? -1 : 1;
	}	
}