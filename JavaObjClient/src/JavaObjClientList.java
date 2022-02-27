import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

// ù ��° ������
// ģ�� ��� ������
class JavaObjClientList extends JFrame {
	private static final long serialVersionUID = 1L;

	public String UserName;

	public JPanel contentPane;
	public JPanel menuPane; // �޴� ĭ
	public JPanel friendPane; // ģ�� ��� ĭ

	public JLabel lblFriend;
	public JButton btnProfile; // ������ ���� ��ư
	public JLabel lblName; // ����� �̸�
	public JLabel lblFriendList; // ģ�� ���

	public JList listFriend; // ģ�� �̸�
	public JavaObjClientView mainview; // ���κ�

	public Frame frame;
	private FileDialog fd;

	int no_user = 1; // ������ ��

	public JLabel lblFriendMenu; // ģ�� ��� �޴�
	public JLabel lblChatMenu; // ä�� ��� �޴�
	public JPanel friendListPane; // ģ�� ��� (�̸�, ����)

	public JLabel lblFriendProfile; // ģ�� ������ ����
	public JCheckBox checkBox; // ģ�� ���� üũ�ڽ�
	public JButton btnChatMake; // ä�ù� ���� ��ư
	public JButton btnLogout; // �α׾ƿ� ��ư

	public ChatMakeAction chatAction = new ChatMakeAction();

	public JLabel lblFriendName; // �� ģ�� �̸�
	public String userlist = "";

	public Vector<JCheckBox> checkVec = new Vector<JCheckBox>();
	public ManageCheck managecheck = new ManageCheck(); // üũ�ڽ� ����

	// �⺻ ������ ����
	public ImageIcon profile;
	// ������ ������ ����
	public ImageIcon newProfile;
	
