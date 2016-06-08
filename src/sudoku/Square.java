package sudoku;

public class Square 
{
	int value;
	Boolean[] potentials = {false, false, false, false, false, false, false, false, false};
	
	public Square(int v) {
		value = v;
	}
	
	public void addPotential(int num) {
		potentials[num] = true;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Boolean[] getPotentials() {
		return potentials;
	}

	public void setPotentials(Boolean[] potentials) {
		this.potentials = potentials;
	}
	
	public int getNumPotentials() {
		int numPotentials = 0;
		for (Boolean b : potentials) {
			if (b == true) {
				numPotentials++;
			}
		}
		return numPotentials;
	}
	
	public int[] getPotentialsAsIntArray() {
		int numPotentials = 0;
		for (Boolean b : potentials) {
			if (b == true) {
				numPotentials++;
			}
		}
		int[] potentialsInt = new int[numPotentials];
		for (int i=0; i<9; i++) {
			if (potentials[i] == true) {
				for (int j=0; j<potentialsInt.length; j++) {
					if (potentialsInt[j] == 0) {
						potentialsInt[j] = i+1;
						j = potentialsInt.length;
					}
				}
			}
		}
		return potentialsInt;
	}
}
