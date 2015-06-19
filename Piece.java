import java.util.List;
import javax.swing.*;
class Piece {

	static int WRAPPING = 1, DOUBLEMOVE = 2;

	char color;
	char name ;
	List<Move> moves;
	boolean wrapping = false;
	boolean doubleMove = false;
	
	int[] abilities;

	boolean hasMoved = false;

	public Piece(char _color, char _name, List<Move> _moves){
		color = _color;
		name = _name;
		moves = _moves;
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
	}

	public char getColor(){
		return color;
	}

	public char getName(){
		return name;
	}



}