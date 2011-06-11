package hypernova
// some nicer api for groovy scripts to use

public class API {
    private static Universe universe
    private static Random rng = new Random()


    public static void setUniverse(Universe u) {
        universe = u
    }

    public static Closure parts(String kind) {
        return { Ship.get(kind) }
    }

    public static Closure ship(String kind) {
        return { new Ship(kind) }
    }

    public static Closure mass(String kind) {
        return { new Mass(kind) }
    }

    public static void withNewPlayer(Closure kind, Closure setup) {
        def player = kind()
        setup(player)
        universe.setPlayer(player)
    }

    public static void withNew(Closure kind, Closure setup) {
        def thing = kind()
        setup(thing)
        universe.add(thing)
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
