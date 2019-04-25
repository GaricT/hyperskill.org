package tictactoe;

import java.util.*;

public class Main {

    public class XOGame {
        String[] field;
        Scanner s;
        Random rnd;
        String[] marker = {"X", "O"};
        String[] players;

        XOGame(String[] players){
            this.s = new Scanner(System.in);
            this.rnd = new Random();
            // Создаем игровое поле
            this.field = this.emptyField();
            // Типы игроков
            this.players = players;
        }

        // Новое поле
        private String[] emptyField() {
            String[] field = new String[9];
            Arrays.fill(field, " ");
            return field;

        }

        // Генератор последовательности массивов [0, 1, 2], [1, 2, 0], [2, 0, 1]...
        private int[] loop(int start) {
            return new int[]{(start++ % 3), (start++ % 3), (start % 3)};
        }

        // Список пустых ячеек
        private int[] freeCells(String[] field){
            int[] tmp = new int[field.length];
            int count = 0;

            for (int i=0; i < field.length; i++){
                if (field[i].equals(" ")){
                    tmp[count++] = i;
                }
            }
            return Arrays.copyOf(tmp, count);
        }

        private int idMarker(String marker){
            return (marker.equals("X"))? 0 : 1;
        }

        // В ряду находится 2 одинаковых заданных маркера и пустая ячейка
        private boolean doubleChk(String[] field, int x1, int x2, int x3, String marker){
            return (field[x1].equals(" ") &&
                    field[x2].equals(marker) &&
                    field[x3].equals(marker));
        }

        // Список ячеек, для победы в 1 ход для заданного маркера
        private int[] doubleCells(String[] field, String marker){
            int[] tmp = new int[field.length];
            int count = 0;
            int[] x;

            for (int i=0; i<3; i++){
                x = loop(i);
                // Диаганальные пары
                if (doubleChk(field, x[0] * 4,x[1] * 4, x[2] * 4, marker)) {
                    tmp[count++] = x[0] * 4;
                }
                else if (doubleChk(field, ++x[0] * 2,++x[1] * 2, ++x[2] * 2, marker)) {
                    tmp[count++] = x[0] * 2;
                }

                for (int j=0; j<3; j++){
                    // Горизонтальные пары
                    x = loop(j);
                    if (doubleChk(field, x[0] + 3 * i,x[1] + 3 * i, x[2] + 3 * i, marker)) {
                        tmp[count++] = x[0] + 3 * i;
                    }
                    // Вертикальные пары
                    else if (doubleChk(field, x[0] * 3 + i,x[1] * 3 + i, x[2] * 3 + i, marker)) {
                        tmp[count++] = x[0] * 3 + i;
                    }
                }
            }
            return Arrays.copyOf(tmp, count);
        }

        // Проверяем что в трех ячейках находится 1 маркер
        private boolean lineChk(String[] field, int x1, int x2, int x3){
            return (field[x1].equals(field[x2]) &&
                    field[x1].equals(field[x3]) &&
                    !field[x1].equals(" "));
        }

        // Получаем текстовое описание состояния
        private String stateTxt(int state){
            String result = "";
            if (state >= marker.length)
                result = "Draw";
            else if (state >= 0)
                result = marker[state].concat(" wins");
            return result;
        }

        // Ход компьютера: Легкий режим
        private String[] aiEasy(String[] field, int id){
            field = field.clone();
            // Доступные для хода ячейки
            int[] cellsList = freeCells(field);
            // Выбираем случайную пустую клетку
            int pos = cellsList[rnd.nextInt(cellsList.length)];
            // Ходим
            field[pos] = marker[id];

            return field;
        }

        // Ход компьютера: Нормальный режим
        private String[] aiMedium(String[] field, int id){
            field = field.clone();
            int[] currentPlayer = doubleCells(field, marker[id]);
            int[] secondPlayer = doubleCells(field, marker[(id + 1) % 2]);

            if (currentPlayer.length > 0) {
                // Выигрываем за 1 ход
                field[currentPlayer[0]] = marker[id];
            }
            else if (secondPlayer.length > 0) {
                // Блокируем второму игроку выигрышь за 1 ход
                field[secondPlayer[0]] = marker[id];
            }
            else {
                // Случайная ячейка
                field = aiEasy(field, id);
            }
            return field;
        }

        // Ход компьютера: Сложный режим
        private String[] aiHard(String[] field, int id){
            int pos;
            int[] result;
            // Доступные для хода ячейки
            int[] cellsList = freeCells(field);

            field = field.clone();
            if (cellsList.length == 9){
                // Первый ход всегда случаен
                // Выбираем случайную пустую клетку
                pos = cellsList[rnd.nextInt(cellsList.length)];
                // Ходим
                field[pos] = marker[id];
            }
            else {
                result = minimax(field.clone(),id, 0);
                field[result[1]] = marker[id];
            }
            return field;
        }

