package hypernova.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import hypernova.Universe;
import hypernova.Hypernova;

public abstract class MenuScreen
{
  public static final int MENU_PAD = 10;
  public static final int ITEM_PAD = 15;
  public MenuScreen parent = null;

  private class ItemTuple {
    public Alignment align;
    public String    img;
    public String    name;
    public String    value;
    public int       func;
    public int       imgW;
    public int       imgH;
    public int       cost;  // Check if player can afford
    public boolean   viewOnly; // Just text, not an actual option
    public boolean   imageOnly; // Just an image, no text
    public boolean   disabled; // Item is not available at this time
    public boolean   ext; // Will bring up another screen
    ItemTuple( Alignment align
             , String    img
             , int       imgW
             , int       imgH
             , String    name
             , String    value
             , int       func
             ) {
      this.align = align;
      this.img   = img;
      this.imgW  = imgW;
      this.imgH  = imgH;
      this.name  = name;
      this.value = value;
      this.func  = func;
    }

    ItemTuple( Alignment align
             , String name
             ) {
      this.align = align;
      this.name  = name;
      this.viewOnly = true;
    }

    ItemTuple( Alignment align
             , String img
             , int imgW
             , int imgH
             ) {
      this.align = align;
      this.name  = img;
      this.img   = img;
      this.imgW  = imgW;
      this.imgH  = imgH;
      this.viewOnly = true;
      this.imageOnly = true;
    }

    public boolean isNamed(String x) {
        if(this.name == null) return false;
        return this.name.equals(x); 
    }
   
  }

  private ArrayList<ItemTuple> items = new ArrayList<ItemTuple>();
  protected String selected = null;

  public enum Alignment { LEFT
                        , RIGHT
                        , CENTER
                        };
 
  public void addText( Alignment align
                     , String    name
                     ) {
    items.add(new ItemTuple(align, name));
  }

  public void addImg( Alignment align
                    , String img
                    , int imgW
                    , int imgH
                    ) {
    items.add(new ItemTuple(align, img, imgW, imgH));
  }

  public void addItem( Alignment align
                     , String img
                     , int imgW
                     , int imgH
                     , String name
                     , String value
                     , int func
                     ) {
    items.add(new ItemTuple(align, img, imgW, imgH, name, value, func));

  }

  public void addItem( Alignment align
                     , String    name
                     , String    value
                     , int       func
                     ) {
    items.add(new ItemTuple(align, null, 0, 0, name, value, func));
    if(selected == null) selected = name;
  }

  private boolean isViewOnly(String n) {
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(n) && (x.viewOnly || x.disabled)) return true;
    }
    return false;
  }

  public void goUp() {
    String newSelect = null;
    for (int i = 1; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) newSelect = items.get(i - 1).name;
    }
    if (newSelect == null) newSelect = items.get(items.size() - 1).name;
    selected = newSelect;
    if ( isViewOnly(newSelect) ) goUp();
    
  }

  public void goDown() {
    String newSelect = null;
    for (int i = 0; i < items.size() - 1; i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) newSelect = items.get(i + 1).name;
    }
    if (newSelect == null) newSelect = items.get(0).name;
    selected = newSelect;
    if ( isViewOnly(newSelect) ) goDown();
  }

  public void select() {
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(selected) ) functions(x.func, x.value);
    }
  }
  
  public void newScreen(MenuScreen n) {
     n.parent = this;
     Menu.newMenu(n, true);
  }
 
  public void back() {
    if (parent != null) Menu.newMenu(parent, false);
  }

  public void updateItem(String name, String value, boolean disabled) {
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if( x.isNamed(name) ) {
        x.value = value;
        x.disabled = disabled;
        if(disabled && x.isNamed(selected)) goDown();
        break;
      }
    }
  }
  
  private void drawImage(Graphics2D g2d, ItemTuple x, int h) {
    try {
      BufferedImage tmpImg = ImageIO.read(new File(x.img));
      int uWidth  = Hypernova.getViewer().getWidth();
      int posHrz = MENU_PAD;
      if( x.align == Alignment.CENTER ) posHrz = uWidth / 2 - x.imgW / 2;
      else if (x.align == Alignment.RIGHT) posHrz = uWidth - MENU_PAD - x.imgW;

      g2d.drawImage(tmpImg, posHrz, h, x.imgW, x.imgH, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void render(Graphics2D g2d) {
    Font oldfont = g2d.getFont();
    g2d.setFont(oldfont.deriveFont(40f));
    FontMetrics fm = g2d.getFontMetrics();
    
    int totalHeight = 0;
    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if(x.imageOnly) totalHeight += x.imgH + ITEM_PAD;
      else if(x.img == null) totalHeight += fm.getAscent() + ITEM_PAD;
    }
    int uHeight  = Hypernova.getViewer().getHeight();
    int startH = uHeight/2 - totalHeight/2;
    int curHeight = startH;    

    for (int i = 0; i < items.size(); i ++) {
      ItemTuple x = items.get(i);
      if(x.imageOnly) {
        drawImage(g2d, x, curHeight);
        curHeight += x.imgH + ITEM_PAD;
        continue;
      }
      int width   = fm.stringWidth(x.name);
      int uWidth  = Hypernova.getViewer().getWidth();
      if(x.isNamed(selected)) g2d.setColor(new Color(0xff, 0xff, 0x77));
      else if(x.disabled) g2d.setColor(new Color(0x50, 0x50, 0x50));
      else g2d.setColor(new Color(0xff, 0xff, 0xff));
 
      int posHrz = MENU_PAD;
      if( x.align == Alignment.CENTER ) posHrz = uWidth / 2 - width / 2;
      else if (x.align == Alignment.RIGHT) posHrz = uWidth - MENU_PAD- width;
      String n = x.name;
      g2d.drawString(n, posHrz, curHeight);
      curHeight += fm.getAscent() + ITEM_PAD;
    }
  }
 
  public abstract void loadMenu();
  public abstract void functions(int func, String value);
 
}
