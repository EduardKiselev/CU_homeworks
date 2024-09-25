import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Calc {
    public static void main(String[] args) {
        char[] arr = StringToArrayConvertion(args);
        var StackNums = new ArrayList<Double>();
        var StackOperator = new ArrayList<Operations>();
        String ParseTmp = "";
        boolean flag_parse_num = false;
        for (int i = 0; i < arr.length; i++) {
            char CurrChar = arr[i];

//            Debug
//            System.out.print("=======" + CurrChar + "========");
//            System.out.print("  flag_parse_num = " + flag_parse_num);
//            System.out.print("  ParseTmp = " + ParseTmp);
//            System.out.print("  StackOperator = " + StackOperator);
//            System.out.println("  StackNums = " + StackNums);

            if (flag_parse_num) {
                if (ParseTmp.isEmpty()) {
                    ParseTmp += CurrChar;
                } else {
                    var StringWithNewChar = ParseTmp + CurrChar;
                    try {
                        Double.parseDouble(StringWithNewChar);
                        ParseTmp += CurrChar;
                    } catch (NullPointerException | NumberFormatException e) {
                        try {
                            StackNums.add(Double.parseDouble(ParseTmp));
                        } catch (NumberFormatException ex) {
                            System.out.println("Cant Parse Number");
                            return;
                        }
                        try {
                            CalcMultiplyDivision(StackOperator, StackNums); } catch (ArithmeticException er) {
                            return;
                        }
                        ParseTmp = "";
                        flag_parse_num = false;
                        i--;

                    }
                }
            } else {
                switch (CurrChar) {
                    case '+' -> StackOperator.add(Operations.PLUS);
                    case '-' -> StackOperator.add(Operations.MINUS);
                    case '*' -> StackOperator.add(Operations.MULTIPLY);
                    case '/' -> StackOperator.add(Operations.DIVISION);
                    case '(' -> StackOperator.add(Operations.OPENBRACKET);
                    case ')' -> {
                        try {
                            CalculateInBraskets(StackOperator, StackNums);
                        } catch (NoSuchElementException | IndexOutOfBoundsException er) {
                            System.out.println("Can't Parse: " + er);
                            return;
                        }
                        try {
                            CalcMultiplyDivision(StackOperator, StackNums); } catch (ArithmeticException e) {
                            return;
                        }
                    }
                    default -> {
                        return;
                    }
                }
                if (i < arr.length - 1 && (arr[i + 1] != '(' && CurrChar != ')')) flag_parse_num = true;
            }
        }
        String result = String.format("%.2f",StackNums.getFirst());
        System.out.println(result);
    }

    public static void CalculateInBraskets(
            ArrayList<Operations> StackOperator, ArrayList<Double> StackNums) {
        double res = 0;
        while (true) {
            if (StackOperator.getLast() == Operations.OPENBRACKET) {
                res = CalcOneOperation(Operations.PLUS, res, StackNums.removeLast());
                StackNums.add(res);
                StackOperator.removeLast();
                break;
            }
            res = CalcOneOperation(StackOperator.removeLast(), StackNums.removeLast(), res);
        }
    }

    public static char[] StringToArrayConvertion(String[] args) {
        StringBuilder str = new StringBuilder();
        str.append("(");
        for (String arg : args) {
            if (!arg.isEmpty()) {
                char[] arg_arr = arg.toCharArray();
                for(int i = 0; i < arg_arr.length; i++) {
                    var ch = arg_arr[i];
                    if (ch == 'f' || ch == 'd') {
                        System.out.println("ParseError - fd");
                        return new char[]{1,};
                    }
                    if (ch != ' ') {
                        str.append(ch);
                    }
                }
            }
        }
        str = new StringBuilder(str + ")");
        return str.toString().toCharArray();

    }

    public static void CalcMultiplyDivision(ArrayList<Operations> StackOperator, ArrayList<Double> StackNums) {
        while (!StackOperator.isEmpty() &&
                (StackOperator.getLast() == Operations.DIVISION ||
                        StackOperator.getLast() == Operations.MULTIPLY)) {
            var num1 = StackNums.getLast();
            StackNums.removeLast();
            var num2 = StackNums.getLast();
            StackNums.removeLast();

            try {
                StackNums.add(CalcOneOperation(StackOperator.getLast(), num2, num1));
                StackOperator.removeLast();
            } catch (ArithmeticException ae) {
                System.out.println("Cant divide by zero");
                throw ae;
            }
        }
    }

    public static double CalcOneOperation(Operations op, double num2, double num1) {
        switch (op) {
            case Operations.PLUS -> {
                return (num1 + num2);
            }
            case Operations.MINUS -> {
                return (num1 - num2);
            }
            case Operations.MULTIPLY -> {
                return (num1 * num2);
            }
            case Operations.DIVISION -> {
                if (Math.abs(num1 - 0) < Math.pow(10, -8)) {
                    throw new ArithmeticException("ZeroDivision");
                }
                return (num2 / num1);
            }
            default -> {
                return 0;
            }
        }
    }

    enum Operations {
        PLUS,
        MINUS,
        DIVISION,
        MULTIPLY,
        OPENBRACKET
    }
}
