package GenarateMaze;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Panel extends JPanel implements ActionListener,ChangeListener{
	
	private final int mazeSize = 600;
	private int cellSize = 40;
	private int slowestDelay = 110;
	private int delay = 10;
	private int running = -1;
	private Maze maze;
	private Timer tm;
	private JPanel smallPanel;
	private Button startButton;
	private Button resetButton;
	private Button reMazeButton;	
	private JLabel cellLabel;
	private JSlider cellSlider;	
	private JLabel speedLabel;
	private JSlider speedSlider;	
	private JCheckBox mazeCheckbox;
	private JComboBox<?> algoBox;
	private int mode ;
	private JLabel algoBoxLabel;
	private JLabel pathLengthLable;
	private JLabel noofChecksJLabel;
	private Button startSolvingButton;
	private boolean flag = true;
	

	public Panel(int windowW,int windowH){
		tm = new Timer(delay,this);
		
		this.setPreferredSize(new Dimension(windowW,windowH));
		this.setBounds(0, 0, windowW, windowH);
		this.setBackground(new Color(250,250,250));
		this.setLayout(null);
		
		
		//cai bang ben phai
		smallPanel = new JPanel();
		smallPanel.setPreferredSize(new Dimension((windowW-mazeSize),windowH));
		smallPanel.setBounds(mazeSize, 0, windowW-mazeSize, windowH);
		smallPanel.setLayout(null);
		this.add(smallPanel);
		
		
		//nut start
		startButton = new Button("Start",(int)(smallPanel.getSize().width/2)-120-30, 30, 120, 50,Color.PINK);
		smallPanel.add(startButton);
		startButton.addActionListener(this);
		
		
		
		//nut re-maze
		reMazeButton = new Button("Re-Maze",(int)(smallPanel.getSize().width/2)+30, 30, 120, 50,Color.CYAN);
		smallPanel.add(reMazeButton);
		reMazeButton.addActionListener(this);
		reMazeButton.setEnabled(false);
		
		
		
		
		//Bang lua chon cellSlider + label cua no
		cellSlider = new JSlider(10,100,40);
		cellSlider.setBounds((windowW-mazeSize)/2-(350/2), 150, 350, 50);
		cellSlider.setPaintTicks(true);
		cellSlider.setMinorTickSpacing(5);
		cellSlider.setPaintTrack(true);
		cellSlider.setMajorTickSpacing(10);
		cellSlider.setSnapToTicks(true);
		cellSlider.setPaintLabels(true);
		cellSlider.addChangeListener(this);

		cellLabel = new JLabel();
		cellLabel.setText("Cell's size adjustment: " + cellSlider.getValue());
		cellLabel.setBounds(30, 130, 250, 20);
		cellLabel.setFont(new Font("",Font.BOLD,18));
		
		smallPanel.add(cellLabel);
		smallPanel.add(cellSlider);
		
		
		//Bang lua chon speed va label cua no
		speedSlider = new JSlider(1,5,5);
		speedSlider.setBounds((windowW-mazeSize)/2-(350/2), 230, 350, 40);
		speedSlider.setPaintTrack(true);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setSnapToTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.addChangeListener(this);

		speedLabel = new JLabel();
		speedLabel.setText("Speed: " + speedSlider.getValue());
		speedLabel.setBounds(30, 210, 180, 20);
		speedLabel.setFont(new Font("",Font.BOLD,18));
				
		smallPanel.add(speedLabel);
		smallPanel.add(speedSlider);
		
		
		//generate Check box
		mazeCheckbox = new JCheckBox();
		mazeCheckbox.setBounds((int)(smallPanel.getSize().width/2)-155, 95, 250, 20);
		mazeCheckbox.setText("Generate instantly");
		mazeCheckbox.setFont(new Font("",Font.BOLD,15));
		mazeCheckbox.setFocusable(false);	
		smallPanel.add(mazeCheckbox);
		
		
		algoBoxLabel = new JLabel("Maze Pathfinding");
		algoBoxLabel.setBounds(smallPanel.getSize().width/2-155, 370, 220, 30);
		algoBoxLabel.setFont(new Font("",Font.BOLD,15));
		smallPanel.add(algoBoxLabel);
		
		
		
		String[] algoList = {"Breadth First Search(BFS)","Depth First Search(DFS)","DijkstraSearch","A* Search"};
		algoBox = new JComboBox(algoList);
		algoBox.setBounds(smallPanel.getSize().width/2-155, 400, 220, 30);
		algoBox.setFont(new Font("",Font.BOLD,15));
		algoBox.setFocusable(false);
		algoBox.addActionListener(this);
		mode = algoBox.getSelectedIndex(); 
		smallPanel.add(algoBox);

		
		//BFS button
		startSolvingButton = new Button("Start",smallPanel.getSize().width/2-155, 300, 130, 60,new Color(255, 231, 122));
		smallPanel.add(startSolvingButton);
		startSolvingButton.addActionListener(this);
		startSolvingButton.setEnabled(false);            
		
		//nut resetMaze
		resetButton = new Button("Reset Maze",smallPanel.getSize().width/2+20, 300, 130, 60,new Color(44, 95, 45));
		smallPanel.add(resetButton);
		resetButton.addActionListener(this);
		
		//path length
		pathLengthLable = new JLabel();
		pathLengthLable.setText("Path Length: " ); // pathLength là độ dài đường đi từ start đến end (lát ông cộng vào thêm ở sau là đc)
		pathLengthLable.setBounds(smallPanel.getSize().width/2 - 155, 450, 250, 30);
		pathLengthLable.setFont(new Font("",Font.BOLD,15));
		smallPanel.add(pathLengthLable);

		noofChecksJLabel = new JLabel();
		noofChecksJLabel.setText("Total Checks: " ); // Check tổng quãng đường đã duyệt qua
		noofChecksJLabel.setBounds(smallPanel.getSize().width/2 - 155, 500, 250, 30);
		noofChecksJLabel.setFont(new Font("",Font.BOLD,15));
		smallPanel.add(noofChecksJLabel);

		initMaze();
		tm.start();
	}
	
	
	private void initMaze() {
		maze = new Maze(mazeSize,cellSize);	
	}

	public void paintComponent(Graphics g) {
		
		if(maze.checkFinished()) {
			reMazeButton.setEnabled(true);
			startSolvingButton.setEnabled(true);
			algoBox.setEnabled(true);
		}else {
			startSolvingButton.setEnabled(false);
			resetButton.setEnabled(false);
			algoBox.setEnabled(false);
		}
		super.paintComponent(g);
		maze.drawMaze(g);
		if(!flag) {
			maze.drawPathFinder(g,mode);
			if(maze.finish) {
				mazeCheckbox.setEnabled(true);
				startSolvingButton.setEnabled(true);
				resetButton.setEnabled(true);
				tm.stop();
			}
		}
		if(!mazeCheckbox.isSelected()) {
			maze.mazeAlgorithm(g);
		}	
		else {
			maze.drawMazeInstantly();
		}
	}
	private void reset() {
		tm.start();
		running = -1;	
		startButton.setEnabled(true);
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//nhan nut re-maze, tao lai maze moi
		if(e.getSource()==reMazeButton) {
			flag = true;
			mazeCheckbox.setEnabled(true);
			initMaze();
			reset();
			startButton.setText("Start");
		}
		
		
			
		//nhan nut start 
		if (e.getSource()==startButton) {
			running *= -1;
			if(running == 1)
				startButton.setText("Pause");
			else
				startButton.setText("Start");
		}
		
	

		if(e.getSource()==algoBox) {
			mode = algoBox.getSelectedIndex();
		}

		//start cua ben thuat toan tim duong
		if(e.getSource() == startSolvingButton) {
			mazeCheckbox.setSelected(false);
			mazeCheckbox.setEnabled(false);
			if(flag) {
				maze.initStartAndEnd();
				flag = false;
			}
			
		}
		
		//nut reset maze
		if(e.getSource()==resetButton) {
			flag = true;
			maze.resetMaze();
			tm.start();
		}
		if(running==1)
			repaint();
	}


	@Override
	public void stateChanged(ChangeEvent e) {
	
		//cellSlider
		if(e.getSource()==cellSlider) {
			if(cellSlider.getValue()%5==0) {
				mazeCheckbox.setEnabled(true);
				cellLabel.setText("Cell's size adjustment: " + cellSlider.getValue());
				cellSize = cellSlider.getValue();
				startButton.setText("Start");
				flag = true;
				initMaze();
				reset();
			}
		}
		//SpeedSlider
		if(e.getSource()==speedSlider) {
				speedLabel.setText("Speed: " + speedSlider.getValue());
				delay = slowestDelay - (speedSlider.getValue()-1)* ((slowestDelay-10)/4) ;
				tm.setDelay(delay);
				System.out.println(delay);
		}

	}
}

