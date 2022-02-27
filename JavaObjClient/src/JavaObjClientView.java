
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
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
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	private JLabel lblPartnerName; // ��� �̸�
	private JTextPane textArea;
	private JTextPane textAreaMenu; // ä�ù� ��� �޴�

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JButton emojiBtn; // �̸�Ƽ�� ��ư

	public JavaObjClientView view;
	public JavaObjClientChat chat;
	public JavaObjClientList list;
	public JavaObjClientChatRoom chatroom;

	// public Vector RoomVec = new Vector(); // ä�ù� ����
	public Vector<JavaObjClientChatRoom> roomVec = new Vector<JavaObjClientChatRoom>(); // room ������ ����

	// ä�ù� ����
	class ManageRoom {
		private String room_id;
		private String userlist;

		// ä�ù� �߰�
		public void addRoom(JavaObjClientChatRoom chatroom) {
			roomVec.add(chatroom);
		}

		// ä�ù� ���� (ä�ù� ��ȣ, ä�ù� ������)
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

		// ä�ù� ��ȣ�� �� ã�� //
		public JavaObjClientChatRoom findRoom(String room_id) {
			for (JavaObjClientChatRoom chatroom : roomVec) {
				if (room_id.equals(chatroom.room_id)) {
					return chatroom; // �� ��ȣ�� �ش��ϴ� ä�ù� ����
				}
			}
			return null;
		}

	}

	public JavaObjClientView(String username, String ip_addr, String port_no) {
		UserName = username;
		view = this;
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no)); // ���� ����
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// Client�� �α��� �ϸ� ������ �޼��� ����
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello"); // �α��� �ڵ� 100
			SendObject(obcm); // ������ �޼��� ����

			// List, Chat ����
			list = new JavaObjClientList(username, view);
			// chat = new JavaObjClientChat(username, view);

			setVisible(false); // view Ŭ���� ȭ�鿡 ������ �ʵ���

			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	// Server Message�� �����ؼ� ȭ�鿡 ǥ��
	class ListenNetwork extends Thread {
		public void run() {
			// ä�ù� ����
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
						mymsg = String.format("%s",cm.getData()); // �޼��� ���븸
					} else
						continue;

					switch (cm.getCode()) {
					// �߰� //
					case "100": // 100 (�α���) �ڵ� ������
						list.AddUser(cm.getId()); // ����� �̸� list�� �����ϱ�

						String newuser = cm.getId();
						cm = new ChatMsg(UserName, "110", "ORIGINAL USER"); // ���� �ִ� Ŭ���̾�Ʈ�� 110 �ڵ� ����
						cm.newuser = newuser;
						SendObject(cm);
						break;
					case "120":
						list.AddUser(cm.getId());
						break;
					case "200": // �Ϲ� chat message (�Ϲ� �޼���)
						chatroom = manageroom.findRoom(cm.room_id); // �� ��ȣ�� ä�ù� ã��

						// �濡 �ִ� ����ڵ鿡�� �޼��� ������
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR(mymsg); // �� �޼����� ������
						else
							chatroom.AppendText(msg);
						break;
					case "300": // Image ÷��
						chatroom = manageroom.findRoom(cm.room_id); // �� ��ȣ�� ä�ù� ã��

						// �濡 �ִ� ����ڵ鿡�� ���� ������
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR("[" + cm.getId() + "]");
						else
							chatroom.AppendText("[" + cm.getId() + "]");
						chatroom.AppendImage(cm.img);
						break;

					case "420": // �α׾ƿ� // �α׾ƿ� �� ����� �̸� list�� �Ѱ��ֱ�
						String outuser = cm.outuser;
						list.managecheck.OutUser(outuser);
						break;

					case "500": // ������ ���� ����
						// ���� Ŭ���̾�Ʈ�� �� ����� ������ ������ ���޹��� �������� �������ֱ�
						ImageIcon changeProfile = cm.img;
						String changeUser = cm.getId();

						break;

					case "520": // ä�ù� ���� �ڵ� ����
						// cm.userlist = �ڽ� ������ ä�ù� ������
						// ä�ù� ����
						chatroom = new JavaObjClientChatRoom(UserName, view, cm.room_id, cm.userlist);
						repaint();

						manageroom.addRoom(chatroom); // �� �߰�
						manageroom.setInfo(cm.room_id, cm.userlist);

						// ClientChat�� ä�ù� ��� �߰� (������ ����̶� room_id �Ѱ������)
						// chat.AddChatRoom(cm.userlist, cm.room_id);
						repaint();
						
						break;

					case "600": // �̸�Ƽ��
						chatroom = manageroom.findRoom(cm.room_id); // �� ��ȣ�� ä�ù� ã��

						// �濡 �ִ� ����ڵ鿡�� �̸�Ƽ�� ������
						if (cm.getId().equals(UserName))
							chatroom.AppendTextR(""); // �ڽ��� ���� �̸�Ƽ���� �̸� ���� �ʵ���
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
					} // catch�� ��
				} // �ٱ� catch����

			}
		}
	}

	// Server���� network���� ����
	public void SendMessage(String msg, String room_id) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg); // �Ϲ� �޼���

			obcm.room_id = room_id; // ���ȣ ����
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

	public void SendObject(Object ob) { // ������ �޼����� ������ �޼ҵ�
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("�޼��� �۽� ����!!\n");
			AppendText("SendObject Error");
		}
	}

	// ȭ�鿡 ���
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");

		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();

		// ������ �̵�
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
