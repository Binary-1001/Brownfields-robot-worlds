package za.co.wethinkcode.robots.client.textUI;

import java.util.List;

import textui.TextUI;
import textui.element.Case;
import textui.element.Display;
import textui.element.Element;
import textui.element.FlexAlign;
import textui.element.TextElement;
import textui.element.UnsortedListElement;
import textui.exception.ChildrenNotAllowedException;
import textui.exception.ElementFullException;
import textui.exception.ValueCannotBeSetException;

/**
 * Class responsible for creating and drawing the text-based user interface
 * for the robot client, displaying robot status, fire results, and surroundings.
 */
public class TextUserInterface {
    /**
     * Creates the left part of the header with the robot's name.
     * <p>
     * // * @param robotName The name of the robot.
     *
     * @return An Element containing the robot's name.
     */

// Domain classes
    public static class RobotInfo {
        private final String direction;
        private final String position;
        private final String state;

        public RobotInfo(String direction, String position, String state) {
            this.direction = direction;
            this.position = position;
            this.state = state;
        }

        public String getDirection() {
            return direction;
        }

        public String getPosition() {
            return position;
        }

        public String getState() {
            return state;
        }

    }

    public static class FireInfo {
        private final String impact;
        private final int distance;
        private final String robotName;
        private final int shield;

        public FireInfo(String impact, int distance, String robotName, int shield) {
            this.impact = impact;
            this.distance = distance;
            this.robotName = robotName;
            this.shield = shield;
        }

        public String getImpact() {
            return impact;
        }

        public int getDistance() {
            return distance;
        }

        public String getRobotName() {
            return robotName;
        }

        public int getShield() {
            return shield;
        }

    }

    private final String robotName;
    private final RobotStatus status;
    private FireInfo fireInfo;

    /**
     * Constructs the TextUserInterface with robot status and environment information.
     *
     * @param robotName The robot's name.
     * @param status    The robot status
     */
    public TextUserInterface(String robotName, RobotStatus status) {
        this.robotName = robotName;
        this.status = status;
        this.fireInfo = new FireInfo("Data unavailable", -1, "Data unavailable", -1);
    }

    public TextUserInterface setFireInfo(FireInfo fireInfo) {
        this.fireInfo = fireInfo;
        return this;
    }

    // a helper reusable helper method that builds the panel

    private static Element buildInfoPanel(String title, List<String> items)
            throws ValueCannotBeSetException, ChildrenNotAllowedException, ElementFullException {
        Element container = new Element(10, 56);
        Element heading = new TextElement(2, 15)
                //makes the heading in uppercase e.g FIRE STATE
                .setWordCase(Case.UPPERCASE)
                .setText(title);
        // list contains Direction, position state...
        UnsortedListElement list = new UnsortedListElement(4, 50);
        for (String item : items) {
            list.addItem(item);
            list.setPadding(1, 2).setBorder();

            container.insertChild(heading).insertChild(list);
            return container;
        }

        //adds spacing inside the list element
        list.setPadding(1, 2).setBorder();
        // Combines heading + list into one UI panel
        container.insertChild(heading).insertChild(list);

        return container;
    }

    // creates a small text box with the robot's name
    private static Element leftHeader(String text) {
        return new TextElement(2, 15).setText(text);
    }


    /**
     * Creates the right part of the header with shield and shots information.
     * <p>
     * //     * @param shield The shield value of the robot.
     * //     * @param shots The number of shots remaining.
     *
     * @return An Element containing shield and shots information.
     * @throws ValueCannotBeSetException   If a property cannot be set.
     * @throws ChildrenNotAllowedException If children cannot be added.
     * @throws ElementFullException        If the element cannot accept more children.
     */
    private static Element rightHeader(RobotStatus status) throws ValueCannotBeSetException, ChildrenNotAllowedException, ElementFullException {
        Element container = new Element(2, 30).setDisplay(Display.FLEX);
        container.insertChild(new TextElement(2, 15).setText(("Shield: " + status.getShield())))
                .insertChild(new TextElement(2, 15).setText("shots: " + status.getShots()));
        return container;
    }

    /**
     * Creates the header element with robot name on the left and shield/shots on the right.
     *
     * @param robotName The robot's name.
     *                  //     * @param shield The robot's shield value.
     *                  //     * @param shots The robot's shots remaining.
     * @return An Element containing the full header.
     */
    //combines the left and right headers into one row
    // Uses FlexAlign.SPACE_APART so that they're at opposite sides
    //result : ROBOT_X03ER                                 Shield: 115   Shots: 3
    private static Element header(String robotName, RobotStatus status) {


        try {
            Element header = new Element(2, 150)
                    .setDisplay(Display.FLEX).setJustifyContent(FlexAlign.SPACE_APART);
            header.insertChild(leftHeader(robotName))
                    .insertChild(rightHeader(status));
            return header;


        } catch (ValueCannotBeSetException | ChildrenNotAllowedException | ElementFullException e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }

        return null;
    }

    /**
     * Builds the robot state display element.
     * <p>
     * //     * @param direction The robot's current direction.
     * //     * @param position The robot's current position.
     * //     * @param state The robot's current state.
     *
     * @return An Element containing the robot state info.
     * @throws ValueCannotBeSetException   If a property cannot be set.
     * @throws ChildrenNotAllowedException If children cannot be added.
     * @throws ElementFullException        If the element cannot accept more children.
     */

    //builds panel info about the robot
    public static Element robotState(RobotInfo info) throws ValueCannotBeSetException, ChildrenNotAllowedException, ElementFullException {
        List<String> items = List.of(
                "DIRECTION: " + info.getDirection(),
                "POSITION: " + info.getPosition(),
                "STATE: " + info.getState()
        );
        return buildInfoPanel("Robot state", items);
    }

