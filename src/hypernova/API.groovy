package hypernova
// some nicer api for groovy scripts to use

public class API {
    private static Universe universe
    private static Random rng = new Random()


    public static void setUniverse(Universe u) {
        universe = u
    }

    public static void withNewPlayer(String kind, Closure setup) {
        def player = new Ship(kind)
        setup(player)
        universe.setPlayer(player)
    }

    public static void withNewShip(String kind, Closure setup) {
        def ship = new Ship(kind)
        setup(ship)
        universe.add(ship)
    }

    public static void withNewMass(String kind, Closure setup) {
        def mass = new Mass(kind)
        setup(mass)
        universe.add(mass)
    }

    public static Iterable<Double> randomPosition(double VAR, double centerX, double centerY) {
        def x = rng.nextGaussian() * VAR + centerX
        def y = rng.nextGaussian() * VAR + centerY
        def theta = rng.nextDouble() * Math.PI * 2
        return [x, y, theta]
    }

    public static void newSpatialRealization(double triggerX, double triggerY, double triggerRad,
                                                    Closure event) {
        boolean haveTriggered = false
        def realization = [
            'shouldTrigger': { playerX, playerY ->
                if (haveTriggered) {
                    return false
                }

                def dx = triggerX - playerX
                def dy = triggerY - playerY

                if ((dx * dx + dy * dy) < (triggerRad * triggerRad)) {
                    haveTriggered = true
                    return true
                }
                return false
            },
            'trigger': event] as Realization
        universe.addRealization(realization)
    }
}
