import java.util.ArrayList;
import java.util.Collections;

class ArtificialPlayer {
	SquareTactics game;

	public ArtificialPlayer(SquareTactics _game){
		game = _game;
	}

	public void takeTurn(){
		ArrayList<Piece> myPieces = game.board.getByColor('b');

		Collections.shuffle(myPieces);

		for (Piece piece : myPieces) {
			game.update(piece.loc_x,piece.loc_y);
			if(game.possibleMoves.size()>0){

				Collections.shuffle(game.possibleMoves);

				BoardMoves move = game.possibleMoves.get(0);
				game.update(move.x,move.y);
			}
		}

	}
}