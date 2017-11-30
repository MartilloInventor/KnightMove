package edu.desktop.knightmove;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
// these imports make in possible to use simpler identifiers
import static edu.desktop.knightmove.OneCoordinate.horizontal.left;
import static edu.desktop.knightmove.OneCoordinate.horizontal.right;
import static edu.desktop.knightmove.OneCoordinate.vertical.down;
import static edu.desktop.knightmove.OneCoordinate.vertical.up;

/**
 * @author jmartillo
 */

//
// Quick and dirty output JPanel extension to display key sequences
// and write a key sequence file.
//
// The constructor does everything for output.

// JTextArea has been deprecated since I developed this simple popup.
// I will fix this later. This logic should also work for scala and clojure.

class mapListGUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextArea mapTA = new JTextArea( 20, 40 );

    /**
     * Constructor sets layout, adds component(s), sets values
     */
    public mapListGUI(Vector<String> kml, FileWriter outfile) {
        String tmp = null;
        this.setLayout( new BorderLayout() );
        this.add( new JScrollPane( mapTA ), BorderLayout.CENTER );
        //... Add knight move list data to text area.
        mapTA.append( "Total number of knight moves is " +
                kml.size() + "\n" );
        try {
            outfile.write( "Total number of knight moves is " +
                    kml.size() + "\n" );
        } catch (IOException e) {
            KnightMove.popUpMessageThenExit( "Unable to output length.\n" );
        }
        for (Iterator<String> it = kml.iterator(); it.hasNext(); ) {
            tmp = it.next();
            mapTA.append( tmp + "\n" );
            try {
                outfile.write( tmp + "\n" );
            } catch (IOException e) {
                KnightMove.popUpMessageThenExit
                        ( "Unable to output knight move.\n" );
            }
        }
        try {
            outfile.close();
        } catch (IOException e) {
            KnightMove.popUpMessageThenExit( "Unable to close file.\n" );
        }
    }
}

/*
This problem could be done as pure initialization because the total calculation is
so small key presses have coordinates and allowed/illegal next keypresses

controlling access: https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html

trying to use access correctly
*/

class OneCoordinate {
    // adding the symbolic statics makes it less likely to mix up row and column
    static final private int firstRow = 0; // row is y coordinate
    static final private int lastRow = 3;

    static final private int firstCol = 0; // col is x coordinate
    static final private int lastCol = 4;

    int row = 0; // I should probably add setters and getters
    int col = 0;
    char letter = '\0';
    boolean vowelletter = false;
    boolean alpha = false;

    // I don't have to worry about bad values
    // unfortunately only the first can
    // be assigned (via "=") an initial integer
    // value, and the following are then
    // sequential -- something I don't want

    // the enums as defined below act almost
    // like single instance objects.

    public enum horizontal {
        right( 1 ), left( -1 );
        private final int motion;

        horizontal(int motion) {
            this.motion = motion;
        }

        public int getValue() {
            return motion;
        }
    }

    public enum vertical {
        up( 1 ), down( -1 );
        private final int motion;

        vertical(int motion) {
            this.motion = motion;
        }

        public int getValue() {
            return motion;
        }
    }

    // create a coordinate that contails alphanumber or *
    OneCoordinate(int r, int c) {
        row = r;
        col = c;
        letter = KnightMove.keyPad[r][c]; // the letter at the coordinate -- never changes
        vowelletter = KnightMove.vowel( KnightMove.keyPad[r][c] ); // is it a vowel?
        alpha = KnightMove.alpha( KnightMove.keyPad[r][c] ); // is it a letter and not a number?
    }

    // movements from a given coordinate
    // checks for legality relative to various counters in KnightMove class.
    // first two steps, then 1
    // rows are up & down, cols are right & left

    // together getStep1(), getStep2(), getStep3() constitute one knight move

    OneCoordinate getStep1(horizontal colmovetype, vertical rowmovetype, boolean verticalfirst) {
        OneCoordinate step1 = null;
        int colmove = colmovetype.getValue();
        int rowmove = rowmovetype.getValue();

        // boundary, col & row are class variables.
        // only need to do these tests once
        if (verticalfirst) {
            if ((col + colmove) >= (lastCol + 1)) {
                // can't move horizontal
                return null;
            }
            if ((col + colmove) < firstCol) {
                // can't move horizontal
                return null;
            }
            if ((row + (2 * rowmove)) >= (lastRow + 1)) {
                //can't move vertical
                return null;
            }
            if ((row + (2 * rowmove)) < firstRow) {
                //can't move vertical
                return null;
            }
            step1 = new OneCoordinate( row + rowmove, col );
        } else { // horizontal first
            // boundary
            if ((col + (2 * colmove)) >= (lastCol + 1)) {
                // can't move horizontal
                return null;
            }
            if ((col + (2 *colmove)) < firstCol) {
                // can't move horizontal
                return null;
            }

            if ((row + rowmove) >= (lastRow + 1)) {
                //can't move vertical
                return null;
            }

            if ((row + rowmove) < firstRow) {
                //can't move vertical
                return null;
            }
            step1 = new OneCoordinate( row, col + colmove );
        }
        return step1;
    }

