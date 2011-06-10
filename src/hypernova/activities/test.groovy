package hypernova;

public class Test extends Activity {
    public void initialize() {
        print "hello from groovy!\n"
        /* Set up player ship. */
	Ship player = new Ship("tenderfoot");
        player.setPosition(0, 0, Math.PI / -2).setFaction("Humans");
        player.setWeapon("blaster", 0);
        universe.setPlayer(player);

        Ship dummy = new Ship("tenderfoot");
        dummy.setPosition(45, 105, Math.PI / 3).setFaction("Humans");
        dummy.setWeapon("blaster", 0);
        dummy.setSize(6.0);
        universe.add(dummy);

        Mass station = new Mass("small-station");
        station.setPosition(100.0, 100.0, 0.0).setFaction("Aliens");
        station.setA(0.01, 1);
        station.setSize(30.0);
        universe.add(station);
    }
}
