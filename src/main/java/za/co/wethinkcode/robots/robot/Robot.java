package za.co.wethinkcode.robots.robot;

import za.co.wethinkcode.robots.direction.Direction;
import za.co.wethinkcode.robots.position.Position;
import za.co.wethinkcode.robots.world.HitInfo;
import za.co.wethinkcode.robots.world.World;

public class Robot implements RobotInterface {
    public static final int BULLET_RANGE = 10;

    private final int VISIBILITY_RANGE;
    private final String NAME;
    private final int BULLETS_MAX;
    private final int SHIELD_STRENGTH_MAX;
    private final int REPAIR_TIME;
    private final int RELOAD_TIME;

    private World world;
    private Direction direction;
    private int bulletsAvail;
    private int shieldStrength;
    private boolean alive;
    private boolean repairMode;
    private boolean reloadMode;

    public Robot(String name, int visibilityRange) {
        this(name, 0, 0, visibilityRange);
    }

    public Robot(String name, int maxShieldStrength, int maxBullets, int visibility_range) {
        this(name, maxShieldStrength, maxBullets, visibility_range, 5, 5);
    }
    
    public Robot(String name, int maxShieldStrength, int maxBullets, int visibilityRange, int reloadTime, int repairTime) {
        this.NAME = name;
        this.VISIBILITY_RANGE = visibilityRange;
        this.BULLETS_MAX = maxBullets;
        this.SHIELD_STRENGTH_MAX = maxShieldStrength;
        this.RELOAD_TIME = reloadTime;
        this.REPAIR_TIME = repairTime;

        this.direction = Direction.NORTH;
        this.bulletsAvail = this.BULLETS_MAX;
        this.shieldStrength = this.SHIELD_STRENGTH_MAX;
        this.alive = true;
        this.reloadMode = false;
        this.repairMode = false;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public int getVisibilityRange() {
        return this.VISIBILITY_RANGE;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public Direction getOrientation() {
        return this.direction;
    }

    @Override
    public int getShieldStrength() {
        return this.shieldStrength;
    }

    @Override
    public int getBulletsAvail() {
        return this.bulletsAvail;
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public boolean isRepairMode() {
        return this.repairMode;
    }

    @Override
    public boolean isReloadMode() {
        return this.reloadMode;
    }

    @Override
    public HitInfo fire() {
        if (this.bulletsAvail > 0) {
            this.bulletsAvail = this.bulletsAvail - 1;
            Bullet bullet = new Bullet(this.direction, BULLET_RANGE, this.getPosition());
            return this.world.handleBulletFired(bullet);
        }
        System.out.println("Out of bullets, robot needs to reload");
        return null;
    }

    @Override
    public void takeDamage() {
        if (this.shieldStrength == 0) {
            this.die();
        }
        this.shieldStrength = this.shieldStrength - 1;
    }

    @Override
    public void die() {
        this.alive = false;
        this.world.reportDeath(this);
    }

    @Override
    public void repair() throws InterruptedException {
        this.repairMode = true;

        Thread repairTask = new Thread(() -> {
            try {
                Thread.sleep(this.REPAIR_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.shieldStrength = this.SHIELD_STRENGTH_MAX;
                this.repairMode = false;
            }
        }, "repairing");

        repairTask.start();
    }

    @Override
    public void reload() throws InterruptedException {
        this.reloadMode = true;

        Thread reloadTask = new Thread(() -> {
            try {
                Thread.sleep(this.RELOAD_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.bulletsAvail = this.BULLETS_MAX;
                this.reloadMode = false;
            }
        }, "reloading");

        reloadTask.start();
    }

    @Override
    public void updateDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public MoveResponse move(int steps) {
        Position currentPosition = this.getPosition();

        int newX = currentPosition.getX();
        int newY = currentPosition.getY();

        switch (this.direction) {
            case NORTH:
                newY = newY - steps;
                break;
            case SOUTH:
                newY = newY + steps;
                break;
            case WEST:
                newX = newX - steps;
                break;
            case EAST:
                newX = newX + steps;
                break;
        }

        Position newPosition = new Position(newX, newY);

        MoveResponse response = this.world.moveRobot(this, newPosition);

        return response;
    }

    @Override
    public Position getPosition() {
        return this.world.getPosition(this);
    }
}