    // getStep1() is always executed before getStep2() -- don't need to perform check again
    OneCoordinate getStep2(horizontal colmovetype, vertical rowmovetype, boolean verticalfirst) {
        OneCoordinate step2 = null;
        int colmove = colmovetype.getValue();
        int rowmove = rowmovetype.getValue();

        if (verticalfirst)
            step2 = new OneCoordinate( row + (2 * rowmove), col );
        else
            step2 = new OneCoordinate( row, col + (2 * colmove) );

        return step2;

    }

    // getStep2() is always executed before getStep3() -- don't need to perform check again
    OneCoordinate getStep3(horizontal colmovetype, vertical rowmovetype, boolean verticlefirst) {
        OneCoordinate step3 = null;
        int colmove = colmovetype.getValue();
        int rowmove = rowmovetype.getValue();

        if (verticlefirst)
            step3 = new OneCoordinate( row + (2 * rowmove), col + colmove );
        else
            step3 = new OneCoordinate( row + rowmove, col + (2 * colmove) );

        return step3;
    }

    // so that I don't need to duplicate statement for each step
    void incVowels() {
        // trick to treat true==1 and false==0
        // How would you make true==0 and false==1?
        KnightMove.numvowels += Boolean.compare( vowelletter, false );
    }

    // will this step mean a wrap?
    // at least this is how I interpret the wrap condition
    // sequences only have each alphanumeric once
    boolean testInstance() {
        if (alpha) {
            if ((KnightMove.letterinstancearray[KnightMove.keyPad[row][col] - 'A'] + 1) > 1)
                return true;
            return false;
        }
        if ((KnightMove.numinstancearray[KnightMove.keyPad[row][col] - '1'] + 1) > 1)
            return true;
        return false;
    }

    void incInstances() {
        if (alpha)
            ++KnightMove.letterinstancearray[KnightMove.keyPad[row][col] - 'A'];
        else
            ++KnightMove.numinstancearray[KnightMove.keyPad[row][col] - '1'];
        return;
    }

    // vertHorz() is a knight move -- it fails if constraints are not met
    OneCoordinate vertHorz(horizontal colmovetype, vertical rowmovetype, boolean verticalfirst) {
        // could use temp0-3 but less clear
        OneCoordinate step1 = null;
        OneCoordinate step2 = null;
        OneCoordinate step3 = null;

        // don't update class variables before we know the knight move is valid
        step1 = getStep1( colmovetype, rowmovetype, verticalfirst );
        if (step1 == null) return null;
        step2 = getStep2( colmovetype, rowmovetype, verticalfirst );
        if (step2 == null) return null;
        step3 = getStep3( colmovetype, rowmovetype, verticalfirst );
        if (step3 == null) return null;

        // can't step through *
        if ((step1.letter == '*') || (step2.letter == '*') || (step3.letter == '*')) {
            return null;
        }

        // trick to treat true==1 and false==0
        // How would you make true==0 and false==1?
        // one shot only place where I test for too many vowels
        if ((KnightMove.numvowels +
                Boolean.compare( step1.vowelletter, false ) +
                Boolean.compare( step2.vowelletter, false ) +
                Boolean.compare( step3.vowelletter, false )) > KnightMove.MAXVOWEL) {
            return null;
        }

        if (step1.testInstance() || step2.testInstance() || step3.testInstance())
            return null;

        step1.incVowels();
        step2.incVowels();
        step3.incVowels();

        step1.incInstances();
        step2.incInstances();
        step3.incInstances();

        return step3;
    }

    // Is this self-documenting code? Prime Computer loved self-documenting code.
    OneCoordinate rightUp() { //1
        return vertHorz( right, up, false );
    }

    OneCoordinate leftUp() { //2
        return vertHorz( left, up, false );
    }

    OneCoordinate rightDown() { //3
        return vertHorz( right, down, false );
    }

    OneCoordinate leftDown() { //4
        return vertHorz( left, down, false );
    }

    OneCoordinate upRight() { //5
        return vertHorz( right, up, true );
    }

    OneCoordinate downRight() { //6
        return vertHorz( right, down, true );
    }

