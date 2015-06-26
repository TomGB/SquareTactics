class BoardMoves {
	int x, y;
	String moveType;
	char pieceType;

	public BoardMoves(int _x, int _y, String _moveType){
		x = _x;
		y = _y;
		moveType = _moveType;
	}

	public BoardMoves(int _x, int _y, String _moveType, char _pieceType){
		x = _x;
		y = _y;
		moveType = _moveType;
		pieceType = _pieceType;
	}
}