    /**
     * Builds the fire state display element.
     * <p>
     * //     * @param impact The impact result of the fire (e.g., "Hit" or "Miss").
     * //     * @param distance Distance to the target hit.
     * //     * @param robotName Name of the robot hit.
     * //     * @param shield Shield value of the robot hit.
     *
     * @return An Element containing the fire result information.
     * @throws ValueCannotBeSetException   If a property cannot be set.
     * @throws ChildrenNotAllowedException If children cannot be added.
     * @throws ElementFullException        If the element cannot accept more children.
     */
    // Displays the result of a firing action
    public static Element fireState(FireInfo fire) throws ValueCannotBeSetException, ChildrenNotAllowedException, ElementFullException {
        List<String> items = List.of(
                "IMPACT: " + fire.getImpact(),
                "DISTANCE: " + fire.getDistance(),
                "ROBOT NAME: " + fire.getRobotName(),
                "SHIED: " + fire.getShield()
        );

        return buildInfoPanel("fire State", items);
    }

    /**
     * Builds the main content element containing robot state and fire state side by side.
     * <p>
     * //     * @param direction Robot's direction.
     * //     * @param position Robot's position.
     * //     * @param state Robot's current state.
     * //     * @param impact Fire impact status.
     * //     * @param distance Distance to the target robot.
     * //     * @param robotName Name of the target robot.
     * //     * @param shield Shield of the target robot.
     *
     * @return The main Element combining robot state and fire state.
     */
    public static Element mainElement(RobotInfo robotInfo, FireInfo fireInfo) {

        try {
            Element main = new Element(10, 150)
                    .setDisplay(Display.FLEX)
                    .setJustifyContent(FlexAlign.SPACE_BETWEEN)
                    .setPadding(5, 0);
            main.insertChild(robotState(robotInfo))
                    .insertChild(fireState(fireInfo));
            return main;

        } catch (ValueCannotBeSetException | ChildrenNotAllowedException | ElementFullException e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1);
            return null;
        }


    }


    /**
     * Builds a directional view listing the items seen in a specific direction.
     *
     * @param direction The direction to look (e.g., "North").
     * @param items     List of items visible in that direction.
     * @return An Element representing the direction's view.
     * @throws ChildrenNotAllowedException If children cannot be added.
     * @throws ElementFullException        If the element cannot accept more children.
     */

    public static Element directionLook(String direction, List<String> items) throws ChildrenNotAllowedException, ElementFullException {
        Element container = new Element(12, 73);
        UnsortedListElement list = new UnsortedListElement(10, 70);

        for (String item : items) list.addItem(item);

        return container.setBorder()
                .insertChild(leftHeader(direction))
                .insertChild(list);
    }

    /**
     * Builds an Element containing all four direction views inline.
     *
     * @param itemsList A list containing 4 lists, one for each direction's items.
     * @return An Element representing the combined directions.
     */

    //No loops, no switch, only one try/catch block around all insertions
    public static Element directions(List<List<String>> itemsList) {
        try {
            Element container = new Element(28, 150).setDisplay(Display.INLINE);

            container.insertChild(directionLook("North", itemsList.get(0)))
                    .insertChild(directionLook("East", itemsList.get(1)))
                    .insertChild(directionLook("South", itemsList.get(2)))
                    .insertChild(directionLook("West", itemsList.get(3)));
            return container;
        } catch (ValueCannotBeSetException | ChildrenNotAllowedException | ElementFullException e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1);
            return null;
        }


    }


    /**
     * Draws the complete UI by constructing all elements and rendering via TextUI.
     */
    public void draw() {
        Element body = new Element(50, 150).setBorder();

        Element header = header(robotName, status);


        RobotInfo robotInfo = new RobotInfo(
                status.getDirection(),
                status.getPosition(),
                status.getState()
        );
        Element main = mainElement(robotInfo, fireInfo);

        Element directionElement = directions(status.getDirectionList());

        try {
            body
                    .insertChild(header)
                    .insertChild(main)
                    .insertChild(directionElement);
        } catch (ChildrenNotAllowedException | ElementFullException e) {
            System.out.println("Error building UI: " + e.getMessage());
            System.exit(1);
        }

        new TextUI(body).draw();
    }
}

//    /**
//     * Test main method demonstrating the UI with sample data.
//     *
//     * @param args Not used.
//     * @throws ChildrenNotAllowedException If children cannot be added.
//     * @throws ElementFullException If element cannot accept more children.
//     * @throws ValueCannotBeSetException If property setting fails.
//     */
//    public static void main(String[] args) throws ChildrenNotAllowedException, ElementFullException, ValueCannotBeSetException {
//        Element body = new Element(52, 150).setBorder();
//
//        Element header = header("X03ER", 115, 3);
//
//
//
//        List<String> north = List.of("MOUNTAIN (3 steps away)");
//        List<String> east = List.of("ROBOT (2 steps away)");
//        List<String> south = List.of("LAKE (4 steps away), EDGE (6 steps away)");
//        List<String> west = List.of("EDGE (10 steps away)");
//
//        List<List<String>> directionList = List.of(north, east, south, west);
//
//        RobotStatus status = new RobotStatus(
//                115,
//                3,
//                "North",
//                "[19, -110]",
//                "Normal",
//                directionList
//        );
//        FireStateData fireData = new FireStateData(
//                "Hit",
//                30,
//                "X03ER",
//                2
//        );
//        Element main = mainElement(status, fireData);
//        Element directionElement = directions(directionList);
//
//        body
//            .insertChild(header)
//            .insertChild(main)
//            .insertChild(directionElement);
//
//        new TextUI(body).draw();
//    }
//}
