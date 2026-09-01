package za.co.wethinkcode.robots.client.custom_types;

public final class IntArgument implements Argument {
    private final int value;

    public IntArgument(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return this.value;
    }
}
