// import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
class Board {
	Piece pieces[][];
	int width, height;
	int turnNum;
	int drawPositionX, drawPositionY;

	int army_cost = 0;

	ArrayList<Piece[][]> history;

	public Board(int _width, int _height, int _posX, int _posY){
		width = _width;
		height = _height;
		drawPositionX = _posX;
		drawPositionY = _posY;
		pieces = new Piece[width][height];
	}

	public void clear(){
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				pieces[i][j]=null;
			}
		}
	}

	public void setUp(){
		history = new ArrayList<Piece[][]>();
		turnNum=0;

		pieces = new Chess().loadPieces();
		// pieces = new DeathFromAbove().loadPieces();
	}

	public int getArmyScore(){
		int temp_score =0;
		for (int i=0; i<8; i++) {
			temp_score += get(0,i).getCostValue();
			temp_score += get(1,i).getCostValue();
		}
		return temp_score;
	}

	public Piece get(int x,int y){
		try{
			return pieces[x][y];
		}catch(Exception e){
			p("out of bounds get: "+e);
			return null;
		}
	}

	public void set(int x, int y, Piece piece){
		try{
			pieces[x][y]=piece;
			if(piece!=null){
				piece.setLocation(x,y);
			}
		}catch(Exception e){
			p("out of bounds at: "+x+", "+y);
		}
	}

	public ArrayList<Piece> getByColor(char color){
		ArrayList<Piece> pieceList = new ArrayList<Piece>();
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				Piece temp = get(i,j);
				if(temp!=null&&temp.getColor()==color){
					pieceList.add(temp);
				}
			}
		}
		return pieceList;
	}

	public void saveHistory(){
		Piece[][] temp = new Piece[pieces.length][pieces[0].length];
		   
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				Piece p = pieces[i][j];
			    if (p != null) {
			        temp[i][j] = new Piece(p);
			    }
			}
		}
		history.add(temp);
	}

	public void loadHistory(){
		pieces = history.remove(--turnNum);
		for (int i=0; i<pieces.length; i++) {
			for (int j=0; j<pieces.length; j++) {
				Piece tempPiece = get(i,j);
				if(tempPiece!=null){
					tempPiece.setLocation(i,j);
				}
			}
		}
	}

	public static void p(Object o){System.out.println(o);}

}