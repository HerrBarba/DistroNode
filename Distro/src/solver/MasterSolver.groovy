package solver

import main.NodeConfig

@Singleton
class MasterSolver {
	
	public void startSolving() {
		Thread.start {
			Set<String> solutions = new HashSet<>();
			
	        while (solutions.size() < 92) {
	            int[] solution = new EightQueensSolver().findSolution()
				if (solutions.add(Arrays.toString(solution))) {
					NodeConfig.instance.water++;
				}
	        }
			
			println solutions
		}
    }
}
