package Model.Animals.Domestics;

import Model.Animals.Domestic;
import Model.Direction;
import Model.Map;
import Model.Position;
import Model.Products.Egg;

public class Hen extends Domestic {
    public Hen(Map map, Direction direction, Position position) {
        super(map, direction, position);
    }

    @Override
    public void makeProduct() {
        new Egg();
        //todo
    }

    @Override
    public Direction getDirection() {
        return null;
    }

    @Override
    public void show() {

    }

    @Override
    public void move() {
        super.move();
    }

    @Override
    public void step() {
        super.step();
    }

    @Override
    public void smartStep() {
        super.smartStep();
    }

    @Override
    public void makeHungry() {
        super.makeHungry();
    }

    @Override
    public void eat() {
        super.eat();
    }
}
