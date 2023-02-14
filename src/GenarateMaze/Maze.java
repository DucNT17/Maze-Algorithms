package GenarateMaze;

import java.awt.*;
import java.util.*;


public class Maze{
	private final int MAZE_SIZE;
	private int CELL_SIZE;
	private final int w ;
	private Cell[][] grids;
	private Cell current;
	private Cell next;
	private Stack<Cell> visitedStack;
	private boolean running = false;
	private int countVisited=0;
	public boolean finish = false;
	private ArrayList<Cell> pathsFromAtoB;
	private Cell start,end;
	private Queue<Cell> visitedQueue;
	private Stack<Cell> visitedS;
	// For dijkstra algorithm
	private int[][] Min_Distance;
	private ArrayList<Cell> VisitedArray;
	private int dijkstra_dem;
	private static final int oo = 100000;
	private Cell temp;
	// For A* algorithm
	private Map<Cell,Double> Open_State,Close_State;
	private ArrayList<Cell> Cur_Neighbor;
	private double [][] Rel_Distance;
	private double count;
	public int min_steps, total_steps;
	Maze(int mazeSize,int cellSize){
		MAZE_SIZE = mazeSize;
		CELL_SIZE = cellSize;
		w = (int)(MAZE_SIZE/CELL_SIZE);
		init();
	}
	
	private void init() {
		grids = new Cell[w][w];
		Min_Distance = new int[w][w];
		Rel_Distance = new double[w][w];
		//
		Open_State = new HashMap<Cell,Double>();
		Close_State = new HashMap<Cell,Double>();
		Cur_Neighbor = new ArrayList<Cell>();
		for (int row=0;row<w;row++) {
			for (int col =0;col<w;col++) {
				grids[row][col] = new Cell(row,col,CELL_SIZE);
				
			}
		}
		current = grids[new Random().nextInt(w)][new Random().nextInt(w)];
		current.visited = true;
		visitedStack = new Stack<Cell>();
		running = true;
		
	}
	public void resetMaze(){
		for (int row=0;row<w;row++) {
			for (int col =0;col<w;col++) {
				grids[row][col].resetCell();
			}
		}
		finish = false;
	}
	
	public void drawMaze(Graphics g) {
		for(int i = 0;i<w;i++) {
			for(int j = 0;j<w;j++) {
				grids[i][j].drawBox(g, new Color(0,0,255,100));
				grids[i][j].drawCell(g);
			}
		}
	}
	public void mazeAlgorithm(Graphics g) {
		if(running) {
			if(countVisited<=w*w)
				current.drawBox(g,new Color(0,255,0,100));
			update();
			System.out.println(countVisited);
		}
	}
	public void drawMazeInstantly() {	
		mazeAlgorithm();
		running = false;
	}
	
	public void drawPathFinder(Graphics g,int mode) {
		if(mode == 0) BFS(g); //Breath First Search
		else if(mode == 1) DFS(g); //Depth First Search
		else if(mode == 2) DijikstraSearch(g); 
		else if(mode == 3) AStarSearch(g);
			
		for(Cell x: pathsFromAtoB) {
			x.drawPath(g, Color.pink);
		}
		
		start.drawBox(g,new Color(252, 118, 106));
		end.drawBox(g,new Color(91, 132, 177));
		for(int i = 0;i<w;i++) {
			for(int j = 0;j<w;j++) {
				grids[i][j].drawCell(g);
			}
		}	

	}
	public boolean checkFinished(){
		if (running)
			return false;
		return true;
	}
	
	public void mazeAlgorithm() {
		Stack<Cell> visitedList = new Stack<>();
		current.visited = true;
		visitedList.push(current);
		while(!visitedList.isEmpty()) {
			current = visitedList.pop();
			if (hasNeighbor(current)) {
				visitedList.push(current);
				next = getOneRandomNeighbor(current);
				wallBreaker(current, next);
				next.visited = true;
				visitedList.push(next);	
			}
		}
	}
	

	
	public void update() {
		if (running == false)
			return;
		
		next = getOneRandomNeighbor(current);
		if(next != null) {
			visitedStack.push(next);
			countVisited++;
			next.visited = true;
			wallBreaker(current, next);
			current = next;
		}else {
			while(!hasNeighbor(current)) {
				if(!visitedStack.empty())
					current = visitedStack.pop();
				else {
					running = false;
					countVisited++;
					return;
				}
			}
			update();
		}
	}
	
