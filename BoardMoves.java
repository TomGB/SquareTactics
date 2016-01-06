public class BoardMoves {
	int x, y;
	String moveType;
	char pieceType;
	Move moveProperties;

	public BoardMoves(int _x, int _y, String _moveType){
		x = _x;
		y = _y;
		moveType = _moveType;
	}

	public BoardMoves(int _x, int _y, String _moveType, Move _move){
		x = _x;
		y = _y;
		moveType = _moveType;
		moveProperties = _move;
	}

	public BoardMoves(int _x, int _y, String _moveType, char _pieceType){
		x = _x;
		y = _y;
		moveType = _moveType;
		pieceType = _pieceType;
	}
}