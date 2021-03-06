package Controller;

import Exceptions.MaxLevelExceededException;
import Exceptions.NotEnoughMoneyException;
import Exceptions.NotFoundException;
import Model.Animals.Animal;
import Model.Animals.Domestic;
import Model.Animals.Domestics.Cow;
import Model.Animals.Domestics.Hen;
import Model.Animals.Domestics.Sheep;
import Model.Animals.Predator;
import Model.Animals.Seekers.Cat;
import Model.Animals.Seekers.Dog;
import Model.Cage;
import Model.Mission;
import Model.Placement.Direction;
import Model.Placement.Map;
import Model.Placement.Position;
import Model.Plant;
import Model.Products.Product;
import Model.TimeDependentRequests.AnimalsExceptDomesticsMovementRequest;
import Model.TimeDependentRequests.DomesticMovingRequest;
import Model.TimeDependentRequests.DomesticsProducingRequest;
import Model.TimeDependentRequests.TimeDependentRequest;
import Utils.Utils;
import View.AnimalViewer;
import View.Animations.AnimalAnimation;
import View.Animations.BuzzAnimation;
import View.GamePlayView;
import View.ProductViewer;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;

public class AnimalController {

    public static void buyAnimal(String animalName, GamePlayView gamePlayView) {
        Direction direction = Utils.getRandomDirection();
        Position position = Utils.getRandomPosition();
        Mission mission = gamePlayView.getMission();
        AnimalViewer animalViewer = null;
        switch (animalName.toLowerCase()) {
            case "cow":
                try {
                    mission.spendMoney(Cow.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    BuzzAnimation.play(gamePlayView.getMoneyLabel());
                    return;
                }
                Cow cow = new Cow(mission.getMap(), direction, position);
                mission.getMap().addToMap(cow);
                animalViewer = new AnimalViewer(cow, gamePlayView);
                cow.setAnimalViewer(animalViewer);

                TimeDependentRequest cowProducingRequest = new DomesticsProducingRequest(mission, cow);
                mission.addTimeDependentRequest(cowProducingRequest);
                cow.setProducingRequest(cowProducingRequest);

                DomesticMovingRequest moveRequest = new DomesticMovingRequest(mission, cow);
                mission.addDomesticMovementRequest(moveRequest);
                cow.setMovingRequest(moveRequest);
                break;

            case "hen":
                try {
                    mission.spendMoney(Hen.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    BuzzAnimation.play(gamePlayView.getMoneyLabel());
                    return;
                }
                Hen hen = new Hen(mission.getMap(), direction, position);
                mission.getMap().addToMap(hen);
                animalViewer = new AnimalViewer(hen, gamePlayView);
                hen.setAnimalViewer(animalViewer);

                TimeDependentRequest henProducingRequest = new DomesticsProducingRequest(mission, hen);
                mission.addTimeDependentRequest(henProducingRequest);
                hen.setProducingRequest(henProducingRequest);

                DomesticMovingRequest henMoveRequest = new DomesticMovingRequest(mission, hen);
                mission.addDomesticMovementRequest(henMoveRequest);
                hen.setMovingRequest(henMoveRequest);
                break;

            case "sheep":
                try {
                    mission.spendMoney(Sheep.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    BuzzAnimation.play(gamePlayView.getMoneyLabel());
                    return;
                }
                Sheep sheep = new Sheep(mission.getMap(), direction, position);
                mission.getMap().addToMap(sheep);
                animalViewer = new AnimalViewer(sheep, gamePlayView);
                sheep.setAnimalViewer(animalViewer);

                TimeDependentRequest sheepProducingRequest = new DomesticsProducingRequest(mission, sheep);
                mission.addTimeDependentRequest(sheepProducingRequest);
                sheep.setProducingRequest(sheepProducingRequest);

                DomesticMovingRequest sheepMoveRequest = new DomesticMovingRequest(mission, sheep);
                mission.addDomesticMovementRequest(sheepMoveRequest);
                sheep.setMovingRequest(sheepMoveRequest);

                break;

            case "cat":
                try {
                    mission.spendMoney(Cat.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    BuzzAnimation.play(gamePlayView.getMoneyLabel());
                    return;
                }
                Cat cat = new Cat(mission, direction, position);
                mission.getMap().addToMap(cat);
                animalViewer = new AnimalViewer(cat, gamePlayView);
                cat.setAnimalViewer(animalViewer);
                break;

            case "dog":
                try {
                    mission.spendMoney(Dog.getBuyCost());
                } catch (NotEnoughMoneyException e) {
                    BuzzAnimation.play(gamePlayView.getMoneyLabel());
                    return;
                }
                Dog dog = new Dog(mission.getMap(), direction, position);
                mission.getMap().addToMap(dog);
                animalViewer = new AnimalViewer(dog, gamePlayView);
                dog.setAnimalViewer(animalViewer);
                break;
        }
    }

    public static void produceProduct(Animal animal, Product product) {
        // TODO: 1/30/2019 check kon hamishe ghablesh aval position e product set shode bashe
        AnimalViewer animalViewer = animal.getAnimalViewer();
        Group root = animalViewer.getGamePlayView().getRoot();
        GamePlayView gamePlayView = animalViewer.getGamePlayView();

        ProductViewer productViewer = new ProductViewer(gamePlayView, product);
        product.setProductViewer(productViewer);
    }

    public static void dogBattle(Dog dog, ArrayList<Predator> predators) {
        AnimalViewer dogViewer = dog.getAnimalViewer();
        GamePlayView gamePlayView = dogViewer.getGamePlayView();
        Map map = dog.getMap();
        Position position = dog.getPosition();
        int row = position.getRow();
        int column = position.getColumn();

        gamePlayView.getRoot().getChildren().remove(dogViewer.getImageView());
        for (Predator predator : predators) {
            gamePlayView.getRoot().getChildren().remove(predator.getAnimalViewer().getImageView());
            try {
                map.discardAnimal(predator);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        AnimalAnimation.battle(dogViewer, gamePlayView.getCellCenterX(row, column),
                gamePlayView.getCellCenterY(row, column));
    }

    public static void predatorKill(AnimalViewer predatorViewer, ArrayList<Domestic> domestics) {
        Mission mission = predatorViewer.getGamePlayView().getMission();
        Map map = domestics.get(0).getMap();
        for (Domestic domestic : domestics) {
            try {
                map.discardAnimal(domestic);
                mission.removeDomesticMovingRequest(domestic.getMovingRequest());
            } catch (NotFoundException e) {
                e.printStackTrace();
                System.out.println("tu catch block exception mide");
            }
            map.print();
            AnimalViewer animalViewer = domestic.getAnimalViewer();
            ImageView imageView = animalViewer.getImageView();
            Image image = imageView.getImage();
            double frameWidth = image.getWidth() / AnimalAnimation.getFramesColumns(domestic, domestic.getDirection().getName());
            double frameHeight = image.getHeight() / AnimalAnimation.getFramesRows(domestic, domestic.getDirection().getName());
            Path path = new Path(new MoveTo(frameWidth / 2, frameHeight / 2),
                    new LineTo(0, -1500));
            PathTransition pathTransition = new PathTransition(Duration.millis(2000), path, imageView);
            pathTransition.play();
            pathTransition.setOnFinished(event -> {
                animalViewer.getGamePlayView().getRoot().getChildren().remove(imageView);
            });
        }
    }

    public static void domesticEat(Domestic domestic) {
        AnimalViewer animalViewer = domestic.getAnimalViewer();
        GamePlayView gamePlayView = animalViewer.getGamePlayView();
        Mission mission = gamePlayView.getMission();

        try {
            mission.removeDomesticMovingRequest(domestic);
            domestic.getAnimalViewer().playEatAnimation();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void cage(Predator predator) {
        AnimalViewer animalViewer = predator.getAnimalViewer();
        GamePlayView gamePlayView = animalViewer.getGamePlayView();
        Mission mission = gamePlayView.getMission();
        ImageView imageView = animalViewer.getImageView();
        Cage cage = new Cage(new Position(predator.getPosition().getRow(), predator.getPosition().getColumn()), predator);
        mission.getMap().addToMap(cage);
        try {
            mission.getMap().discardAnimal(predator);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        imageView.setOnMouseClicked(event -> {
        });
        animalViewer.cage(predator);
    }

    public static void upgradeCats(GamePlayView gamePlayView) {
        Mission mission = gamePlayView.getMission();

        ArrayList<Cat> catsInMap = mission.getMap().getCats();
        if (catsInMap.isEmpty()) {
            try {
                mission.increaseCatsBeginningLevel();
                mission.spendMoney(Cat.getCatUpgradeCost());
            } catch (MaxLevelExceededException e) {
                // TODO: 2/2/2019 kari nemishe kard inja
                return;
            } catch (NotEnoughMoneyException e) {
                BuzzAnimation.play(gamePlayView.getMoneyLabel());
                return;
            }
        } else {
            try {
                catsInMap.get(0).upgrade();
            } catch (NotEnoughMoneyException e) {
                BuzzAnimation.play(gamePlayView.getMoneyLabel());
                return;
            } catch (MaxLevelExceededException e) {
                // TODO: 2/2/2019 kari nemishe kard
                return;
            }
        }
    }
}
