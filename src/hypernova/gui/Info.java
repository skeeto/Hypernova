package hypernova.gui;

import org.apache.log4j.Logger;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

import hypernova.Universe;
import hypernova.Ship;
import hypernova.Hypernova;
import hypernova.sounds.VolumeEffect;


public class Info {
    public static final int INFO_WIDTH = 120;
    public static final int INFO_X = 10;
    public static final int INFO_Y = 10;
    public static final int INFO_PAD = 4;
    public static final Color INFO_COLOR = new Color(0x1f, 0x1f, 0x1f, 0xa0);
    public static final Color INFO_BORDER = new Color(0x4f, 0x4f, 0x4f);
    public static final Color INFO_TEXT = Color.WHITE;
    public static final Color HP_BACK = new Color(0x00, 0x4f, 0x00);
    public static final Color HP_FRONT = new Color(0x00, 0xbf, 0x00);
    public static final Color BATT_BACK = new Color(0x00, 0x4f, 0x00);
    public static final Color HIGH_FRONT = new Color(0x00, 0x4f, 0xbf);
    public static final Color LOW_FRONT = new Color(0xbf, 0x4f, 0x00);
    public static final int HP_HEIGHT = 3;
    public static final int BATT_HEIGHT = 3;
    
    public static boolean showInfo        = false;
    public static boolean visibleHP       = true;
    public static boolean visibleCooldown = true;
    public static boolean visiblePosition = true;
    public static boolean visibleVelocity = true;
    public static boolean visibleScore    = true;
    public static boolean visibleTimer    = false;
    public static boolean visibleCounter  = false;

    public static int timeLeft = 0;
    public static int shipsLeft = 0;

    private static int stringH = 0;
    private static long framesElapsed = 0;
    private static long lastFrames = 0;
    private static long lastTime = System.currentTimeMillis();

    private static final DecimalFormat COORD_FMT = new DecimalFormat("0");

    private static int totalHeight()
    {
        int totalH = 2*INFO_PAD;
        if (visibleCooldown) totalH += HP_HEIGHT + INFO_PAD;
        if (visibleHP) totalH += HP_HEIGHT + INFO_PAD;
        if (visiblePosition) totalH += stringH + INFO_PAD;
        if (visibleVelocity) totalH += stringH + INFO_PAD;
        if (visibleScore) totalH += stringH + INFO_PAD;
        if (visibleTimer) totalH += stringH + INFO_PAD;
        if (visibleCounter) totalH += stringH + INFO_PAD;
        if (Hypernova.debug) totalH += 3*stringH + INFO_PAD;
        return totalH;
    }

    public static void paintInfo(Graphics2D g2d, int maxHeight,
                                 double focusX, double focusY) {
        Graphics g = g2d.create(INFO_X, INFO_Y, INFO_WIDTH, maxHeight);
        Ship player = Universe.get().getPlayer();
        if (player == null) return;
    
        FontMetrics fm = g.getFontMetrics();
        stringH = fm.getAscent(); 
        int curH    = INFO_PAD;
        String str  = ""; 

        g.setColor(INFO_COLOR);
        g.fillRect(0, 0, INFO_WIDTH, totalHeight());
        g.setColor(INFO_BORDER);
        g.drawRect(0, 0, INFO_WIDTH, totalHeight());
         
        /* Health bar */
        if( visibleHP ) {
          g.setColor(HP_BACK);
          int hpWMax = INFO_WIDTH - INFO_PAD * 2;
          g.fillRect(INFO_PAD, curH, hpWMax, HP_HEIGHT);
          int hpW = (int) (player.getHP() * 1d / player.getMaxHP() * hpWMax);
          g.setColor(HP_FRONT);
          g.fillRect(INFO_PAD, curH, hpW, HP_HEIGHT);
          g.setColor(INFO_TEXT);
          curH += HP_HEIGHT+ INFO_PAD;
        }

        /* Cooldown Bar*/
        if( visibleCooldown ) {
          g.setColor(BATT_BACK);
          int battWMax = INFO_WIDTH - INFO_PAD * 2;
          g.fillRect(INFO_PAD, curH, battWMax, BATT_HEIGHT);
          int battW = (int) (player.getBatt() * 1d / player.getMaxBatt() * battWMax);
          if( player.getBatt() < player.getMaxBatt()/4) g.setColor(LOW_FRONT);
          else g.setColor(HIGH_FRONT);
          g.fillRect(INFO_PAD, curH, battW, BATT_HEIGHT);
          g.setColor(INFO_TEXT);
          curH += HP_HEIGHT+ INFO_PAD;
        }

        /* Position */
        if( visiblePosition ) {
          curH += stringH + INFO_PAD;
          String coords = "(" + COORD_FMT.format(focusX) + ", "
                          + COORD_FMT.format(focusY) + ")";
          int coordsW = fm.stringWidth(coords);
          g.drawString(coords, INFO_WIDTH / 2 - coordsW / 2, curH);
        }
 
        /* Velocity */
        if( visibleVelocity) {
          curH += stringH + INFO_PAD;
          int oneNorm = (int) (100*Math.abs(player.getX(1)) 
                        + Math.abs(100*player.getY(1)));
          String coords = oneNorm + " km/h";
          VolumeEffect.amount = oneNorm;
          int coordsW = fm.stringWidth(coords);
          g.drawString(coords, INFO_WIDTH / 2 - coordsW / 2, curH);
        } 


        /* Score */
        if( visibleScore ) {
          curH += stringH + INFO_PAD;
          str = "Gold: " + Universe.get().getGold();
          g.drawString(str, INFO_PAD, curH);
        }
   
        /* Timer */
        if( visibleTimer ) {
          curH += stringH + INFO_PAD;
          str = "Time Left: " + timeLeft;
          g.drawString(str, INFO_PAD, curH);
        }

        /* Count Down */
        if( visibleCounter ) {
          curH += stringH + INFO_PAD;
          str = "Ships: " + shipsLeft;
          g.drawString(str, INFO_PAD, curH);
        }

        /* FPS */
        if(Hypernova.debug) {
           curH += 3*stringH + INFO_PAD;
           long tmpTime = System.currentTimeMillis();
           framesElapsed ++;
           if (tmpTime >= lastTime + 1000) {
               lastTime = tmpTime;
               lastFrames = framesElapsed;
               framesElapsed = 0;
           }
           str = "FPS: " + lastFrames;
           g.drawString(str, INFO_PAD, curH);
        }
    }
}
