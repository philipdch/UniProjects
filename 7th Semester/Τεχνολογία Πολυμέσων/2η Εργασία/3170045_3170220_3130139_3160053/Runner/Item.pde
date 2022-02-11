/*This class defines 3 different items that boost the player and it is a subclass from gameObject
Overrides move and collision from gameObject.
ITEMS
coin -> with 50 or more coins player gains a life
slow ->image of a snail slowing down player
herat -> adds another life to the player
*/
class Item extends gameObject{
  //type of the item
  int type;
  //Constructor defines position x,y and the type of the item
  Item(float posX,float posY,int type){
    this.posX=posX;
    this.posY=posY;
    this.type =type;
    switch(type){
        case 1: image=coin;
                break;
        case 2: image=slow;
                break;
        case 3: image=heart1;
                break;
        default:image=coin;
                break;
    }  
    Width=image.width;
    Height=image.height;
 }
  //Just appear image at the specific position
  void show(){    
    image(image, posX, posY);
  }
  
  boolean collision(gameObject obj){
    //calls collision from superclass(determines if the player collected the item)
    boolean s = super.collision( obj);
    // if item is snail slowdown by 2
    if(s && type==2)speed-=2;
    
    // don't let speed drop down from  8 
    if (speed<8)speed=8;
    return s;
  }
  
   
}
