package KSudoku;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Controller class from the MVC pattern in KSudoku.
 * Reacts to player's actions, i.e. adds listeners to the View components
 * and takes proper action when and event occurs.
 * Takes new solutions from the Model.
 */
public class Controller {
    private View theView;                       /* the KSudoku View */
    private Model theModel;                     /* the KSudoku Model */
    private char[][] correctTable;              /* properly filled table */
    private ButtonListener buttonListener;      /* View's buttons listener */
    private ColorListener colorListener;        /* View's color check box listener */
    private FieldListener[][] gridListeners;    /* View's table fields listener */

    private boolean colorFields;                /* is color fields option currently turned on */

    /**
     * Controller class default constructor.
     * Adds components listeners in the given View, gets a new solution
     * from the given Model and shares it with the View.
     * @param view  the KSudoku View reference
     * @param model the KSudoku Model reference
     */
    public Controller (View view, Model model){
        theView = view;
        theModel = model;
        correctTable = new char[9][9];

        /* Create and assign listeners. */
        buttonListener = new ButtonListener();
        colorListener = new ColorListener();
        theView.addCheckListener(buttonListener);
        theView.addResetListener(buttonListener);
        theView.addColorListener(colorListener);

        gridListeners = new FieldListener[9][9];
        for (int i = 0; i < 9; ++i)
            for (int j = 0; j < 9; ++j){
                gridListeners[i][j] = new FieldListener(i,j);
                theView.addFieldListener(i,j,gridListeners[i][j]);
            }

        /* Get a new solution, send it to the view. */
        correctTable = theModel.getNewSolution();
        boolean[][] visibilityArray = theModel.getVisibilityArray();
        theView.newGame(correctTable, visibilityArray);

        /* Initially, the color fields option is turned on. */
        colorFields = true;
    }


    /**
     * View's color check box item listener class, extends ItemListener.
     * Sets/resets the colorFields variable when the check box selection changes.
     */
    class ColorListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                colorFields = false;

