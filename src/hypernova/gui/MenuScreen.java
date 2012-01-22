package hypernova.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import hypernova.Universe;
import hypernova.Hypernova;

public abstract class MenuScreen
{
  private MenuScreen parent = null;

  private class ItemTuple {
    Alignment align;
    String    img;
    String    name;
    String    value;
    int       func;
    boolean   ext; 
    ItemTuple( Alignment align
             , String    img
             , String    name
             , String    value
             , int       func
             , boolean   ext 
             ) {
      this.align = align;
      this.img   = img;
      this.name  = name;
      this.value = value;
      this.func  = func;
      this.ext   = ext;
    }

    public boolean isNamed(String x) {
        return this.name.equals(x); 
    }
   
    public void changeValue(String x) {
        this.value = x;
    }
  }

  ArrayList<ItemTuple> items = new ArrayList<ItemTuple>();

  public enum Alignment { LEFT
                        , RIGHT
                        , CENTER
                        };
 
  public void addItem( Alignment align
                     , String    img
                     , String    name
                     , String    value
                     , int       func
                     , boolean   ext 
                     ) {
    items.add(new ItemTuple(align, img, name, value, func, ext));   
  }

  public void updateItem(String name, String value) {
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(name) ) {
        x.changeValue(value);
        break;
      }
    }
  }
  
  public abstract void loadMenu();
  public abstract void functions(int func, String value);
 
}
