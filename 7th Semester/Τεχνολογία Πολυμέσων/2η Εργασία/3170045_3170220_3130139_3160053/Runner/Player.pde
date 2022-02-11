class Player extends gameObject{
  
int frame = -1;
int runCount = 0;
public int lives=3;
float falling=0;
boolean movedown;
boolean jump=false;
boolean doubleJump=false;

   //---------------------------------------------------------------
  //initialize player position, image,Width, Height
  Player(){
    posY=height-100;
    image=runner1;
    Width=image.width-10;
    Height=image.height;
    posX=30.0;
    base=posY+image.height;
    
  }
   //---------------------------------------------------------------
  //
  void jump(){
    if(!jump){
      
      jump=true;
      down=0.1;
      up=13;
       posY-=up;
       up-=down;
    }
    else if(!doubleJump){
      up+=20;
      doubleJump=true;
      if(up>18){
        up=18;
      }
    }
  } 
  
 //---------------------------------------------------------------
  void move(){
     
     posY-=up;
     up-=down;
      
   if(!movedown){ 
      if(posY+Height>=base){
        
        posY=base-Height;
        jump=false;
        doubleJump=false;
        up=0;
        down=0;
      }
      else{
        down=1;
      }
   }else{
     
     falling--;
     posY+=5;
     if(falling<=0){
       movedown=false;
       base=ground;
     }
   }
  }
   
   //---------------------------------------------------------------
    void fall(float bottom){
        
    posY=bottom;
    up=0;
    down=1;
    up-=3;
    posY-=1;
    jump=true;
    doubleJump=true;
    
    }
    
  boolean lifeLoss(){
    lives--;
    if(lives>0){
     return true; 
    }
    return false;
  }
  
  void moveDown(){
    if(base!=ground){
      falling=4;
      movedown=true;
      
    }
  }
  void shadow(PImage img){
    if(base>=ground&&down==0&&up==0){
               tint(15, 60); 
               pushMatrix();
               translate(posX+Width,posY+Height);
               rotate(PI);
               scale(-1,1);
               image(img,-Width,-Height+3);
               noTint();
               popMatrix();

          }
             tint(15, 50);
           image(img, posX-5,posY);
           noTint();
  }

  void draw(){

    if(up==0){
        
    shadow( running[runCount]);
    image( running[runCount],posX,posY);
    }  
    else{
      image(runnerJump, posX,posY);
         
    }
   runCount++;
    
    if(runCount >= 30){
      runCount = 0;
    }
  }
}
 
  
   

  
 
