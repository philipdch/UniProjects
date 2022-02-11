public class OnOffButton extends Button{
  boolean isOn;
  
  OnOffButton(int x, int y, float width, float height, String label){
      super(x, y, width, height, label);
      isOn = true;
  }
  
  void display(){
        if(isOn){
            fill(20, 225, 20);
        }else{
            fill(225, 20, 20);
        }
        rect(x, y, width, height);
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        if(isOn){
            text(label + " ON", x +width/2, y + height/2);
        }else{
            text(label + " OFF", x +width/2, y + height/2);
        } 
    }
    
    boolean getState(){
        return isOn;
    }
    
    void setState(boolean state){
        isOn = state;
    }
    
    void clickEvent(){
       if(mouseX > x && mouseX < x + this.width && mouseY> y && mouseY < y + this.height){
            isOn = isOn? false : true;
        }
    }

}
