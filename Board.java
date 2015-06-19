import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
class Board {
	Piece pieces[][];
	int width, height;
	int turnNum;

	ArrayList<String> history;;

	public Board(int _width, int _height){
		width = _width;
		height = _height;
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
		history = new ArrayList<String>();
		turnNum=0;



		List<Move> pawnMoves = Arrays.asList(
			new Move(0,1,"Step", new int[]{Move.ONLYMOVE,Move.DOUBLEFIRST}),new Move(1,1,"Step", new int[]{Move.ONLYCAPTURE}),new Move(-1,1,"Step", new int[]{Move.ONLYCAPTURE})
		);

		List<Move> kingMoves = Arrays.asList(
			new Move(0,1,"Step"),new Move(0,-1,"Step"),new Move(1,1,"Step"),new Move(1,0,"Step"),new Move(1,-1,"Step"),new Move(-1,0,"Step"),new Move(-1,1,"Step"),new Move(-1,-1,"Step")
		);

		// List<Move> queenMoves = Arrays.asList(
		// 	new Move(1,1,"Slide"),new Move(1,0,"Slide"),
		// 	new Move(1,-1,"Slide"),new Move(0,-1,"Slide"),
		// 	new Move(-1,-1,"Slide"),new Move(-1,0,"Slide"),
		// 	new Move(-1,1,"Slide"),new Move(0,1,"Slide")
		// );

		// List<Move> castleMoves = Arrays.asList(
		// 	new Move(1,0,"Slide"),new Move(0,1,"Slide"),
		// 	new Move(-1,0,"Slide"),new Move(0,-1,"Slide")
		// );

		// List<Move> horseMoves = Arrays.asList(
		// 	new Move(2,-1,"Jump"),new Move(2,1,"Jump"),new Move(1,2,"Jump"),new Move(-1,2,"Jump"),new Move(-2,1,"Jump"),new Move(-2,-1,"Jump"),new Move(1,-2,"Jump"),new Move(-1,-2,"Jump")
		// );

		// List<Move> bishopMoves = Arrays.asList(
		// 	new Move(1,1,"Slide"),new Move(-1,1,"Slide"),
		// 	new Move(-1,-1,"Slide"),new Move(1,-1,"Slide")
		// );

		List<Move> queenMoves = Arrays.asList(
		 new Move(0,2,"Jump"),new Move(-1,2,"Jump"),new Move(1,2,"Jump"),
			new Move(0,-2,"Jump"),new Move(-1,-2,"Jump"),new Move(1,-2,"Jump"),
			new Move(2,0,"Jump"),new Move(2,-1,"Jump"),new Move(2,1,"Jump"),
			new Move(-2,0,"Jump"),new Move(-2,-1,"Jump"),new Move(-2,1,"Jump")
		);


		List<Move> castleMoves = Arrays.asList(
			new Move(0,1,"Step"),new Move(0,-1,"Step"),new Move(1,0,"Step"),new Move(-1,0,"Step"),new Move(-2,-2,"Jump"),new Move(-2,2,"Jump"),new Move(2,-2,"Jump"),new Move(2,2,"Jump")
		);

		List<Move> horseMoves = Arrays.asList(
			new Move(1,1,"Step"),new Move(1,-1,"Step"),new Move(-1,1,"Step"),new Move(-1,-1,"Step"),
			new Move(2,2,"Jump"),new Move(0,2,"Jump"),new Move(-2,2,"Jump"),new Move(2,0,"Jump"),new Move(-2,0,"Jump"),new Move(0,-2,"Jump")
		);
		

		List<Move> bishopMoves = Arrays.asList(
			new Move(-1,-1,"Step"),new Move(1,-1,"Step"),
			new Move(-1,1,"Slide"),new Move(1,1,"Slide")
		);

		for (int i=0; i<8; i++) {
			set(1,i,new Piece('b','p',pawnMoves));
		}
		set(0,3,new Piece('b','q', queenMoves	, new int[]{Piece.DOUBLEMOVE}));
		set(0,4,new Piece('b','k', kingMoves	));
		set(0,0,new Piece('b','c', castleMoves	));
		set(0,7,new Piece('b','c', castleMoves	));
		set(0,1,new Piece('b','h', horseMoves	));
		set(0,6,new Piece('b','h', horseMoves	));
		set(0,2,new Piece('b','b', bishopMoves	, new int[]{Piece.WRAPPING}));
		set(0,5,new Piece('b','b', bishopMoves	, new int[]{Piece.WRAPPING}));

		for (int i=0; i<8; i++) {
			set(6,i,new Piece('w','p',pawnMoves));
		}
		set(7,3,new Piece('w','q', queenMoves	, new int[]{Piece.DOUBLEMOVE}));
		set(7,4,new Piece('w','k', kingMoves	));
		set(7,0,new Piece('w','c', castleMoves	));
		set(7,7,new Piece('w','c', castleMoves	));
		set(7,1,new Piece('w','h', horseMoves	));
		set(7,6,new Piece('w','h', horseMoves	));
		set(7,2,new Piece('w','b', bishopMoves	, new int[]{Piece.WRAPPING}));
		set(7,5,new Piece('w','b', bishopMoves	, new int[]{Piece.WRAPPING}));
	}
	public boolean offBoard(int x,int y){
		return !(x>=0&&x<width&&y>=0&&y<height);
	}
	public Piece get(int x,int y){
		try{
			return pieces[x][y];
		}catch(Exception e){
			p("out of bounds "+e);
			return null;
		}
	}
	public void set(int x, int y, Piece _piece){
		try{
			pieces[x][y]=_piece;
		}catch(Exception e){

		}
	}
	public boolean isBlack(int x, int y){
		Piece tempPiece = get(x,y);
		if(tempPiece==null){
			return false;
		}else{
			return (tempPiece.getColor()=='b');
		}
	}
	public boolean isWhite(int x, int y){
		Piece tempPiece = get(x,y);
		if(tempPiece==null){
			return false;
		}else{
			return (tempPiece.getColor()=='w'||tempPiece.getColor()=='k');
		}
		
	}
	public boolean validMove(int x1, int y1, int x2, int y2, Piece piece){
		// if(get(x2,y2)!=null){
		// 	return false;
		// }
		// if(x1==x2){ //piece moved in y axis
		// 	int low = (y1<y2?y1:y2);
		// 	int high = (y1>y2?y1:y2);
		// 	for (int i=low+1; i<high; i++) {
		// 		if(get(x1,i)!=null){
		// 			return false;
		// 		}
		// 	}
		// }else if(y1==y2){ //if piece moved in x axis
		// 	int low = (x1<x2?x1:x2);
		// 	int high = (x1>x2?x1:x2);
		// 	for (int i=low+1; i<high; i++) {
		// 		if(get(i,y1)!=null){
		// 			return false;
		// 		}
		// 	}
		// }else{ //if attempted to move diagonally
			// return false;
		// }
		// if(piece.getColor()!='k'&&isKingSpace(x2,y2)){
		// 	return false;
		// }
		return true;
	}
	public boolean isKingSpace(int x, int y){
		return ((x==0&&y==0)||(x==0&&y==height-1)||(x==width-1&&y==0)||(x==width-1&&y==height-1)||(x==width/2&&y==height/2));
	}
	public boolean isEnemyKingSpace(int x, int y){
		return ((x==0&&y==0)||(x==0&&y==height-1)||(x==width-1&&y==0)||(x==width-1&&y==height-1)||(x==width/2&&y==height/2&&!(get(x,y).getColor()=='k')));
	}
	// public void takePieces(int x, int y, Piece piece){
	// 	if(y<width-2&&isEnemyPawn(get(x, y+1), piece)&&(isFriend(x, y+2, piece)||isEnemyKingSpace(x,y+2))){
	// 		set(x, y+1, null);
	// 	}
	// 	if(x<width-2&&isEnemyPawn(get(x+1, y), piece)&&(isFriend(x+2, y, piece)||isEnemyKingSpace(x+2,y))){
	// 		set(x+1, y, null);
	// 	}
	// 	if(y>1&&isEnemyPawn(get(x, y-1), piece)&&(isFriend(x, y-2, piece)||isEnemyKingSpace(x,y-2))){
	// 		set(x, y-1, null);
	// 	}
	// 	if(x>1&&isEnemyPawn(get(x-1, y), piece)&&(isFriend(x-2, y, piece)||isEnemyKingSpace(x-2,y))){
	// 		set(x-1, y, null);
	// 	}
	// }
	public boolean isEnemyPawn(Piece piece2, Piece piece){
		if(piece2!=null){
			return ((piece2.getColor()=='b'&&(piece.getColor()=='w'||piece.getColor()=='k')))||(piece2.getColor()=='w'&&piece.getColor()=='b');
		}else{
			return false;
		}
	}
	public boolean isFriend(int x, int y, Piece piece){
		char color = piece.getColor();
		Piece tempPiece = get(x,y);
		if(tempPiece==null){
			return false;
		}else{
			return ((get(x,y).getColor()=='b'&&color=='b')||((get(x,y).getColor()=='w'||get(x,y).getColor()=='k')&&(color=='w'||color=='k')));
		}
	}
	public boolean checkKing(){
		return false;
	}
	public boolean checkWin(){
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				Piece tempPiece = get(i,j);
				if(tempPiece!=null&&tempPiece.getColor()=='k'){
					if(i==width/2&&j==height/2){
						return false;
					}
					return(isKingSpace(i,j));
				}
			}
		}
		return false;
	}
	public void saveHistory(){
		// String temp = "";
		// for (int i=0; i<width; i++) {
		// 	for (int j=0; j<height; j++) {
		// 		temp+=pieces[i][j].getColor();
		// 	}
		// }
		// history.add(temp);
	}
	public void loadHistory(){
		// String temp = history.remove(--turnNum);
		// for (int i=0; i<width; i++) {
		// 	for (int j=0; j<height; j++) {
		// 		pieces[i][j]=temp.charAt(height*i+j);
		// 	}
		// }
		// p("history loaded");
	}

	public static void p(Object o){System.out.println(o);}

}