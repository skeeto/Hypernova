package hypernova.pilots;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

import hypernova.Hypernova;

public class KeyboardPilot extends Pilot implements KeyListener {
    private static Logger log = Logger.getLogger("KeyboardPilot");

    public KeyboardPilot() {
        super(Hypernova.universe.getPlayer());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.trace("keyPressed() " + e);
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            ship.turnLeft(true);
            break;
        case KeyEvent.VK_RIGHT:
            ship.turnRight(true);
            break;
        case KeyEvent.VK_UP:
            ship.setEngines(true);
            break;
        case KeyEvent.VK_SPACE:
            ship.setFire(0, true);
            break;
        default:
            log.trace("Unkown key " + e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.trace("keyReleased() " + e);
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            ship.turnLeft(false);
            break;
        case KeyEvent.VK_RIGHT:
            ship.turnRight(false);
            break;
        case KeyEvent.VK_UP:
            ship.setEngines(false);
            break;
        case KeyEvent.VK_SPACE:
            ship.setFire(0, false);
            break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void drive() {
        /* Driving is handled entirely by keyboard events. */
    }
}
