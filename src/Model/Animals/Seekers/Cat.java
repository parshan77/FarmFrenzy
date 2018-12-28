package Model.Animals.Seekers;

import Exceptions.CapacityExceededException;
import Exceptions.LevelFinishedException;
import Exceptions.MaxLevelExceededException;
import Exceptions.NotEnoughMoneyException;
import Interfaces.Upgradable;
import Model.Animals.Seeker;
import Model.Mission;
import Model.Placement.Direction;
import Model.Placement.Position;
import Model.Products.Product;

import java.util.ArrayList;


public class Cat extends Seeker implements Upgradable {
    private int level;
    private Mission mission;
    private static int CAT_PACE = 1;
    public static int CAT_MAX_LEVEL = 1;
    private static int CAT_UPGRADE_COST = 200;
    private static int CAT_BUY_COST = 500;

    // TODO: 12/27/2018 harkate gorbe chejurie? vaghti kala bashe ru zamin soratesh taghir mikone?

    public Cat(Mission mission, Direction direction, Position position) {
        super(mission.getMap(), direction, position);
        this.mission = mission;
        level = mission.getCatsBeginningLevel();
        name = "Cat";
        pace = CAT_PACE;
    }

    public static int getBuyCost() {
        return CAT_BUY_COST;
    }

    private void collect() throws LevelFinishedException {
        ArrayList<Product> collectedProducts = map.getAndDiscardProductsInCell(position);
        if (collectedProducts.isEmpty())
            return;
        ArrayList<Product> storedProducts = new ArrayList<>();
        for (Product product : collectedProducts) {
            try {
                mission.getWarehouse().store(product);
                storedProducts.add(product);
            } catch (CapacityExceededException e) {
                break;
            }
        }
        collectedProducts.removeAll(storedProducts);
        if (collectedProducts.isEmpty())
            return;
        for (Product product : collectedProducts) {
            map.addToMap(product);
        }
    }

    // TODO: 12/27/2018 move e cat joda seda zade beshe
    public void moveCat() throws LevelFinishedException {
        // TODO: 12/27/2018 exception e level finished ro chikaresh konam!?
        if (level == 0)
            for (int i = 0; i < pace; i++)
                normalStep();
        else
            for (int i = 0; i < pace; i++)
                smartStep();
    }

    private void normalStep() throws LevelFinishedException {
        super.step();
        collect();
    }

    private void smartStep() throws LevelFinishedException {
        Product closestProduct = map.getClosestProduct(position);
        if (super.smartStep(closestProduct.getPosition()))
            collect();
        // TODO: 12/27/2018 if lazem nist, haminjuri mahze etminan gozashtam
    }

    @Override
    public void upgrade() throws NotEnoughMoneyException, MaxLevelExceededException {
        if (level == CAT_MAX_LEVEL)
            throw new MaxLevelExceededException();
        mission.spendMoney(CAT_UPGRADE_COST);
        mission.increaseCatsBeginningLevel();
        for (Cat cat : mission.getMap().getCats()) {
            cat.increaseLevel();
        }
    }

    private void increaseLevel() {
        level++;
    }

    public static int getCatUpgradeCost() {
        return CAT_UPGRADE_COST;
    }

    @Override
    public void show() {

    }
}
