package za.co.wethinkcode.robots.command;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LazilyParsedNumber;

import za.co.wethinkcode.robots.world.World;

/**
 * Command that moves a robot backward by inverting the argument
 * and delegating the execution to a ForwardCommand with the negated value.
 */
public class BackCommand extends Command {
    public BackCommand(World world, JsonObject jsonObject) {
        super(world, jsonObject);
    }

    /**
     * Executes the "back" command by converting it into a "forward" command
     * with a negative step value.
     *
     * @return a JsonObject containing the result of the movement
     * @throws InterruptedException if the command execution is interrupted
     */
    @Override
    public JsonObject execute() throws InterruptedException {
        // Internally treat "back" as "forward" with a negative value
        Command moveCommand = new ForwardCommand(this.world, this.jsonObject);

        JsonArray arguments = new JsonArray();

        List<Object> args = this.getArguments();

        // Negate the movement value to simulate moving backward
        int negateValue = ((LazilyParsedNumber) args.get(0)).intValue() * -1;
        arguments.add(new LazilyParsedNumber(Integer.toString(negateValue)));

        // Set the modified arguments and execute as a forward command
        setArguments(moveCommand, arguments);
        return moveCommand.execute();
    }
}
