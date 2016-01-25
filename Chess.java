import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

class Chess implements Army{

	Piece pieces[][];

	List<Move> pawnMoves;
	List<Move> kingMoves;
	List<Move> queenMoves;
	List<Move> castleMoves;
	List<Move> horseMoves;
	List<Move> bishopMoves;

	public Chess(){

		pieces = new Piece[8][8];

		pawnMoves = Arrays.asList(
			new Move(0,1,"Step", new int[]{Move.ONLYMOVE,Move.DOUBLEFIRST}),new Move(1,1,"Step", new int[]{Move.ONLYCAPTURE}),new Move(-1,1,"Step", new int[]{Move.ONLYCAPTURE})
		);

		kingMoves = Arrays.asList(
			new Move(0,1,"Step"),new Move(0,-1,"Step"),new Move(1,1,"Step"),new Move(1,0,"Step"),new Move(1,-1,"Step"),new Move(-1,0,"Step"),new Move(-1,1,"Step"),new Move(-1,-1,"Step")
		);

		queenMoves = Arrays.asList(
			new Move(1,1,"Slide"),new Move(1,0,"Slide"),
			new Move(1,-1,"Slide"),new Move(0,-1,"Slide"),
			new Move(-1,-1,"Slide"),new Move(-1,0,"Slide"),
			new Move(-1,1,"Slide"),new Move(0,1,"Slide")
		);

		castleMoves = Arrays.asList(
			new Move(1,0,"Slide"),new Move(0,1,"Slide"),
			new Move(-1,0,"Slide"),new Move(0,-1,"Slide")
		);

		horseMoves = Arrays.asList(
			new Move(2,-1,"Jump"),new Move(2,1,"Jump"),new Move(1,2,"Jump"),new Move(-1,2,"Jump"),new Move(-2,1,"Jump"),new Move(-2,-1,"Jump"),new Move(1,-2,"Jump"),new Move(-1,-2,"Jump")
		);

		bishopMoves = Arrays.asList(
			new Move(1,1,"Slide"),new Move(-1,1,"Slide"),
			new Move(-1,-1,"Slide"),new Move(1,-1,"Slide")
		);
	}

	// public Piece get(int x, int y){
	// 	try{
	// 		Piece piece = pieces[x][y];
	// 		if(piece!=null){
	// 			return piece;
	// 		}
	// 	}catch(Exception e){
	// 		p("out of bounds at: "+x+", "+y);
	// 	}

	// 	return null;
	// }

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

	public Piece[][] loadPieces(char board_side){
		if(board_side == 'l'){
			for (int i=0; i<8; i++) {
				set(1,i,new Piece('b','p',pawnMoves));
			}
			set(0,3,new Piece('b','q', queenMoves	, new int[]{Piece.PROMOTION}));
			set(0,4,new Piece('b','k', kingMoves	));
			set(0,0,new Piece('b','c', castleMoves	));
			set(0,7,new Piece('b','c', castleMoves	));
			set(0,1,new Piece('b','h', horseMoves	));
			set(0,6,new Piece('b','h', horseMoves	));
			set(0,2,new Piece('b','b', bishopMoves	));
			set(0,5,new Piece('b','b', bishopMoves	));
			return pieces;
		}else if(board_side == 'r'){
			for (int i=0; i<8; i++) {
				set(6,i,new Piece('w','p',pawnMoves));
			}
			set(7,3,new Piece('w','q', queenMoves	, new int[]{Piece.DOUBLEMOVE, Piece.PROMOTION}));
			set(7,4,new Piece('w','k', kingMoves	));
			set(7,0,new Piece('w','c', castleMoves	));
			set(7,7,new Piece('w','c', castleMoves	));
			set(7,1,new Piece('w','h', horseMoves	));
			set(7,6,new Piece('w','h', horseMoves	));
			set(7,2,new Piece('w','b', bishopMoves	));
			set(7,5,new Piece('w','b', bishopMoves	));
			return pieces;
		}
		return null;
	}
	public static void p(Object o){System.out.println(o);}
}