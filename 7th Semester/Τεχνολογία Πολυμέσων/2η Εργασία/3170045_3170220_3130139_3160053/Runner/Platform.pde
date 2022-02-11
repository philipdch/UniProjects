/*This class defines the platforms and the item that are spawn on them.
Overrides move and collision from gameObject.
*/
class Platform extends gameObject{
  //Determines the vertical position of the platform
  int level;
  ArrayList<Item> items = new ArrayList<Item>();
  //how many images should be combined for given width
  int imageCount;
  
  Platform(float w,float h,int level){
    this.posY=(level+1)*150;
    Width=w;
    Height=h;
    base=posY;
    this.level =level;
    imageCount = (int)w/plat.width; 
    int maxItems = imageCount/3;
    
    for(int i = 0; i < imageCount; i++){
      //Randomly display items , coins are appear more frequently
      if(int(random(100)) >= 75 && items.size() <= maxItems){
        int rand=(int)random(25);
        if(rand>=24) rand=2;
        else if(rand>=23 && rand<24)rand=3;
        else rand=1;
        
        items.add(new Item(this.posX  + (i*30), this.posY-coin.height,rand));
        
      }
    }
  }
  //---------------------------------------------------------------
  float getY(){
   return posY; 
  }
  
  //Overrided move method ,items move along platforms
 void move(float speed){
    for(Item item: items){item.move(speed);}
    super.move(speed);
 }
 //---------------------------------------------------------------
  boolean collision(gameObject obj){
    Switch=false;
    boolean s=super.collision(obj);
      if(s){
        for(int i = 0; i <items.size(); i++){
          if(items.get(i).collision(obj)){
            if(items.get(i).type==1){
              coins++;
              coinsound.play();
            }else if(items.get(i).type==2){
              addLife();
              powerUp1.play();
            }else powerUp2.play();
            
            items.remove(i);
            break;
         
        }
      }
    }
    return s; 
  }
//---------------------------------------------------------------
 boolean onTop(gameObject obj){
  
   if(obj.posY<posY&&obj.posY+obj.Height<posY+Height){
      
      return true;
    }
    
    return false;
  }
  //--------------------------------------------------------------- 
   
  void draw(){
    
     for(int i = 0; i<imageCount; i++){
      image(plat, (i*30) + posX, posY);
    }
   for(Item item: items){item.show();}
    
  }

}