	public void BFS(Graphics g) {

		next = getOneNeighbor(current);
		tracedFromAtoB(current,next);
		if(next == end) {
			System.out.println("Finished");
			genaratePathFromEndToStart();
			return;
		}
		
		if(next != end && next != null) {
			visitedQueue.offer(next);
			next.visitedPath = true;
			total_steps ++;
			next.visited = true;
			next.drawPath(g, Color.RED);
		}else {
			if(!visitedQueue.isEmpty())
				current = visitedQueue.poll();
			else {
				return;
			}
		}
	}
	
	
	public void DFS(Graphics g) {
		next = getOneNeighbor(current);
		tracedFromAtoB(current,next);
		if(next == end) {
			System.out.println("Finished");
			genaratePathFromEndToStart();
			return;
		}
		
		if(next != end && next != null) {
			visitedS.push(next);
			
			next.visitedPath = true;
			next.visited = true;
			total_steps ++;
			next.drawPath(g, Color.RED);
		}else {
			if(!visitedS.isEmpty()) {
				current = visitedS.pop();
			}
			else {
				return;
			}
		}
	}
	
//	public void DijikstraSearch(Graphics g) {
//		VisitedArray = new ArrayList<>();
//		for (int i = 0;i < w; i++) {
//			for (int j = 0; j < w; j++) {
//				Min_Distance[i][j] = countDistance(current,grids[i][j]);
//			}
//		}
//		current.visitedPath = true;	
//		current.visited = true;
//		VisitedArray.add(current);
//		next = current;
//		while (VisitedArray.size() != w*w) {
//			current = next;
//			next = findMin(Min_Distance,grids);
//			VisitedArray.add(next);
//			next.visitedPath = true;
//			next.visited = true;
//			next.drawBox(g, Color.red);
//			for (int i = 0;i < w; i++) { 
//				for (int j = 0; j < w; j++) {
//					if (grids[i][j].visitedPath == false) {
//						if (Min_Distance[i][j] > Min_Distance[next.row][next.col] + countDistance(next,grids[i][j])){
//							Min_Distance[i][j] = Min_Distance[next.row][next.col] + countDistance(next,grids[i][j]);
//							
//						}
//					}
//				}
//		
//			}
//}
//		finish = true;
//		System.out.println(Min_Distance[end.row][end.col]);
//		return;
//	}
	public void DijikstraSearch(Graphics g) {
		
		if (next != end) {
			dijkstra_dem += 1;
			total_steps ++;
			if (dijkstra_dem == w*w){
				total_steps -= 1;
			}
			current = next;
			next = findMin(Min_Distance,grids);
			if (Min_Distance[next.row][next.col] == 1) next.parent = start;
			next.visitedPath = true;
			next.visited = true;
			next.drawPath(g,Color.red);
			for (int i = 0;i < w; i++) { 
				for (int j = 0; j < w; j++) {
					if (grids[i][j].visitedPath == false) {
						if (Min_Distance[i][j] > Min_Distance[next.row][next.col] + countDistance(next,grids[i][j])){
							Min_Distance[i][j] = Min_Distance[next.row][next.col] + countDistance(next,grids[i][j]);
							grids[i][j].parent = next;
						}
					}
				}
		
			}
		}
		else {
			dijkstra_dem = 0;
			genaratePathFromEndToStart();
			System.out.println(Min_Distance[end.row][end.col]);
			System.out.println("finish");
			return;
		}
	}
//	public void AStarSearch(Graphics g) {
//		for (int i = 0; i < w; i++) {
//			for (int j = 0; j < w; j++) {
//				Rel_Distance[i][j] = caculateDistance_h(grids[i][j], end);
//			}
//		}
//		Open_State = new HashMap<Cell,Double>();
//		Close_State = new HashMap<Cell,Double>();
//		Cur_Neighbor = new ArrayList<Cell>();
//		Open_State.put(current,Rel_Distance[current.row][current.col]);
//		while (!Open_State.isEmpty()) {
//			Map<Cell,Double> Cur = new HashMap<Cell,Double>();
//			Cur = BestRelDis(Open_State);  
//			for (Cell x:Cur.keySet()) {
//				Open_State.remove(x);
//				next = x;
//				next.visited = true;
//				Close_State.put(x,Cur.get(x));
//				if (x == end) {
//					System.out.print("finish");
//					System.out.println(" " + Cur.get(x));
//					finish = true;
//					return;
//				}
//				else {
//					Cur_Neighbor = getNeighbor(x);
//					for (Cell neighbor:Cur_Neighbor) {
//						if (Close_State.get(neighbor) != null) {
//							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
//							if (Close_State.get(neighbor) > count ) {
//								Close_State.remove(neighbor);
//								Open_State.put(neighbor, count);
//						}
//					}
//						else if (Open_State.get(neighbor) == null) {
//							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
//							Open_State.put(neighbor, count);
//						
//						
//					}
//						else if (Open_State.get(neighbor) != null) {
//							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
//							if (Open_State.get(neighbor) > count) {
//								Open_State.put(neighbor,count);
//						}
//					}
//				}
//				
//			}
//			 
//		}
//			
//		
//		}
//		return;
//	}
	public void AStarSearch(Graphics g) {
		if (!Open_State.isEmpty()) {
			Map<Cell,Double> Cur = new HashMap<Cell,Double>();
			Cur = BestRelDis(Open_State);
			for (Cell x:Cur.keySet()) {
				total_steps ++;
				current = next;
				Open_State.remove(x);
				next = x;
				next.visitedPath = true;
				next.visited = true;
				next.drawPath(g, Color.RED);
				Close_State.put(x, Cur.get(x));
				if (x == end) {
					System.out.print("finish");
					System.out.println(" " + Cur.get(x));
					genaratePathFromEndToStart();
					for (int i = 0; i < w; i++) {
						for (int j = 0; j < w; j++) {
							Open_State.remove(grids[i][j]);
							Close_State.remove(grids[i][j]);
						}
					}
					return;
				}
					Cur_Neighbor = getNeighbor(x);
					for (Cell neighbor:Cur_Neighbor) {
						if (Close_State.get(neighbor) != null) {
							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
							if (Close_State.get(neighbor) > count ) {
								Close_State.remove(neighbor);
								Open_State.put(neighbor, count);
						}
					}
						else if (Open_State.get(neighbor) == null) {
							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
							Open_State.put(neighbor, count);
							neighbor.parent = x;
						
					}
						else if (Open_State.get(neighbor) != null) {
							count  = Cur.get(x) - Rel_Distance[x.row][x.col] + 1 + Rel_Distance[neighbor.row][neighbor.col];
							if (Open_State.get(neighbor) > count) {
								Open_State.put(neighbor,count);
						}
							neighbor.parent = x;
					}
				}
					return;
			 
			}
			
		
		}
		else return;
	}
	private void genaratePathFromEndToStart() {
		System.out.println("Nhap");
		pathsFromAtoB.add(end);
		min_steps = 1;
		Cell tempParent = end.parent;
		while(tempParent != start) {
			min_steps ++;
			pathsFromAtoB.add(tempParent);
			System.out.println(tempParent.row + " " + tempParent.col);
			tempParent = tempParent.parent;
		}
		finish = true;
		System.out.println(min_steps + " " + total_steps);
	}
	
