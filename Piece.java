import java.util.List;
import javax.swing.*;
import java.util.ArrayList;
class Piece {

	static int WRAPPING = 1, DOUBLEMOVE = 2, PROMOTION = 3;

	int loc_x, loc_y;

	char color;
	char name ;
	List<Move> moves;
	boolean wrapping = false;
	boolean double_move = false;
	boolean alive = true;

	boolean has_jumping_moves = false;

	int cost_value = 0;
	
	int[] abilities;

	boolean has_moved = false;
	ArrayList<BoardMoves> possibleMoves = new ArrayList<BoardMoves>();

	public Piece(Piece oldPiece){
		loc_x = oldPiece.loc_x;
		loc_y = oldPiece.loc_y;
		color = oldPiece.color;
		name = oldPiece.name;
		moves = oldPiece.moves;
		wrapping = oldPiece.wrapping;
		double_move = oldPiece.double_move;
		has_moved = oldPiece.has_moved;
	}

	public Piece(char _color, char _name, List<Move> _moves){
		color = _color;
		name = _name;
		moves = _moves;
		constructor();
	}

	public Piece(char _color, char _name, List<Move> _moves, int[] _abilities){
		color = _color;
		name = _name;
		moves = _moves;
		abilities = _abilities;
		
		for (int i=0; i<abilities.length; i++) {
			if(abilities[i]==WRAPPING){
				wrapping = true;
			}else if(abilities[i]==DOUBLEMOVE){
				double_move = true;
			}
		}
		constructor();
	}

	public void constructor(){

		//calculate the cost value of the piece with it's current moves

		for (Move this_move : moves ) {

			cost_value+=this_move.getCost();

			if(this_move.isJumpingMove()){
				has_jumping_moves = true;
			}
		}

		if(has_jumping_moves){
			cost_value+=2;
		}

		if(this.wrapping){
			cost_value+=2;
		}
	}

	public boolean getDoubleMove(){
		return double_move;
	}

	public void moveThePiece(){
		has_moved = true;
	}

	public boolean getHasMoved(){
		return has_moved;
	}

	public void setLocation(int x, int y){
		loc_x = x;
		loc_y = y;
	}

	public char getColor(){
		return color;
	}

	public char getName(){
		return name;
	}

	public int getCostValue(){
		return cost_value;
	}
}