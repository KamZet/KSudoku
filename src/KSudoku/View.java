package KSudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;



/**
 * View class from the MVC pattern in KSudoku.
 * Displays sudoku grid and buttons.
 * Contains package methods for Controller to add
 * components' listeners and get/set components' features.
 */
public class View {

    private JFrame frame;                   /* the application frame */
    private JButton resetButton,            /* generate new game button */
                checkButton;                /* check fields filling correctness button */
    private JCheckBox colorBox;          /* on/off fields coloring check box */
    private JPanel buttonsPanel,            /* panel for buttons and check box */
                   wholeGrid;               /* sudoku grid */
    private JPanel[] smallGrid;             /* nine 3x3 tables, of which the grid consists */
    private TableField[][] table;           /* fields for digits */
    private Font SudokuFont;                /* custom font used for fields and buttons */

    /**
     * View class default constructor.
     * Initializes graphical components and their fields.
     */
    public View() {
        /* Custom font */
        SudokuFont = new Font ("SudokuFont", Font.PLAIN, 18);

        /* Main frame */
        frame = new JFrame("KSudoku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 580);
        frame.setLayout(new GridLayout());

        /* Buttons, box and their panel */
        resetButton = new JButton("Generate new game");
        resetButton.setBackground(Color.YELLOW);
        resetButton.setFont (SudokuFont);
        checkButton = new JButton("Check correctness");
        checkButton.setBackground(Color.YELLOW);
        checkButton.setFont(SudokuFont);
        colorBox = new JCheckBox("Color fields");
        colorBox.setFont (SudokuFont);
        colorBox.setSelected(true);

        buttonsPanel = new JPanel();
        buttonsPanel.add(resetButton);
        buttonsPanel.add(checkButton);
        buttonsPanel.add(colorBox);
        buttonsPanel.setLayout(new GridLayout(4,1));

        /* Sudoku grid */
        table = new TableField[9][9];
        wholeGrid = new JPanel(new GridLayout(3,3));
        smallGrid = new JPanel[9];

        int min_j, max_j;
        for (int k = 0; k < 9; ++k) {
            smallGrid[k] = new JPanel(new GridLayout(3, 3));
            smallGrid[k].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            if (k < 3) {
                min_j = 0;
                max_j = 3;
            } else if (k < 6) {
                min_j = 3;
                max_j = 6;
            } else {
                min_j = 6;
                max_j = 9;
            }
            for (int i = (k * 3) % 9; i < (k * 3) % 9 + 3; ++i)
                for (int j = min_j; j < max_j; ++j) {
                    table[i][j] = new TableField();
                    table[i][j].setFont(SudokuFont);
                    table[i][j].setHorizontalAlignment(JTextField.CENTER);
                    smallGrid[k].add(table[i][j]);
                }
        }

        for (int i = 0; i < 3; ++i) {
            wholeGrid.add(smallGrid[i]);
            wholeGrid.add(smallGrid[i+3]);
            wholeGrid.add(smallGrid[i+6]);
        }

        /* Add panels to the frame, make everything visible */
        frame.add(wholeGrid);
        frame.add(buttonsPanel);
        frame.setVisible(true);
    }

    /**
     * Extends JTextField, adding field 'Color defaultColor' with setter and getter.
     * Default color is different for initially visible digits and
     * for digits which are to be filled by the player.
     * Is used when a field is colored green/red and is to be colored
     * back to the default color.
     */
    class TableField extends JTextField {
        private Color defaultColor;

        public TableField(){}

        public void setDefaultColor (Color c){
            defaultColor = c;
        }
        public Color getDefaultColor (){
            return defaultColor;
        }
    }

    /**
     * Displays a text message for the player.
     * @param msg message in String which is to be displayed.
     */
    void displayMessage (String msg){
        JOptionPane.showMessageDialog(frame, msg);
    }


    /**
     * Adds a new action listener for the new game generating button.
     * @param resetListener action listener for the button.
     */
    void addResetListener (ActionListener resetListener){
        resetButton.addActionListener(resetListener);
    }

    /**
     * Adds a new action listener for the correctness checking button.
     * @param checkListener action listener for the button.
     */
    void addCheckListener (ActionListener checkListener){
        checkButton.addActionListener(checkListener);
    }

    /**
     * Adds a new item listener for the coloring check box.
     * @param colorListener item listener for the check box.
     */
    void addColorListener (Controller.ColorListener colorListener){
        colorBox.addItemListener(colorListener);
    }

    /**
     * Adds document listener and focus listener for a textfield from the table.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @param fieldListener object of a custom class that contains both document and
     *                      focus listeners.
     */
    void addFieldListener (int x, int y, Controller.FieldListener fieldListener){
        table[x][y].getDocument().addDocumentListener(fieldListener.listener);
        table[x][y].addFocusListener(fieldListener.focusListener);
    }

    /**
     * Returns text contained by a textfield.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @return text in the field, in String.
     */
    String getFieldText (int x, int y){
        return table[x][y].getText();
    }

    /**
     * Colors background of a field.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @param c color to set field background with.
     */
    void setFieldBackground (int x, int y, Color c){
        table[x][y].setBackground(c);
    }


    /**
     * Returns field's backgrouond color.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @return color of the field's background.
     */
    Color getFieldBackground (int x, int y){
        return table[x][y].getBackground();
    }

    /**
     * Sets default background color for a field.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @param c new field's default color.
     */
    void setDefaultFieldBackground (int x, int y, Color c){
        table[x][y].setDefaultColor(c);
    }

    /**
     * Returns default background color of a field.
     * @param x field x coordinate in the table.
     * @param y field y coordinate in the table.
     * @return tablefield's default color.
     */
    Color getDefaultFieldBackground (int x, int y){
        return table[x][y].getDefaultColor();
    }

    /**
     * Sets a new sudoku grid.
     * Fields marked as visible (true in corresponding visibilityArray's position)
     * display digits, are disabled and are colored gray.
     * Other fiedls are white, enabled and contain no text, waiting for the player
     * to fill them.
     * @param newTable array of digits to fill the fields with.
     * @param visibilityArray boolean array, indicating which digits are shown.
     */
    void newGame (char[][] newTable, boolean[][] visibilityArray) {
        for (int i = 0; i < 9; ++i)
            for (int j = 0; j < 9; ++j) {
                TableField currentField = table[i][j];

                if (visibilityArray[i][j]) {
                    currentField.setText(String.valueOf(newTable[i][j]));
                    currentField.setDefaultColor(Color.GRAY);
                    currentField.setBackground(Color.GRAY);
                    currentField.setEnabled(false);
                } else {
                    currentField.setText("");
                    currentField.setDefaultColor(Color.WHITE);
                    currentField.setBackground(Color.WHITE);
                    currentField.setEnabled(true);
                }
            }
    }

    /**
     * Informs the player that the table is whole filled correctly.
     * Disables the table and colors it green, and displays a message.
     */
    void filledCorrectly () {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                table[i][j].setBackground(Color.GREEN);
                table[i][j].setEnabled(false);
            }
        }

        displayMessage("Correct!");
    }
}