	private void tracedFromAtoB(Cell A,Cell B) {
		if(B!=null) {
			B.parent = A;
		}
		if(A!=null)
			A.next.add(B);
	}	

	public void initStartAndEnd() {
		min_steps = total_steps = 0;
		total_steps = 1;
		for(int i = 0; i < w; i++){
			for(int j = 0; j < w; j++){
				Open_State.remove(grids[i][j]);
			}
		}
		start = grids[new Random().nextInt(w)][new Random().nextInt(w)];
		end = grids[new Random().nextInt(w)][new Random().nextInt(w)];
		next = start;
		//
		for (int i = 0;i < w; i++) {
			for (int j = 0; j < w; j++) {
				Min_Distance[i][j] = countDistance(start,grids[i][j]);
			}
		}
		
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				Rel_Distance[i][j] = caculateDistance_h(grids[i][j], end);
			}
		}
		
		if (start == end){
			end = grids[new Random().nextInt(w)][new Random().nextInt(w)];
		}
		Open_State.put(start,Rel_Distance[start.row][start.col]);
		
		for(int i = 0;i<w;i++) {
			for(int j = 0;j<w;j++) {
				grids[i][j].visited = false;
			}
		}
		
		start.visited = true;
		end.visited = true;
		visitedQueue = new LinkedList<Cell>();
		visitedS = new Stack<Cell>();
		pathsFromAtoB = new ArrayList<Cell>();
		current = start;
		next = current;

	}
		
	
	
	public Cell getOneNeighbor(Cell currentCell){ //for pathfinder
		ArrayList<Cell> neighbors = new ArrayList<>();
		
		if (currentCell.row-1 >= 0 && grids[currentCell.row-1][currentCell.col].visitedPath == false && currentCell.Walls[0] == false) {
			neighbors.add(grids[currentCell.row-1][currentCell.col]); // top
		}
		
		if (currentCell.col+1 < w && grids[currentCell.row][currentCell.col+1].visitedPath == false && currentCell.Walls[1] == false) {
			neighbors.add(grids[currentCell.row][currentCell.col+1]); //right
		}
		
		if (currentCell.row + 1 < w && grids[currentCell.row+1][currentCell.col].visitedPath == false && currentCell.Walls[2] == false) {
			neighbors.add(grids[currentCell.row+1][currentCell.col]); // bot
		}
		
		if (currentCell.col-1 >= 0 && grids[currentCell.row][currentCell.col-1].visitedPath == false && currentCell.Walls[3] == false) {
			neighbors.add(grids[currentCell.row][currentCell.col-1]); //left
		}
		
		
		if (neighbors.isEmpty())
			return null;
		
		return  neighbors.get(0);
	}
	
	public Cell getOneRandomNeighbor(Cell currentCell) { //for maze generator
		ArrayList<Cell> neighbors = new ArrayList<>();
		
		if (currentCell.row-1 >= 0 && grids[currentCell.row-1][currentCell.col].visited == false) {
			neighbors.add(grids[currentCell.row-1][currentCell.col]); // top
		}
		
		if (currentCell.col+1 < w && grids[currentCell.row][currentCell.col+1].visited == false) {
			neighbors.add(grids[currentCell.row][currentCell.col+1]); //right
		}
		
		if (currentCell.row + 1 < w && grids[currentCell.row+1][currentCell.col].visited == false) {
			neighbors.add(grids[currentCell.row+1][currentCell.col]); // bot
		}
		
		if (currentCell.col-1 >= 0 && grids[currentCell.row][currentCell.col-1].visited == false) {
			neighbors.add(grids[currentCell.row][currentCell.col-1]); //left
		}
		
		
		if (neighbors.isEmpty())
			return null;
		
		return  neighbors.get(new Random().nextInt(neighbors.size()));
		
	}
	public boolean hasNeighbor(Cell currentCell) {
		if (getOneRandomNeighbor(currentCell)!=null)
			return true;
		return false;
	}
	public void wallBreaker(Cell cellA,Cell cellB) {
		if (cellA.row == cellB.row+1) { // B top A
			cellA.Walls[0] = false;
			cellB.Walls[2] = false;
			return;
		}
		
		if (cellA.row == cellB.row-1) { // B bot A
			cellA.Walls[2] = false;
			cellB.Walls[0] = false;
			return;
		}
		
		if (cellA.col == cellB.col+1) { // B left A
			cellA.Walls[3] = false;
			cellB.Walls[1] = false;
			return;
		}
		
		if (cellA.col == cellB.col-1) { // B right A
			cellA.Walls[1] = false;
			cellB.Walls[3] = false;
			return;
		}
	}
	public int getCellSize() {
		return CELL_SIZE;
	}
	public void setCellSize(int cellSize) {
		CELL_SIZE = cellSize;
	}
	// Dijkstra Algorithm
	public int countDistance(Cell cellA,Cell cellB) {
		if (((cellA.row == cellB.row + 1) && (cellA.col == cellB.col) && (cellA.Walls[0] == false) )
		   ||((cellA.row == cellB.row ) && (cellA.col == cellB.col - 1) && (cellA.Walls[1] == false))
		   ||((cellA.row == cellB.row - 1) && (cellA.col == cellB.col) && (cellA.Walls[2] == false))
		   ||((cellA.row == cellB.row ) && (cellA.col == cellB.col + 1) && (cellA.Walls[3] == false)))
			return 1;
		else return oo;
	}
	public Cell findMin(int [][] Min_Distance,Cell [][] grids) {
		int min = oo;
		for(int i = 0;i<w;i++) {
			for(int j = 0;j<w;j++) {
				if ((Min_Distance[i][j] < min) && (grids[i][j].visitedPath == false)){
					min = Min_Distance[i][j];
					temp = grids[i][j];
				}
			}
		}
		return temp;
	}
	//A star Algorithm
	public double caculateDistance_h(Cell CellA,Cell end) {
		double temp_h = (CellA.row - end.row) * (CellA.row - end.row) + (CellA.col - end.col) * (CellA.col - end.col) ;
		temp_h = Math.abs(temp_h);
		temp_h = Math.sqrt(temp_h);
		return temp_h;
	}
	public ArrayList<Cell> getNeighbor(Cell CellA){
		ArrayList<Cell> Neighbor = new ArrayList<>();
		if ((CellA.row > 0) && (CellA.Walls[0] == false)) Neighbor.add(grids[CellA.row - 1][CellA.col]);
		if ((CellA.row >= 0) && (CellA.row != w) && (CellA.Walls[2] == false)) Neighbor.add(grids[CellA.row + 1][CellA.col]);
		if ((CellA.col > 0) && (CellA.Walls[3] == false)) Neighbor.add(grids[CellA.row ][CellA.col - 1]);
		if ((CellA.col >= 0) && (CellA.col != w) && (CellA.Walls[1] == false)) Neighbor.add(grids[CellA.row][CellA.col + 1]);
		return Neighbor;
		
	}
	
	public Map<Cell,Double> BestRelDis(Map<Cell,Double> OpenState){
		Map<Cell, Double> BestDis = new HashMap<Cell, Double>();
		double min = oo;
		for (Cell x : OpenState.keySet()) {
			if (OpenState.get(x) < min) {
				temp = x;
				min = OpenState.get(x);
			}
		}
		BestDis.put(temp,min);
		return BestDis;
	}

	
	
}
