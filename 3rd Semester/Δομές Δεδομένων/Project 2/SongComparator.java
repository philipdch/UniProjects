
import java.util.Comparator;

final class SongComparator implements Comparator<Song> {

	public int compare(Song songA, Song songB) {
		return songA.compareTo(songB);
	}
	
}
