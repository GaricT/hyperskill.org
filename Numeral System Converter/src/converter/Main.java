package converter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NumberConv number = new NumberConv();

        if (number.setRadix(scanner.nextLine())
                && number.setNumber(scanner.nextLine())
                && number.setRadix(scanner.nextLine())){
            System.out.println(number);
        }
        else {
            System.out.println("error");

        }
    }
}

class NumberConv{
    private long intPart;
    private double fracPart;
    private int radix;
    private boolean isInt = true;
    private int accuracy = 5;

    boolean setRadix(String radix) {
        // Установка основания
        boolean result = false;

        // Проверяем корректность основания
        if (this.chkRadix(radix)) {
            setRadix(Integer.parseInt(radix));
            result = true;
        }
        return result;
    }

    boolean setRadix(int radix) {
        // Установка основания
        boolean result = false;

        // Проверяем корректность основания
        if (this.chkRadix(radix)) {
            this.radix = radix;
            result = true;
        }
        return result;
    }

    private boolean chkRadix(String val){
        // Проверка основания (строка)
        boolean isOk = false;

        // Содержит только числа
        if (val.matches("\\d+")){
            isOk = this.chkRadix(Integer.parseInt(val));
        }
        return isOk;
    }

    private boolean chkRadix(int val){
        // Проверка основания (число)
        boolean isOk = false;
        // Находится в нужном интервале
        if (val> 0 && val < 37){
            isOk = true;
        }
        return isOk;
    }

    public boolean setNumber(String number) {
        // Установка основания
        boolean result = false;

        // Проверяем корректность основания
        // Разбиваем на 2 части
        String[] val = number.split("\\.");

        // Проверяем на количество разделителей
        if (val.length < 3){

            // Проверяем целую часть
            if (chkNumber(val[0])){
                intPart = this.toLong(val[0]);

                // Проверяем дробную часть
                if (val.length > 1) {
                    if (chkNumber(val[1])) {
                        // Число дробное
                        fracPart = this.parseFrac(val[1]);
                        isInt = false;
                        result = true;
                    }
                }
                else {
                    result = true;
                }
            }
        }
        return result;
    }

    private long toLong(String strVal){
        // Преобразуем число в десятичное
        long val = 0;
        if (radix == 1){
            val = strVal.length();
        }
        else if (radix > 0){
            val = Long.parseLong(strVal, radix);
        }
        return val;
    }

    private double parseFrac(String strVal){
        // Преобразуем дробную часть в десятичное
        double frac = 0;
        double digit;

        if (radix > 0){
            for (int i = 0; i < strVal.length(); i ++){
                // Вырезаем 1 цифру
                digit = this.toLong(strVal.substring(i, i+1));
                // Переводим в 10 систему
                frac += digit / Math.pow(radix, i +1);
            }
        }
        return frac;
    }

    private String toNumber(long val){
        // Преобразуем из десятичного в число по другому основанию
        String strVal = "";
        if (radix == 1){
            strVal = "1".repeat((int)val);
        }
        else if (radix > 0){
            strVal = Long.toString(val, radix);
        }
        return strVal;
    }

    private String toFrac(double val){
        // Преобразуем из десятичного в число по другому основанию
        String strVal = "";
        long tmpVal;

        if (radix > 0){
            for (int i = 0; i < accuracy; i ++){
                val *= radix;
                tmpVal = (long)val;
                strVal += this.toNumber(tmpVal);
                val -= tmpVal;
            }
        }
        return strVal;
    }

    public String toString(){
        String tmpVal;
        if (radix == 0){
            // Ошибка
            tmpVal = "error";
        }
        else {
            // Вывод числа
            tmpVal = this.toNumber(intPart);
            if (!isInt && radix != 1){
                tmpVal = tmpVal + "." + this.toFrac(fracPart);
            }
        }
        return tmpVal;
    }

    private boolean chkNumber(String val){
        String mask;
        if (radix == 1){
            mask = "1";
        }
        else {
            mask = "0123456789abcdefghijklmnopqrstuvwxyz";
            mask = mask.substring(0, radix);
        }
        return val.matches("[" + mask + "]+");
    }
}
