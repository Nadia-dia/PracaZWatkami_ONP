import java.io.*;

public class ONP implements Serializable {
    private static final long serialVersionUID = 1L;
    private final TabStack stack = new TabStack();

    boolean czyPoprawneRownanie(String rownanie) {
        if (!rownanie.endsWith("=")) {
            throw new IllegalArgumentException("Błąd: równanie musi kończyć się znakiem '='");
        }

        int balans = 0;
        for (char znak : rownanie.toCharArray()) {
            if (znak == '(') {
                balans++;
            } else if (znak == ')') {
                balans--;
                if (balans < 0) {
                    throw new IllegalArgumentException("Błąd: niepoprawna liczba nawiasów");
                }
            }
        }

        if (balans != 0) {
            throw new IllegalArgumentException("Błąd: niepoprawna liczba nawiasów");
        }

        return true;
    }

    public String obliczOnp(String rownanie) {
        czyPoprawneRownanie(rownanie);
        String wynik = "";
        Double a, b = 0.0;

        try {
            for (int i = 0; i < rownanie.length(); i++) {
                char znak = rownanie.charAt(i);

                if (Character.isDigit(znak)) {
                    wynik += znak;
                    if (!(rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9')) {
                        stack.push(wynik);
                        wynik = "";
                    }
                } else if (znak == '=') {
                    String rezultat = stack.pop();
                    return rezultat;
                } else if (znak != ' ') {
                    try {
                        if (znak != '!') {
                            b = Double.parseDouble(stack.pop());
                            a = Double.parseDouble(stack.pop());
                        } else {
                            a = Double.parseDouble(stack.pop());
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("Błąd: Za mało operandów dla operatora '" + znak + "'");
                    }

                    switch (znak) {
                        case '+': stack.push(String.valueOf(a + b)); break;
                        case '-': stack.push(String.valueOf(a - b)); break;
                        case '*': case 'x': stack.push(String.valueOf(a * b)); break;
                        case '/':
                            if (b == 0) throw new ArithmeticException("Błąd: Dzielenie przez zero");
                            stack.push(String.valueOf(a / b));
                            break;
                        case '^': stack.push(String.valueOf(Math.pow(a, b))); break;
                        case 'r':
                            if (b < 0) throw new IllegalArgumentException("Stopień pierwiastka powinien być liczbą dodatnią");
                            if (b % 2 == 0 && a < 0) throw new IllegalArgumentException("Nie można obliczyć pierwiastka parzystego stopnia z liczby ujemnej");
                            stack.push(String.valueOf(Math.pow(a, 1.0 / b)));
                            break;
                        case '%':
                            if (b == 0) throw new ArithmeticException("Błąd: Dzielenie przez zero");
                            stack.push(String.valueOf(a % b));
                            break;
                        case '!':
                            if (a < 0 || a != Math.floor(a)) throw new IllegalArgumentException("Silnia wymaga liczby całkowitej nieujemnej");
                            stack.push(String.valueOf(silnia(a.intValue())));
                            break;
                        default:
                            throw new UnsupportedOperationException("Błąd: Nieobsługiwany operator '" + znak + "'");
                    }
                }
            }
        } catch (Exception e) {
            return "Błąd: " + e.getMessage();
        }
        return "0.0";
    }

    private int silnia(int a) {
        int wynik = 1;
        for (int i = 2; i <= a; i++) {
            wynik *= i;
        }
        return wynik;
    }


    /**
     * Metoda zamienia równanie na postać ONP
     *
     * @param rownanie równanie do zamiany na postać ONP
     * @return równanie w postaci ONP
     */
    public String przeksztalcNaOnp(String rownanie) {
        if (czyPoprawneRownanie(rownanie)) {
            String wynik = "";
            for (int i = 0; i < rownanie.length(); i++) {
                if (rownanie.charAt(i) >= '0' && rownanie.charAt(i) <= '9') {
                    wynik += rownanie.charAt(i);
                    if (!(rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9'))
                        wynik += " ";
                } else
                    switch (rownanie.charAt(i)) {
                        case ('+'):
                            ;
                        case ('-'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('x'):
                            ;
                        case ('*'):
                            ;
                        case ('/'):
                            ;
                        case ('%'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")
                                    && !stack.showValue(stack.getSize() - 1).equals("+")
                                    && !stack.showValue(stack.getSize() - 1).equals("-")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('^'): {
                            while (stack.getSize() > 0 && stack.showValue(stack.getSize() - 1).equals("^")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('r'):{
                            stack.push(String.valueOf(rownanie.charAt(i)));
                            break;
                        }
                        case ('!'):{
                            wynik += "! ";
                            break;
                        }
                        case ('('): {
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case (')'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            // zdjęcie ze stosu znaku (
                            stack.pop();
                            break;
                        }
                        case ('='): {
                            while (stack.getSize() > 0) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            wynik += "=";
                        }
                    }
            }
            return wynik;
        } else
            return "null";
    }
}

