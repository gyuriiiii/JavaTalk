//JavaObjServer.java ObjectStream ��� ä�� Server

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

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����

	public Vector<Room> RoomVec = new Vector<Room>(); // ä�ù� ����
	public ManageRoom manageRoom = new ManageRoom(); // �� ����

	// ä�ù� ����
	class ManageRoom {
		public Vector<Room> roomVec = new Vector<Room>(); // room ������ ����

		// ä�ù� �߰� //
		public void addRoom(Room room) {
			roomVec.add(room);
		}

		// ä�ù� ���� (ä�ù� ��ȣ, ä�ù� ������) //
		public String getInfo(Room room) {
			return room.room_id + " " + room.userlist;
		}

		// ä�ù� ��ȣ�� �� ã�� //
		public Room findRoom(String room_id) {
			for (Room room : roomVec) {
				if (room_id.equals(room.room_id)) {
					return room; // ä�ù� ��ȣ�� �ش��ϴ� �� ����
				}
			}
			return null;
		}

		// ä�ù� ��ȣ�� ä�ù� ������ ã�� //
		public String findUser(String room_id) {
			for (Room room : roomVec) {
				if (room_id.equals(room.room_id)) {
					return room.getRoomUser(); // ������ ��ȯ
				}
			}
			return null;
		}

	}

	class Room {
		private Vector<String> roomUser = new Vector<String>(); // ä�ù� �����

		String room_id;
		String userlist;

		public Room(String room_id, String userlist) {
			this.room_id = room_id;
			this.userlist = userlist;
		}

		// �� ��ȣ
		public String getRoomid() {
			return room_id;
		}

		// �� �����
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
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);

					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getId() + "\n");
		textArea.append("data = " + msg.getData() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
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
			// �Ű������� �Ѿ�� �ڷ� ����
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
//				UserStatus = "O"; // Online ����
//				Login();
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("���ο� ������ " + UserName + " ����.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "�� ȯ���մϴ�.\n"); // ����� ����ڿ��� ���������� �˸�

			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			WriteOthers(msg); // ���� user_vc�� ���� ������ user�� ���Ե��� �ʾҴ�. (�� ������ �ٸ� ������� �޼��� ������)
		}

		public void Logout() {
			// �α׾ƿ� �ڵ� ������
			ChatMsg obcm = new ChatMsg("SERVER", "420", "LOGOUT");
			obcm.outuser = UserName; 
			WriteOthersObject(obcm); // ���� ������ �ٸ� ������� ������
			
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			WriteAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			AppendText("����� " + "[" + UserName + "] ����. ���� ������ �� " + UserVec.size());
			
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteOne() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteOne() �� ȣ���Ѵ�.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this) { // ���� ������ ����ڵ鿡��
					user.WriteOne(str);
				}
			}
		}

		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}

		public void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this) { // ���� ������ ����ڵ鿡��
					user.WriteOneObject(ob);
				}
			}
		}

		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
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

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}

		// �ӼӸ� ����
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("�ӼӸ�", "200", msg);
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
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
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
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

					// ����� �̸� �迭 ���� �� ���� ���� ���������� ������ //
					String[] friendArray = new String[100];
					for (int i = 0; i < friendArray.length; i++) {
						friendArray[i] += cm.getId(); // �̸� ��� �迭�� ����
					}

					if (cm.getCode().matches("100")) { // �ڵ� 100 ������ (�α��� �ڵ�)
						UserName = cm.getId(); // ���ο� ������ �̸�
						UserStatus = "O"; // Online ����

						WriteOthersObject(cm); // code 100�� ������ cm ��ü ������
					} else if (cm.getCode().matches("110")) { // �ڵ� 110 ������ (���� ����ڿ���)
						ChatMsg obcm3 = new ChatMsg(UserName, "120", "ORIGINAL USRE NAME"); // �� ����ڿ��� ���� ����� �̸� �Ѱ��ֱ� ����
						String newuser = cm.newuser;
						obcm3.newuser = newuser; // ���� ������ ��� �̸� ����

						// ���� ���� ����ڿ��Ը� ������
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if (user.UserName.equals(newuser)) { // ���ο� ����ڿ��Ը�
								user.WriteOneObject(obcm3); // 120 �ڵ� (���� ����� �̸�)������
							}
						}
					} else if (cm.getCode().matches("200")) { // 200 �ڵ� ������
						msg = String.format("[%s] %s", cm.getId(), cm.getData());
						AppendText(msg); // server ȭ�鿡 ���
						String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
						if (args.length == 1) { // Enter key �� ���� ��� Wakeup ó���� �Ѵ�.
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
						} else if (args[1].matches("/to")) { // �ӼӸ�
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
									String msg2 = "";
									for (int j = 3; j < args.length; j++) { // ���� message �κ�
										msg2 += args[j];
										if (j < args.length - 1)
											msg2 += " ";
									}
									// /to ����.. [�ӼӸ�] [user1] Hello user2..
									user.WritePrivate(args[0] + " " + msg2 + "\n");
									// user.WriteOne("[�ӼӸ�] " + args[0] + " " + msg2 + "\n");
									break;
								}
							}
						} else { // �Ϲ� ä�� �޽��� (UserName, "200", msg);
							UserStatus = "O";

							Room room = manageRoom.findRoom(cm.room_id); // room_id�� �� ã��
							String[] roomUserlist = room.getRoomUser().split(" "); // �� ���� ����ڵ�

							for (int u = 0; u < roomUserlist.length; u++) {
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									if (user.UserName.equals(roomUserlist[u])) // ä�ù� ���� ����鿡��
										user.WriteOneObject(cm); // ���� �޼��� �״�� ������
								}
							}
						}
					} 
					else if (cm.getCode().matches("300")) { // �̹���
						Room room = manageRoom.findRoom(cm.room_id); // room_id�� �� ã��
						String[] roomUserlist = room.getRoomUser().split(" "); // �� ���� ����ڵ�

						for (int u = 0; u < roomUserlist.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(roomUserlist[u])) // ä�ù� ���� ����鿡��
									user.WriteOneObject(cm); // ���� �޼��� �״�� ������
							}
						}
					}
					else if (cm.getCode().matches("400")) { // logout message ó��
						Logout();
						break;
						
					} 
					else if (cm.getCode().matches("410")) { // logout ó��

						ChatMsg obcm3 = new ChatMsg("SERVER", "420", "LOGOUT");
						obcm3.outuser = UserName; 
						WriteOthersObject(obcm3); // ���� ������ �ٸ� ������� ������
						
						Logout();
						break;
					}
					// ������ ���� ����
					else if (cm.getCode().matches("500")) { // 500 �ڵ� ������
						WriteOthersObject(cm);
					}
					// UserName, "510", "NEW CHAT MAKE"
					else if (cm.getCode().matches("510")) { // 510 �ڵ� ������ (ä�ù� ���� �ڵ�)
						ChatMsg obcm2 = new ChatMsg("SERVER", "520", cm.userlist); // Ʋ �����

						String[] chatuser = cm.userlist.split(" "); // ä�� ������ �����ڵ� �и�

						String room_id = UUID.randomUUID().toString(); // ä�ù� id ����
						obcm2.room_id = room_id; // ä�ù� ���̵� ����
						obcm2.userlist = cm.userlist; // ä�ù� ������ ����

						Room room = new Room(room_id, obcm2.userlist); // ä�ù� ����
						manageRoom.addRoom(room); // ������ �� �߰�

						AppendText("ä�ù� ����");

						// chatuser �ȿ� �ִ� ����鿡�� ����
						for (int u = 0; u < chatuser.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(chatuser[u])) // chatuser�� �ִ� �������
									user.WriteOneObject(obcm2);
							}
						}

					}
					// �̸�Ƽ��
					else if (cm.getCode().matches("600")) {
						Room room = manageRoom.findRoom(cm.room_id); // room_id�� �� ã��
						String[] roomUserlist = room.getRoomUser().split(" "); // �� ���� ����ڵ�

						for (int u = 0; u < roomUserlist.length; u++) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.equals(roomUserlist[u])) // ä�ù� ���� ����鿡��
									user.WriteOneObject(cm); // ���� �޼��� �״�� ������
							}
						}
					}
					
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // ������ �� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}

}
