package Controller;

import Exceptions.*;
import Interfaces.Storable;
import Interfaces.Upgradable;
import Interfaces.VisibleInMap;
import Model.*;
import Model.Animals.Animal;
import Model.Animals.Domestics.Cow;
import Model.Animals.Domestics.Hen;
import Model.Animals.Domestics.Sheep;
import Model.Animals.Predator;
import Model.Animals.Seekers.Cat;
import Model.Animals.Seekers.Dog;
import Model.Placement.Direction;
import Model.Placement.Position;
import Model.Products.Product;
import Model.TimeDependentRequests.*;
import Model.Vehicles.Helicopter;
import Model.Vehicles.Truck;
import Model.Workshops.Workshop;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

import static Model.Placement.Map.MAP_SIZE;

public class Controller {
    private static Mission mission;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LevelRequirementsChecker lrc = new LevelRequirementsChecker(0, 3, 0,
                0, 0, 0, 0, 3, 0,
                0, 0, 0, 0);

        mission = new Mission(5000, "firstMission", lrc, null);

        //todo: sharte While -> check requirements

        String input = scanner.nextLine().toLowerCase();
        while (!input.toLowerCase().equals("exit")) {

            String[] splittedInput = input.split(" ");
            switch (splittedInput[0]) {
                //todo: switch case baraye string ha kar mikone( .equals va == )
                case "buy":
                    Animal boughtAnimel = buyAnimalRequestHandler(splittedInput[1], lrc);
                    try {
                        lrc.updateState(boughtAnimel);
                    } catch (LevelFinishedException e) {
                        mission.setMissionAsCompleted();
                    }
                    break;

                case "pickup":
                    int row = Integer.parseInt(splittedInput[1]);
                    int column = Integer.parseInt(splittedInput[2]);
                    try {
                        pickupRequestController(row, column);
                    } catch (LevelFinishedException e) {
                        mission.setMissionAsCompleted();
                    }
                    break;

                case "plant":
                    row = Integer.parseInt(splittedInput[1]);
                    column = Integer.parseInt(splittedInput[2]);
                    plantRequestHandler(row, column);
                    break;

                case "cage":
                    row = Integer.parseInt(splittedInput[1]);
                    column = Integer.parseInt(splittedInput[2]);
                    cageAnimalController(row, column);
                    break;

                case "well":
                    wellRequestHandler();
                    break;

                case "start":
                    startWorkshopRequestHandler(splittedInput[1]);
                    break;

                case "upgrade":
                    upgradeRequestHandler(splittedInput[1]);
                    break;

                case "print":
                    printRequestHandler(splittedInput[1], lrc);
                    break;

                case "loadcostume":
                    // TODO: 12/28/2018
                    loadCostumeRequestHandler(splittedInput[1]);
                    break;

                case "run":
                    // TODO: 12/28/2018
                    runRequestHandler(splittedInput[1]);
                    break;

                case "savegame":
                    // TODO: 12/28/2018
                    saveGameRequestHandler(splittedInput[1]);
                    break;

                case "loadgame":
                    // TODO: 12/28/2018
                    loadGameRequestHandler(splittedInput[1]);
                    break;

                case "turn":
                    turnRequestHandler(Integer.parseInt(splittedInput[1]));
                    break;

                case "truck":
                    switch (splittedInput[1]) {
                        case "add":
                            addToTruckListRequestHandler(splittedInput[2], Integer.parseInt(splittedInput[3]));
                            break;
                        case "clear":
                            clearTruckListRequestHandler();
                            break;
                        case "go":
                            goTruckRequestHandler();
                            break;
                    }
                    break;

                case "helicopter":
                    switch (splittedInput[1]) {
                        case "add":
                            addToHelicopterListRequestHandler(splittedInput[2], Integer.parseInt(splittedInput[3]));
                            break;
                        case "clear":
                            clearHelicopterListRequestHandler();
                            break;
                        case "go":
                            goHelicopterRequestHandler();
                            break;
                    }
                    break;
            }

            if (mission.isCompleted()) {
                System.out.println("Mission is completed!");
                break;
            }

            input = scanner.nextLine().toLowerCase();
        }
    }

    private static void startWorkshopRequestHandler(String workshopName) {
        Workshop workshop;
        try {
            workshop = mission.getWorkshop(workshopName);
        } catch (NotFoundException e) {
            System.out.println("entered name isn't a valid name.");
            return;
        }
        ArrayList<Product> inputs;
        try {
            inputs = workshop.collectInputs();
        } catch (NotEnoughResourcesException e) {
            System.out.println("there isn't enough resources to start workshop.");
            return;
        }
        mission.addTimeDependentRequest(new StartWorkshopRequest(workshop, inputs));
    }

    private static void goTruckRequestHandler() {
        Truck truck = mission.getTruck();

        if (isVehicleOnWay("truck")) {
            System.out.println("Truck is on its way.");
            return;
        }

        if (truck.getList().isEmpty()) {
            System.out.println("Truck list is empty.");
            return;
        }
        mission.addTimeDependentRequest(new TruckGoingRequest(mission));
    }

    private static void goHelicopterRequestHandler() {
        Helicopter helicopter = mission.getHelicopter();

        if (isVehicleOnWay("helicopter")) {
            System.out.println("Helicopter is on its way");
            return;
        }

        if (helicopter.getList().isEmpty()) {
            System.out.println("Helicopter List is empty.");
            return;
        }

        int cost = helicopter.calculateCost();
        try {
            mission.spendMoney(cost);
        } catch (NotEnoughMoneyException e) {
            System.out.println("you don't have enough money to buy these items.");
            return;
        }

        mission.addTimeDependentRequest(new HelicopterGoingRequest(mission));
    }

    private static boolean isVehicleOnWay(String vehicleName) {
        if (vehicleName.toLowerCase().equals("helicopter")) {
            ArrayList<TimeDependentRequest> remainedRequests = mission.getRemainedRequests();
            for (TimeDependentRequest request : remainedRequests) {
                if ((request instanceof HelicopterGoingRequest) || (request instanceof HelicopterGettingBackRequest)) {
                    return true;
                }
            }
            return false;
        } else /*if (vehicleName.toLowerCase().equals("truck"))*/ {     // TODO: 12/28/2018 esme vasile haro dorost benevis
            ArrayList<TimeDependentRequest> remainedRequests = mission.getRemainedRequests();
            for (TimeDependentRequest request : remainedRequests) {
                if ((request instanceof TruckGoingRequest) || (request instanceof TruckGettingBackRequest)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static void clearHelicopterListRequestHandler() {
        if (isVehicleOnWay("helicopter")) {
            System.out.println("Helicopter is on its way. wait till it gets back and try again.");
            return;
        }
        mission.getHelicopter().clearList();
    }

    private static void addToHelicopterListRequestHandler(String itemName, int itemCount) {
        int numberOfAddedObjects = 0;
        Helicopter helicopter = mission.getHelicopter();

        if (isVehicleOnWay("helicopter")) {
            System.out.println("Truck is on its way.wait till it gets back and try again.");
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            Product product;
            try {
                product = Utils.getProductObject(itemName);
                helicopter.addToList(product);
                numberOfAddedObjects++;
            } catch (CapacityExceededException e) {
                if (numberOfAddedObjects == 0) {
                    System.out.println("there isn't enough space in helicopter, no items added to helicopter list.");
                    break;
                } else {
                    System.out.println("there isn't enough space in helicopter, %d %ss added to helicopters list.");
                    break;
                }
            } catch (NotFoundException e) {
                System.out.println("not valid item name.");
                break;
            }
        }
    }

    private static void clearTruckListRequestHandler() {
        //truck o warehousemun joda an -> momkene natunim list ro khali konim
        Truck truck = mission.getTruck();
        Warehouse warehouse = mission.getWarehouse();

        if (isVehicleOnWay("truck")) {
            System.out.println("Truck is on its way.wait till it gets back and try again.");
            return;
        }

        ArrayList<Storable> itemsInList = truck.getList();
        mission.getTruck().clearList();
        ArrayList<Storable> storedItems = new ArrayList<>();
        for (Storable storable : itemsInList) {
            try {
                warehouse.store(storable);
                storedItems.add(storable);
            } catch (CapacityExceededException e) {
                break;
            } catch (LevelFinishedException e1) {
                e1.printStackTrace();      //nabayad rokh bede
            }
        }
        if (storedItems.isEmpty()) {
            System.out.println("there isn't enough space in warehouse to store items in truck list.");
            return;
        }

        itemsInList.removeAll(storedItems);
        for (Storable storable : itemsInList) {
            try {
                truck.addToList(storable);
            } catch (CapacityExceededException e) {
                e.printStackTrace();        //nabayad rokh bede -> az khodesh gereftim!
            }
        }
    }

    private static void addToTruckListRequestHandler(String itemName, int itemCount) {
        int numberOfAddedObjects = 0;

        if (isVehicleOnWay("truck")) {
            System.out.println("Truck is on its way.wait till it gets back and try again.");
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            Storable obj = null;
            try {
                obj = mission.getWarehouse().getAndDiscard(itemName);
                mission.getTruck().addToList(obj);
                numberOfAddedObjects++;
            } catch (NotFoundException e) {
                if (numberOfAddedObjects == 0)
                    System.out.println("This item does not exist in warehouse!");
                else
                    System.out.printf("you only have %d %ss.\n", numberOfAddedObjects, itemName);
                break;

            } catch (CapacityExceededException e) {
                System.out.printf("there isn't enough space in Truck,%d %ss added to list.\n", numberOfAddedObjects, itemName);
                try {
                    mission.getWarehouse().store(obj);
                } catch (CapacityExceededException | LevelFinishedException ignored) {
                }
                break;

            }
        }
    }

    private static void turnRequestHandler(int turnsPassed) {
        mission.passSeveralTurns(turnsPassed);
    }

    private static void printRequestHandler(String input, LevelRequirementsChecker lrc) {
        boolean requestHandled = false;
        switch (input.toLowerCase()) {
            case "requests":
                for (TimeDependentRequest remainedRequest : mission.getRemainedRequests()) {
                    System.out.println(remainedRequest.getClass().getName());
                }
                requestHandled = true;
                break;

            case "info":
                System.out.println("Money :" + mission.getMoney());
                System.out.println("Turn : " + mission.getTimeNow());
                lrc.printInfo();
                requestHandled = true;
                break;

            case "map":
                mission.getMap().print();
                requestHandled = true;
                break;

            case "helicopter":
                mission.getHelicopter().printInfo();
                requestHandled = true;
                break;

            case "truck":
                mission.getTruck().printInfo();
                requestHandled = true;
                break;

            case "well":
                mission.getWell().printInfo();
                requestHandled = true;
                break;

            case "warehouse":
                mission.getWarehouse().printInfo();
                requestHandled = true;
                break;

            case "levels":
                printLevelsRequestHandler();
                // TODO: 12/28/2018
                requestHandled = true;
                break;
        }
        if (!requestHandled) {
            Workshop workshop;
            try {
                workshop = mission.getWorkshop(input);
            } catch (NotFoundException e) {
                System.out.println("not valid request.");
                return;
            }
            workshop.printInfo();
        }

    }

    private static void printLevelsRequestHandler() {
        // TODO: 12/28/2018 print levels ro nazadi
    }

    private static void loadGameRequestHandler(String pathToJsonFile) {
        // TODO: 12/24/2018
    }

    private static void saveGameRequestHandler(String pathToJsonFile) {
        // TODO: 12/24/2018
    }

    private static void runRequestHandler(String mapName) {
        // TODO: 12/24/2018
    }

    private static void loadCostumeRequestHandler(String pathToCostumeDirectory) {
        // TODO: 12/24/2018
    }

    private static void upgradeRequestHandler(String upgradingUnitName) {
        if (upgradingUnitName.toLowerCase().equals("cat")) {
            ArrayList<Cat> catsInMap = mission.getMap().getCats();
            if (catsInMap.isEmpty()) {
                try {
                    mission.increaseCatsBeginningLevel();
                    mission.spendMoney(Cat.getCatUpgradeCost());
                } catch (MaxLevelExceededException e) {
                    System.out.println("cats are at their maximum possible level");
                    return;
                } catch (NotEnoughMoneyException e) {
                    System.out.println("your money isn't enough.");
                    return;
                }
            } else {
                try {
                    catsInMap.get(0).upgrade();
                } catch (NotEnoughMoneyException e) {
                    System.out.println("your money isn't enough.");
                    return;
                } catch (MaxLevelExceededException e) {
                    System.out.println("cats are at their maximum possible level.");
                    return;
                }
            }
        }
        try {
            Upgradable upgradableUnit = mission.getUpgradableUnit(upgradingUnitName);
            if (upgradableUnit == null) {   //momkene custom workshop nadashte bashim va gofte bashe uno upgrade kon.
                System.out.println("this unit doesn't exist.");
                return;
            }
            upgradableUnit.upgrade();
        } catch (NotEnoughMoneyException e) {
            System.out.printf("Your money is not enough for upgrading %s\n", upgradingUnitName);
        } catch (MaxLevelExceededException maxLevelExceeded) {
            System.out.printf("%s is already at it's max possible level\n", upgradingUnitName);
        } catch (NotFoundException e2) {
            System.out.println("enter a correct name.");
        }
    }

    private static void wellRequestHandler() {
        if (!mission.getWell().isEmpty()) {
            System.out.println("Well isn't Empty.");
            return;
        }
        if (mission.getWell().getRefillCost() > mission.getMoney()) {
            System.out.println("Your money isn't enough!");
            return;
        }
        mission.addTimeDependentRequest(new RefillWellRequest(mission));
    }

    private static void cageAnimalController(int row, int column) {
        ArrayList<Predator> predators = mission.getMap().getPredatorsInCell(row, column);

        if (predators == null) {
            System.out.println("There isn't any predator in that cell");
            return;
        }

        for (Predator predator : predators) {
            Cage cage = new Cage(new Position(row, column), predator);
            mission.getMap().addToMap(cage);
            try {
                mission.getMap().discardAnimal(predator);
            } catch (NotFoundException e) {
                e.printStackTrace();    //chon predator haro az map gereftim nabayad in exception ettefagh biofte
            }
        }
    }

    private static void plantRequestHandler(int row, int column) {
        int minRow = Math.max(0, row - 1);
        int minColumn = Math.max(0, column - 1);
        int maxRow = Math.min(MAP_SIZE - 1, row + 1);
        int maxColumn = Math.min(MAP_SIZE - 1, column + 1);

        int numberOfPlantsPlanted = calculatePlantsNeeded(row, column);
        if (numberOfPlantsPlanted == 0) {
            System.out.println("all cells in that area are already planted.");
            return;
        }

        try {
            mission.getWell().extractWater(Plant.PLANTING_NEEDED_WATER);
        } catch (WellNotEnoughWaterException e) {
            System.out.println("refill well first.");
            return;
        }

        for (int row1 = minRow; row1 <= maxRow; row1++)
            for (int column1 = minColumn; column1 <= maxColumn; column1++)
                if (!mission.getMap().isPlanted(row1, column1)) {
                    Plant plant = new Plant(new Position(row1, column1));
                    mission.getMap().addToMap(plant);
                }
    }

    private static int calculatePlantsNeeded(int row, int column) {
        int minRow = Math.max(0, row - 1);
        int minColumn = Math.max(0, column - 1);
        int maxRow = Math.min(MAP_SIZE - 1, row + 1);
        int maxColumn = Math.min(MAP_SIZE - 1, column + 1);

        int numberOfPlantsPlanted = 0;
        for (int row1 = minRow; row1 <= maxRow; row1++)
            for (int column1 = minColumn; column1 <= maxColumn; column1++)
                if (!mission.getMap().isPlanted(row1, column1))
                    numberOfPlantsPlanted++;

        return numberOfPlantsPlanted;
    }

    private static void pickupRequestController(int row, int column) throws LevelFinishedException {
        Warehouse warehouse = mission.getWarehouse();
        ArrayList<Storable> items = mission.getMap().getAndDiscardProductsAndCagedAnimals(row, column);
        ArrayList<Storable> storedItems = new ArrayList<>();

        for (Storable item : items) {
            try {
                warehouse.store(item);
                storedItems.add(item);
            } catch (CapacityExceededException e) {
                System.out.println("there isn't enough capacity in warehouse.");
                break;
            }
        }

        items.removeAll(storedItems);
        //age natuneste bashim ye seri az kala haro bar darim(exception) bareshun migardunim be map
        for (Storable item : items)
            mission.getMap().addToMap((VisibleInMap) item);
    }

    private static Animal buyAnimalRequestHandler(String animalName, LevelRequirementsChecker lrc) {
        Direction direction = Utils.getRandomDirection();
        Position position = Utils.getRandomPosition();
        switch (animalName.toLowerCase()) {
            case "cow":
                try {
                    mission.spendMoney(Cow.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    System.out.println("You don't have enough money!");
                    return null;
                }
                Cow cow = new Cow(mission.getMap(), direction, position);
                mission.getMap().addToMap(cow);
                return cow;

            case "hen":
                try {
                    mission.spendMoney(Hen.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    System.out.println("You don't have enough money!");
                    return null;
                }
                Hen hen = new Hen(mission.getMap(), direction, position);
                mission.getMap().addToMap(hen);
                return hen;

            case "sheep":
                try {
                    mission.spendMoney(Sheep.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    System.out.println("You don't have enough money!");
                    return null;
                }
                Sheep sheep = new Sheep(mission.getMap(), direction, position);
                mission.getMap().addToMap(sheep);
                return sheep;

            case "cat":
                try {
                    mission.spendMoney(Cat.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    System.out.println("You don't have enough money!");
                    return null;
                }
                Cat cat = new Cat(mission, direction, position);
                mission.getMap().addToMap(cat);
                return cat;

            case "dog":
                try {
                    mission.spendMoney(Dog.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    System.out.println("You don't have enough money!");
                    return null;
                }
                Dog dog = new Dog(mission.getMap(), direction, position);
                mission.getMap().addToMap(dog);
                return dog;
        }
        System.out.println("this isn't a valid animal name.");
        return null;
    }
}
