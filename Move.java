class Move {

	static int ONLYMOVE = 1;
	static int ONLYCAPTURE = 2;
	static int DOUBLEFIRST = 3;

	int x;
	int y;
	int[] special;

	boolean capture = true;
	boolean restricted = false;
	public boolean doubleFirst = false;

	String moveType;

	public Move(int _x, int _y, String _moveType) {
		x = _x;
		y = _y;
		moveType = _moveType;
	}
	public Move(int _x, int _y, String _moveType, int[] _special) {
		x = _x;
		y = _y;
		moveType = _moveType;
		special = _special;
		for (int i=0; i<special.length; i++) {
			if(special[i]==ONLYMOVE){
				capture = false;
				restricted = true;
			}else if(special[i]==ONLYCAPTURE){
				capture = true;
				restricted = true;
			}else if(special[i]==DOUBLEFIRST){
				doubleFirst = true;
			}
		}
	}
	public boolean canOnlyMove(){
		return restricted&&!capture;
	}
	public boolean canOnlyCapture(){
		return restricted&&capture;
	}
}