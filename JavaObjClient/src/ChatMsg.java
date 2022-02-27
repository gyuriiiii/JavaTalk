// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image
	private String data;
	public ImageIcon img;
	public ImageIcon emoji;
	public ImageIcon profile; // 프로필 사진

	public String userlist; // 참가자 목록 변수
	public String room_id; // 채팅방 고유번호
	public String newuser; // 새로운 참가자 이름
	
	public String outuser; // 나간 사용자
	public String chatwith; // 채팅 상대

	public ChatMsg(String id, String code, String msg) {
		this.id = id;
		this.code = code;
		this.data = msg;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public String getId() {
		return id;
	}
	
	public String getUserlist() {
		return userlist;
	}
	
	public String getRoomId() {
		return room_id;
	}
	
	public String getNewuser() {
		return newuser;
	}
	
	public String getOutuser() {
		return outuser;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setImg(ImageIcon img) {
		this.img = img;
	}
	
	public void setEmoji(ImageIcon emoji) {
		this.emoji = emoji;
	}
	
	public void setProfile(ImageIcon profile) {
		this.profile = profile;
	}
	
	public void setUserlist(String userlist) {
		this.userlist = userlist;
	}
	
	public void setRoomId(String room_id) {
		this.room_id = room_id;
	}
	
	public void setNewuser(String newuser) {
		this.newuser = newuser;
	}
	
	public void setOutuser(String outuser) {
		this.outuser = outuser;
	}
	
	public void setChatwith(String chatwith) {
		this.chatwith = chatwith;
	}
}