                for (int i = 0; i < 9; ++i)
                    for (int j = 0; j < 9; ++j)
                        if (theView.getFieldBackground(i, j) != theView.getDefaultFieldBackground(i, j))
                            theView.setFieldBackground(i, j, theView.getDefaultFieldBackground(i, j));
            }
            else
                colorFields = true;
        }
    }

    /**
     * View's button's listener class.
     * Takes proper action when the player clicks a specific button.
     */
    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            /* The new game generation button was clicked. */
            if ( ( ((JButton) e.getSource()).getText() ).equals("Generate new game") ) {
                correctTable = theModel.getNewSolution();
                boolean[][] visibilityArray = theModel.getVisibilityArray();
                theView.newGame(correctTable, visibilityArray);
            }
            /* The correctness checking button was clicked. */
            else{
                Color colorToSet;
                boolean wholeCorrect = true;

                /* Color fields green when a digit provided by the player
                 * matches the one from the proper solution. Color red when
                 * the character is wrong. Don't color if no character in the field.
                 */
                for (int i = 0; i < 9; ++i)
                    for (int j = 0; j < 9; ++j)
                        if (theView.getFieldBackground(i,j) != Color.GRAY) {
                            if (theView.getFieldText(i, j).length() > 0) {
                                if (theView.getFieldText(i, j).charAt(0) == correctTable[i][j]) {
                                    colorToSet = Color.GREEN;
                                } else {
                                    colorToSet = Color.RED;
                                    wholeCorrect = false;
                                }

                                if (colorFields)
                                    theView.setFieldBackground(i, j, colorToSet);
                            } else
                                wholeCorrect = false;
                        }

                /* If all white fields turned green, the whole grid is filled correctly. */
                if (wholeCorrect)
                    theView.filledCorrectly();
                else
                    /* If coloring is off, let the player know that there are mistakes. */
                    if (!colorFields)
                        theView.displayMessage("Incorrect!");
            }
        }
    }

    /**
     * Contains a document listener and a focus listener for a single text field
     * in the sudoku grid.
     */
    class FieldListener {
        private int x, y;               /* field's coordinates in the table */
        DocumentListener listener;      /* field's document listener */
        FocusListener focusListener;    /* field's focus listener */

        /**
         * Default constructor. Contains overrided methods definitions.
         * @param _x field x coordinate.
         * @param _y field y coordinate.
         */
        FieldListener(int _x, int _y){
            x = _x;
            y = _y;

            /* Create and define focus listener. */
            focusListener = new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {}

                /* Set default color for the field, do so with
                 * all potentially colliding fields (they may be colored red).
                 */
                @Override
                public void focusLost(FocusEvent e) {
                    if (theView.getFieldBackground(x,y) == Color.RED)
                        colorCollisionPointsDefault(x,y);

                    theView.setFieldBackground(x,y,Color.WHITE);
                }
            };

            /* Create and define the document listener. */
            listener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    this.changedUpdate(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (theView.getFieldText(x,y).length() < 2)
                        this.changedUpdate(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (!colorFields)
                        return;

                    boolean notColliding = true;
                    String currentText = theView.getFieldText(x,y);

                    /* Always white when no character. Stop coloring collisions. */
                    if (currentText.length() < 1){
                        theView.setFieldBackground(x, y, Color.WHITE);
                        colorCollisionPointsDefault(x, y);
                        return;
                    }
                    /* Always red when improper/more than one character. Stop coloring collisions. */
                    if (currentText.length() > 1
                            || (int)currentText.charAt(0) < 49
                            || (int)currentText.charAt(0) > 57)
                    {
                        theView.setFieldBackground(x,y,Color.RED);
                        colorCollisionPointsDefault(x,y);
                        return;
                    }

                    /* Look for collisions. Color the field and colliding fields red. */
                    /* The column. */
                    for (int i = 0; i < 9; ++i) {
                        if (theView.getFieldText(i, y).length() > 0
                                && theView.getFieldText(i, y).charAt(0) == currentText.charAt(0)
                                && x != i)
                        {
                            if (notColliding)
                                theView.setFieldBackground(x, y, Color.RED);
                            theView.setFieldBackground(i, y, Color.RED);
                            notColliding = false;
                        }
                    }

                    /* The row. */
                    for (int j = 0; j < 9; ++j) {
                        if (theView.getFieldText(x, j).length() > 0
                                && theView.getFieldText(x, j).charAt(0) == currentText.charAt(0)
                                && y != j)
                        {
                            if (notColliding)
                                theView.setFieldBackground(x, y, Color.RED);
                            theView.setFieldBackground(x, j, Color.RED);
                            notColliding = false;
                        }
                    }

                    /* Remaining fields from the 9x9 sub-grid. */
                    int cX, cY;
                    for (int i = 1; i < 3; ++i) {
                        cX = (x + i) % 3 + x - (x % 3);

                        for (int j = 1; j < 3; ++j) {
                            cY = (y + j) % 3 + y - (y % 3);

                            String compText = theView.getFieldText(cX, cY);

                            if (compText.length() > 0 && compText.charAt(0) == currentText.charAt(0)) {
                                if (notColliding)
                                    theView.setFieldBackground(x, y, Color.RED);
                                theView.setFieldBackground(cX, cY, Color.RED);
                                notColliding = false;
                            }
                        }
                    }

                    /* Color the field green if contains one [1-9] digit and no collisions found. */
                    if (notColliding)
                        theView.setFieldBackground(x,y,Color.GREEN);
                }
            };
        }
    }


    /**
     * Sets all potentially colliding fields' colors to their default colors.
     * @param x x coordinate of the field with which other fields are colliding.
     * @param y y coordinate of the field with which other fields are colliding.
     */
    private void colorCollisionPointsDefault (int x, int y)
    {
        for (int i = 0; i < 9; ++i)
            if (theView.getFieldBackground(i,y) == Color.RED && i != x)
                theView.setFieldBackground(i,y, theView.getDefaultFieldBackground(i, y));
        for (int j = 0; j < 9; ++j)
            if (theView.getFieldBackground(x,j) == Color.RED && j != y)
                theView.setFieldBackground(x, j, theView.getDefaultFieldBackground(x, j));

        int cX, cY;
        for (int i = 1; i < 3; ++i) {
            cX = (x + i) % 3 + x - (x % 3);
            for (int j = 1; j < 3; ++j) {
                cY = (y + j) % 3 + y - (y % 3);

                if (theView.getFieldBackground(cX, cY) == Color.RED)
                    theView.setFieldBackground(cX, cY, theView.getDefaultFieldBackground(cX, cY));
            }
        }
    }
}
