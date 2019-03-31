package KSudoku;

import java.util.*;

/**
 * Model class of the MVC pattern in KSudoku.
 * Stores all solutions, corresponding mask arrays.
 * Contains package methods to take a new solution and mask array.
 */
public class Model {
    private final int SOLUTIONS_COUNT = 3;  /* constant number of available solutions */
    private char[][][] solutionsArray;      /* three 9x9 tables, stored solutions */
    private boolean[][][] visibilityArray;  /* which digits are initially seen in a solution */
    private int currentSolutionNumber;      /* which solution is currently on the table */

    private Random rand;                    /* random numbers generator */

    /**
     * Model class default constructor.
     * Initializes solutions array and array of mask arrays.
     * Randomly chooses current solution.
     */
    public Model() {
        rand = new Random();
        currentSolutionNumber = rand.nextInt(SOLUTIONS_COUNT);

        solutionsArray = new char[][][]{
                {
                        {'7', '1', '4', '3', '9', '5', '2', '6', '8'},
                        {'8', '6', '9', '2', '7', '4', '1', '3', '5'},
                        {'5', '2', '3', '1', '6', '8', '7', '4', '9'},
                        {'4', '7', '6', '3', '5', '2', '9', '8', '1'},
                        {'2', '9', '1', '6', '8', '7', '3', '5', '4'},
                        {'3', '5', '8', '4', '9', '1', '6', '2', '7'},
                        {'1', '3', '5', '8', '2', '9', '4', '7', '6'},
                        {'6', '4', '7', '5', '1', '3', '8', '9', '2'},
                        {'9', '8', '2', '7', '4', '6', '5', '1', '3'}
                },
                {
                        {'9', '6', '1', '2', '5', '7', '8', '3', '4'},
                        {'3', '5', '4', '1', '9', '8', '7', '6', '2'},
                        {'8', '7', '2', '4', '6', '3', '5', '1', '9'},
                        {'2', '9', '3', '7', '4', '5', '1', '8', '6'},
                        {'4', '8', '6', '9', '3', '1', '2', '7', '5'},
                        {'7', '1', '5', '6', '8', '2', '9', '4', '3'},
                        {'5', '2', '7', '3', '1', '6', '4', '9', '8'},
                        {'6', '4', '8', '5', '7', '9', '3', '2', '1'},
                        {'1', '3', '9', '8', '2', '4', '6', '5', '7'}
                },
                {
                        {'1', '2', '3', '9', '5', '8', '6', '4', '7'},
                        {'9', '6', '4', '1', '3', '7', '5', '8', '2'},
                        {'8', '5', '7', '6', '4', '2', '1', '3', '9'},
                        {'5', '9', '8', '3', '6', '1', '2', '7', '4'},
                        {'2', '4', '1', '7', '9', '5', '8', '6', '3'},
                        {'7', '3', '6', '8', '2', '4', '9', '5', '1'},
                        {'4', '7', '2', '5', '8', '9', '3', '1', '6'},
                        {'3', '1', '5', '2', '7', '6', '4', '9', '8'},
                        {'6', '8', '9', '4', '1', '3', '7', '2', '5'}
                }
        };

        visibilityArray = new boolean[][][]{
                {
                        {false, false, true, false, true, false, true, true, true},
                        {false, false, false, false, false, false, false, true, false},
                        {true, true, false, false, true, false, true, true, false},
                        {false, false, false, false, false, false, true, true, true},
                        {false, false, false, true, true, true, false, false, false},
                        {false, false, false, true, false, false, true, true, true},
                        {false, true, true, true, true, false, false, false, true},
                        {false, true, true, false, false, true, false, false, false},
                        {false, true, true, false, false, false, true, true, true}
                },
                {
                        {false, true, true, true, false, true, true, false, false},
                        {true, true, true, true, true, true, true, true, true},
                        {true, true, false, true, true, true, false, false, false},
                        {true, false, true, false, true, true, true, false, false},
                        {true, true, true, true, true, true, false, true, true},
                        {true, true, false, false, true, false, true, true, false},
                        {true, false, true, false, true, false, true, true, false},
                        {false, false, false, false, false, false, true, false, false},
                        {true, false, false, true, true, true, false, false, false}
                },
                {
                        {false, false, false, true, false, true, false, true, false},
                        {true, false, false, true, false, false, true, false, false},
                        {false, false, false, true, true, false, false, true, true},
                        {true, true, true, true, false, true, true, false, false},
                        {false, false, true, false, true, true, true, true, true},
                        {true, false, true, false, false, true, false, true, true},
                        {false, true, true, false, false, false, false, true, false},
                        {true, true, false, false, false, false, false, true, true},
                        {true, true, false, false, false, false, false, true, true}
                }
        };
    }

    /**
     * Sets random solution as the current solution and returns corresponding table.
     * @return table of digits for the corresponding solution number.
     */
    char[][] getNewSolution() {

        /* Choose randomly which array to return. *
         * Make sure it's not the same as the previous one. */
        int oldSolutionNumber = currentSolutionNumber;
        do
            currentSolutionNumber = rand.nextInt(SOLUTIONS_COUNT);
        while
        (currentSolutionNumber == oldSolutionNumber);

        /* Create an empty 9x9 char array and fill it with a solution
         * the currentSolutionNumber corresponds to. */
        char[][] result = new char[9][9];

        for (int i = 0; i < 9; ++i)
            for (int j = 0; j < 9; ++j)
                result[i][j] = solutionsArray[currentSolutionNumber][i][j];

        /* Return the array. */
        return result;
    }

    /**
     * Returns 9x9 boolean array that indicates which digits of the solution
     * are shown to the player.
     * @return boolean array of masks.
     */
    boolean[][] getVisibilityArray() {
        boolean[][] result = new boolean[9][9];

        for (int i = 0; i < 9; ++i)
            for (int j = 0; j < 9; ++j)
                result[i][j] = visibilityArray[currentSolutionNumber][i][j];

        /* return the array */
        return result;
    }
}