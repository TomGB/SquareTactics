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

	String move_type;

	public Move(int _x, int _y, String _move_type) {
		x = _x;
		y = _y;
		move_type = _move_type;
	}
	public Move(int _x, int _y, String _move_type, int[] _special) {
		x = _x;
		y = _y;
		move_type = _move_type;
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