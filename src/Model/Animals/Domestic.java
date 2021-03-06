package Model.Animals;

import Controller.AnimalController;
import Exceptions.AnimalDiedException;
import Exceptions.NotFoundException;
import Model.Placement.Direction;
import Model.Placement.Map;
import Model.Plant;
import Model.Placement.Position;
import Model.Products.Product;
import Model.TimeDependentRequests.DomesticMovingRequest;
import Model.TimeDependentRequests.TimeDependentRequest;
import Utils.Utils;


public abstract class Domestic extends Animal {
    private String productName;
    private double hunger = 0;
    private static double HUNGER_INCREASING_VALUE_PER_TURN = 0.5;
    private static double DYING_HUNGER_LIMIT = 10;
    private static double LIMIT_OF_BEING_HUNGERY = 4;
    private static double HUNGER_DECREASING_VALUE_AFTER_EATING = 4;
    private int hungryMovingPace;
    private TimeDependentRequest producingRequest;
    private boolean isDead = false;

    protected DomesticMovingRequest movingRequest;

    public Domestic(Map map, Direction direction, Position position, String productName, int hungryMovingPace) {
        super(map, direction, position);
        this.productName = productName;
        this.hungryMovingPace = hungryMovingPace;
    }

    public void makeProduct() {
        try {
            Product product = Utils.getProductObject(productName);
            product.setPosition(new Position(position.getRow(), position.getColumn()));
            map.addToMap(product);
            AnimalController.produceProduct(this, product);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void makeHungry() throws AnimalDiedException {
        hunger += HUNGER_INCREASING_VALUE_PER_TURN;
        if (hunger > LIMIT_OF_BEING_HUNGERY) {
            pace = hungryMovingPace;
        }
        if (hunger >= DYING_HUNGER_LIMIT) {
            isDead = true;
            throw new AnimalDiedException();
        }
    }

    public void move() {
        int coveredDistance = 0;
        //momkene ghabl az inke be andaze hunger pace rah bere alaf bokhore
        //ba'd az sir shodan dg nabayad ba sorate harkate moghe e gorosnegi harkat kone
        // TODO: 2/2/2019 sharte dovome while ro baraye chi zade budam?
        while ((hunger >= LIMIT_OF_BEING_HUNGERY) && (coveredDistance < hungryMovingPace)) {
            smartStep();
            coveredDistance++;
        }
        if (coveredDistance < pace)
            for (int i = 0; i < (pace - coveredDistance); i++)
                step();
    }

    public void step() {
        super.step();
        if (map.isPlanted(position))
            if (hunger >= LIMIT_OF_BEING_HUNGERY)
                eat();
    }

    private void smartStep() {
        Plant closestPlant = map.getClosestPlant(position);
        if (closestPlant == null)
            super.step();
        else if (super.smartStep(closestPlant.getPosition()))
            eat();
    }

    private void eat() {
        AnimalController.domesticEat(this);
        hunger -= HUNGER_DECREASING_VALUE_AFTER_EATING;
        pace = 1;
    }

    public TimeDependentRequest getProducingTimeDependentRequest() {
        return producingRequest;
    }

    public void setProducingRequest(TimeDependentRequest producingRequest) {
        this.producingRequest = producingRequest;
    }

    public DomesticMovingRequest getMovingRequest() {
        return movingRequest;
    }

    public void setMovingRequest(DomesticMovingRequest movingRequest) {
        this.movingRequest = movingRequest;
    }

    public boolean isDead() {
        return isDead;
    }
}



