package solver

import java.util.ArrayList
import java.util.Arrays

import utils.XmlUtils

class EightQueensSolver {
	
	static int[] solution
	
	public int[] findSolution(boolean doPrint = false) {
		solution = new int[8]
		ArrayList yValues = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7 )
		Collections.shuffle(yValues)
		solve(yValues, 0)
		
		if (doPrint) {
			printSolution()
		}
		
		return solution
	}
	
	private void printSolution() {
		println XmlUtils.createSolverMsgXml("A1", solution)
	}
	
	boolean solve(ArrayList list, int currX) {
		if (currX == 8) return true
		
		for (int y: list.findAll { isDiagonalFree(it, currX) }) {			
			ArrayList yValues = list.clone()
			yValues.remove((Object) y)
			solution[currX] = y;
			if (solve(yValues, currX + 1)) {
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
