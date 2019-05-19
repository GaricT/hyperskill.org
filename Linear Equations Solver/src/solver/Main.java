package solver;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        String inFile = "Linear Equations Solver\\src\\solver\\in.txt";
        String outFile= "Linear Equations Solver\\src\\solver\\out.txt";

        // Парсим входящие параметры
        for (int i=0; i<args.length; i++){
            switch (args[i]){
                case "-in":
                    inFile = args[i+1];
                    break;
                case "-out":
                    outFile = args[i+1];
                    break;
                default:
                    break;
            }
        }
        // Читаем матрицу с коэффициентами
         Complex[][] matrix = matrixFromFile(inFile);
        if (matrix.length != 0){
            // Создаем класс линейных уравнений
            LinearEquation lEquation = new LinearEquation(matrix);
            // Находим решение
            lEquation.solve();
            // Выводим решение в файл
            answerToFile(outFile, lEquation);

        }
   }

    static Complex[][] matrixFromFile(String fileName){
        // Чтение коэффициентов из файла
        File file = new File(fileName);
        Complex[][] row = {{}};

        try (Scanner scanner = new Scanner(file)){
            // Количество переменных
            int countVar = scanner.nextInt() + 1;
            // Количество строк
            int countRow = scanner.nextInt();
            // Матрица с коэффициентами
            row = new Complex[countRow][countVar];

            // Читаем данные
            for (int i=0; i < row.length; i++){
                for (int j=0; j < row[0].length; j++){
                    row[i][j] = Complex.parseString(scanner.next());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + fileName);
         }
         return row;
    }

    static void answerToFile(String fileName, LinearEquation lSolution){
        // Запись решения в файл
        File file = new File(fileName);

        // Выводим результат в файл
        try (FileWriter outWrite = new FileWriter(fileName)){
            switch (lSolution.state){
                case -1:
                    outWrite.write("No solutions\n");
                    break;

                case 0:
                    outWrite.write("Infinitely many solutions\n");
                    break;

                default:
                    for (Complex res: lSolution.answer){
                        outWrite.write(res +"\n");
                    }
                    break;

            }
        } catch (IOException e) {
            System.out.println("No file found: " + fileName);
        }
    }
}

class LinearEquation {
    // Класс линейных уравнений
    Complex[][] rows;
    Complex[] answer;
    int state = 1;

    LinearEquation(Complex[][] args){
        rows = args.clone();
        answer = new Complex[rows[0].length - 1];
    }

    void solve(){
        // Номер текущий переменной
        int numVar = answer.length - 1;
        // Номер свободного члена
        int lastCol = rows[0].length - 1;
        int pos = 0;

        // Приводим к диагональному виду
        for (int i=0; i< rows.length; i++){
            while (true){
                pos = Math.min(pos, lastCol - 1);
                // Находим строку с ненулевым коэффициентом
                this.sortLeadZero(i, pos);
                // Приводим коэффициент у переменной к 1
                this.argToOne(i,pos);
                // Вычитаем текущую строку из нижестоящих
                this.subtract(i, pos);

                // Находим "ступеньки" в решении
                if (rows[i][pos++].equals(0) && pos != lastCol){
                    continue;
                }
                break;
            }
        }

        for (int i=rows.length -1; i>=0 && state > 0; i--){
            if (!this.isZero(i)){

                pos = this.nonZeroPos(i);
                if (pos == lastCol){
                    // Не имеет решения
                    state = -1;
                }
                else if (numVar != pos){
                    // в каких то строках есть нулевые коэффициенты, получаем "лесенку"
                    state = 0;
                }
                else {
                    numVar--;
                    answer[pos] = rows[i][lastCol];
                    for (int j=pos + 1; j < rows[0].length - 1; j++){
                        answer[pos] = answer[pos].sub(rows[i][j].mult(answer[j]));
                    }
                }

            }
        }
        if (state > 0 && numVar != -1){
            state = 0;
        }


    }

