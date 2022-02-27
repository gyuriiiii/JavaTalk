// ChatMsg.java ä�� �޽��� ObjectStream ��.
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String code; // 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image
	private String data;
	public ImageIcon img;
	public ImageIcon emoji;
	public ImageIcon profile; // ������ ����

	public String userlist; // ������ ��� ����
	public String room_id; // ä�ù� ������ȣ
	public String newuser; // ���ο� ������ �̸�
	
	public String outuser; // ���� �����
	public String chatwith; // ä�� ���

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