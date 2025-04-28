/**
 * @author Sławek
 * Klasa implementująca stos za pomocą tablicy
 */
public class TabStack {
    private static final int MAX_SIZE = 40;
    private String[] stack = new String[MAX_SIZE];
    private int size = 0;
    /**
     * Metoda zdejmująca wartość ze stosu
     * @return wartość z góry stosu
     */
    public String pop() {
        try {
            if (size == 0) {
                throw new IllegalStateException("Błąd: Stos jest pusty, nie można zdjąć elementu");
            }
            size--;
            return stack[size];
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * metoda dokładająca na stos
     * @param a - wartość dokładana do stosu
     */

    public void push(String a) {
        try {
            if (size >= MAX_SIZE) {
                throw new StackOverflowError("Błąd: Przepełnienie stosu, za dużo wartości");
            }
            stack[size] = a;
            size++;
        } catch (StackOverflowError e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
    /**
     * Metoda zwraca tekstową reprezentację stosu
     */
    public String toString(){
        String tmp = "";
        for(int i = 0; i < size; i++)
            tmp += stack[i] + " ";
        return tmp;
    }
    /**
     * Metoda zwraca rozmiar stosu
     * @return size rozmiar stosu
     */
    public int getSize(){
        return size;
    }
    /**
     * Ustawia wartość stosu
     * @param i
     */
    public void setSize(int i){
        size = i;
    }
    /**
     * Metoda zwraca wartość z określonej pozycji stosu
     * @param i pozycja parametru do zobaczenia
     * @return wartość stosu
     */
    public String showValue(int i) {
        try {
            if (i >= size) {
                throw new IndexOutOfBoundsException("Błąd: Indeks poza zakresem stosu");
            }
            return stack[i];
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}
