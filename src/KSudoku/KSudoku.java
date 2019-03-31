package KSudoku;
/**
 * Class containing the main method of the KSudoku application.
 */
public class KSudoku {
    /**
     * Sudoku main method.
     * Creates Model, View and Controller of the MVC pattern.
     * @param args
     */
    public static void main (String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(view, model);
    }
}
