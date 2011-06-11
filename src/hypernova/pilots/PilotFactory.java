package hypernova.pilots;

import hypernova.Ship;

public abstract class PilotFactory {
    public abstract Pilot create(Ship ship);

    public static class HunterSeekerFactory extends PilotFactory {
        public Pilot create(Ship ship) {
            return new HunterSeeker(ship);
        }
    }

    public static class HunterFactory extends PilotFactory {
        private Ship target;

        public HunterFactory(Ship target) {
            this.target = target;
        }

        public Pilot create(Ship ship) {
            return new Hunter(ship, target);
        }
    }
}
