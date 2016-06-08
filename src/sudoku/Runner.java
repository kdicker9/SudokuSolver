package sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Runner {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Square[][] puzzle = read_puzzle("evil2");
		printPuzzle(puzzle);
		
		long startTime = System.currentTimeMillis();
		Solver solver = new Solver(puzzle);
		puzzle = solver.solve();
		long endTime = System.currentTimeMillis();
		
		printPuzzle(puzzle);
		printPuzzleWithPotentials(puzzle);
		
		// time to run program in milliseconds
		long totalTime = endTime - startTime;
		int unsolved = numUnsolved(puzzle);
		
		System.out.println("Time:\t" + totalTime + "ms");
		System.out.println("Unsolved Squares:\t" + unsolved);
	}
	
	static Square[][] read_puzzle(String file) throws FileNotFoundException, IOException {
		// read in file to temporary 2d array of ints
		int[][] intPuzzle = new int[9][9];
		int rowIndex = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String currentRow = br.readLine();
		while (currentRow != null) {
			String[] strArray = currentRow.split(",");
			int[] intArray = new int[strArray.length];
			for (int i=0; i<strArray.length; i++) {
				intArray[i] = Integer.parseInt(strArray[i]);
			}
			intPuzzle[rowIndex] = intArray;
			rowIndex++;
			currentRow = br.readLine();
		}
		br.close();
		
		// use temporary array to fill 2d array of square objects
		Square[][] puzzle = new Square[9][9];
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				Square square = new Square(intPuzzle[i][j]);
				puzzle[i][j] = square;
			}
		}
		
		// getPotentials moved to Solver
//		for (int i=0; i<9; i++) {
//			for (int j=0; j<9; j++) {
//				puzzle[i][j].setPotentials(getPotentials(puzzle, i, j));
//			}
//		}
		
		return puzzle;
	}
	
	static void printPuzzle(Square[][] puzzle) {
		for (int i=0; i<9; i++) {
			if (i != 0)
				System.out.print("\n");
			for (int j=0; j<9; j++) {
				int value = puzzle[i][j].getValue();
				System.out.print(value + " ");
			}
		}
		System.out.println("\n");
	}
	
	static void printPuzzleWithPotentials(Square[][] puzzle) {
		for (int i=0; i<9; i++) {
			if (i != 0)
				System.out.print("\n");
			for (int j=0; j<9; j++) {
				int value = puzzle[i][j].getValue();
				if (value != 0) {
					System.out.print(value + "\t");
				}
				else {
					int[] pots = puzzle[i][j].getPotentialsAsIntArray();
					System.out.print("{");
					for (int p : pots) {
						System.out.print(p);
					}
					System.out.print("}\t");
				}
			}
		}
		System.out.println("\n");
	}
	
	static int numUnsolved(Square[][] puzzle) {
		int unsolved = 0;
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (puzzle[i][j].getValue() == 0)
					unsolved++;
			}
		}
		return unsolved;
	}
}