    OneCoordinate upLeft() { //7
        return vertHorz( left, up, true );
    }

    OneCoordinate downLeft() { //8
        return vertHorz( left, down, true );
    }

    // one operation translation of key press to coordinate
    // I hate case sensitivity (I also hate when someone uses caps lock.)
    static OneCoordinate translateKeyPress(char key) {
        switch (key) {
            case 'A':
            case 'a':
                return new OneCoordinate( 0, 0 );
            case 'B':
            case 'b':
                return new OneCoordinate( 0, 1 );
            case 'C':
            case 'c':
                return new OneCoordinate( 0, 2 );
            case 'D':
            case 'd':
                return new OneCoordinate( 0, 3 );
            case 'E':
            case 'e':
                return new OneCoordinate( 0, 4 );
            case 'F':
            case 'f':
                return new OneCoordinate( 1, 0 );
            case 'G':
            case 'g':
                return new OneCoordinate( 1, 1 );
            case 'H':
            case 'h':
                return new OneCoordinate( 1, 2 );
            case 'I':
            case 'i':
                return new OneCoordinate( 1, 3 );
            case 'J':
            case 'j':
                return new OneCoordinate( 1, 4 );
            case 'K':
            case 'k':
                return new OneCoordinate( 2, 0 );
            case 'L':
            case 'l':
                return new OneCoordinate( 2, 1 );
            case 'M':
            case 'm':
                return new OneCoordinate( 2, 2 );
            case 'N':
            case 'n':
                return new OneCoordinate( 2, 3 );
            case 'O':
            case 'o':
                return new OneCoordinate( 2, 4 );
            case '1':
                return new OneCoordinate( 3, 1 );
            case '2':
                return new OneCoordinate( 3, 2 );
            case '3':
                return new OneCoordinate( 3, 3 );
            default:
                return null;
        }
    }
}

public final class KnightMove {
    static final int MAXVOWEL = 2;
    static final private int MAXKNIGHTMOVES = 3;
    static final char keyPad[][] = {
            {'A', 'B', 'C', 'D', 'E'},
            {'F', 'G', 'H', 'I', 'J'},
            {'K', 'L', 'M', 'N', 'O'},
            {'*', '1', '2', '3', '*'}
    };
    static int letterinstancearray[] = new int[('O' - 'A') + 1];
    static int numinstancearray[] = new int[('3' - '1') + 1];
    static int numvowels = 0;
    static final private String allowedKeys = "[ABCDEFGHIJKLMNOabcdefghijklmno123]";
    static private Vector<String> kmlist = new Vector<String>();
    static private Vector<OneCoordinate> coordinateList = new Vector<OneCoordinate>();

    static void popUpMessageThenExit(String message) {
        JOptionPane.showMessageDialog( null, message, "Knight Moves", 1 );
        System.exit( 1 );
    }

    // why bother iterating and testing?
    static boolean vowel(char letter) {
        switch (letter) {
            case 'A':
            case 'E':
            case 'I':
            case 'O':
                return true;
            default:
                return false;
        }
    }
    // internally only use capitals
    static boolean alpha(char letter) {
        if (('A' <= letter) && (letter <= 'O'))
            return true;
        return false;
    }

    // descending in search for valid knight move
    static void addCoordinateListToKnightMoveList() {
        Iterator<OneCoordinate> ocitrt = coordinateList.iterator();
        String moves = "";

        while(ocitrt.hasNext())
            moves += ocitrt.next().letter;

        kmlist.addElement(moves);

    }

    // used when backing up in the depth first search
    static void undoCounts(OneCoordinate coord) {
        if (coord.vowelletter) {
            if ((--KnightMove.numvowels) < 0) {
                System.err.printf( "Vowel count (%c) was incorrect.", coord );
                System.exit( -1 );
            }
        }
        if (coord.alpha) {
            if (--KnightMove.letterinstancearray[KnightMove.keyPad[coord.row][coord.col] - 'A'] < 0) {
                System.err.printf( "Vowel count (%c) was incorrect.", coord );
                System.exit( -1 );
            }
        } else {
            if (--KnightMove.numinstancearray[KnightMove.keyPad[coord.row][coord.col] - '1'] < 0) {
                System.err.printf( "Vowel count (%c) was incorrect.", coord );
                System.exit( -1 );
            }
        }
    }

