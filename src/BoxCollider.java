import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BoxCollider implements Serializable{
	
	static final long serialVersionUID = -7298352464830308761L;
	
	Vec3 vec1;
	Vec3 vec2;
	static Timer timer = new Timer();
	BoxCollider(Vec3 vec1, Vec3 vec2){
		this.vec1 = vec1;
		this.vec2 = vec2;
	}
	
	public boolean isTouching(BoxCollider b){
		Vec3 B1 = this.vec1;
		Vec3 B2 = this.vec2;
		
		Vec3 A1 = b.vec1;
		Vec3 A2 = b.vec2;
		
		if(((A1.x<=B2.x && A2.x>=B2.x) || (A1.x>=B2.x && A2.x<=B2.x)) &&
			((A1.y<=B2.y && A2.y>=B2.y) || (A1.y>=B2.y && A2.y<=B2.y)) &&
			((A1.z<=B2.z && A2.z>=B2.z) || (A1.z>=B2.z && A2.z<=B2.z))){
			return true;
		}
		return false;
	}
	
	public void isTouchingArrayGrav(ArrayList objs){
		BoxCollider[] array = new BoxCollider[objs.size()];
		for(int i=0; i<objs.size(); i++){
			array[i] = ((Object3d)objs.get(i)).boxCollider;
		}
		for(BoxCollider b:array){
			if(isTouching(b)){
				if(!Draw.jump){
					Object3d.setVelocityArrayY(objs, 0);
					Draw.grounded = true;
					break;
//					Draw.toggled = true;
//					Draw.toggled = false;
				}else{
					timer.schedule(new setVals(), 40);
				}
			}else{
				timer.schedule(new setGrounded(), 10);
			}
		}
	}
	
	public void translate(Vec3 vec){
		vec1.translate(vec);
		vec2.translate(vec);
	}
	class setGrounded extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!Draw.jump){
				timer.schedule(new setVals(), 0);
			}
		}
	}
	class setVals extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Draw.jump = false;
			Draw.grounded = false;
		}
	}
}
