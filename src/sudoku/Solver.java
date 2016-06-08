package sudoku;

import java.util.Arrays;

public class Solver {
	static Square[][] puzzle;
	
	public Solver(Square[][] p) {
		puzzle = p;
	}
	
	Square[][] solve() {
		renewPotentials();
		solveSingles();
		while (moreSingles() == true) {
			solveSingles();
		}
		solveGroupSingles();
		while (solveGroupSingles() > 0) {
			solveGroupSingles();
		}
//		findPotentialPairs();
//		while (moreSingles() == true) {
//			solveSingles();
//		}
//		while (solveGroupSingles() > 0) {
//			solveGroupSingles();
//		}
		int potentialPairs = 0;
		while (potentialPairs != findPotentialPairs()) {
			while (moreSingles() == true) {
				solveSingles();
			}
			while (solveGroupSingles() > 0) {
				solveGroupSingles();
			}
			potentialPairs = findPotentialPairs();
			while (moreSingles() == true) {
				solveSingles();
			}
			while (solveGroupSingles() > 0) {
				solveGroupSingles();
			}
		}
		findPotentialPairs();
		
		return puzzle;
	}
	
	// renews the potential values for all squares in the puzzle
	static void renewPotentials() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				puzzle[i][j].setPotentials(getPotentials(puzzle, i, j));
			}
		}
	}
	
	// solves all squares with a single potential value
	static void solveSingles() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				int value = thereCanOnlyBeOne(puzzle[i][j]);
				if (value > 0 && value <= 9) {
					puzzle[i][j].setValue(value);
				}
			}
		}
		renewPotentials();
	}
	
	// returns a non-zero if square only has one potential value, otherwise returns zero
	static int thereCanOnlyBeOne(Square s) {
		if (s.getValue() != 0)
			return 0;
		int numTrue = 0;
		int num = 0;
		for (int i=0; i<9; i++) {
			if (s.getPotentials()[i] == true) {
				numTrue++;
				num = i+1;
			}
		}
		if (numTrue == 1)
			return num;
		else
			return 0;
	}
	
	// returns true if there are more squares with one potential value
	static Boolean moreSingles() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				Boolean[] pots = puzzle[i][j].getPotentials();
				int numTrue = 0;
				for (Boolean b : pots) {
					if (b == true)
						numTrue++;
				}
				if (numTrue == 1)
					return true;
			}
		}
		return false;
	}
	
	// checks a group to see if any squares have the only potential value for a certain value for that group
	// returns number of changed values to help solver know when to stop running this method
	static int solveGroupSingles() {
		int numChanged = 0;
		// solve rows
		for (int i=0; i<9; i++) {
			Square[] row = puzzle[i];
			numChanged += solveGroupSinglesLogic(row);
		}
		
		// solve columns
		for (int j=0; j<9; j++) {
			Square[] col = new Square[9];
			for (int k=0; k<9; k++) {
				col[k] = puzzle[k][j];
			}
			numChanged += solveGroupSinglesLogic(col);
		}
		
		// solve blocks
		for (int m=0; m<9; m++) {
			Square[] block = getBlockByIndex(m);
			numChanged += solveGroupSinglesLogic(block);
		}
		
		return numChanged;
	}
	
	// returns number of changed values, to help solver know when to stop running this method
	static int solveGroupSinglesLogic(Square[] group) {
		int numChanged = 0;
		int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (Square s : group) {
			Boolean[] p = s.getPotentials();
			for (int j=0; j<9; j++) {
				if (p[j] == true) {
					values[j]++;
				}
			}
		}
		for (int k=0; k<9; k++) {
			for (Square s : group) {
				Boolean[] p = s.getPotentials();
				if (values[k] == 1 && p[k] == true) {
					s.setValue(k+1);
					numChanged++;
				}
			}
		}
		renewPotentials();
		return numChanged;
	}
	
	// check if any two in same group have same two potential values
	// returns number of pairs found
	// solver should run this, then run solvesingles/groupsingles
	static int findPotentialPairs() {
		int pairs = 0;
		
		// check rows
		for (int i=0; i<9; i++) {
			Square[] row = puzzle[i];
			pairs += findPotentialPairsLogic(row);
		}
		
		// check columns
		for (int j=0; j<9; j++) {
			Square[] col = new Square[9];
			for (int k=0; k<9; k++) {
				col[k] = puzzle[k][j];
			}
			pairs += findPotentialPairsLogic(col);
		}
		
		//check blocks
		for (int m=0; m<9; m++) {
			Square[] block = getBlockByIndex(m);
			pairs += findPotentialPairsLogic(block);
		}
		renewPotentials();
		return pairs;
	}
	
	// returns number of pairs found
	static int findPotentialPairsLogic(Square[] group) {
		int pairs = 0;
		for (int i=0; i<9; i++) {
			Square currentSquare = group[i];
			if (currentSquare.getNumPotentials() == 2) {
				for (int j=0; j<9; j++) {
					if (i != j) {
						Square nextSquare = group[j];
						int[] currentPotentials = currentSquare.getPotentialsAsIntArray();
						int[] nextPotentials = nextSquare.getPotentialsAsIntArray();
						if (Arrays.equals(currentPotentials, nextPotentials)) {
							pairs++;
							// remove those two potentials for every other square in group
							for (int k=0; k<9; k++) {
								if (k != i && k != j) {
									Square otherSquare = group[k];
									Boolean[] otherPotentials = otherSquare.getPotentials();
									otherPotentials[currentPotentials[0]-1] = false;
									otherPotentials[currentPotentials[1]-1] = false;
								}
							}
							i = 9;
						}
					}
				}
			}
		}
		return pairs;
	}
	
	// given an index 0-9, return the block as an array of squares
	static Square[] getBlockByIndex(int index) {
		int r, c;
		if (index==0) {r=0; c=0;}
		else if (index==1) {r=0; c=3;}
		else if (index==2) {r=0; c=6;}
		else if (index==3) {r=3; c=0;}
		else if (index==4) {r=3; c=3;}
		else if (index==5) {r=3; c=6;}
		else if (index==6) {r=6; c=0;}
		else if (index==7) {r=6; c=3;}
		else if (index==8) {r=6; c=6;}
		else {r=0; c=0;}
		
		return getBlock(r, c);
	}
	
	// returns an array of all potential values for a square at an index
	static Boolean[] getPotentials(Square[][] puzzle, int rowNum, int colNum) {
		if (puzzle[rowNum][colNum].getValue() != 0)
			return new Boolean[]{false, false, false, false, false, false, false, false, false};
		
		Boolean[] potentials = {true, true, true, true, true, true, true, true, true};
		
		Square[] row = puzzle[rowNum];
		Square[] col = new Square[9];
		for (int i=0; i<9; i++) {
			col[i] = puzzle[i][colNum];
		}
		Square[] block = getBlock(rowNum, colNum);
		
		Boolean[] rowPots = checkGroup(row);
		Boolean[] colPots = checkGroup(col);
		Boolean[] blockPots = checkGroup(block);
		
		for (int i=0; i<9; i++) {
			if (rowPots[i] == false || colPots[i] == false || blockPots[i] == false) {
				potentials[i] = false;
			}
		}
		
		return potentials;
	}
	
	// for every non-zero in group, returns boolean array with false for every non-zero index - 1
	static Boolean[] checkGroup(Square[] group) {
		Boolean[] groupNums = {true, true, true, true, true, true, true, true, true};
		for (Square s : group) {
			if (s.getValue() > 0 && s.getValue() <= 9) {
				groupNums[s.getValue()-1] = false;
			}
		}
		return groupNums;
	}
	
	// returns an array of 9 squares representing the block for the given square
	static Square[] getBlock(int row, int col) {
		Square[] block = new Square[9];
		int bRow, bCol;
		
		// get the row the block begins at
		if (row >= 6) {bRow = 6;}
		else if (row >= 3) {bRow = 3;}
		else {bRow = 0;}
		// get the column the block begins at
		if (col >= 6) {bCol = 6;}
		else if (col >= 3) {bCol = 3;}
		else {bCol = 0;}
		
		block[0] = puzzle[bRow][bCol];
		block[1] = puzzle[bRow][bCol+1];
		block[2] = puzzle[bRow][bCol+2];
		block[3] = puzzle[bRow+1][bCol];
		block[4] = puzzle[bRow+1][bCol+1];
		block[5] = puzzle[bRow+1][bCol+2];
		block[6] = puzzle[bRow+2][bCol];
		block[7] = puzzle[bRow+2][bCol+1];
		block[8] = puzzle[bRow+2][bCol+2];
		
		return block;
	}
}