        private int[] minimax(String[] field, int id, int deep){
            int[] cells = freeCells(field);
            String[] tmpFiled;
            int state;
            int idMarker = (id + deep)  % 2;
            int best_pos = -1;
            int best_w = 0;
            int weight;

            for (int pos: cells){
                tmpFiled = field.clone();
                tmpFiled[pos] = marker[idMarker];

                state = chkState(tmpFiled);
                if (state < 0) {
                    // Игра продолжается
                    state = minimax(tmpFiled, id, (deep + 1) % 2)[0];
                }
                // TODO: Добавить веса для разного уровня вложенности. Победа в 1 ход != в 10 ходов
                switch (deep) {
                    case 0:
                        // Максимальная выборка, собственный ход
                        if (state == id) {
                            // Победа
                            return new int[]{10, pos};
                        }
                        else {
                            // 3 - Ничья
                            weight = 0;
                            if (best_pos == -1 || best_w < weight){
                                best_w = weight;
                                best_pos = pos;
                            }
                        }
                        break;
                    case 1:
                        // Минимальная выборка, ход противника
                        if (state == idMarker) {
                            // Победа противника (наш проигрышь)
                            return new int[]{-10, pos};
                        }
                        else {
                            // 3 - Ничья
                            weight = 0;
                            if (best_pos == -1 || best_w > weight){
                                best_w = weight;
                                best_pos = pos;
                            }
                        }
                        break;
                }

            }
            return new int[]{best_w, best_pos};
        }

        // Ход игрока
        private String[] userTurn(String[] field, int id){
            int x;
            int y;
            field = field.clone();

            while (true){
                System.out.print("Enter the coordinates: ");
                try {
                    x = s.nextInt();
                    y = s.nextInt();
                }
                catch (InputMismatchException e){
                    System.out.println("You should enter numbers!");
                    s.next();
                    continue;
                }
                if (x < 1 || x > 3 || y < 1 || y > 3 ) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    continue;
                }
                x = x - 1 + (3 - y)*3;

                if (field[x].equals(" ")){
                    field[x] = marker[id];
                    break;
                }
                else
                    System.out.println("This cell is occupied! Choose another one!");
            }
            return field;
        }

        // Вывод игрового поля на экран
        void prnField(String[] field){
            System.out.println("---------");
            for (int i =0; i < field.length; i += 3){
                System.out.printf("| %s %s %s |\n",
                        field[i],
                        field[i + 1],
                        field[i + 2]);
            }
            System.out.println("---------");
        }

        // Ход игрока id
        void playerTurn(int id){
            switch (players[id]) {
                case "user":
                    this.field = userTurn(this.field, id);
                    break;

                case "easy":
                    System.out.println("Making move level \"easy\"");
                    this.field = aiEasy(this.field, id);
                    break;

                case "medium":
                    System.out.println("Making move level \"medium\"");
                    this.field = aiMedium(this.field, id);
                    break;

                case "hard":
                    System.out.println("Making move level \"hard\"");
                    this.field = aiHard(this.field, id);
                    break;
            }
        }

        // Обновляем состояние поля
        int chkState(String[] field){
            int result = -1; // Есть доступные ячейки

            for (int i = 0; i < 3  && result == -1; i++){
                // Вертикальные строки
                if (lineChk(field, i, i +3, i + 6))
                    result = idMarker(field[i]);
                // Горизонтальные строки
                else if (lineChk(field, i * 3, i * 3 + 1, i * 3 + 2))
                    result = idMarker(field[i * 3]);

            }
            if (result == -1) {

                // '\' - диагональ
                if (lineChk(field, 0,4,8))
                    result = idMarker(field[0]);
                // '/' - диагональ
                else if (lineChk(field, 2,4,6))
                    result = idMarker(field[2]);
                // Нет пустых ячеек: конец игры
                else if (freeCells(field).length == 0)
                    result = 3;
            }
            return result;
        }
    }

    public static void main(String[] args) {
        String availCommand = "user|easy|medium|hard";
        int userId;
        int state;
        String[] commands;
        Main.XOGame game;
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.print("Input command: ");
            commands = scanner.nextLine().split(" ");

            switch (commands[0]) {
                case "exit":
                    return;

                case "start":
                    if (commands.length == 3 &&
                            commands[1].matches(availCommand) &&
                            commands[2].matches(availCommand))
                    {
                        game = new Main().new XOGame(Arrays.copyOfRange(commands, 1,3));
                        // Новое поле
                        game.field = game.emptyField();
                        // Отрисовка поля
                        game.prnField(game.field);
                        userId = 0;
                        state = -1;

                        while (state == -1){
                            // Ход
                            game.playerTurn(userId);
                            // Отрисовка поля
                            game.prnField(game.field);
                            // Смена игрока
                            userId = ++userId % 2;
                            // Состояние игры
                            state = game.chkState(game.field);
                        }
                        System.out.println(game.stateTxt(state));
                    }
                    else
                    {
                        System.out.println("Bad parameters!");
                    }
                    break;
            }
        }
    }
}
