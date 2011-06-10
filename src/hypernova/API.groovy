package hypernova
// some nicer api for groovy scripts to use

public class API {
    private static Universe universe

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
}
