import java.awt.*;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import java.util.*;
import java.util.Timer;
public class Draw implements Serializable{
	
	static final long serialVersionUID = -7298352464830308761L;
	boolean keyDown = false;
	float mul = 1;
	public int score = 0;
	public ArrayList<Object3d> objects = new ArrayList<Object3d>();
	public static int W = 700;
	public static int H = 700;
	public static int camX = W/2;
	public static int camY = H/2;
	public static int camZ = 0;
	public static int x = 0;
	public static int y = 200;
	public static int z = 0;
	public static final Vec3 CENTER = new Vec3(W/2, H/2, 0);
	public FrameDraw panel;
	public JLabel scoreLabel;
	public static JFrame frame;
	public static Object3d player;
//	public BoxCollider playerBox;
	public static boolean jump = false;
	public static boolean grounded = false;
	public static boolean toggled = false;
	static int objNum = 8;
	public static float frameRate = 10;
	public static float rate = frameRate/30;
	static boolean[] keys;
	void load(){
		for(int i = 0; i < 1000; i ++){
			keys = new boolean[i];
		}
		player = new RectPrism(CENTER.add(new Vec3(0, 50, 0)), 50, 50, 50);
//		playerBox = new BoxCollider(CENTER.add(new Vec3(0, 50, 0)), CENTER.add(new Vec3(1, 50, 1)));
//		player.boxCollider = playerBox;
		frame = new JFrame();
		
		scoreLabel = new JLabel(String.valueOf(score));
		scoreLabel.setSize(new Dimension(100, 100));
		scoreLabel.setHorizontalAlignment((int) JLabel.CENTER_ALIGNMENT);
		scoreLabel.setFont(new Font("Arial", 50, 50));
		scoreLabel.setOpaque(true);
		
		panel = new FrameDraw();
		panel.setSize(new Dimension(W, H));
		
		frame.setPreferredSize(new Dimension(W, H + 50));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(scoreLabel, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		
		(panel).paintComponent(panel.getGraphics());
		
		frame.pack();
		Timer t = new Timer();
		t.schedule(new DoStuff(), 0, (long) frameRate);
	}
	void init(){
		for(int i=objNum-1; i>=0; i--){
			RectPrism p = new RectPrism(CENTER, 250, 10, 250);
			p.translate(new Vec3(0, 200, 500*i));
			objects.add(p);
		}
		load();
	}
	
	@SuppressWarnings("null")
	Draw() {
		init();
	}
	class DoStuff extends TimerTask{
		@Override
		public void run() {
			(panel).repaint();
		}
	}
	
	@SuppressWarnings("unused")
	private static void sleep(int m){
		try{Thread.sleep(m);}catch(Exception e){}
	}
	
	@SuppressWarnings("serial")
	class FrameDraw extends JPanel implements KeyListener{
		public static final long MAX_DIST = 1l;
		FrameDraw(){
			addKeyListener(this);
		}
		protected void paintComponent(Graphics g) {
			update();
			g.clearRect(0, 0, W*2, H*2);
			int[] a1 = {0,4,5,1};
			int[] a2 = {2,0,1,3};
			int[] a3 = {6,7,5,4};
			int[] a4 = {6,7,3,2};
			int[] a5 = {3,7,5,1};
			int[] a6 = {2,6,4,0};
			int[][] arrays = {a1, a2, a3, a4, a5, a6};
			arrays = sortFaces(arrays);
			
			g.setColor(Color.black);
			for(int j=0; j<objects.size(); j++){
				Object3d obj = objects.get(j);
				for (int i = 0; i < obj.size(); i++) {
					Vec3 vec = (Vec3) obj.get(i);
					drawPoint(vec, g);
					char[] charAr = {Integer.toString(i).charAt(0)};
					g.drawChars(charAr, 0, 1, getX(vec.x, vec.z), getY(vec.y, vec.z));
				}
			}
			draw3d(arrays, g);

			Object3d obj = player;
			for (int i = 0; i < obj.size(); i++) {
				Vec3 vec = (Vec3) obj.get(i);
				drawPoint(vec, g);
				char[] charAr = {Integer.toString(i).charAt(0)};
				g.drawChars(charAr, 0, 1, getX(vec.x, vec.z), getY(vec.y, vec.z));
			}
			
			g.setColor(Color.green);
//			drawPlayerPoint(CENTER.add(new Vec3(0, 190, 0)), g, 50);
		}
		private int[][] getArray(ArrayList<Vec3> vecs, int[] array){
			int[][] rArray = new int[2][array.length];
			
			for(int i=0; i<array.length; i++){
				Vec3 vec = (Vec3)(vecs.get(array[i]));
				int x = (int)vec.x;
				int y = (int)vec.y;
				int z = (int)vec.z;
				rArray[0][i] = getX(x, z);
				rArray[1][i] = getY(y, z);
			}
			return rArray;
		}
		
		private void draw3d(int[][] arrays, Graphics g){
			for(int i=0; i<objects.size(); i++){
				Object3d obj = objects.get(i);
				for(int j=0; j<arrays.length; j++){
					int[] aInt = arrays[j];
					int[][] aaInt = getArray(obj.vecs, aInt);
					g.setColor(Color.red);
					g.fillPolygon(aaInt[0], aaInt[1], aInt.length);
					g.setColor(Color.black);
					g.drawPolygon(aaInt[0], aaInt[1], aInt.length);
				}
			}
		}
		private void drawPoint(Vec3 vec, Graphics g){
			g.fillRect(getX(vec.x, vec.z), getY(vec.y, vec.z), 5, 5);
		}
		private void drawPlayerPoint(Vec3 vec, Graphics g, int size){
			g.fillRect((int)(vec.x - size/2), (int)(vec.y-size/2), size, size);
		}
		private int getX(float x, float z){
//			int rInt = (int)(((x-W/2)*MAX_DIST/z)+W/2);
			int average_len = W/2;
			int rInt = (int) (((x-camX) * ( average_len ) ) / ( z + ( W/2) -camZ)) + W/2;

			if(camZ >= z+average_len){
				rInt = (int) (((x-camX) * ( average_len ) ) / (150)) + W/2;
			}
			return rInt;
		}
		private int getY(float y, float z){
//			int rInt = (int)(((y-H/2)*MAX_DIST/z)+H/2);
			int average_len = H/2;
			int rInt = (int) (((y-camY) * ( average_len) ) / ( z+ ( average_len) -camZ)) + H/2;
			if(camZ >= z+average_len){
				rInt=(int) (W-camY + y);
			}
			return rInt;
		}
		private int[][] sortFaces(int[][] arrays){
			Object3d obj = objects.get(0);
			Integer[] VecsMidZ = new Integer[arrays.length];
			for(int i=0; i<arrays.length; i++){
				Vec3 vec1 = new Vec3(0,0,0);
				vec1.x = ((Vec3)obj.get(arrays[i][0])).x;
				vec1.y = ((Vec3)obj.get(arrays[i][0])).y;
				vec1.z = ((Vec3)obj.get(arrays[i][0])).z;
				Vec3 vec2 = new Vec3(0,0,0);//(Vec3) vecs.get(arrays[i][2]);
				vec2.x = ((Vec3)obj.get(arrays[i][2])).x;
				vec2.y = ((Vec3)obj.get(arrays[i][2])).y;
				vec2.z = ((Vec3)obj.get(arrays[i][2])).z;
				Vec3.midpoint(vec1, vec2);
				
				VecsMidZ[i]=(Integer)(int) vec1.z;
//				VecsMidZ[i] = ((int) ((Vec3)(vecs.get(iArray[0]))).z);
				VecsMidZ[i] *= 10;
				VecsMidZ[i] += i;
			}
			Arrays.sort(VecsMidZ, Collections.reverseOrder());
			@SuppressWarnings("unused")
			int[] sorted = new int[arrays.length];
			int[][] temp = new int[arrays.length][arrays[0].length];
			for(int i=0; i<arrays.length; i++){
				for(int j=0; j<arrays[0].length; j++){
					temp[i][j] = arrays[i][j];
				}
			}
//			temp = arrays;
			
			for(int i=0; i<arrays.length; i++){
				int mod = VecsMidZ[i]%10;
				if (mod<0) mod += 10;
				arrays[i] = temp[mod];
			}
			
			return arrays;
		}
		public void addNotify() {
	        super.addNotify();
	        requestFocus();
	    }
		
		@Override
		public void keyTyped(KeyEvent e) {

		}
		int keyNum;
		int ticks = 0;
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			keyDown = true;
			keyNum = e.getKeyCode();
			keys[e.getKeyCode()] = true;//pressed
			if(keys[KeyEvent.VK_SPACE] && /*toggled*/ grounded){
				jump=true;
//				BoxCollider.timer.schedule(new setToggled(), 40);
			}
		}
		class setToggled extends TimerTask{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Draw.toggled = false;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(keys[KeyEvent.VK_W] && keyDown){
				ticks = 5;
			}
			keys[e.getKeyCode()] = false;
			
		}
		public void update(){
			BoxCollider playerBox = player.boxCollider;
			if(ticks > 0 && mul == 1){
				ticks -= rate;
			}
			panel.requestFocus();
//			Object3d.addVelocityArray(objects, new Vec3(0, -1, 0).multiply(0.6f*rate));//gravity happens to be 0.32 units be second
			player.addVelocity(new Vec3(0, 1, 0).multiply(0.6f*rate));
			ArrayList left = new ArrayList<Object3d>();
			ArrayList right = new ArrayList<Object3d>();
			for(int i=objects.size()-1; i>=0; i--){
				if(i%2 == 0){
					left.add(objects.get(i));
				}else{
					right.add(objects.get(i));
				}
			}
			Object3d.setVelocityArrayX(left, 1, 500);
			Object3d.setVelocityArrayX(right, -1, 500);
			playerBox.isTouchingArrayGrav(objects);
			for(int i=objects.size()-1; i>=0; i--){
				Object3d obj = objects.get(i);
				if(playerBox.isTouching(obj.boxCollider)){
					if(objects.size() - i+1>score){
						score = objects.size() - i;
						scoreLabel.setText(String.valueOf(score));
					}
					if(score == objects.size()){
						scoreLabel.setText("YOU WIN!!!");
					}
				}
			}
			Object3d.updateArray(objects);
			boolean updated = false;
			mul = 1;
			if(keys[KeyEvent.VK_SHIFT] || keys[KeyEvent.VK_W] && ticks > 0 || keys[KeyEvent.VK_R]){
				mul = 2;
			}
			mul*=rate;
			if(keys[KeyEvent.VK_W] && keyDown){
//				Object3d.translateArray(objects, Vec3.FORWARD.multiply(10*mul));
				player.translate(Vec3.FORWARD.multiply(10*mul));
//				player.update();
				camZ = (int) player.position.z;
				z -= 10;
			} if(keys[KeyEvent.VK_S] && keyDown){
//				Object3d.translateArray(objects, Vec3.BACKWARD.multiply(10*mul));
				player.translate(Vec3.BACKWARD.multiply(10*mul));
//				player.update();
				camZ = (int) player.position.z;
				z += 10;
			} if(keys[KeyEvent.VK_A] && keyDown){
//				Object3d.translateArray(objects, Vec3.LEFT.multiply(10*mul));
				player.translate(Vec3.LEFT.multiply(10*mul));
//				player.update();
				camX = (int) player.position.x;
				x -= 10;
			} if(keys[KeyEvent.VK_D] && keyDown){
//				Object3d.translateArray(objects, Vec3.RIGHT.multiply(10*mul));
				player.translate(Vec3.RIGHT.multiply(10*mul));
//				player.update();
				camX = (int) player.position.x;
				x += 10;
			} if(keys[KeyEvent.VK_SPACE] && keyDown && /*toggled*/ grounded){
				Vec3 jumpVec = new Vec3(0, -20, 0);
				player.setVelocity(jumpVec.multiply(rate));
//				player.update();
//				Object3d.addVelocityArray(objects, jumpVec.multiply(rate));
			}
			if(!updated){
				player.update();
			}
			camY = (int) player.position.y - 150;
			
			if(keys[KeyEvent.VK_M]){
				Main.save();
			}
			if(keys[KeyEvent.VK_L]){
				Main.load();
			}
			if(keys[KeyEvent.VK_R]){
				Main.restart();
			}
//			System.out.println(y);
		}
	}
}
