import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

interface Army {

	public void set(int x, int y, Piece piece);

	public Piece[][] loadPieces(char board_side);

	public static void p(Object o){System.out.println(o);}
}