	public JavaObjClientList(String username, JavaObjClientView view) throws IOException {
		mainview = view;
		setTitle("KaKaoTalk");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		lblFriend = new JLabel("ģ��");
		lblFriend.setFont(new Font("���� ���", Font.BOLD, 24));
		lblFriend.setBounds(30, 30, 50, 40);
		friendPane.add(lblFriend);
		repaint();

		// ������ ���� ����
		btnProfile = new JButton("");

		Image originalProfile = ImageIO.read(new File("src/profile.jpg")); // �⺻ ������ ����
		Image userprofile;
		ImageIcon profile;
		ImageIcon userprofileIcon;
		try {
			userprofile = ImageIO.read(new File("src/" + username + ".jpg")); // ������ ���� �ҷ�����
			profile = new ImageIcon(userprofile);
			Image profileimg = profile.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image profilesize = profileimg.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
			userprofileIcon = new ImageIcon(profilesize);
		} catch (IOException e) {
			userprofileIcon = new ImageIcon(originalProfile);
		}

		btnProfile.setIcon(userprofileIcon);
		btnProfile.setBackground(Color.WHITE);
		btnProfile.setBounds(30, 85, 55, 55);

		// ������ ���� �߰�
		ProfileImgAction action = new ProfileImgAction();
		btnProfile.addActionListener(action); // �׼Ǹ����� �ޱ�
		friendPane.add(btnProfile);
		repaint();

		lblName = new JLabel(""); // ����� �̸�
		lblName.setFont(new Font("���� ���", Font.BOLD, 14));
		lblName.setBounds(100, 90, 150, 40);
		lblName.setText(username);
		friendPane.add(lblName);
		repaint();
		setVisible(true);

		// ģ�� ���
		lblFriendList = new JLabel("ģ��");
		lblFriendList.setForeground(new Color(105, 105, 105));
		lblFriendList.setFont(new Font("���� ���", Font.PLAIN, 12));
		lblFriendList.setBounds(30, 160, 40, 40);
		friendPane.add(lblFriendList);
		repaint();

		// ģ�� �޴� //
		ImageIcon person = new ImageIcon("src/icon1.png");
		Image img = person.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image personImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon personIcon = new ImageIcon(personImg); // �̹��� �ٽ� �̹��� ���������� ��ȯ
		JLabel lblFriendMenu = new JLabel(personIcon);
		lblFriendMenu.setBounds(30, 35, 40, 40);
		menuPane.add(lblFriendMenu);
		repaint();
		setVisible(true);

		// ä�� ��� //
		ImageIcon chat = new ImageIcon("src/icon2.png");
		Image img2 = chat.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image chatImage = img2.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		ImageIcon chatIcon = new ImageIcon(chatImage); // �̹��� �ٽ� �̹��� ���������� ��ȯ
		JLabel lblChatMenu = new JLabel(chatIcon);
		// ä�� ��� Ŭ�� ��
		// ClickMouseListener clickListener = new ClickMouseListener();
		// lblChatMenu.addMouseListener(clickListener);

		lblChatMenu.setBounds(25, 100, 50, 40);
		menuPane.add(lblChatMenu);
		repaint();

		// ģ�� ��� Pane
		friendListPane = new JPanel();
		friendListPane.setBounds(10, 200, 140, 380); // ��ġ ����
		friendListPane.setBackground(Color.WHITE);
		friendListPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // ���� ����
		friendPane.add(friendListPane);
		repaint();

		// ä�ù� ���� ��ư //
		btnChatMake = new JButton("ä�ù�");
		btnChatMake.setBackground(Color.WHITE);
		btnChatMake.setFont(new Font("���� ���", Font.PLAIN, 12));
		ChatMakeAction chatAction = new ChatMakeAction();
		btnChatMake.addActionListener(chatAction);
		btnChatMake.setBounds(7, 520, 85, 23);
		menuPane.add(btnChatMake);
		repaint();

		// �α׾ƿ� ��ư //
		btnLogout = new JButton("�α׾ƿ�");
		btnLogout.setBackground(Color.WHITE);
		btnLogout.setFont(new Font("���� ���", Font.PLAIN, 12));
		// �α׾ƿ� ��ư Ŭ�� �̺�Ʈ
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "410", "LOGOUT");
				msg.outuser = UserName; // ���� ��� �̸� ����
				mainview.SendObject(msg); // mainview ���� ������ ������
				System.exit(0);
			}
		});
		btnLogout.setBounds(7, 550, 85, 23);
		menuPane.add(btnLogout);
		repaint();

		UserName = username;
		setVisible(true);
	}

	// ���콺 �̺�Ʈ //
	// ä�� ��� �޴� Ŭ�� �� //
	class ClickMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) { // ���콺 Ŭ�� ��
			JavaObjClientChat chat = new JavaObjClientChat(UserName, mainview); // ��ư ������ Chat.java ����
			setVisible(false);
		}
	}

	// ���� ���� ��� üũ�ڽ� ����
	public void AddUser(String username) {
		String newuser = username; // ���޹��� ���ο� ������ �̸�

		// üũ�ڽ� ���� //
		checkBox = new JCheckBox("    " + newuser); // ���� ���� ����� �̸����� üũ�ڽ� ����
		checkBox.setFont(new Font("���� ���", Font.BOLD, 13));
		checkBox.setSize(170, 20);
		checkBox.setBackground(Color.WHITE);
		managecheck.addCheck(checkBox); // ���Ϳ� �߰�

		checkBox.addActionListener(new ActionListener() { // üũ �ڽ� ���� �� ������ �̸� �߰�
			public void actionPerformed(ActionEvent e) {
				managecheck.AllCheck(checkBox); // üũ�ڽ� üũ�ƴ��� Ȯ��
			}
		});

		friendListPane.add(checkBox);
		repaint();

		setVisible(true);
		no_user++; // ������ + 1
		repaint();
	}

	// üũ�ڽ� ����
	public class ManageCheck {
		// üũ�ڽ� �ʱ�ȭ
		public void resetAll() {
			for (JCheckBox ch : checkVec) // checkVec�� �ִ� checkBox �ʱ�ȭ
				ch.setSelected(false);
		}

		// ä�ù� �߰�
		public void addCheck(JCheckBox checkBox) {
			checkVec.add(checkBox);
		}

		// üũ�ڽ� üũ�ƴ��� Ȯ��
		public Object AllCheck(JCheckBox checkBox) {
			for (JCheckBox ch : checkVec) { // ��� üũ�ڽ�
				if (ch.isSelected()) // üũ�ڽ� üũ������
					userlist += (ch.getText().trim()); // userlist�� �̸� �߰�
			}
			return null;
		}

		// �α׾ƿ� �� ����� üũ�ڽ� �� �ٲٱ�
		public void OutUser(String outuser) {
			for (JCheckBox ch : checkVec) {
				if (ch.getText().equals(outuser))
					checkBox.setForeground(Color.RED);
			}
		}
	}

	// ä�ù� ��ư���� ä�ù� ���� action
	class ChatMakeAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnChatMake) {
				// ȿ���� ���
				SoundPlay soundplay = new SoundPlay(); // ȿ���� ���
				soundplay.test();

				// ������ 510 ä�ù� ���� �ڵ� ������
				ChatMsg cm = new ChatMsg(UserName, "510", "MULTI CHAT MAKE");
				// cm.chatwith = userlist; // �ڽ��� �̸� ������ ä�� ���� �������ֱ�
				cm.userlist = UserName + " " + userlist; // �ڽ� �̸� + ������ �̸���(������ ���� ��)
				mainview.SendObject(cm); // ���κ�� ������

				// ä�ù� ���� �ڵ� ������ userlist �ʱ�ȭ
				userlist = "";
				// üũ�ڽ� �ʱ�ȭ
				managecheck.resetAll();
			}
		}

	}

	// ä�ù� ���� ��ư ������ ���� ���
	class SoundPlay {
		public void test() {
			File bgm;
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;

			bgm = new File("src/sound/chat.wav");

			Clip clip;

			try {
				stream = AudioSystem.getAudioInputStream(bgm);
				format = stream.getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(stream);
				clip.start();

			} catch (Exception e) {
				System.out.println("err : " + e);
			}
		}
	}

	// ���콺 �̺�Ʈ
	class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // ���� Ŭ�� ��
				String chatUser = lblFriendName.getText(); // ���� Ŭ���� ģ�� �̸�

				// ������ 510 ä�ù� ���� �ڵ� ������
				ChatMsg cm = new ChatMsg(UserName, "510", "NEW CHAT MAKE" + chatUser + "�� ä���� ��");
				cm.userlist = UserName + " " + chatUser; // �ڽ� �̸��� �����ؼ�
				mainview.SendObject(cm); // ���κ�� ������

				// ä�ù� ���� �ڵ� ������ userlist �ʱ�ȭ
				userlist = "";
			}
		}
	}

	// ������ ���� ����
	public void ManageProfile() {
		frame = new Frame("������ ���� ����");
		fd = new FileDialog(frame, "������ ���� ����", FileDialog.LOAD);
		fd.setVisible(true);

		ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
		Image changeProfile = img.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image changeProfileImg = changeProfile.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
		ImageIcon newProfileIcon = new ImageIcon(changeProfileImg);

		BufferedImage newProfile = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		Graphics g = newProfile.getGraphics();
		g.drawImage(changeProfile, 0, 0, null);
		g.dispose();

		// ���� �ٲ� �̹��� ����
		try {
			String filename = "src/" + UserName + ".jpg"; // ������ ���� �ٲ� ����� �̸����� ���� ����
			ImageIO.write(newProfile, "jpg", new File(filename));

		} catch (IOException e) {
			e.printStackTrace();
		}
		btnProfile.setIcon(newProfileIcon); // ������ ���� ������ �������� ����
		repaint();

		ChatMsg obcm = new ChatMsg(UserName, "500", "PROFILE IMG"); // ������ ���� �ڵ�
		obcm.setProfile(newProfileIcon); // �ٲ� �̹����� �����ؼ� ������ ������
		mainview.SendObject(obcm); // 500 �ڵ� ������
	}

	// ������ ���� ���� action
	class ProfileImgAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnProfile) { // ������ ���� ��ư ������
				ManageProfile(); // ������ ���� ���� ����
			}
		}
	}
}