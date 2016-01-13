import java.util.List;
import javax.swing.*;
import java.util.ArrayList;
class Piece {

	static int WRAPPING = 1, DOUBLEMOVE = 2, PROMOTION = 3;

	int locX, locY;

	char color;
	char name ;
	List<Move> moves;
	boolean wrapping = false;
	boolean double_move = false;
	boolean alive = true;

	boolean has_jumping_moves = false;

	int cost_value = 0;
	
	int[] abilities;

	boolean hasMoved = false;
	ArrayList<BoardMoves> possibleMoves = new ArrayList<BoardMoves>();

	public Piece(Piece oldPiece){
		locX = oldPiece.locX;
		locY = oldPiece.locY;
		color = oldPiece.color;
		name = oldPiece.name;
		moves = oldPiece.moves;
		wrapping = oldPiece.wrapping;
		double_move = oldPiece.double_move;
		hasMoved = oldPiece.hasMoved;
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

		for (Move thisMove : moves ) {
			System.out.println(thisMove.moveType);
			if(thisMove.moveType == "Step"){
				System.out.println("step move");
				cost_value++;
			}
			if(thisMove.moveType == "Slide"){
				System.out.println("slide move");
				cost_value+=2;
			}
			if(thisMove.moveType == "Jump"){
				has_jumping_moves = true;
				System.out.println("slide move");
				cost_value++;
			}
		}

		if(has_jumping_moves){
			cost_value+=2;
		}

		if(wrapping){
			cost_value+=2;
		}
	}

	public void setLocation(int x, int y){
		locX = x;
		locY = y;
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