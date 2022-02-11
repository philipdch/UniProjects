public class TreeTest{
	public static void main(String args[]){
		ST tree = new ST();
		tree.load("speech.txt");
		tree.insert(new WordFreq("Subscribe"));
		tree.insert(new WordFreq("To"));
		tree.insert(new WordFreq("PDP"));
		tree.insert(new WordFreq("crash"));
		tree.insert(new WordFreq("ally"));
		tree.insert(new WordFreq("dog"));
		tree.update("ally");
		tree.update("dog");
		tree.update("dog");
		tree.printAlphabetically(System.out);
		System.out.println("**********");
		System.out.println(tree.getTotalWords());
		System.out.println(tree.getMeanFrequency());
		System.out.println("**********");
		tree.printByFrequency(System.out);
		tree.remove("crash");
		System.out.println("**********");
		tree.printAlphabetically(System.out);
		System.out.println("**********");
		System.out.println(tree.search("ally"));
		System.out.println(tree.getMaxFrequency());
	}
}
		
		
		