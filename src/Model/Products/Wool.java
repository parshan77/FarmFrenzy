package Model.Products;

import Model.Placement.Position;

public class Wool extends Product {
    public static int volume = 5;
    public static int buyCost = 200;
    public static int sellCost = 100;
    public static String name ="Wool";

    public Wool(Position position) {
        super(position);
    }

    public Wool() {
    }

    public void show() {
        //TODO
    }
}
