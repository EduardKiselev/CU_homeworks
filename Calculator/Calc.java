import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Calc {
    public static void main(String[] args) {
        char[] arr = stringToArrayConvertion(args);
        var StackNums = new ArrayList<Double>();
        var StackOperator = new ArrayList<Operations>();
        String ParseTmp = "";
        for (int i=0;i<arr.length;i++) {
            char CurrChar = arr[i];
            if(CurrChar=='f' || CurrChar=='d'){
                System.out.println("ParseError");
                return;
            }
            if (CurrChar == '*' || CurrChar == '-' || CurrChar == '+' || CurrChar == '/' || CurrChar == '(' || CurrChar == ')') {
                if (!ParseTmp.isEmpty()) {
                    try {
                        var num = Double.parseDouble(ParseTmp);
                        StackNums.add(num);
                        ParseTmp = "";
                    } catch (NullPointerException | NumberFormatException e) {
                        System.out.println("Can't parse: '" + ParseTmp + "'");
                        return;
                    }
                }
                switch (CurrChar) {
                    case '+' -> StackOperator.add(Operations.PLUS);
                    case '-' -> StackOperator.add(Operations.MINUS);
                    case '*' -> StackOperator.add(Operations.MULTIPLY);
                    case '/' -> StackOperator.add(Operations.DIVISION);
                    case '(' -> StackOperator.add(Operations.OPENBRACKET);
                    case ')' -> {
                        try{
                            calculateInBraskets(StackOperator, StackNums);
                    //        System.out.println(StackNums);
                    //        System.out.println(StackOperator);
                        }
                        catch (NoSuchElementException er) {
                            System.out.println("Can't Parse - NoSuchElementException");
                            return;
                        }
                    }
                    default -> {
                    }
                }
                continue;
            }
            ParseTmp += CurrChar;
        }
        System.out.print("Result is: ");
        System.out.println(StackNums.getFirst());
    }

    public static void calculateInBraskets(ArrayList<Operations> StackOperator, ArrayList<Double> StackNums) {
        double res = 0;
        int sdvig = 0;
        while (StackOperator.get(StackOperator.size() - 1 - sdvig) != Operations.OPENBRACKET) {
            if (StackOperator.get(StackOperator.size() - 1 - sdvig) == Operations.MULTIPLY ||
                    StackOperator.get(StackOperator.size() - 1 - sdvig) == Operations.DIVISION) {
                var CurrIndex = StackNums.size() - 2 - sdvig;
                var NextIndex = StackNums.size() - 1 - sdvig;
                var OperatorIndex = StackOperator.size() - 1 - sdvig;
                try {
                    StackNums.set(CurrIndex,
                            calcOneOperation(StackOperator.get(OperatorIndex),
                                    StackNums.get(CurrIndex),
                                    StackNums.get(NextIndex)));
                } catch (ArithmeticException ae) {
                    System.out.println("Cant divide by zero");
                    return;
                }
                StackNums.remove(NextIndex);
                StackOperator.remove(OperatorIndex);
            } else {
                sdvig++;
            }
        }
        while (true) {
            if (StackOperator.getLast() == Operations.OPENBRACKET) {
                res = calcOneOperation(Operations.PLUS, res, StackNums.removeLast());
                StackNums.add(res);
                StackOperator.removeLast();
                break;}
            res = calcOneOperation(StackOperator.removeLast(), StackNums.removeLast(), res);
        }
    }

    public static char[] stringToArrayConvertion(String[] args){
        StringBuilder str = new StringBuilder();
        for (String arg : args) {
            if(!arg.isEmpty()){
                char[] arg_arr = arg.toCharArray();
                for (char ch: arg_arr) {
                    if (ch != ' ') {
                        str.append(ch);
                    }
                }
            }
        }
        str = new StringBuilder("(" + str + ")");
        System.out.println(str);
        return str.toString().toCharArray();
    }

    public static double calcOneOperation(Operations op, double num2, double num1) {
        switch (op) {
            case Operations.PLUS -> {return (num1 + num2);}
            case Operations.MINUS -> {return (num1 - num2);}
            case Operations.MULTIPLY -> {return (num1 * num2);}
            case Operations.DIVISION -> {
                if(Math.abs(num1-0)<Math.pow(10,-8)) {
                    throw new ArithmeticException("ZeroDivision");}
                return (num2 / num1);
            }
            default -> {return 0;}
        }
    }
}

enum Operations {
    PLUS,
    MINUS,
    DIVISION,
    MULTIPLY,
    OPENBRACKET};