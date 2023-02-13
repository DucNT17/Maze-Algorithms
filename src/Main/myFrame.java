package Main;

import javax.swing.JFrame;

import GenarateMaze.Panel;

public class myFrame extends JFrame{
	public Panel panel;

	public final int windowWidthSize = 1000;
	public final int windowHeightSize = 600;
	myFrame(){
		
		this.setTitle("Maze Generator and Pathfinding Algorithms");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(windowWidthSize, windowHeightSize);
		this.setResizable(false);
		panel = new Panel(windowWidthSize,windowHeightSize);
		this.add(panel);
		this.pack();
		this.setLayout(null);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

}