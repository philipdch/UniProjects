import java.util.*;
public class WordFreq{
	private String word;
	private int frequency;
	
	public WordFreq(String word){
		this.word = word.toLowerCase();
		frequency++;
	}
	
	public String key(){
		return word;
	}
	
	public void increment(){
		this.frequency++;
	}
	
	public void increment(int value){
		this.frequency += value;
	}
	
	public int getFrequency(){
		return frequency;
	}
	
	public String toString(){
		return word +" ,appears " + frequency +" times ";
	}
}