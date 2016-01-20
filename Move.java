class Move {

	static int ONLYMOVE = 1;
	static int ONLYCAPTURE = 2;
	static int DOUBLEFIRST = 3;

	int x;
	int y;
	int[] special;

	int move_cost = 0;

	boolean capture = true;
	boolean restricted = false;
	public boolean doubleFirst = false;

	String move_type;

	public Move(int _x, int _y, String _move_type) {
		x = _x;
		y = _y;
		move_type = _move_type;
		calculateCost();
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
		calculateCost();
	}

	private void calculateCost(){
		if(move_type == "Step"){
			System.out.println("step move");
			move_cost++;
		}
		if(move_type == "Slide"){
			System.out.println("slide move");
			move_cost+=2;
		}
		if(move_type == "Jump"){
			System.out.println("slide move");
			move_cost++;
		}
	}

	public int getCost(){
		return move_cost;
	}

	public boolean isJumpingMove(){
		return (move_type == "Jump");
	}

	public boolean canOnlyMove(){
		return restricted&&!capture;
	}
	public boolean canOnlyCapture(){
		return restricted&&capture;
	}
}