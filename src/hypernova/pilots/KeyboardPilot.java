package hypernova.pilots;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

import hypernova.Universe;
import hypernova.Ship;
import hypernova.SaveGame;
import hypernova.audio.SongPlaylist;

public class KeyboardPilot extends Pilot implements KeyListener {
    private static Logger log = Logger.getLogger("KeyboardPilot");
    private static KeyboardPilot INSTANCE = new KeyboardPilot();

    private KeyboardPilot() {
        super(null);
    }

    public static KeyboardPilot get() {
        return INSTANCE;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Ship ship = getShip();
        if (ship == null) {
            log.trace("no ship");
            return;
        }

        int kc = e.getKeyCode();
        
        switch (kc) {
        case KeyEvent.VK_L:
            SaveGame.checkpoint();
            break;
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_A:
            ship.turnLeft(true);
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
            ship.turnRight(true);
            break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_W:
            ship.setEngines(1.0);
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_S:
            ship.setEngines(-1.0);
            break;
        case KeyEvent.VK_Z:
            ship.setFire(0, true);
            break;
        case KeyEvent.VK_X:
            ship.setFire(3, true);
            break;
        case KeyEvent.VK_C:
            ship.setFire(4, true);
            break;
        case KeyEvent.VK_COMMA:
            SongPlaylist.backwardSong();
            break;
        case KeyEvent.VK_PERIOD:
            SongPlaylist.forwardSong();
            break;
        default:
            log.trace("Unkown key " + e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Ship ship = getShip();
        if (ship == null) {
            log.trace("no ship");
            return;
        }

        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_A:
            ship.turnLeft(false);
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
            ship.turnRight(false);
            break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_W:
            ship.setEngines(false);
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_S:
            ship.setEngines(false);
            break;
        case KeyEvent.VK_Z:
            ship.setFire(0, false);
            break;
        case KeyEvent.VK_X:
            ship.setFire(3, false);
            break;
        case KeyEvent.VK_C:
            ship.setFire(4, false);
            break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void drive(double dt) {
        /* Driving is handled entirely by keyboard events. */
    }
}
