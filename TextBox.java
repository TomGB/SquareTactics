import java.util.ArrayList;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

class TextBox {

	static ArrayList<TextBox> ALL_TEXT_BOXES = new ArrayList<TextBox>();

	SquareTactics squaretactics;

	int posX, posY, width, height;
	boolean active = true, visible = false;
	String text;
	Image image;
	final Color light = new Color(255,255,255,200);
	final Color dark = new Color(0,0,0,40);

	public TextBox(SquareTactics _squaretactics, int _posX, int _posY, int _width, int _height, String _text){
		squaretactics = _squaretactics;
		posX = _posX;
		posY = _posY;
		width = _width;
		height = _height;
		text = _text;

		ALL_TEXT_BOXES.add(this);
	}
	public TextBox(SquareTactics _squaretactics, int _posX, int _posY, int _width, int _height, Image _image){
		squaretactics = _squaretactics;
		posX = _posX;
		posY = _posY;
		width = _width;
		height = _height;
		image = _image;

		ALL_TEXT_BOXES.add(this);
	}

	public static boolean CHECK_HOVER(int mX, int mY){
		for(TextBox text_box : ALL_TEXT_BOXES){
  			if(text_box.visible && text_box.active && text_box.inside(mX, mY)){
  				text_box.on_hover();
  			}
		}
		return false;
	}

	public static void CHECK_CLICK(int mX, int mY){
		for(TextBox text_box : ALL_TEXT_BOXES){
  			if(text_box.visible && text_box.active && text_box.inside(mX, mY)){
  				text_box.on_click();
  			}
		}
	}

	public static void DRAW_ALL(Graphics g){
		for(TextBox text_box : ALL_TEXT_BOXES){
  			if(text_box.visible){
  				text_box.draw(g);
  			}
		}
	}

	public static void HIDE_ALL(){
		for(TextBox text_box : ALL_TEXT_BOXES){
  			text_box.set_visible(false);
		}
	}

	public void do_action(){

	}

	public boolean on_hover(){
		return active;
	}

	public void on_click(){
		if(active){
			do_action();
		}
	}
	public void draw(Graphics g){
		g.setColor(active?light:dark);
		g.fillRect(posX,posY,width,height);
		g.setColor(Color.black);
		g.drawRect(posX,posY,width,height);
		if(text!=null){
			g.drawString(text,posX+20,posY+26);
		}else{
			g.drawImage(image,74,12, null);
		}
	}
	public void set_active() {
		active = true;
	}
	public void set_inactive() {
		active = false;
	}
	public void set_visible(boolean input) {
		visible = input;
	}
	public boolean inside(int x, int y){
		return (x>posX&&x<posX+width&&y>posY&&y<posY+height);
	}
}