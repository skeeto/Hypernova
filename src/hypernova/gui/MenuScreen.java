package hypernova.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;

import hypernova.Universe;
import hypernova.Hypernova;

public abstract class MenuScreen
{
  public static final int MENU_PAD = 100;
  public static final int ITEM_PAD = 10;
  public MenuScreen parent = null;

  private class ItemTuple {
    public Alignment align;
    public String    img;
    public String    name;
    public String    value;
    public int       func;
    public boolean   ext; 
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

  private ArrayList<ItemTuple> items = new ArrayList<ItemTuple>();
  private String selected = null;

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
    if(selected == null) selected = name;
  }

  public void goUp() {
    String newSelect = null;
    for (int i = 1; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) newSelect = items.get(i - 1).name;
    }
    if (newSelect == null) newSelect = items.get(items.size() - 1).name;
    selected = newSelect;
  }

  public void goDown() {
    String newSelect = null;
    for (int i = 0; i < items.size() - 1; i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) newSelect = items.get(i + 1).name;
    }
    if (newSelect == null) newSelect = items.get(0).name;
    selected = newSelect;
  }

  public void select() {
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) functions(x.func, x.value);
    }
  }
  
  public void back() {
    // TODO: Should pop up a level if not null parent
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
  
  public void render(Graphics2D g2d) {
    Font oldfont = g2d.getFont();
    g2d.setFont(oldfont.deriveFont(45f));
    FontMetrics fm = g2d.getFontMetrics();
    int height = fm.getAscent() + ITEM_PAD; 
    
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      int width  = fm.stringWidth(x.name);
      if(x.isNamed(selected)) g2d.setColor(new Color(0xff, 0xff, 0x77));
      else g2d.setColor(new Color(0xff, 0xff, 0xff));
      g2d.drawString(x.name, 10, MENU_PAD + i*height);
    }
  }
 
  public abstract void loadMenu();
  public abstract void functions(int func, String value);
 
}
