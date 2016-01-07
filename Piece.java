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
	boolean doubleMove = false;
	boolean alive = true;

	int costValue = 0;
	
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
		doubleMove = oldPiece.doubleMove;
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
				doubleMove = true;
			}
		}
		constructor();
	}

	public void constructor(){
		for (Move thisMove : moves ) {
			System.out.println(thisMove.moveType);
			if(thisMove.moveType == "Step"){
				System.out.println("step move");
				costValue++;
			}
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



}