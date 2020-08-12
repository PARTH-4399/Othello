import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicToolBarUI.DockingListener;

import java.awt.event.*;

public class Player1 implements ActionListener {

    // declarations
    static JFrame frame = new JFrame("Othello - Player1(Black)");
    static JTextField status = new JTextField();
    static JButton[][] grid = new JButton[8][8];
    static JTextField p1 = new JTextField("O");
    static JTextField s1 = new JTextField("2");
    static JTextField sign = new JTextField("\u2190");
    static JTextField p2 = new JTextField("O");
    static JTextField s2 = new JTextField("2");
    static char[][] gridc = new char[8][8];
    static boolean turn = false;

    static ServerSocket ss;
    static Socket s;
    static PrintStream ps;
    static BufferedReader br;

    Player1() throws IOException {

        // setting the frame characteristics
        frame.setSize(440, 580);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting the status textfield characteristics
        status.setLocation(10, 10);
        status.setSize(400, 30);
        status.setVisible(true);
        status.setEditable(false);
        status.setBorder(new EtchedBorder());
        status.setHorizontalAlignment(JTextField.CENTER);
        status.setText("Waiting for Player 2...");

        // setting the p1 textfield characteristics
        p1.setFont(new Font("Dialog", Font.BOLD, 40));
        p1.setVisible(true);
        p1.setEditable(false);
        p1.setBorder(new EtchedBorder());
        p1.setLocation(10, 50);
        p1.setSize(70, 70);
        p1.setHorizontalAlignment(JTextField.CENTER);

        // setting the s1 textfield characteristics
        s1.setFont(new Font("Dialog", Font.PLAIN, 40));
        s1.setVisible(true);
        s1.setEditable(false);
        s1.setBorder(new EtchedBorder());
        s1.setLocation(90, 50);
        s1.setSize(70, 70);
        s1.setHorizontalAlignment(JTextField.CENTER);

        // setting the sign textfield characteristics
        sign.setFont(new Font("Dialog", Font.PLAIN, 40));
        sign.setVisible(true);
        sign.setEditable(false);
        sign.setBorder(new EtchedBorder());
        sign.setLocation(175, 50);
        sign.setSize(70, 70);
        sign.setHorizontalAlignment(JTextField.CENTER);

        // setting the s2 textfield characteristics
        s2.setFont(new Font("Dialog", Font.PLAIN, 40));
        s2.setVisible(true);
        s2.setEditable(false);
        s2.setBorder(new EtchedBorder());
        s2.setLocation(260, 50);
        s2.setSize(70, 70);
        s2.setHorizontalAlignment(JTextField.CENTER);

        // setting the p2 textfield characteristics
        p2.setFont(new Font("Dialog", Font.BOLD, 40));
        p2.setVisible(true);
        p2.setEditable(false);
        p2.setBorder(new EtchedBorder());
        p2.setLocation(340, 50);
        p2.setSize(70, 70);
        p2.setHorizontalAlignment(JTextField.CENTER);

        // adding elements to the frame
        frame.add(status);
        frame.add(p1);
        frame.add(p2);
        frame.add(s1);
        frame.add(s2);
        frame.add(sign);

        // setting up the starting environment
        p1.setBackground(Color.WHITE);
        status.setBackground(Color.WHITE);
        p1.setForeground(Color.BLACK);
        s1.setForeground(Color.BLACK);
        s2.setForeground(Color.BLACK);
        sign.setForeground(Color.BLACK);
        status.setForeground(Color.BLACK);

        p2.setBackground(Color.BLACK);
        p2.setForeground(Color.WHITE);

        // setting all the grid button characteristics
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                grid[i][j] = new JButton();
                grid[i][j].setBounds(10 + i * 50, 130 + j * 50, 50, 50);
                grid[i][j].setSize(50, 50);
                grid[i][j].setBackground(Color.GREEN);
                grid[i][j].setFont(new Font("Dialog", Font.BOLD, 21));
                grid[i][j].addActionListener(this);
                gridc[i][j] = '-';
                frame.add(grid[i][j]);
            }
        }

        // setting up the initial state
        gridc[3][3] = 'B';
        gridc[3][4] = 'W';
        gridc[4][3] = 'W';
        gridc[4][4] = 'B';

        grid[3][3].setForeground(Color.BLACK);
        grid[3][3].setText("O");
        grid[3][4].setForeground(Color.WHITE);
        grid[3][4].setText("O");
        grid[4][3].setForeground(Color.WHITE);
        grid[4][3].setText("O");
        grid[4][4].setForeground(Color.BLACK);
        grid[4][4].setText("O");

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (gridc[i][j] == '-')
                    grid[i][j].setForeground(Color.GREEN);
            }
        }

        grid[2][4].setForeground(new Color(15, 199, 2));
        grid[2][4].setBackground(new Color(15, 199, 2));
        gridc[2][4] = 'X';
        grid[3][5].setForeground(new Color(15, 199, 2));
        grid[3][5].setBackground(new Color(15, 199, 2));
        gridc[3][5] = 'X';
        grid[4][2].setForeground(new Color(15, 199, 2));
        grid[4][2].setBackground(new Color(15, 199, 2));
        gridc[4][2] = 'X';
        grid[5][3].setForeground(new Color(15, 199, 2));
        grid[5][3].setBackground(new Color(15, 199, 2));
        gridc[5][3] = 'X';

        // waiting for Player2 to join
        s = ss.accept();

        // PrintStream and BufferedReader for sending and receiving the moves
        ps = new PrintStream(s.getOutputStream());
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        // first turn is of Player1
        status.setText("Player2 is ready. Let's Go. Make a move.");
        turn = true;
    }

    public void actionPerformed(ActionEvent e) {

        // getting the positions of the button clicked
        int rowp = 0, colp = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (e.getSource() == grid[i][j]) {
                    rowp = i;
                    colp = j;
                    break;
                }
            }
        }

        // perform the move only if turn is there and the made move is a valid one
        if (turn && gridc[rowp][colp] == 'X') {

            int changes = 0, i, j;

            // changing the turn and status
            turn = false;
            sign.setText("\u2192");
            status.setText("You have made a move, Wait for Player2 to make a move.");

            // changing the present move
            gridc[rowp][colp] = 'B';
            grid[rowp][colp].setText("O");
            grid[rowp][colp].setForeground(Color.BLACK);

            // traversing up and checking
            for (i = rowp - 1; i > -1; --i) {
                if (gridc[i][colp] != 'W')
                    break;
            }
            if (i > -1 && gridc[i][colp] == 'B') {
                ++i;
                while (i != rowp) {
                    ++changes;
                    gridc[i][colp] = 'B';
                    grid[i][colp].setText("O");
                    grid[i][colp].setForeground(Color.BLACK);
                    ++i;
                }
            }

            // traversing down and checking
            for (i = rowp + 1; i < 8; ++i) {
                if (gridc[i][colp] != 'W')
                    break;
            }
            if (i < 8 && gridc[i][colp] == 'B') {
                --i;
                while (i != rowp) {
                    ++changes;
                    gridc[i][colp] = 'B';
                    grid[i][colp].setText("O");
                    grid[i][colp].setForeground(Color.BLACK);
                    --i;
                }
            }

            // traversing left aand checking
            for (j = colp - 1; j > -1; --j) {
                if (gridc[rowp][j] != 'W')
                    break;
            }
            if (j > -1 && gridc[rowp][j] == 'B') {
                ++j;
                while (j != colp) {
                    ++changes;
                    gridc[rowp][j] = 'B';
                    grid[rowp][j].setText("O");
                    grid[rowp][j].setForeground(Color.BLACK);
                    ++j;
                }
            }

            // traversing right and checking
            for (j = colp + 1; j < 8; ++j) {
                if (gridc[rowp][j] != 'W')
                    break;
            }
            if (j < 8 && gridc[rowp][j] == 'B') {
                --j;
                while (j != colp) {
                    ++changes;
                    gridc[rowp][j] = 'B';
                    grid[rowp][j].setText("O");
                    grid[rowp][j].setForeground(Color.BLACK);
                    --j;
                }
            }

            // traversing left-up diagnol and checking
            for (i = rowp - 1, j = colp - 1; i > -1 && j > -1; --i, --j) {
                if (gridc[i][j] != 'W')
                    break;
            }
            if (i > -1 && j > -1 && gridc[i][j] == 'B') {
                ++i;
                ++j;
                while (i != rowp) {
                    ++changes;
                    gridc[i][j] = 'B';
                    grid[i][j].setText("O");
                    grid[i][j].setForeground(Color.BLACK);
                    ++i;
                    ++j;
                }
            }

            // traversing right-up diagnol and checking
            for (i = rowp + 1, j = colp - 1; i < 8 && j > -1; ++i, --j) {
                if (gridc[i][j] != 'W')
                    break;
            }
            if (i < 8 && j > -1 && gridc[i][j] == 'B') {
                --i;
                ++j;
                while (i != rowp) {
                    ++changes;
                    gridc[i][j] = 'B';
                    grid[i][j].setText("O");
                    grid[i][j].setForeground(Color.BLACK);
                    --i;
                    ++j;
                }
            }

            // traversing left-down diagnol and checking
            for (i = rowp - 1, j = colp + 1; i > -1 && j < 8; --i, ++j) {
                if (gridc[i][j] != 'W')
                    break;
            }
            if (i > -1 && j < 8 && gridc[i][j] == 'B') {
                ++i;
                --j;
                while (i != rowp) {
                    ++changes;
                    gridc[i][j] = 'B';
                    grid[i][j].setText("O");
                    grid[i][j].setForeground(Color.BLACK);
                    ++i;
                    --j;
                }
            }

            // traversing right-down diagnol and checking
            for (i = rowp + 1, j = colp + 1; i < 8 && j < 8; ++i, ++j) {
                if (gridc[i][j] != 'W')
                    break;
            }
            if (i < 8 && j < 8 && gridc[i][j] == 'B') {
                --i;
                --j;
                while (i != rowp) {
                    ++changes;
                    gridc[i][j] = 'B';
                    grid[i][j].setText("O");
                    grid[i][j].setForeground(Color.BLACK);
                    --i;
                    --j;
                }
            }

            // setting all colors
            for (i = 0; i < 8; ++i) {
                for (j = 0; j < 8; ++j) {
                    grid[i][j].setBackground(Color.GREEN);
                    if (gridc[i][j] != 'W' && gridc[i][j] != 'B')
                        gridc[i][j] = '-';
                }
            }

            // updating the score
            int p1s = Integer.parseInt(s1.getText());
            int p2s = Integer.parseInt(s2.getText());

            // adding the current move
            ++p1s;
            // adding and subtracting the current changes
            p1s += changes;
            p2s -= changes;
            // updating the changes
            s1.setText(String.valueOf(p1s));
            s2.setText(String.valueOf(p2s));

            // send the move made to player2
            ps.println("(" + String.valueOf(rowp) + "," + String.valueOf(colp) + ")");
        }
    }

    public static void main(String args[]) throws Exception {

        // creating a socket server
        ss = new ServerSocket(888);

        // calling the constructor
        new Player1();

        // continue the game while all any of the Player completely dominates or no
        // moves left
        while (true) {

            // checking if the game is finished or not
            int cnt = 0, b = 0, w = 0;
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (gridc[i][j] == '-' || gridc[i][j] == 'X')
                        ++cnt;
                    else if (gridc[i][j] == 'B')
                        ++b;
                    else if (gridc[i][j] == 'W')
                        ++w;
                }
            }
            if (cnt == 0 || b == 0 || w == 0)
                break;

            // waiting to receive the opponents move
            String move;
            move = br.readLine();
            int orow = Integer.parseInt(move.substring(1, 2));
            int ocol = Integer.parseInt(move.substring(3, 4));

            int changes = 0, i, j;

            // setting the turn and status
            turn = true;
            sign.setText("\u2190");

            // if opponent didn't have a move to make, skip the part of changes made by
            // opponent's move
            if (orow == 9 && ocol == 9)
                status.setText("Player2 don't have a place to make a move, So its your turn again.");
            else {
                status.setText("Player2 have made a move, Now its your turn, make a move.");

                // changing the present move
                gridc[orow][ocol] = 'W';
                grid[orow][ocol].setText("O");
                grid[orow][ocol].setForeground(Color.WHITE);

                // traversing up and checking
                for (i = orow - 1; i > -1; --i) {
                    if (gridc[i][ocol] != 'B')
                        break;
                }
                if (i > -1 && gridc[i][ocol] == 'W') {
                    ++i;
                    while (i != orow) {
                        ++changes;
                        gridc[i][ocol] = 'W';
                        grid[i][ocol].setText("O");
                        grid[i][ocol].setForeground(Color.WHITE);
                        ++i;
                    }
                }

                // traversing down and checking
                for (i = orow + 1; i < 8; ++i) {
                    if (gridc[i][ocol] != 'B')
                        break;
                }
                if (i < 8 && gridc[i][ocol] == 'W') {
                    --i;
                    while (i != orow) {
                        ++changes;
                        gridc[i][ocol] = 'W';
                        grid[i][ocol].setText("O");
                        grid[i][ocol].setForeground(Color.WHITE);
                        --i;
                    }
                }

                // traversing left aand checking
                for (j = ocol - 1; j > -1; --j) {
                    if (gridc[orow][j] != 'B')
                        break;
                }
                if (j > -1 && gridc[orow][j] == 'W') {
                    ++j;
                    while (j != ocol) {
                        ++changes;
                        gridc[orow][j] = 'W';
                        grid[orow][j].setText("O");
                        grid[orow][j].setForeground(Color.WHITE);
                        ++j;
                    }
                }

                // traversing right and checking
                for (j = ocol + 1; j < 8; ++j) {
                    if (gridc[orow][j] != 'B')
                        break;
                }
                if (j < 8 && gridc[orow][j] == 'W') {
                    --j;
                    while (j != ocol) {
                        ++changes;
                        gridc[orow][j] = 'W';
                        grid[orow][j].setText("O");
                        grid[orow][j].setForeground(Color.WHITE);
                        --j;
                    }
                }

                // traversing left-up diagnol and checking
                for (i = orow - 1, j = ocol - 1; i > -1 && j > -1; --i, --j) {
                    if (gridc[i][j] != 'B')
                        break;
                }
                if (i > -1 && j > -1 && gridc[i][j] == 'W') {
                    ++i;
                    ++j;
                    while (i != orow) {
                        ++changes;
                        gridc[i][j] = 'W';
                        grid[i][j].setText("O");
                        grid[i][j].setForeground(Color.WHITE);
                        ++i;
                        ++j;
                    }
                }

                // traversing right-up diagnol and checking
                for (i = orow + 1, j = ocol - 1; i < 8 && j > -1; ++i, --j) {
                    if (gridc[i][j] != 'B')
                        break;
                }
                if (i < 8 && j > -1 && gridc[i][j] == 'W') {
                    --i;
                    ++j;
                    while (i != orow) {
                        ++changes;
                        gridc[i][j] = 'W';
                        grid[i][j].setText("O");
                        grid[i][j].setForeground(Color.WHITE);
                        --i;
                        ++j;
                    }
                }

                // traversing left-down diagnol and checking
                for (i = orow - 1, j = ocol + 1; i > -1 && j < 8; --i, ++j) {
                    if (gridc[i][j] != 'B')
                        break;
                }
                if (i > -1 && j < 8 && gridc[i][j] == 'W') {
                    ++i;
                    --j;
                    while (i != orow) {
                        ++changes;
                        gridc[i][j] = 'W';
                        grid[i][j].setText("O");
                        grid[i][j].setForeground(Color.WHITE);
                        ++i;
                        --j;
                    }
                }

                // traversing right-down diagnol and checking
                for (i = orow + 1, j = ocol + 1; i < 8 && j < 8; ++i, ++j) {
                    if (gridc[i][j] != 'B')
                        break;
                }
                if (i < 8 && j < 8 && gridc[i][j] == 'W') {
                    --i;
                    --j;
                    while (i != orow) {
                        ++changes;
                        gridc[i][j] = 'W';
                        grid[i][j].setText("O");
                        grid[i][j].setForeground(Color.WHITE);
                        --i;
                        --j;
                    }
                }

                // setting all colors
                for (i = 0; i < 8; ++i) {
                    for (j = 0; j < 8; ++j) {
                        if (gridc[i][j] != 'B' && gridc[i][j] != 'W')
                            gridc[i][j] = '-';
                    }
                }

                // updating the score
                int p1s = Integer.parseInt(s1.getText());
                int p2s = Integer.parseInt(s2.getText());

                // adding the current move
                ++p2s;
                // adding and subtracting the current changes
                p2s += changes;
                p1s -= changes;
                // updating the changes
                s1.setText(String.valueOf(p1s));
                s2.setText(String.valueOf(p2s));
            }

            int next = 0;

            // setting the positions for next move
            for (i = 0; i < 8; ++i) {
                for (j = 0; j < 8; ++j) {
                    // up
                    if (i > 0 && gridc[i][j] == 'B' && gridc[i - 1][j] == 'W') {
                        int i1 = i - 1;
                        while (i1 >= 0 && gridc[i1][j] == 'W')
                            --i1;
                        if (i1 >= 0 && gridc[i1][j] == '-') {
                            grid[i1][j].setForeground(new Color(15, 199, 2));
                            grid[i1][j].setBackground(new Color(15, 199, 2));
                            gridc[i1][j] = 'X';
                            ++next;
                        }
                    }
                    // down
                    if (i < 7 && gridc[i][j] == 'B' && gridc[i + 1][j] == 'W') {
                        int i1 = i + 1;
                        while (i1 <= 7 && gridc[i1][j] == 'W')
                            ++i1;
                        if (i1 <= 7 && gridc[i1][j] == '-') {
                            grid[i1][j].setForeground(new Color(15, 199, 2));
                            grid[i1][j].setBackground(new Color(15, 199, 2));
                            gridc[i1][j] = 'X';
                            ++next;
                        }
                    }
                    // left
                    if (j > 0 && gridc[i][j] == 'B' && gridc[i][j - 1] == 'W') {
                        int j1 = j - 1;
                        while (j1 >= 0 && gridc[i][j1] == 'W')
                            --j1;
                        if (j1 >= 0 && gridc[i][j1] == '-') {
                            grid[i][j1].setForeground(new Color(15, 199, 2));
                            grid[i][j1].setBackground(new Color(15, 199, 2));
                            gridc[i][j1] = 'X';
                            ++next;
                        }
                    }
                    // right
                    if (j < 7 && gridc[i][j] == 'B' && gridc[i][j + 1] == 'W') {
                        int j1 = j + 1;
                        while (j1 <= 7 && gridc[i][j1] == 'W')
                            ++j1;
                        if (j1 <= 7 && gridc[i][j1] == '-') {
                            grid[i][j1].setForeground(new Color(15, 199, 2));
                            grid[i][j1].setBackground(new Color(15, 199, 2));
                            gridc[i][j1] = 'X';
                            ++next;
                        }
                    }
                    // left-up
                    if (i > 0 && j > 0 && gridc[i][j] == 'B' && gridc[i - 1][j - 1] == 'W') {
                        int i1 = i - 1, j1 = j - 1;
                        while (i1 >= 0 && j1 >= 0 && gridc[i1][j1] == 'W') {
                            --i1;
                            --j1;
                        }
                        if (i1 >= 0 && j1 >= 0 && gridc[i1][j1] == '-') {
                            grid[i1][j1].setForeground(new Color(15, 199, 2));
                            grid[i1][j1].setBackground(new Color(15, 199, 2));
                            gridc[i1][j1] = 'X';
                            ++next;
                        }
                    }
                    // right up
                    if (i < 7 && j > 0 && gridc[i][j] == 'B' && gridc[i + 1][j - 1] == 'W') {
                        int i1 = i + 1, j1 = j - 1;
                        while (i1 <= 7 && j1 >= 0 && gridc[i1][j1] == 'W') {
                            ++i1;
                            --j1;
                        }
                        if (i1 <= 7 && j1 >= 0 && gridc[i1][j1] == '-') {
                            grid[i1][j1].setForeground(new Color(15, 199, 2));
                            grid[i1][j1].setBackground(new Color(15, 199, 2));
                            gridc[i1][j1] = 'X';
                            ++next;
                        }
                    }
                    // left down
                    if (i > 0 && j < 7 && gridc[i][j] == 'B' && gridc[i - 1][j + 1] == 'W') {
                        int i1 = i - 1, j1 = j + 1;
                        while (i1 >= 0 && j1 <= 7 && gridc[i1][j1] == 'W') {
                            --i1;
                            ++j1;
                        }
                        if (i1 >= 0 && j1 <= 7 && gridc[i1][j1] == '-') {
                            grid[i1][j1].setForeground(new Color(15, 199, 2));
                            grid[i1][j1].setBackground(new Color(15, 199, 2));
                            gridc[i1][j1] = 'X';
                            ++next;
                        }
                    }
                    // right down
                    if (i < 7 && j < 7 && gridc[i][j] == 'B' && gridc[i + 1][j + 1] == 'W') {
                        int i1 = i + 1, j1 = j + 1;
                        while (i1 <= 7 && j1 <= 7 && gridc[i1][j1] == 'W') {
                            ++i1;
                            ++j1;
                        }
                        if (i1 <= 7 && j1 <= 7 && gridc[i1][j1] == '-') {
                            grid[i1][j1].setForeground(new Color(15, 199, 2));
                            grid[i1][j1].setBackground(new Color(15, 199, 2));
                            gridc[i1][j1] = 'X';
                            ++next;
                        }
                    }
                }
            }

            // if there is no available move, pass the turn to opponent
            if (next == 0) {
                status.setText("Opps! No place left where you can make a move, Wait for Player2 to make a move.");
                turn = false;
                ps.println("(9,9)");
                sign.setText("\u2192");
            }
        }

        // game is over, get the scores and declare who is the winner
        status.setText("Game Finished");
        int score1 = Integer.parseInt(s1.getText());
        int score2 = Integer.parseInt(s2.getText());

        String res = "";
        if (score1 > score2)
            res = "Player1(Black) wins!";
        else if (score2 > score1)
            res = "Player2(White) wins!";
        else
            res = "Game resulted in a tie!";

        JOptionPane.showMessageDialog(frame, res);

        // close connection
        ps.close();
        br.close();
        ss.close();
        s.close();

        System.exit(0);
    }
}