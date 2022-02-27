
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaObjClientView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	private JLabel lblPartnerName; // 상대 이름
	private JTextPane textArea;
	private JTextPane textAreaMenu; // 채팅방 상단 메뉴

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JButton emojiBtn; // 이모티콘 버튼

	public JavaObjClientView view;
	public JavaObjClientChat chat;
	public JavaObjClientList list;
	public JavaObjClientChatRoom chatroom;

	// public Vector RoomVec = new Vector(); // 채팅방 관리
	public Vector<JavaObjClientChatRoom> roomVec = new Vector<JavaObjClientChatRoom>(); // room 관리할 벡터

	// 채팅방 관리
	class ManageRoom {
		private String room_id;
		private String userlist;

		// 채팅방 추가
		public void addRoom(JavaObjClientChatRoom chatroom) {
			roomVec.add(chatroom);
		}

		// 채팅방 정보 (채팅방 번호, 채팅방 참여자)
		public void setInfo(String room_id, String userlist) {
			this.room_id = room_id;
			this.userlist = userlist;
		}

		public String getRoomid() {
			return room_id;
		}

		public String getUserlist() {
			return userlist;
		}

		// 채팅방 번호로 방 찾기 //
		public JavaObjClientChatRoom findRoom(String room_id) {
			for (JavaObjClientChatRoom chatroom : roomVec) {
				if (room_id.equals(chatroom.room_id)) {
					return chatroom; // 방 번호에 해당하는 채팅방 리턴
				}
			}
			return null;
		}

	}

	public JavaObjClientView(String username, String ip_addr, String port_no) {
		UserName = username;
		view = this;
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no)); // 소켓 생성
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// Client가 로그인 하면 서버로 메세지 전송
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello"); // 로그인 코드 100
			SendObject(obcm); // 서버로 메세지 전송

			// List, Chat 생성
			list = new JavaObjClientList(username, view);
			// chat = new JavaObjClientChat(username, view);

			setVisible(false); // view 클래스 화면에 보이지 않도록

			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			// 채팅방 관리
			ManageRoom manageroom = new ManageRoom();

			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					String mymsg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.getId(), cm.getData());
						mymsg = String.format("%s",cm.getData()); // 메세지 내용만
					} else
						continue;

					switch (cm.getCode()) {
					// 추가 //
					case "100": // 100 (로그인) 코드 받으면
						list.AddUser(cm.getId()); // 사용자 이름 list에 전달하기

						String newuser = cm.getId();
						cm = new ChatMsg(UserName, "110", "ORIGINAL USER"); // 기존 있던 클라이언트가 110 코드 보냄
						cm.newuser = newuser;
						SendObject(cm);
						break;
					case "120":
						list.AddUser(cm.getId());
						break;
					case "200": // 일반 chat message (일반 메세지)
						chatroom = manageroom.findRoom(cm.room_id); // 방 번호로 채팅방 찾기

						// 방에 있는 사용자들에게 메세지 보내기
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR(mymsg); // 내 메세지는 우측에
						else
							chatroom.AppendText(msg);
						break;
					case "300": // Image 첨부
						chatroom = manageroom.findRoom(cm.room_id); // 방 번호로 채팅방 찾기

						// 방에 있는 사용자들에게 사진 보내기
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR("[" + cm.getId() + "]");
						else
							chatroom.AppendText("[" + cm.getId() + "]");
						chatroom.AppendImage(cm.img);
						break;

					case "420": // 로그아웃 // 로그아웃 한 사용자 이름 list로 넘겨주기
						String outuser = cm.outuser;
						list.managecheck.OutUser(outuser);
						break;

					case "500": // 프로필 사진 관리
						// 받은 클라이언트는 그 사람의 프로필 사진을 전달받은 사진으로 변경해주기
						ImageIcon changeProfile = cm.img;
						String changeUser = cm.getId();

						break;

					case "520": // 채팅방 생성 코드 수신
						// cm.userlist = 자신 포함한 채팅방 참여자
						// 채팅방 생성
						chatroom = new JavaObjClientChatRoom(UserName, view, cm.room_id, cm.userlist);
						repaint();

						manageroom.addRoom(chatroom); // 방 추가
						manageroom.setInfo(cm.room_id, cm.userlist);

						// ClientChat에 채팅방 목록 추가 (참여자 목록이랑 room_id 넘겨줘야함)
						// chat.AddChatRoom(cm.userlist, cm.room_id);
						repaint();
						
						break;

					case "600": // 이모티콘
						chatroom = manageroom.findRoom(cm.room_id); // 방 번호로 채팅방 찾기

						// 방에 있는 사용자들에게 이모티콘 보내기
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR(""); // 자신이 보낸 이모티콘은 이름 뜨지 않도록
							// chatroom.AppendTextR("[" + cm.getId() + "]"); 
						else
							chatroom.AppendText("[" + cm.getId() + "]");
						chatroom.AppendImage(cm.emoji);
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg, String room_id) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg); // 일반 메세지

			obcm.room_id = room_id; // 방번호 설정
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");

		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();

		// 끝으로 이동
		// textArea.setCaretPosition(len);
		// textArea.replaceSelection(msg + "\n");

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
