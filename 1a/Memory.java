import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Memory class

public class Memory extends JFrame {
    private MemoryCircle previousCircle = null; //Previously selected memory circle
    private int correctMatches = 0; //Number of correct matches
    private int errorCount = 0; //Error count

    private Timer gameTimer; //Timer for the game duration
    private long startTime; //Start time
    private int[][] randomNumbers; //Matrix of random numbers

    // Constructor
    public Memory() {
        setTitle("Memory Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(400, 400);
        setLayout(new GridLayout(4, 4));

        setResizable(false);

        ImageIcon image = new ImageIcon("Logo.jpeg");
        setIconImage(image.getImage()); 

        randomNumbers = generateRandomNumbers();

        createGamePanels();
        
        revealCircles(3000); // Three seconds. Be fast :)

        setVisible(true);
        
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        int optionyesno = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the game?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (optionyesno == JOptionPane.YES_OPTION) { 
            System.exit(0);
        }
        else if(optionyesno == JOptionPane.NO_OPTION)
        {
            remove(optionyesno);
        }
    }
    
    //Generates random numbers.
    private int[][] generateRandomNumbers() {
        
        int[] numbers = {1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8};
        Random rand = new Random() ; 

        for (int i = numbers.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }

        int[][] result = new int[4][4];

        for (int row= 0 , k=0 ; row < 4 ; row++){
            for(int column=0 ; column < 4 ; column++){
                result[row][column] = numbers[k++];
            }
        }

        return result;
    }

    // Helper method to create game panels.(Border, layouts)
    private  void createGamePanels() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panel.setLayout(new GridBagLayout());

                int randomNumber = randomNumbers[i][j];

                //Each MemoryCircle is created with a specific color and number (I mean,  color and number)
                MemoryCircle memoryCircle = new MemoryCircle(selectColor(randomNumber), randomNumber);

                gameTimer = new Timer();
                startTime = System.currentTimeMillis();

                //Add a mouse listener (to handle the click event for each circle)
                memoryCircle.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCircleClick(memoryCircle);
                    }
                });

                panel.add(memoryCircle);
                add(panel);
            }
        }
    }

    //Handles the click event on a circle.
    private void handleCircleClick(MemoryCircle clickedCircle) {
        if (!clickedCircle.isOpen()) {
            if (previousCircle == null) {
                //First click
                previousCircle = clickedCircle;
                clickedCircle.open();
            } else {
                //Second click
                MemoryCircle secondCircle = clickedCircle;
                secondCircle.open();

                if (previousCircle.getNumber() == secondCircle.getNumber()) {
                    // Numbers match, hide the circles(it's work when your first and second click are have the same number)
                    previousCircle.setVisible(false);
                    secondCircle.setVisible(false);
                    previousCircle = null;
                    correctMatches++;

                    if (correctMatches == 8) {
                        showCongratulationsMessage();
                    }
                } else {
                    //If numbers are not the same.
                    errorCounter();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(() -> {
                                previousCircle.close();
                                secondCircle.close();
                                previousCircle = null;
                            });
                        }
                    }, 1000); //1 second delay.(be fast ;) )
                }
            }
        }
    }

    //Reveals some circles at the beginning.
    private void revealCircles(int delay) {
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                MemoryCircle memoryCircle = (MemoryCircle) panel.getComponent(0);
                memoryCircle.open();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        closeCircles();
                    });
                }
            }, delay);
        }
    }

    //Closes all circles.
    private void closeCircles() {
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                MemoryCircle memoryCircle = (MemoryCircle) panel.getComponent(0);
                memoryCircle.close();
            }
        }
    }

    //Counts errors and ends the game when a certain limit is reached.(If error count is reached 3)
    private void errorCounter() {
        errorCount++;
        if (errorCount == 3) {
            JOptionPane.showMessageDialog(this, "Unfortunately! You lost the game.", "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    //Shows a congratulations message when the game is completed.
    private void showCongratulationsMessage() {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        //Shows elapsed time and congratulation
        JOptionPane.showMessageDialog(
                this,
                "Congratulations! You completed the game. Time: " + elapsedTime + " seconds",
                "Game Completed",
                JOptionPane.INFORMATION_MESSAGE);

        gameTimer.cancel();
        System.exit(0);
    }

    //Selects a color based on the given number.
    private Color selectColor(int number) {
    
        switch (number) {
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.RED;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.PINK;
            case 7:
                return Color.CYAN;
            case 8:
                return Color.MAGENTA;
            default:
                return Color.BLACK;
        }
    }

    //Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Memory();
        });
    }
}

//Subclass of JComponent
class MemoryCircle extends JComponent {
    private Color color; // Circle color
    private int number; // Circle number
    private boolean isOpen = false; //Flag indicating whether the circle is open 

    //Constructor for the MemoryCircle class.
    public MemoryCircle(Color color, int number) {
        this.color = color;
        this.number = number;

        setPreferredSize(new Dimension(85, 85)); //width and height
    }

    // Opens the circle, making it visible.
    public void open() {
        isOpen = true;
        repaint(); //repaint the circle
    }

    //Closes the circle, making it invisible.
    public void close() {
        isOpen = false;
        repaint();
    }

    // Checks if the circle is open.
    public boolean isOpen() {
        return isOpen;
    }

    // Returns the number assigned to the circle.
    public int getNumber() {
        return number;
    }

    //Draws the appearance of the circle.
    @Override //it overrides the paintComponent method from the JComponent class.
    public void paintComponent(Graphics grap) {
        super.paintComponent(grap);
    // super.paintComponent(g) calls the paintComponent method of the superclass
    
        int width = getWidth();
        int height = getHeight();

        if (isOpen) {
            // In the open state, draw a colored circle and the number
            grap.setColor(color);
            grap.fillOval(0, 0, width, height);

            grap.setColor(Color.BLACK);
            FontMetrics fm = grap.getFontMetrics();
            String numberString = String.valueOf(number);
            //(width - a)/2, (height + a)/2 for the number
            int x = (width - fm.stringWidth(numberString)) / 2;
            int y = (height + fm.getAscent() - fm.getDescent()) / 2;
            grap.drawString(numberString, x, y);
            //After filling the circle, the code sets the color to black and retrieves FontMetrics to calculate the position (x, y) to draw the number inside the circle.
            //drawString method draw the number. 
            
        } else {
            // In the closed state, draw only a gray rectangle
            grap.setColor(Color.GRAY);
            grap.fillRect(0, 0, width, height);
        }
    }
}