//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaObjServer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	public Vector<Room> RoomVec = new Vector<Room>(); // 채팅방 관리
	public ManageRoom manageRoom = new ManageRoom(); // 방 관리

	// 채팅방 관리
	class ManageRoom {
		public Vector<Room> roomVec = new Vector<Room>(); // room 관리할 벡터

		// 채팅방 추가 //
		public void addRoom(Room room) {
			roomVec.add(room);
		}

		// 채팅방 정보 (채팅방 번호, 채팅방 참여자) //
		public String getInfo(Room room) {
			return room.room_id + " " + room.userlist;
		}

		// 채팅방 번호로 방 찾기 //
		public Room findRoom(String room_id) {
			for (Room room : roomVec) {
				if (room_id.equals(room.room_id)) {
					return room; // 채팅방 번호에 해당하는 방 리턴
				}
			}
			return null;
		}

		// 채팅방 번호로 채팅방 참가자 찾기 //
		public String findUser(String room_id) {
			for (Room room : roomVec) {
				if (room_id.equals(room.room_id)) {
					return room.getRoomUser(); // 참가자 반환
				}
			}
			return null;
		}

	}

	class Room {
		private Vector<String> roomUser = new Vector<String>(); // 채팅방 사용자

		String room_id;
		String userlist;

		public Room(String room_id, String userlist) {
			this.room_id = room_id;
			this.userlist = userlist;
		}

		// 방 번호
		public String getRoomid() {
			return room_id;
		}

		// 방 사용자
		public String getRoomUser() {
			return userlist;
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjServer frame = new JavaObjServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaObjServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);

					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getId() + "\n");
		textArea.append("data = " + msg.getData() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;

		public UserService(Socket client_socket) {
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
//				is = client_socket.getInputStream();
//				dis = new DataInputStream(is);
//				os = client_socket.getOutputStream();
//				dos = new DataOutputStream(os);

				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

				// line1 = dis.readUTF();
				// /login user1 ==> msg[0] msg[1]
//				byte[] b = new byte[BUF_LEN];
//				dis.read(b);		
//				String line1 = new String(b);
//
//				//String[] msg = line1.split(" ");
//				//UserName = msg[1].trim();
//				UserStatus = "O"; // Online 상태
//				Login();
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림

			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다. (날 제외한 다른 사람에게 메세지 보내기)
		}

		public void Logout() {
			// 로그아웃 코드 보내기
			ChatMsg obcm = new ChatMsg("SERVER", "420", "LOGOUT");
			obcm.outuser = UserName; 
			WriteOthersObject(obcm); // 나를 제외한 다른 사람에게 보내기
			
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
			
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteOne() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteOne() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this) { // 나를 제외한 사용자들에게
					user.WriteOne(str);
				}
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}

		public void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this) { // 나를 제외한 사용자들에만
					user.WriteOneObject(ob);
				}
			}
		}

		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);

			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		public void WriteOneObject(Object ob) {
			try {
				oos.writeObject(ob);
			} catch (IOException e) {
				AppendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}

		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;

					// 사용자 이름 배열 생성 후 새로 들어온 참가자한테 보내기 //
					String[] friendArray = new String[100];
					for (int i = 0; i < friendArray.length; i++) {
						friendArray[i] += cm.getId(); // 이름 모두 배열에 저장
					}

					if (cm.getCode().matches("100")) { // 코드 100 받으면 (로그인 코드)
						UserName = cm.getId(); // 새로운 접속자 이름
						UserStatus = "O"; // Online 상태

						WriteOthersObject(cm); // code 100을 포함한 cm 전체 보내기
					} else if (cm.getCode().matches("110")) { // 코드 110 받으면 (기존 사용자에게)
						ChatMsg obcm3 = new ChatMsg(UserName, "120", "ORIGINAL USRE NAME"); // 새 사용자에게 기존 사용자 이름 넘겨주기 위해
						String newuser = cm.newuser;
						obcm3.newuser = newuser; // 새로 참가한 사람 이름 저장

						// 새로 들어온 사용자에게만 보내기
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if (user.UserName.equals(newuser)) { // 새로운 사용자에게만
								user.WriteOneObject(obcm3); // 120 코드 (기존 사용자 이름)보내기
							}
						}
					} else if (cm.getCode().matches("200")) { // 200 코드 받으면
						msg = String.format("[%s] %s", cm.getId(), cm.getData());
						AppendText(msg); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
							UserStatus = "O";
						} else if (args[1].matches("/exit")) {
							Logout();
							break;
						} else if (args[1].matches("/list")) {
							WriteOne("User list\n");
							WriteOne("Name\tStatus\n");
							WriteOne("-----------------------------\n");
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								WriteOne(user.UserName + "\t" + user.UserStatus + "\n");
							}
							WriteOne("-----------------------------\n");
						} else if (args[1].matches("/sleep")) {
							UserStatus = "S";
						} else if (args[1].matches("/wakeup")) {
							UserStatus = "O";
						} else if (args[1].matches("/to")) { // 귓속말
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
									String msg2 = "";
									for (int j = 3; j < args.length; j++) { // 실제 message 부분
										msg2 += args[j];
										if (j < args.length - 1)
											msg2 += " ";
									}
									// /to 빼고.. [귓속말] [user1] Hello user2..
									user.WritePrivate(args[0] + " " + msg2 + "\n");
									// user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
									break;
								}
							}
						} else { // 일반 채팅 메시지 (UserName, "200", msg);
							UserStatus = "O";

							Room room = manageRoom.findRoom(cm.room_id); // room_id로 방 찾기
							String[] roomUserlist = room.getRoomUser().split(" "); // 룸 안의 사용자들

							for (int u = 0; u < roomUserlist.length; u++) {
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									if (user.UserName.equals(roomUserlist[u])) // 채팅방 안의 사람들에게
										user.WriteOneObject(cm); // 받은 메세지 그대로 보내기
								}
							}
						}
					} 
					else if (cm.getCode().matches("300")) { // 이미지
						Room room = manageRoom.findRoom(cm.room_id); // room_id로 방 찾기
						String[] roomUserlist = room.getRoomUser().split(" "); // 룸 안의 사용자들

						for (int u = 0; u < roomUserlist.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(roomUserlist[u])) // 채팅방 안의 사람들에게
									user.WriteOneObject(cm); // 받은 메세지 그대로 보내기
							}
						}
					}
					else if (cm.getCode().matches("400")) { // logout message 처리
						Logout();
						break;
						
					} 
					else if (cm.getCode().matches("410")) { // logout 처리

						ChatMsg obcm3 = new ChatMsg("SERVER", "420", "LOGOUT");
						obcm3.outuser = UserName; 
						WriteOthersObject(obcm3); // 나를 제외한 다른 사람에게 보내기
						
						Logout();
						break;
					}
					// 프로필 사진 변경
					else if (cm.getCode().matches("500")) { // 500 코드 받으면
						WriteOthersObject(cm);
					}
					// UserName, "510", "NEW CHAT MAKE"
					else if (cm.getCode().matches("510")) { // 510 코드 받으면 (채팅방 생성 코드)
						ChatMsg obcm2 = new ChatMsg("SERVER", "520", cm.userlist); // 틀 만들기

						String[] chatuser = cm.userlist.split(" "); // 채팅 참가할 참여자들 분리

						String room_id = UUID.randomUUID().toString(); // 채팅방 id 생성
						obcm2.room_id = room_id; // 채팅방 아이디 설정
						obcm2.userlist = cm.userlist; // 채팅방 참여자 설정

						Room room = new Room(room_id, obcm2.userlist); // 채팅방 생성
						manageRoom.addRoom(room); // 생성한 방 추가

						AppendText("채팅방 생성");

						// chatuser 안에 있는 사람들에게 전송
						for (int u = 0; u < chatuser.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(chatuser[u])) // chatuser에 있는 사람에게
									user.WriteOneObject(obcm2);
							}
						}

					}
					// 이모티콘
					else if (cm.getCode().matches("600")) {
						Room room = manageRoom.findRoom(cm.room_id); // room_id로 방 찾기
						String[] roomUserlist = room.getRoomUser().split(" "); // 룸 안의 사용자들

						for (int u = 0; u < roomUserlist.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(roomUserlist[u])) // 채팅방 안의 사람들에게
									user.WriteOneObject(cm); // 받은 메세지 그대로 보내기
							}
						}
					}
					
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가 난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