    // descend and backup
    // we use PH Winston's book entitled Artifical Intelligence in AI course, which I taught
    // https://books.google.com/books/about/Artificial_Intelligence.html?id=b4owngEACAAJ
    static void addCoordinateSequence(OneCoordinate coord, OneCoordinate nextcoord, int numknightmoves,
                                      OneCoordinate.horizontal colmove, OneCoordinate.vertical rowmove, boolean verticalfirst) {
        // coord should already be added

        OneCoordinate step1 = coord.getStep1( colmove, rowmove, verticalfirst );
        OneCoordinate step2 = coord.getStep2( colmove, rowmove, verticalfirst );

        // remember nextcoord is step3

        coordinateList.add( step1 );
        coordinateList.add( step2 );
        coordinateList.add( nextcoord );

        if (numknightmoves == 0) { // numknightmoves already decremented
            // a complete KnightMove sequence
            addCoordinateListToKnightMoveList();
        } else {
            getKnightMoveList( nextcoord, numknightmoves );
        }
        coordinateList.removeElementAt( coordinateList.size() - 1 );
        coordinateList.removeElementAt( coordinateList.size() - 1 );
        coordinateList.removeElementAt( coordinateList.size() - 1 );
        undoCounts( nextcoord );
        undoCounts( step2 );
        undoCounts( step1 );
    }

    static void getKnightMoveList(OneCoordinate coord, int numknightmoves) {
        OneCoordinate nextcoord = null;
        OneCoordinate step1 = null;
        OneCoordinate step2 = null;
        OneCoordinate step3 = null;

        if (numknightmoves > MAXKNIGHTMOVES) {
            // bad sequence index (high)
            return;
        }

        if ((--numknightmoves) < 0) {
            // subsequent moves if numknightmoves starts at 3
            // 2, 1, 0 (0 is stop point)
            // bad sequence index (nonpositive)
            return;
        }

        if (coord == null) { // bad coordinate
            return;
        }

        // initialization starts here

        if (numknightmoves == (MAXKNIGHTMOVES - 1)) {
            // making the first move
            numvowels = 0;
            Arrays.fill( letterinstancearray, 0 );
            Arrays.fill( numinstancearray, 0 );
            // Have to add the initial key press cordinate data
            coordinateList = new Vector<OneCoordinate>();
            coord.incVowels();
            coord.incInstances();
            coordinateList.add( coord );
        }
        // initialization ends here, could be done before first call
        // might be easier to understand logic if done here
        nextcoord = coord.rightUp(); // if its not null all the counters have been incremented
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, right, up, false );
        }

        nextcoord = coord.leftUp();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, left, up, false );
        }

        nextcoord = coord.rightDown();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, right, down, false );
        }

        nextcoord = coord.leftDown();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, left, down, false );
        }

        nextcoord = coord.upRight();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, right, up, true );
        }

        nextcoord = coord.upLeft();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, left, up, true );
        }

        nextcoord = coord.downRight();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, right, down, true );
        }

        nextcoord = coord.downLeft();
        if (nextcoord != null) {
            addCoordinateSequence( coord, nextcoord, numknightmoves, left, down, true );
        }

        return;
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        JFrame mapwindow = new JFrame( "Knight Moves" );
        String keypress = null;
        OneCoordinate initial = null;
        String home = null;
        String filename = "keyseq.txt";
        File keyseqfile = null;
        FileWriter outfile = null;

        home = System.getenv( "USERPROFILE" );
        if (home == null) {
            home = System.getenv( "HOME" );
        }
        if (home == null) {
            popUpMessageThenExit
                    ( "Unable to get home directory/folder\n" );
        }
        filename = home + File.separator + filename;
        keyseqfile = new File( filename );
        try {
            keyseqfile.createNewFile();
        } catch (IOException e1) {
            popUpMessageThenExit( "Unable to create " + filename );
        }
        try {
            outfile = new FileWriter( keyseqfile );
        } catch (IOException e) {
            popUpMessageThenExit( "Unable to write file.\n" );
        }
        keypress = JOptionPane.showInputDialog( null,
                "Start key ([A-O]|[a-o]|[1-3]): ",
                "Knight Moves", 1 );
        if (keypress == null) {
            popUpMessageThenExit( "Initial keypress information incomplete." );
        } else if (!keypress.matches( allowedKeys )) {
            popUpMessageThenExit( "Bad key." );
        }
        initial = OneCoordinate.translateKeyPress( keypress.charAt( 0 ) );
        // coordinateList is used to make sure that each coordinate is only
        // traversed once -- to prevent infinite looping.
        coordinateList = new Vector<OneCoordinate>();
        // knightMoveList could have been a local variable but
        // I considered making available to the output routines.
        getKnightMoveList( initial, 3 );
        if (kmlist.size() == 0) {
            popUpMessageThenExit( "No legal key sequences." );
        }
        mapwindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mapwindow.setContentPane( new mapListGUI( kmlist, outfile ) );
        mapwindow.pack();
        mapwindow.setVisible( true );
    }
}
