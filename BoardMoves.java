public class BoardMoves {
	int x, y;
	String move_type;
	char piece_type;
	Move move_properties;
	boolean only_first_move = false;

	public BoardMoves(int _x, int _y, String _move_type){
		x = _x;
		y = _y;
		move_type = _move_type;
	}

	public BoardMoves(int _x, int _y, String _move_type, Move _move){
		x = _x;
		y = _y;
		move_type = _move_type;
		move_properties = _move;
	}

	public BoardMoves(int _x, int _y, String _move_type, Move _move, boolean _only_first_move){
		x = _x;
		y = _y;
		move_type = _move_type;
		move_properties = _move;
		only_first_move = _only_first_move;
	}

	public BoardMoves(int _x, int _y, String _move_type, char _piece_type){
		x = _x;
		y = _y;
		move_type = _move_type;
		piece_type = _piece_type;
	}
}