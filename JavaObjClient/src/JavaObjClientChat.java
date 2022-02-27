import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.events.MouseEvent;

// ģ�� ��� â
class JavaObjClientChat extends JFrame {
	private JPanel contentPane;
	public JPanel menuPane; // �޴� ĭ
	public JPanel friendPane; // ģ�� ��� ĭ

	private JLabel lblFriend;

	public JLabel lblFriendMenu; // ģ�� ��� �޴�
	public JLabel lblChatMenu; // ä�� ��� �޴�

	private String Username;
	public JLabel lblChatRoom; // ä�ù� ���

	private JavaObjClientView mainview;
	public String userlist; // ä�ù� ������ �̸�

	public JPanel ChatListPane; // ģ�� ��� �г�
	public JLabel lblChatUsers; // �� ģ�� �̸�

	public JavaObjClientChat(String username, JavaObjClientView view) {
		mainview = view;

		setTitle("KaKaoTalk");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 394, 630);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		menuPane = new JPanel(); // �̷��� �ؾ� �Ʒ��ʿ��� ���������� ����� �� ����.
		menuPane.setBounds(0, 0, 100, 593);
		menuPane.setBackground(new Color(236, 236, 237));
		menuPane.setLayout(null);
		contentPane.add(menuPane);
		repaint();

		friendPane = new JPanel(); // ģ�� ���
		friendPane.setBounds(100, 0, 280, 593);
		friendPane.setBackground(Color.WHITE);
		friendPane.setLayout(null);
		contentPane.add(friendPane);
		repaint();

		lblFriend = new JLabel("ä��");
		lblFriend.setFont(new Font("���� ���", Font.BOLD, 24));
		lblFriend.setBounds(30, 30, 50, 40);
		friendPane.add(lblFriend);
		repaint();
		ImageIcon addchat = new ImageIcon("src/addchat.png");
		repaint();

		// ģ�� �޴� //
		ImageIcon person = new ImageIcon("src/icon1.png");
		Image img = person.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image personImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon personIcon = new ImageIcon(personImg); // �̹��� �ٽ� �̹��� ���������� ��ȯ
		JLabel lblFriendMenu = new JLabel(personIcon);
		
		// ģ�� ��� Ŭ�� ��
		// ClickMouseListener clickListener = new ClickMouseListener();
		// lblFriendMenu.addMouseListener(clickListener);
		lblFriendMenu.setBounds(30, 35, 40, 40);
		menuPane.add(lblFriendMenu);
		repaint();

		// ä�� ��� //
		ImageIcon chat = new ImageIcon("src/icon2.png");
		Image img2 = chat.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image chatImage = img2.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		ImageIcon chatIcon = new ImageIcon(chatImage); // �̹��� �ٽ� �̹��� ���������� ��ȯ
		JLabel lblChatMenu = new JLabel(chatIcon);
		lblChatMenu.setBounds(25, 100, 50, 40);
		menuPane.add(lblChatMenu);
		repaint();
		setVisible(true);

		// ä�� ��� ��� //
		lblChatRoom = new JLabel();
		// lblChatRoom.setBounds(30, 100, 230, 50);
		// friendPane.add(lblChatRoom);
		repaint();

		// ä�� ��� Pane
		ChatListPane = new JPanel();
		ChatListPane.setBounds(18, 90, 170, 50); // ��ġ ����
		ChatListPane.setBackground(Color.WHITE);
		ChatListPane.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); // ���� ����
		friendPane.add(ChatListPane);
		repaint();

		setVisible(true);
	}

	// ���콺 �̺�Ʈ //
	// ģ�� ��� �޴� Ŭ�� �� //
	class ClickMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) throws IOException { // ���콺 Ŭ�� ��
			JavaObjClientList list = new JavaObjClientList(Username, mainview); // ��ư ������ Chat.java ����
			setVisible(false);
		}
	}

	// ä�ù� ��Ͽ� �߰��ϱ� //
	public void AddChatRoom(String userlist, String room_id) {
		ImageIcon profile2 = new ImageIcon("src/profile.jpg");
		Image imgProfile = profile2.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image profileImg = imgProfile.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon profileIcon = new ImageIcon(profileImg); // �̹��� �ٽ� �̹��� ���������� ��ȯ
		JLabel lblFriendProfile = new JLabel(profileIcon); // ģ�� ������ ���� ����
		ChatListPane.add(lblFriendProfile); // ģ�� ��� pane�� �߰�
		repaint();

		this.userlist = userlist;
		lblChatUsers = new JLabel(userlist); // ������ �̸����� Label ����
		lblChatUsers.setFont(new Font("���� ���", Font.BOLD, 13));

		ChatListPane.add(lblChatUsers);
		repaint();
		setVisible(true);
	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(Username, "500", "MOUSE");
		mainview.SendObject(cm); // �ڱ� �ڽ��� �ƴ϶� mainview�� �ִ� sendObject�� (������� mainview���� �����)
	}

}