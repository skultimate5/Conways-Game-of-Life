import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GameOfLife extends JFrame implements ActionListener, KeyListener{

	private int[][] cellStateArray;
	private JButton[][] cellArray;
	private int rows;
	private int cols;
	private int[][] firstClick;
	private boolean startStop;
	private int simNum;
	private int totalSims;
	private boolean end;
	private JLabel numSoFarLabel;
	private JTextField howManySims;
	private boolean enter;
	private int liveCount;
	private JLabel numLiveCells;
	private JLabel numDeadCells;
	private static boolean random;
	private static double thresh;
	private JFrame frame;
	

	//constructor 
	public GameOfLife(int rows, int cols) {
		if (random)
			createBoard(rows, cols, thresh);
		else
			createBoardClick(rows, cols);
		
		simNum = 0;
		//totalSims = 100;
		numSoFarLabel.setText(Integer.toString(simNum));
		while (!enter) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				//Handle exception
			}
		}
		totalSims = Integer.parseInt(howManySims.getText());
		while (simNum < totalSims && !end) {
			//if start has been pressed, increment the simulation count
			//slow down the while loop so it will work
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				//Handle exception
			}
			if (startStop){
				simNum = simNum + 1;
				liveCount = 0; // initialize liveCount before each simulation
				
				startGame();
				numSoFarLabel.setText(Integer.toString(simNum));
				numLiveCells.setText(Integer.toString(liveCount));
				int deadCells = (rows * cols) - liveCount;
				numDeadCells.setText(Integer.toString(deadCells));
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					//Handle exception
				}
			}
		}
	}

	//making a board that can be initialized by clicking buttons instead of randomly
	public void createBoardClick(int rows, int cols){
		startStop = false;   //initialize to not running
		
		this.rows = rows;
		this.cols = cols;  
		frame = new JFrame("Game of Life");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel board = new JPanel(new GridLayout(rows, cols));
		
		createSide();
		
		
		frame.getContentPane().add(board, "Center");
		frame.setSize(1200, 700);
		cellArray = new JButton[rows][cols];   //array of cell buttons
		cellStateArray = new int[rows][cols];  // array of the state of a cell (0=dead, 1=live)
		firstClick = new int[rows][cols];   //array of if first click of button or not
		//loop to create buttons, add them to the JFrame, and give them a value of 0 or 1
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				firstClick[i][j] = 0; // initialize firstClick to 0
				cellStateArray[i][j] = 0; //initialize cellState to 0
				cellArray[i][j] = new JButton(); // creates the button
				board.add(cellArray[i][j]); // adds the button to the JFrame
				cellArray[i][j].addActionListener(this);   //add an action listener to the button
			}
		}
		updateCells();
		frame.setVisible(true);
	}
	
	//makes the side panel that has info and buttons
	public void createSide() {
		//side panel with information and start/stop buttons
		JPanel side = new JPanel();
		side.setLayout(new GridLayout(5,1,10,0));

		// start, pause, and stop buttons
		JPanel topButtons = new JPanel();
		JButton start = new JButton("Start");
		JButton pause = new JButton("Pause");
		JButton stop = new JButton("Stop");
		topButtons.add(start);
		topButtons.add(pause);
		topButtons.add(stop);

		//choose how many simulations you want
		JPanel simInput = new JPanel();
		JLabel howManySimsLabel = new JLabel("How many simulations?", JLabel.LEFT);
		howManySims = new JTextField(4);
		howManySims.addKeyListener(this); //key listener for enter press
		simInput.add(howManySimsLabel);
		simInput.add(howManySims);

		//show how many simulations have been run so far
		JPanel firstInfo = new JPanel();
		JLabel soFar = new JLabel("# Sims so far:");
		numSoFarLabel = new JLabel();
		firstInfo.add(soFar);
		firstInfo.add(numSoFarLabel);

		//show how many live cells there are
		JPanel secondInfo = new JPanel();
		JLabel liveCells = new JLabel("# Live Cells:");
		numLiveCells = new JLabel();
		secondInfo.add(liveCells);
		secondInfo.add(numLiveCells);

		//show how many dead cells there are
		JPanel thirdInfo = new JPanel();
		JLabel deadCells = new JLabel("# Dead Cells:");
		numDeadCells = new JLabel();
		thirdInfo.add(deadCells);
		thirdInfo.add(numDeadCells);

		//add all panels to side panel
		side.add(simInput);
		side.add(firstInfo);
		side.add(secondInfo);
		side.add(thirdInfo);
		side.add(topButtons);
		
		// add side panel to frame
		frame.getContentPane().add(side, "East");

		//start simulation when start button is pressed
		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				end = false;
				startStop = true;
				if (!enter){
					// show an error message if haven't hit enter
					JOptionPane.showMessageDialog(frame,
						    "Press enter after typing number of simulations. Then press start.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					startStop = false;
				}
			}
		});

		//pause simulation when pause button is pressed
		pause.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				end = false;
				startStop = false;
			}
		});

		//stop simulation when stop button is pressed
		stop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				end = true;
			}
		});
	}
			
	//keyListener methods
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_ENTER)  //if enter button is pressed, set number of simulations
			enter = true;
	}

	//when a button is clicked, change the color to opposite color
	public void actionPerformed(ActionEvent e){
		JButton click = (JButton)e.getSource();  // gets the address of the first button that was clicked
		int rowIndex = getRowLocation(click);  
		int columnIndex = getColumnLocation(click);
		if (firstClick[rowIndex][columnIndex] == 0){ //if first click, change cells to live
			cellStateArray[rowIndex][columnIndex] = 1;
			firstClick[rowIndex][columnIndex] = 1;
		}
		else{  // if second click, change cells back to dead
			cellStateArray[rowIndex][columnIndex] = 0;
			firstClick[rowIndex][columnIndex] = 0;
			}
				
		updateCells();
	}

	// method that finds the row location of the button click
	public int getRowLocation(JButton click){
		int rowLocation = 0;

		// loops through array to find location of JButton
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				if (cellArray[i][j] == click){ //checks to see if JButtons are the same;
					rowLocation = i;
				}
			}
		}
		return rowLocation;
	}

	// method that finds the column location of the button click 
	public int getColumnLocation(JButton click){
		int columnLocation = 0;

		// loops through array to find location of JButton
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				if (cellArray[i][j] == click){ // checks to see if JButtons are the same
					columnLocation = j;
				}
			}
		}
		return columnLocation;
	}
	
	//cases for when a cell lives or dies
	public void startGame() {
		int[][] newCellStateArray = new int[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++){
				int count = countLive(i, j);  // number of live cells surrounding current cell
				//cases for cell death/birth/survival
				if (cellStateArray[i][j] == 0 && count == 3)  //if 3 live neighbors, then becomes a live cell
					newCellStateArray[i][j] = 1;
				else if (cellStateArray[i][j] == 1 && (count == 2 || count == 3)) // if live and has 2 or 3 live neighbors, stays alive
					newCellStateArray[i][j] = 1;
				else   // otherwise cell dies or stays dead
					newCellStateArray[i][j] = 0;
			}
		}
		cellStateArray = newCellStateArray;
		updateCells();

	}

	// check to see if cells around it fit cases
	public int countLive(int i, int j){
		int count = 0;
		if (i == 0 && j == 0){   //top left corner
			if (cellStateArray[i + 1][j] == 1)
				count = count + 1;
			if (cellStateArray[i + 1][j + 1] == 1)
				count = count + 1;
			if (cellStateArray[i][j + 1] == 1)
				count = count + 1;
		}
		else if (i == 0 && j == cols - 1){  // top right corner
			if (cellStateArray[i + 1][j] == 1)
				count = count + 1;
			if (cellStateArray[i + 1][j - 1] == 1)
				count = count + 1;
			if (cellStateArray[i][j - 1] == 1)
				count = count + 1;
		}
		else if (i == rows - 1 && j == 0){  // bottom left corner
			if (cellStateArray[i - 1][j] == 1)
				count = count + 1;
			if (cellStateArray[i - 1][j + 1] == 1)
				count = count + 1;
			if (cellStateArray[i][j + 1] == 1)
				count = count + 1;
		}
		else if (i == rows - 1 && j == cols - 1){  // bottom right corner
			if (cellStateArray[i - 1][j - 1] == 1)
				count = count + 1;
			if (cellStateArray[i - 1][j] == 1)
				count = count + 1;
			if (cellStateArray[i][j - 1] == 1)
				count = count + 1;
		}
		else if (i == 0) { // top row
			if (cellStateArray[i][j-1] == 1)
				count = count + 1;
			if (cellStateArray[i][j+1] == 1)
				count = count + 1;
			for (int colInd = j -1; colInd <= j + 1; colInd++){
				if (cellStateArray[i + 1][colInd] == 1)
					count = count + 1;
			}
		}
		else if (i == rows - 1){ // bottom row
			if (cellStateArray[i][j-1] == 1)
				count = count + 1;
			if (cellStateArray[i][j+1] == 1)
				count = count + 1;
			for (int colInd = j -1; colInd <= j + 1; colInd++){
				if (cellStateArray[i - 1][colInd] == 1)
					count = count + 1;
			}
		}
		else if (j == 0) {  // left edge
			if (cellStateArray[i-1][j] == 1)
				count = count + 1;
			if (cellStateArray[i+1][j] == 1)
				count = count + 1;
			for (int rowInd = i -1; rowInd <= i + 1; rowInd++){
				if (cellStateArray[rowInd][j+1] == 1)
					count = count + 1;
			}
		}
		else if (j == cols - 1){ //right edge
			if (cellStateArray[i-1][j] == 1)
				count = count + 1;
			if (cellStateArray[i+1][j] == 1)
				count = count + 1;
			for (int rowInd = i -1; rowInd <= i + 1; rowInd++){
				if (cellStateArray[rowInd][j-1] == 1)
					count = count + 1;
			}
		}
		else { // anywhere besides edges
			for (int rowInd = i-1; rowInd <= i + 1; rowInd++){
				for(int colInd = j-1; colInd <= j + 1; colInd++){
					if (rowInd != i || colInd != j){  //don't count  [i][j] cell
						if  (cellStateArray[rowInd][colInd] == 1){
							count = count + 1;
						}
					}
				}
			}
		}
		return count;
	}

	//method to create the board of buttons given the number of rows and cols and threshhold for death/live cells
	public void createBoard(int rows, int cols, double thresh) {
		this.rows = rows;
		this.cols = cols;  
		frame = new JFrame("Game of Life");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel board = new JPanel(new GridLayout(rows, cols));
		frame.getContentPane().add(board, "Center"); // add board
		createSide();  // add side panel of info and buttons
		frame.setSize(1200, 700);
		cellArray = new JButton[rows][cols];   //array of cell buttons
		cellStateArray = new int[rows][cols];  // array of the state of a cell (0=dead, 1=live)
		//loop to create buttons, add them to the JFrame, and give them a value of 0 or 1
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				cellArray[i][j] = new JButton(); // creates the button
				board.add(cellArray[i][j]); // adds the button to the JFrame
				//cellArray[i][j].addActionListener(this);      
				double numRand = Math.random(); //creates a random number between 0 and 1
				if (numRand < thresh)
					cellStateArray[i][j] = 0;
				else
					cellStateArray[i][j] = 1;;
			}
		}
		updateCells();
		frame.setVisible(true);
	}

	//update the board to reflect how cells have changed
	public void updateCells() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j< cols; j++) {
				if (cellStateArray[i][j] == 1){
					cellArray[i][j].setBackground(Color.GREEN);
					liveCount = liveCount + 1;  // count the number of live cells
				}
				else 
					cellArray[i][j].setBackground(Color.GRAY);
			}
		}

	}

	//main method. No arguments for 60x60 random board with threshhold = 0.5
	//2 args for set your own board (first arg is rows, second is cols)
	// 3 args for random board (first arg is rows, second is cols, third is a double value for threshold)
	public static void main(String[] args) {
		GameOfLife game;  //initialize the GameOfLife object
		if (args.length == 3){
			random = true;
			thresh = Double.parseDouble(args[2]);
			game = new GameOfLife(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		else if (args.length == 2){
			random = false;
			game = new GameOfLife(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
		}
		else{
			random = true;
			thresh = 0.5;
			game = new GameOfLife(60, 60);
		}
	}

}
