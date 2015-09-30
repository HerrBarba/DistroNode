package solver

import java.util.ArrayList
import java.util.Arrays

class EightQueensSolver {
	
	static int[] solution
	
	public void findSolution() {
		solution = new int[8]
		ArrayList yValues = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7 )
		Collections.shuffle(yValues);
		for (int x = 0; x < 8; x++){
			if (solve(yValues.clone(), x)) {
				solution[0] = x + 1
				printSolution()
				return;
			}
		}
	}
	
	private void printSolution() {
		String message = "<sdo2015><respuesta id=\"A1\"><ochoreinas><posicion x=\"0\" y=\"${solution[0]}\"/><posicion x=\"1\" y=\"${solution[1]}\"/><posicion x=\"2\" y=\"${solution[2]}\"/><posicion x=\"3\" y=\"${solution[3]}\"/><posicion x=\"4\" y=\"${solution[4]}\"/><posicion x=\"5\" y=\"${solution[5]}\"/><posicion x=\"6\" y=\"${solution[6]}\"/><posicion x=\"7\" y=\"${solution[7]}\"/></ochoreinas></respuesta></sdo2015>"
		println message
	}
	
	boolean solve(ArrayList list, int currX) {
		if (currX == 8) return true
		ArrayList yValues = list.clone()
		
		for (int y: yValues.findAll { isDiagonalFree(it, currX) }) {
			list.remove((Object) y)
			solution[currX] = y;
			if (solve(list, currX + 1)) {
				solution[currX] = y;
				return true
			}
		}
		
		return false
	}
	
	boolean isDiagonalFree(int y, int currX) {
		for (int x = 0; x < currX; x++) {
			if (isDiagonal(x, currX, solution[x], y)) return false
		}
		
		return true;
	}
	
	boolean isDiagonal(int x1, int x2, int y1, int y2) {
		return Math.abs((y2 - y1) / (x2 - x1)) == 1
	}
}
