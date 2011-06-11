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

    public static Iterable<Double> randomPosition(double VAR, double MEAN) {
        double dirx = 1.0
        double diry = 1.0
        if (rng.nextInt(2) == 0) {
            dirx = -1.0
        }
        if (rng.nextInt(2) == 0) {
            diry = -1.0
        }
        def x = rng.nextGaussian() * VAR + MEAN * dirx
        def y = rng.nextGaussian() * VAR + MEAN * diry
        def theta = rng.nextDouble() * Math.PI * 2
        return [x, y, theta]
    }
}
