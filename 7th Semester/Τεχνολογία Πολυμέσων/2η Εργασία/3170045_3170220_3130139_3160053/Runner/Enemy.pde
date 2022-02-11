/*This class is a subclass of the abstract class gameObject.
It defines every enemy ,with their movement ,position and has a method to detect collisions.
Overrides move and collision from gameObject.
ENEMIES:
obstacle1/obstacle2 -> road sign 
obstacle3 -> mini tornado
car1/car2 -> 2 cars running to opposite directions 
uap -> ufo wants to kidnap palyer
virus -> jumping up and down infects Player
*/
class Enemy extends gameObject{
  //variable tha determines the type of the enemy(obstacle,car,uat,virus)
  int type;
  //variable that randomly assing a different speed to uat , virus
  float extraSpeed;
  
  //Constructor - defines width,height,type of every enemy
  Enemy(float ground,int type){
    
   extraSpeed= random(1.1,1.2);
    switch(type){
    case 0: this.posY=ground-obstacle1.height;
            Width=obstacle1.width;
            Height=obstacle1.height;
            image=obstacle1;
            break;
    case 1: this.posY=ground-obstacle2.height;
            Width=obstacle2.width;
            Height=obstacle2.height;
            image=obstacle2;
            break;
    case 2: this.posY=ground-obstacle3.height;
            Width=obstacle3.width;
            Height=obstacle3.height;
            image=obstacle3;
            break;
    case 3: if(random(1)>0.5)image=car;
            else image=car2;
            this.posY=ground-car.height+40;
            Width=image.width;
            Height=image.height;
            break;         
    case 4: this.posY=150-uap.height-25;
            Width=uap.width;
            Height=uap.height;
            image=uap;
            break;       
    case 5: this.posY=300-uap.height-25;
            Width=uap.width;
            Height=uap.height;
            image=uap;
            break;     
    case 6: this.posY=ground-virus.height-20;
            Width=virus.width;
            Height=virus.height;
            image=virus;
            down=0.1;
            break;
    }
    this.type=type;
  }     
 void move(float speed){
    //If the enemy  is a virus move it up and down with the general speed(=10)
    if(type==6){
      posY +=  + up;
      up+= + down;
      if (posY>ground-50){
        up*= -1.5;
        posY=ground-50;
      }
      super.move(speed);
    }
    //if the enemy is a car modify speed
    else if(type==3)super.move( speed-3);
    //specify speed of uat and virus 
    else  if(type==4||type==5||type==6)
      super.move( speed*extraSpeed);
    //specify speed of the rest obstacles
    else super.move( speed); 
  }
  
  void draw(){
    //Specify tint of enemies
     tint(15, 40);
    //saves the current coordinate system to the stack 
     pushMatrix();
    //Adds shading at enemies
     if(type<4){
          translate(posX+Width,posY+Height);
          rotate(PI);
          scale(-1,1);
          image(image,-Width,-Height+10);
      }
      if(type>=4){
          translate(posX,ground);
          rotate(PI);
          scale(-1,1);
          image(image,0,-Height-10);
       }
        noTint();
      //restores the prior coordinate system 
        popMatrix();
      //Calls draw from superclass (displays image) 
        super.draw();
  }
  
  boolean collision(gameObject obj){
    //calls collision from superclass(determines if the enemy hit the obstacle)
    boolean s=super.collision(obj);
    if(s){
      //if the ememy is hit then display circumstantial message 
       switch(type){
         case 1: image=brokensign;
                 break;
         case 3: gameOverSound=policehit;
                 endMessage[0] = "Caught for speeding!";
                 endMessage[1] = "Pay more attention to the signs!";
                 break;
         case 5: gameOverSound= ufo;
                 endMessage[0] = "Taken to Outer Space!";
                 endMessage[1] = "Eventually you stopped thinking.";
                 break;
         case 6: endMessage[0] = "You died!";
                 endMessage[1] = "Cause of death yet to be determined!";
                 break;
         default:endMessage[0] = "Broke a leg!";
                 endMessage[1] = "Must be an actor";
                 gameOverSound= signbreak;
         }  
      }
    return s;
  }
}
