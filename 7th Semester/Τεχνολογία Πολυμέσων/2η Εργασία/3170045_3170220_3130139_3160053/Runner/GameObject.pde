//Abstract class that initializes generally used methods
abstract class gameObject {
  
//Initialize variables  
float up=0;          //
float down=0;        //    
float posX=width;    //Position x of the specific object
float posY;          //Position y of the specific object
float  Width, Height;// width and height of images
float base;          //
boolean Switch=false;//Determines if there is a collision or not 
PImage  image;       //Image that will be displayed
boolean invisible=false;
  //---------------------------------------------------------------
  boolean collision(gameObject obj){
    
  if(Switch) return false;
  // if position of this object is between the position of the given object
  //there is a collision and the method returns true
  if(this.posX+this.Width+speed>=obj.posX  && this.posX+speed<obj.posX+obj.Width){
      if((obj.posY<=posY&&obj.posY+obj.Height>=posY)||(obj.posY>=posY && obj.posY<=posY+Height)||(obj.posY<=posY&&obj.posY+obj.Height>=posY+Height)){
         Switch=true;
         return true;
      }
    }
  return false;
  } 
   //---------------------------------------------------------------
  void move(float speed){
    posX -= speed;
  } 
   //---------------------------------------------------------------
  boolean checkBounds(){
    if(posX+Width<0){
      return true;
  }
    return false;
  }
   //---------------------------------------------------------------
  void draw(){
   //rect(posX ,posY,Width,Height);
   image(image, posX , posY);
     
  }
   //---------------------------------------------------------------
  void setBase(float base){
      
    this.base=base;
  }
  //---------------------------------------------------------------
  float getBase(){   
    return base; 
   }
    //---------------------------------------------------------------
   float getBotom(){
     return base+Height;
   }
}
