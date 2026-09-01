package za.co.wethinkcode.robots.client.custom_types;

public final class StringArgument implements Argument {
    private final String value;

    public StringArgument(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return this.value;
    }
}
