package pathfind;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Grids {
	int[][] gridsMat; // matrix of grids
	ArrayList<Grid> gridList = new ArrayList<Grid>();
	int n, m; // dimensions of the matrix of grids
	int width, height; // dimensions of the canvas
	float gridWidth, gridHeight; // dimensions of a grid
	boolean startSet = false;
	boolean goalSet = false;
	boolean adjVisit = false;
	PApplet c;
	Grid startGrid = null;
	Grid goalGrid = null;
	Path path = null;
	int algorithm = 2; // path finding algorithm (0: greedy, 1: Dijkstra, 2: A*)
	private class Grid {
		float x, y; // coordinates of grid		
		int type;		
		int i, j;		
		public Grid(int i, int j, int type) {
			this.x = (float) (i * gridWidth);
			this.y = (float) (j * gridHeight);
			this.type = type;
			this.i = i;
			this.j = j;
		}
		public float x() {
			return x;
		}
		public float y() {
			return y;
		}
		public float centerX() {
			return (float) (x + 0.5 * gridWidth);
		}
		public float centerY() {
			return (float) (y + 0.5 * gridHeight);
		}
		public int i() {
			return i;
		}
		public int j() {
			return j;
		}
		public float distSqrtTo(Grid that) {
			return (x - that.x()) * (x - that.x()) + (y - that.y()) * (y - that.y());  
		}
		public void draw() {
			if (type == 1) { // visited grid
				c.fill(100);
			}
			if (type == 2) { // obstacle
				c.fill(0);
			}
			if (type == 3) { // start grid
				c.fill(0, 0, 255);
			}
			if (type == 4) { // goal grid
				c.fill(0, 255, 0);
			}			
			c.rect(x, y, gridWidth, gridHeight);
			c.noFill();
		}		
		public void setType(int type) {
			this.type = type;
		}
		public ArrayList<Grid> adjEmptyGrids() {
			// add adjacent empty grids
			ArrayList<Grid> adjList = new ArrayList<Grid>();
			if (adjVisit) {
				for (int i = -1; i < 2; i++) {
					for (int j = -1; j < 2; j++) {
						int tmpI = this.i + i;
						int tmpJ = this.j + j;
						if (((tmpI) >= 0 & ((tmpI) < gridsMat.length)) &
							((tmpJ) >= 0 & ((tmpJ) < gridsMat[0].length)) &
							(!(i == 0 & j == 0))) {
							if (gridsMat[tmpI][tmpJ] == 0 | gridsMat[tmpI][tmpJ] == 4) {
								adjList.add(new Grid(tmpI, tmpJ, 1));
							}																		
						}
					}
				}
			}
			else {
				for (int i = -1; i < 2; i++) {
					for (int j = -1; j < 2; j++) {
						int tmpI = this.i + i;
						int tmpJ = this.j + j;
						if (((tmpI) >= 0 & ((tmpI) < gridsMat.length)) &
							((tmpJ) >= 0 & ((tmpJ) < gridsMat[0].length)) &
							(!(i == 0 & j == 0)) &
							(i == 0 | j == 0)) {
							if (gridsMat[tmpI][tmpJ] == 0 | gridsMat[tmpI][tmpJ] == 4) {
								adjList.add(new Grid(tmpI, tmpJ, 1));
							}																		
						}
					}
				}
			}
			
			return adjList;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			Grid that = (Grid) obj;
			return ((this.i == that.i) & (this.j == that.j));
		}
	}
	private class Node implements Comparable<Node>{
		Grid grid;
		Node prevNode;
		float f; // weighting function for path finding algorithms
		float distance = 0; // distance from starting node
		public Node(Grid grid) {
			this.grid = grid;
		}
		public void setF(float val) {
			this.f = val;
		}
		public void setDistance(float distance) {
			this.distance = distance;
		}
		public float getDistance() {
			return distance;
		}
		public Grid getGrid() {
			return grid;
		}
		public Node getPrevNode() {
			return prevNode;
		}
		public void setPrevNode(Node node) {
			prevNode = node;
		}
		public float getF() {
			return f;
		}
		@Override
		public int compareTo(Node that) {
			// TODO Auto-generated method stub
			if (this.f < that.f) {
				return -1;
			}
			if (this.f > that.f) {
				return 1;
			}
			return 0;
		}		
	}
	private class Path {		
		PriorityQueue<Node> pQueue = new PriorityQueue<Node>();
		ArrayList<Node> path = new ArrayList<Node>();
		Node startNode;
		Node goalNode;
		public Path(Grid startGrid, Grid goalGrid) {
			float distance;
			float heuristic = 0;
			float f = 0;
			startNode = new Node(startGrid);			
			startNode.setF(0);
			pQueue.add(startNode);			
			while (!pQueue.isEmpty()) {				
				Node nextNode = pQueue.poll();
				if (nextNode.getGrid().equals(goalGrid)) {					
					goalNode = nextNode;
					break;					
				}
				for (Grid g : nextNode.getGrid().adjEmptyGrids()) {
					gridsMat[g.i()][g.j()] = 1; // set visited
					gridList.add(g);
					Node adjNode = new Node(g);
					distance = (float) Math.sqrt(g.distSqrtTo(nextNode.getGrid()));
					if (algorithm == 0) {
						heuristic = g.distSqrtTo(goalGrid);
						f = heuristic;
					}
					if (algorithm == 1) {						
						f = distance;
					}
					if (algorithm == 2) {						
						heuristic = g.distSqrtTo(goalGrid);
						f = distance + heuristic;
					}
					adjNode.setF(nextNode.getF() + f);
					adjNode.setDistance(nextNode.getDistance() + distance);
					adjNode.setPrevNode(nextNode);
					pQueue.add(adjNode);
			    }
			}
			System.out.println("path length: " + goalNode.getDistance());
		}
		public void draw() {
			Node prevNode;
			Node currentNode;
			prevNode = goalNode.getPrevNode();
			currentNode = goalNode;
			while (prevNode != null) {
				c.line(prevNode.getGrid().centerX(), prevNode.getGrid().centerY(), currentNode.getGrid().centerX(), currentNode.getGrid().centerY());
				currentNode = prevNode;
				prevNode = prevNode.getPrevNode();				
			}
		}
	}
	public Grids(int[][] gridsMat, PApplet c) {
		this.gridsMat = gridsMat;
		this.n = this.gridsMat[0].length;
		this.m = this.gridsMat.length;
		this.width = c.width;
		this.height = c.height;
		gridWidth = (float) this.width / this.n;
		gridHeight = (float) this.height / this.m;		
		this.c = c;
	}
	public void setGrid(float x, float y, int type) {
		// set the type of grid (0: empty, 1: visited, 2: obstacle, 3: start, 4: end) by coordinates
		int i = (int) (x / gridWidth); 
		int j = (int) (y / gridHeight);		
		if (type == 0) {
			setGridEmptyIJ(i, j);
		}
		else {				
			setGridIJ(i, j, type);
		}		
	}
	public void setGridIJ(int i, int j, int type) {
		if (gridsMat[i][j] == 0) {
			if (type == 3) {
				if (startGrid != null) {
					gridsMat[startGrid.i()][startGrid.j()] = 0;
				}			
				startGrid = new Grid(i, j, type);			
			}
			else if (type == 4) {
				if (goalGrid != null) {
					gridsMat[goalGrid.i()][goalGrid.j()] = 0;
				}			
				goalGrid = new Grid(i, j, type);			
			}
			else {
				gridList.add(new Grid(i, j, type));
			}		
			gridsMat[i][j] = type;
		}		
	}
	public void setGridEmptyIJ(int i, int j) {
		Grid emptyGrid = new Grid(i, j, 0);
		gridList.remove(emptyGrid);
		// clear start/goal grid if they exist
		if (startGrid != null) {
			if (startGrid.equals(emptyGrid)) {
				startGrid = null;
			}
		}
		if (goalGrid != null) {
			if (goalGrid.equals(emptyGrid)) {
				goalGrid = null;
			}
		}
		gridsMat[i][j] = 0;
	}
	public void findPath() {
		if (startGrid != null & goalGrid != null) {
			path = new Path(startGrid, goalGrid);
		}
		else {
			System.out.println("Starting/Goal grid not defined!");
		}
	}
	public float getGridWidth() {
		return gridWidth;
	}
	public float getGridHeight() {
		return gridHeight;
	}
	public int xToi(float x) {
		return (int) (x / gridWidth);
	}
	public int yToj(float y) {
		return (int) (y / gridHeight);
	}
	public void setGridsMat(int[][] newGridsMat) {
		reset();
		gridsMat = newGridsMat;
	}
	public void reset() {
		gridsMat = new int[m][n];
		gridList.clear();
		path = null;
		startGrid = null;
		goalGrid = null;
	}
	public ArrayList<String> output() {
		ArrayList<String> output = new ArrayList<String>();
		output.add(String.valueOf(n));
		output.add(String.valueOf(m));
		if (startGrid != null) {
			output.add(startGrid.i + "," + startGrid.j + "," + startGrid.type);	
		}
		if (goalGrid != null) {
			output.add(goalGrid.i + "," + goalGrid.j + "," + goalGrid.type);
		}		
		for (Grid g : gridList) {
			if (g.type != 1 & g.type != 0) {
				output.add(g.i + "," + g.j + "," + g.type);
			}
		}
		return output;
	}
	public void draw() {		
		// draw vertical grid lines
		for (float x = 0; x <= width; x += gridWidth) {			
			c.line(x, 0, x, height);
		}
		// draw horizontal grid lines
		for (float y = 0; y <= height; y += gridHeight) {			
			c.line(0, y, width, y);
		}
		// draw grids
		for (Grid g : gridList) {
			g.draw();
		}
		if (startGrid != null) {
			startGrid.draw();
		}
		if (goalGrid != null) {
			goalGrid.draw();
		}
		if (path != null) {
			path.draw();
		}
	}

}
