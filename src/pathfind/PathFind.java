package pathfind;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


import processing.core.PApplet;


public class PathFind extends PApplet {
	int width = 600;
	int height = 600;
	int type = 2;	
	int n = 20;
	int m = 20;
	Grids grids;
	Path fileTmpWritePath = FileSystems.getDefault().getPath("./bin/tmp.txt");
	Path fileTmpReadPath = FileSystems.getDefault().getPath("./bin/tmp.txt");
	public void mouseClicked() {		
		//System.out.println("mouse click at: " + mouseX + ", " + mouseY);
		System.out.println("mouse click at grid: (" + grids.xToi(mouseX) + ", " + grids.yToj(mouseY) + ")");
		grids.setGrid(mouseX, mouseY, type);		
	}
	public void keyPressed() {
		if (key == '2') {
			System.out.println("adding obstacle..");
			type = 2;
		}
		if (key == '3') {			
			System.out.println("adding start point..");
			type = 3;
		}
		if (key == '4') {
			System.out.println("adding goal point..");
			type = 4;
		}
		if (key == '0') {
			System.out.println("clear grid..");
			type = 0;
		}
		if (key == 'p') {
			System.out.println("find path..");
			grids.findPath();
		}
		if (key == 'r') {
			System.out.println("reset grid");
			grids.reset();
		}
		if (key == 'w') {
			System.out.println("saving grids");
			writeGrids(fileTmpWritePath);
		}
		if (key == 'l') {
			System.out.println("loadng grids");
			readGrids(fileTmpReadPath);
		}
	}
	public void writeGrids(Path filePath) {		
		try {
			Files.write(filePath, grids.output());
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}
	public void readGrids(Path filePath) {		
		try {
			List<String> lines = Files.readAllLines(fileTmpReadPath);			
			int n = Integer.parseInt(lines.remove(0));
			int m = Integer.parseInt(lines.remove(0));			
			grids = new Grids(new int[n][m], this);
			for (String line : lines) {
				int i = Integer.parseInt(line.split(",")[0]);
				int j = Integer.parseInt(line.split(",")[1]);
				int type = Integer.parseInt(line.split(",")[2]);
				grids.setGridIJ(i, j, type);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setup() {
		background(255);				
	}
	public void settings() {		
		size(width, height);
		int[][] test = new int[n][m];
		grids = new Grids(test, this);		
	}
	public void draw() {
		background(255);
		grids.draw();
	}
	public static void main(String args[]) {
	    PApplet.main(new String[] { pathfind.PathFind.class.getName() });	    
	}
}
