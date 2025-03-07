package edu.ncsu.csc411.ps04.agent;

import java.util.ArrayList;
import edu.ncsu.csc411.ps04.environment.Environment;
import edu.ncsu.csc411.ps04.environment.Position;
import edu.ncsu.csc411.ps04.environment.Status;

public class StudentRobot extends Robot {
  
  /** The maximum depth for minimax */
  final int MAX_DEPTH = 8;
  
  /** The player color */
  private Status player;
  
  /** The opponent's color */
  private Status opp;

	public StudentRobot(Environment env) {
		super(env);
	}
	
	/**
	 * The getAction() method relies on the minimax algorithm. First, figure out my 
	 * and my opponents role. Then iterate over all of the possible moves (columns 0-6).
	 * Then call minVal on a representation of the gameboard after this move has been made.
	 * The representation is made via perceive(). Minimax runs until a terminal state
	 * or depth maximum has been reached. The depth is set at 5. It gives good accuracy
	 * while not taking too long. A terminal state is if someone won. The terminal function
	 * returns -10 if the opponent would win; 10 if I would win. 
	 * 
	 * clonePosition() ensures that the reference semantics do not mess with perceive() and
	 * potential states. Only 1 layer of moves is considered at each iteration of minimax.
	 * 
	 */
	@Override
	public int getAction() {
	  // Finds the player and opponent roles.
	  if (this.getRole() == Status.YELLOW) {
	    player = Status.YELLOW;
	    opp = Status.RED;
	  } else {
	    player = Status.RED;
	    opp = Status.YELLOW;
	  }
	  // Initializes some variables
	  int move = 0;
	  int val = Integer.MIN_VALUE;
	  int curr;
	  // Gets a representation of the current game board
    Position[][] positions = env.clonePositions();
    // Check all valid moves
	  for (int m : moves(positions)) {
	    positions = env.clonePositions();
	    curr = minVal(perceive(m, positions, player), 0);
	    // Finds the max value from minimax
	    if (curr > val) {
	      val = curr;
	      move = m;
  	  }
	  }
	  return move;
	}
	
	/**
	 * Helper method for minimax representing the max player (me)
	 */
	private int maxVal(Position[][] positions, int depth) {
	  depth++;
	  int val = terminal(positions);
	  if (val != 0 || depth >= MAX_DEPTH) {
	    return val;
	  }
	  int max = Integer.MIN_VALUE;
	  for (int m : moves(positions)) {
	    Position[][] newPos = clonePosition(positions);
	    max = Math.max(max, minVal(perceive(m, newPos, player), depth));
	  }
	  return max;
	}
	
	 /**
	  * Helper method for minimax representing the min player (opponent)
	  */
	 private int minVal(Position[][] positions, int depth) {
	    depth++;
	    int val = terminal(positions);
	    if (val != 0 || depth >= MAX_DEPTH) {
	      return val;
	    }
	    int min = Integer.MAX_VALUE;
	    for (int m : moves(positions)) {
	      Position[][] newPos = clonePosition(positions);
	      min = Math.min(min, maxVal(perceive(m, newPos, opp), depth));
	    }
	    return min;
	  }
	
	
	/**
	 * Finds all available moves of the current position
	 */
	private ArrayList<Integer> moves(Position[][] positions) {
	   ArrayList<Integer> actions = new ArrayList<Integer>();
	    for(int col = 0; col < env.getCols(); col++) {
	      if (positions[0][col].getStatus() == Status.BLANK) {
	          actions.add(col);
	      }
	    }
	    return actions;
	}
	
	
	/**
   * The opponent winning is -10; me winning is 10; no winner found after
   * a certain depth is 0. Uses the four check line functions from Environment
	 */
	private int terminal(Position[][] positions) {
    // Iterate through each tile, checking if there are 4 adjacent tiles that
    // are the same
    for(int row = 0; row < env.getRows(); row++) {
      for (int col = 0; col < env.getCols(); col++) {
        Status status = positions[row][col].getStatus();
        boolean horizontal = checkHorizontal(row, col, positions);
        boolean vertical = checkVertical(row, col, positions);
        boolean diagonalBR = checkDiagonalBottomRight(row, col, positions);
        boolean diagonalTR = checkDiagonalTopRight(row, col, positions);
        if (horizontal || vertical || diagonalBR || diagonalTR) {
          if(status == opp) {
            return -10;
          } else {
            return 10;
          }
        }
      }
    }
    return 0;
	}
	
	 /** 
   * Looks 4 tiles to the right to see if there are 4 adjacent tiles with
   * the same color
   */
  private boolean checkHorizontal(int row, int col, Position[][] positions) {
    Status status = positions[row][col].getStatus();
    if (status != Status.YELLOW && status != Status.RED) {
      return false;
    }
    if (col+4 > env.getCols()) return false; //out of bounds
    for (int i = 0; i < 4; i++) {
      if(status != positions[row][col+i].getStatus())
        return false;
    }
    return true;
  }
  
  /** 
   * Looks 4 tiles downward to see if there are 4 adjacent tiles with
   * the same color
   */
  private boolean checkVertical(int row, int col, Position[][] positions) {
    Status status = positions[row][col].getStatus();
    if (status != Status.YELLOW && status != Status.RED) {
      return false;
    }
    if (row+4 > env.getRows()) return false; //out of bounds
    for (int i = 0; i < 4; i++) {
      if(status != positions[row+i][col].getStatus())
        return false;
    }
    return true;
  }
  
  /** 
   * Looks 4 tiles to the bottom-right to see if there are 4 adjacent tiles with
   * the same color
   */
  private boolean checkDiagonalBottomRight(int row, int col, Position[][] positions) {
    Status status = positions[row][col].getStatus();
    if (status != Status.YELLOW && status != Status.RED) {
      return false;
    }
    if (row+4 > env.getRows()) return false; //out of bounds
    if (col+4 > env.getCols()) return false; //out of bounds
    for (int i = 0; i < 4; i++) {
      if(status != positions[row+i][col+i].getStatus())
        return false;
    }
    return true;
  }
  
  /** 
   * Looks 4 tiles to the top-right to see if there are 4 adjacent tiles with
   * the same color
   */
  private boolean checkDiagonalTopRight(int row, int col, Position[][] positions) {
    Status status = positions[row][col].getStatus();
    if (status != Status.YELLOW && status != Status.RED) {
      return false;
    }
    if (row-3 < 0) return false; //out of bounds
    if (col+4 > env.getCols()) return false; //out of bounds
    for (int i = 0; i < 4; i++) {
      if(status != positions[row-i][col+i].getStatus())
        return false;
    }
    return true;
  }
  
  /** 
   * Creates an identical copy of the provided array that is separate in
   * memory. This is the same method as found in Environment
   */
  private Position[][] clonePosition(Position[][] positions) {
    Position[][] clone = new Position[env.getRows()][env.getCols()];
    for (int row = 0; row < env.getRows(); row++) {
      for (int col = 0; col < env.getCols(); col++) {
        if (positions[row][col].getStatus() == Status.BLANK)
          clone[row][col] = new Position(row, col, Status.BLANK);
        else if (positions[row][col].getStatus() == Status.RED)
          clone[row][col] = new Position(row, col, Status.RED);
        else if (positions[row][col].getStatus() == Status.YELLOW)
          clone[row][col] = new Position(row, col, Status.YELLOW);
      }
    }
    return clone;
  }
}
