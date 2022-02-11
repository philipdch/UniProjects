public class Button{
    int x;
    int y;
    float width;
    float height;
    String label;
    PFont font;
    
    Button(float width, float height, String label){
        this(0, 0, width, height, label);
    }
    
    Button(int x, int y, float width, float height, String label){
        this.x= x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        font = createFont("Arial Bold", 14);
    }
    
    void display(){
        fill(112,128,144);
        rect(x, y, width, height);
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(label, x +width/2, y + height/2);
          
    }
    
    int getX(){
        return x;
    }
    
    int getY(){
        return y;
    }
    
    float getWidth(){
        return width;
    }
    
    float getHeight(){
        return height;
    }
    
    boolean inEvent(){
       if(mouseX > x && mouseX < x + this.width && mouseY> y && mouseY < y + this.height){
            return true;
        }
        return false;
    }
}
