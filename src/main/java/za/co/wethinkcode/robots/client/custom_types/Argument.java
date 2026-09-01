package za.co.wethinkcode.robots.client.custom_types;

public sealed interface Argument permits IntArgument, StringArgument {
    Object value();
}