    int nonZeroPos(int row){
        int pos = -1;
        for (int i=0; i< rows[0].length && pos < 0; i++){
            if (!rows[row][i].equals(0)){
                pos = i;
            }
        }
        return pos;
    }

    void sortLeadZero(int row, int pos){

        if (rows[row][pos].equals(0)){
            Complex[] tmp = rows[row];
            // Нулевой элемент на диагонали, поменять строки
            for (int i = row+1; i < rows.length; i++){
                if (!rows[i][pos].equals(0)){
                    rows[pos] = rows[i];
                    rows[i] = tmp;
                    break;
                }
            }
        }

    }

    void argToOne(int row, int pos){
        // Делим всю строку на число
         Complex tmp = this.rows[row][pos];
        if (!tmp.equals(0) && !tmp.equals(1)){
            for (int i = pos; i < rows[pos].length; i++){
                rows[row][i] = rows[row][i].div(tmp);
            }
        }
    }

    void subtract(int row, int pos){
        // вычитаем строку row из строк находящихся ниже
        Complex mult;
        for (int i= row + 1; i< rows.length; i++){
            mult = rows[i][pos];
            for (int j = pos; j< rows[0].length; j++){
                rows[i][j] = rows[i][j].sub(rows[row][j].mult(mult));
            }
        }
    }

    boolean isZero(int row){
        // В строке одни нули
        boolean result = true;
        for(Complex i: rows[row]){
            if (!i.equals(0)){
                result = false;
                break;
            }
        }
        return result;
    }

    public String toString(){
        String[] str = new String[rows.length];
        for(int i=0; i<rows.length; i++){
            str[i] = Arrays.toString(rows[i]);
        }
        return String.join("\n", str);
    }
}

class Complex {
    float real; // Действительная часть числа
    float imag; // Комплексная

    Complex(float real, float imag){
        this.real = real;
        this.imag = imag;
    }

    Complex con(){
        // Сопряженное
        return new Complex(real, -1 * imag);
    }

    Complex add(Complex a){
        // Сложение
        return new Complex(real + a.real, imag + a.imag);
    }

    Complex sub(Complex a){
        // Вычитание
        return new Complex(real - a.real, imag - a.imag);
    }

    Complex mult(Complex a){
        // Умножение
        return new Complex(real*a.real - imag*a.imag, real*a.imag + imag*a.real);
    }

    Complex div(Complex a){
        //Деление
        Complex top = this.mult(a.con());
        float bottom = a.real * a.real + a.imag * a.imag;
        return new Complex(top.real / bottom,top.imag / bottom);
    }

    boolean equals(Complex a){
        return real == a.real && imag == a.imag;
    }

    boolean equals(float a){
        return real == a && imag == 0;
    }

    @Override
    public String toString() {
        String str = "";
        if (real != 0){
            str += real;
        };
        if (imag>0){
            str += (real!=0 ? "+" : "") + imag + "i";
        }
        else if (imag<0){
            str += imag + "i";
        }
        else if (imag == 0 && real == 0){
            str = "0";
        }
        return str;
    }

    public static Complex parseString(String str){
        String[] part = new String[]{"0", "0"};
        int pos;

        if (str.matches(".+[+-].*i")){
            // Число содержит действительную и мнимую часть
            pos = Math.max(str.lastIndexOf("+"), str.lastIndexOf("-"));
            part[0] = str.substring(0, pos);
            part[1] = str.substring(pos, str.length() - 1);

        }
        else if (str.endsWith("i")){
            //число содержит только мнимую часть
            part[1] = str.substring(0, str.length() - 1);
        }
        else {
            //число содержит только действительную часть
            part[0] = str;
        }
        if (part[1].matches("[+-]") || part[1].equals("")){
            part[1] += "1";
        }
        try {
            return new Complex(Float.parseFloat(part[0]), Float.parseFloat(part[1]));
        }
        catch (NumberFormatException e){
            System.out.println(str);
            throw e;
        }
    }
}