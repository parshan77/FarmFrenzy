package Model.Products;

import Model.Placement.Position;

public class EggPowder extends Product{
    public static int volume = 2;
    public static int buyCost = 100;
    public static int sellCost = 50;
    public static String name = "EggPowder";

    public EggPowder(Position position) {
        super(position);
    }

    public EggPowder() {
    }

    public void show(){
        //TODO:
    }

}
