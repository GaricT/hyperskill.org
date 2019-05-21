package machine;

import java.util.Scanner;

// Состояние кофе машины
enum State {OFF, MAIN, FILL, BUY}


interface Coffee{
    // Стоимость чашки
    int cost();
    // Ингридиенты
    int[] supplies();
}


interface CoffeeFabric{
    // Рецепт кофе
    Coffee getCoffee(int type);
    // Проверить допустимость выбора
    boolean chkType(int type);
    // Список доступных рецептов
    String[] nameList();
}


public class CoffeeMachine {

    public static void main(String[] args) {
        // Инициализируем хранилище
        Storage storage = new Storage(400, 540, 120, 9, 550);
        // Кофе рецепты
        CoffeeFabric coffeeFabric = new SimpleCoffeeFabric();
        // Логика управления
        CoffeeMachineLogic logic = new CoffeeMachineLogic(coffeeFabric, storage);
        // Пользовательский ввод
        Scanner scanner = new Scanner(System.in);

        while (logic.state != State.OFF){
            logic.showInfo();
            logic.userInput(scanner.next());
        }
    }
}

class CoffeeMachineLogic {
    State state = State.MAIN;
    private CoffeeFabric coffeeFabric;
    private Storage storage;
    private int fillTank = 0;

    CoffeeMachineLogic(CoffeeFabric coffeeFabric, Storage storage){
        this.coffeeFabric = coffeeFabric;
        this.storage = storage;
    }

    void showInfo(){
        // Отображаемое меню
        switch (state){
            case MAIN:
                mainRequest();
                break;

            case BUY:
                chooseCoffee();
                break;

            case FILL:
                fillStorage();
                break;

            default:
                break;
        }
    }

    private void mainRequest(){
        // Основное меню
        System.out.print("Write action (buy, fill, take, remaining, exit): ");
    }

    private void takeMoney(){
        // Инкассируем наличность
        int cash = storage.take();
        System.out.println("");
        System.out.printf("I gave you $%s\n", cash);
        System.out.println("");

    }


    private void chooseCoffee(){
        // Список доступного кофе
        String[] listCoffee = coffeeFabric.nameList();
        System.out.println("");
        System.out.print("What do you want to buy?\n");

        for (int i=0; i < listCoffee.length; i++){
            System.out.printf("%s - %s,\n",
                    i + 1,
                    listCoffee[i]);
        }
        System.out.print("back - to main menu: ");
    }

    private void printStorageState(){
        // Список доступных ресурсов
        String[] name = {"%s of water\n",
                "%s of milk\n",
                "%s of coffee beans\n",
                "%s of disposable cups\n",
                "$%s of money\n"};
        int[] count = storage.state();

        System.out.println("");
        System.out.println("The coffee machine has:");
        for (int i=0; i< name.length; i++){
            System.out.printf(name[i], count[i]);
        }
        System.out.println("");
    }

    private void fillStorage(){
        // Пополняемый ресурс
        String[] name = {"Write how many ml of water do you want to add: ",
                "Write how many ml of milk do you want to add: ",
                "Write how many grams of coffee beans do you want to add: ",
                "Write how many disposable cups of coffee do you want to add: "};
        System.out.print(name[fillTank]);
    }

    void userInput(String command){
        if (state == State.MAIN) {
            // Основное меню
            switch (command){
                case "exit":
                    state = State.OFF;
                    break;

                case "remaining":
                    printStorageState();
                    break;

                case "buy":
                    state = State.BUY;
                    break;

                case "fill":
                    state = State.FILL;
                    System.out.println("");
                    break;

                case "take":
                    takeMoney();
                    break;

                default:
                    break;
            }
        }
        else if (state == State.BUY) {
            // Меню выбора кофе
            if (!command.equals("back")) {
                // Готовим кофе
                int choice = Integer.parseInt(command);

                if (coffeeFabric.chkType(choice)) {
                    // Выбранный кофе доступен
                    if (storage.make(coffeeFabric.getCoffee(choice))) {
                        // Ингридиентов достаточно
                        System.out.println("I have enough resources, making you a coffee!");
                        System.out.println("");
                    } else {
                        // Ингридиентов не достаточно
                        System.out.printf("Sorry, not enough %s\n", storage.failureResources());
                        System.out.println("");
                    }
                }
            }
            else {
                System.out.println("");
            }
            state = State.MAIN;
        }
        else if (state == State.FILL){
            // Меню пополнения автомата
            int maxTank = 3;
            int[] resource = new int[maxTank + 1];
            int count = Integer.parseInt(command);
            resource[fillTank++] = count;
            storage.fill(resource);
            if (fillTank > maxTank){
                fillTank = 0;
                state = State.MAIN;
                System.out.println("");
            }
        }
    }
}

class Storage {
    // Хранилище ингридиентов
    private String[] listResources = {"water", "milk", "coffee beans", "disposable cups", "money"};
    private StringBuilder failureResources = new StringBuilder("");
    private int[] countResources = new int[listResources.length];

    Storage (int... supply){
        this.fill(supply);

    }

    void fill (int... supply){
        // Наполнение
        for (int i=0; i< supply.length && i<listResources.length; i++){
            countResources[i] += supply[i];
        }
    }

    int take (){
        // Инкассация денег
        int tmp = countResources[countResources.length - 1];
        countResources[countResources.length - 1] = 0;
        return tmp;
    }

    int[] state(){
        // Количество ингридиентов в хранилище
        return countResources.clone();
    }

    String failureResources(){
        // Ошибки при приготовлении кофе
        return failureResources.toString();
    }

    boolean make(Coffee coffee){
        // Готовка
        int[] supplies = coffee.supplies();
        int i = -1;
        boolean result = true;
        failureResources.setLength(0);

        // Проверяем ингридиенты
        for (int supply: supplies){
            i++;
            if (supply > countResources[i]){
                result = false;
                // Запоминаем чего не хватило
                if (failureResources.length()>0){
                    failureResources.append(", ");
                }
                failureResources.append(listResources[i]);
            }
        }
        // Проверяем чашки
        if (countResources[++i] < 1){
            result = false;
            // Запоминаем чего не хватило
            if (failureResources.length()>0){
                failureResources.append(", ");
            }
            failureResources.append(listResources[i]);
        }

        if (result){
            // Увеличиваем деньги
            countResources[i + 1] += coffee.cost();
            // Уменьшаем стаканчики
            countResources[i--]--;
            // Уменьшаем ингридиенты
            for (; i>=0; i--){
                countResources[i] -= supplies[i];
            }
        }

        return result;
    }
}

class Espresso implements Coffee {
    // Рецепт эспрессо

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public int[] supplies() {
        return new int[] {250, 0, 16};
    }
}

class Latte implements Coffee {
    // Рецепт латте

    @Override
    public int cost() {
        return 7;
    }

    @Override
    public int[] supplies() {
        return new int[] {350, 75, 20};
    }
}

class Cappuccino implements Coffee {
    // Рецепт капучино

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public int[] supplies() {
        return new int[] {200, 100, 12};
    }
}

class SimpleCoffeeFabric implements CoffeeFabric {
    @Override
    public Coffee getCoffee(int type) {
        Coffee newCup = null;
        switch (type){
            case 1:
                newCup = new Espresso();
                break;

            case 2:
                newCup = new Latte();
                break;

            case 3:
                newCup = new Cappuccino();
                break;

            default:
                break;
        }
        return newCup;
    }

    @Override
    public boolean chkType(int type) {
        return type >0 && type <= 3;
    }

    @Override
    public String[] nameList() {
        return new String[]{"espresso", "latte", "cappuccino"};
    }
}

