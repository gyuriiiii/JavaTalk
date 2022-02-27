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

// 첫 번째 프레임
// 친구 목록 프레임
class JavaObjClientList extends JFrame {
	private static final long serialVersionUID = 1L;

	public String UserName;

	public JPanel contentPane;
	public JPanel menuPane; // 메뉴 칸
	public JPanel friendPane; // 친구 목록 칸

	public JLabel lblFriend;
	public JButton btnProfile; // 프로필 관리 버튼
	public JLabel lblName; // 사용자 이름
	public JLabel lblFriendList; // 친구 목록

	public JList listFriend; // 친구 이름
	public JavaObjClientView mainview; // 메인뷰

	public Frame frame;
	private FileDialog fd;

	int no_user = 1; // 접속자 수

	public JLabel lblFriendMenu; // 친구 목록 메뉴
	public JLabel lblChatMenu; // 채팅 목록 메뉴
	public JPanel friendListPane; // 친구 목록 (이름, 프사)

	public JLabel lblFriendProfile; // 친구 프로필 사진
	public JCheckBox checkBox; // 친구 선택 체크박스
	public JButton btnChatMake; // 채팅방 생성 버튼
	public JButton btnLogout; // 로그아웃 버튼

	public ChatMakeAction chatAction = new ChatMakeAction();

	public JLabel lblFriendName; // 새 친구 이름
	public String userlist = "";

	public Vector<JCheckBox> checkVec = new Vector<JCheckBox>();
	public ManageCheck managecheck = new ManageCheck(); // 체크박스 관리

	// 기본 프로필 사진
	public ImageIcon profile;
	// 변경한 프로필 사진
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

		menuPane = new JPanel(); // 이렇게 해야 아래쪽에서 전역변수를 사용할 수 있음.
		menuPane.setBounds(0, 0, 100, 593);
		menuPane.setBackground(new Color(236, 236, 237));
		menuPane.setLayout(null);
		contentPane.add(menuPane);
		repaint();

		friendPane = new JPanel(); // 친구 목록
		friendPane.setBounds(100, 0, 280, 593);
		friendPane.setBackground(Color.WHITE);
		friendPane.setLayout(null);
		contentPane.add(friendPane);
		repaint();

		lblFriend = new JLabel("친구");
		lblFriend.setFont(new Font("한컴 고딕", Font.BOLD, 24));
		lblFriend.setBounds(30, 30, 50, 40);
		friendPane.add(lblFriend);
		repaint();

		// 프로필 사진 관리
		btnProfile = new JButton("");

		Image originalProfile = ImageIO.read(new File("src/profile.jpg")); // 기본 프로필 사진
		Image userprofile;
		ImageIcon profile;
		ImageIcon userprofileIcon;
		try {
			userprofile = ImageIO.read(new File("src/" + username + ".jpg")); // 프로필 사진 불러오기
			profile = new ImageIcon(userprofile);
			Image profileimg = profile.getImage(); // 이미지 아이콘 이미지로 변환
			Image profilesize = profileimg.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
			userprofileIcon = new ImageIcon(profilesize);
		} catch (IOException e) {
			userprofileIcon = new ImageIcon(originalProfile);
		}

		btnProfile.setIcon(userprofileIcon);
		btnProfile.setBackground(Color.WHITE);
		btnProfile.setBounds(30, 85, 55, 55);

		// 프로필 사진 추가
		ProfileImgAction action = new ProfileImgAction();
		btnProfile.addActionListener(action); // 액션리스너 달기
		friendPane.add(btnProfile);
		repaint();

		lblName = new JLabel(""); // 사용자 이름
		lblName.setFont(new Font("한컴 고딕", Font.BOLD, 14));
		lblName.setBounds(100, 90, 150, 40);
		lblName.setText(username);
		friendPane.add(lblName);
		repaint();
		setVisible(true);

		// 친구 목록
		lblFriendList = new JLabel("친구");
		lblFriendList.setForeground(new Color(105, 105, 105));
		lblFriendList.setFont(new Font("한컴 고딕", Font.PLAIN, 12));
		lblFriendList.setBounds(30, 160, 40, 40);
		friendPane.add(lblFriendList);
		repaint();

		// 친구 메뉴 //
		ImageIcon person = new ImageIcon("src/icon1.png");
		Image img = person.getImage(); // 이미지 아이콘 이미지로 변환
		Image personImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon personIcon = new ImageIcon(personImg); // 이미지 다시 이미지 아이콘으로 변환
		JLabel lblFriendMenu = new JLabel(personIcon);
		lblFriendMenu.setBounds(30, 35, 40, 40);
		menuPane.add(lblFriendMenu);
		repaint();
		setVisible(true);

		// 채팅 목록 //
		ImageIcon chat = new ImageIcon("src/icon2.png");
		Image img2 = chat.getImage(); // 이미지 아이콘 이미지로 변환
		Image chatImage = img2.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		ImageIcon chatIcon = new ImageIcon(chatImage); // 이미지 다시 이미지 아이콘으로 변환
		JLabel lblChatMenu = new JLabel(chatIcon);
		// 채팅 목록 클릭 시
		// ClickMouseListener clickListener = new ClickMouseListener();
		// lblChatMenu.addMouseListener(clickListener);

		lblChatMenu.setBounds(25, 100, 50, 40);
		menuPane.add(lblChatMenu);
		repaint();

		// 친구 목록 Pane
		friendListPane = new JPanel();
		friendListPane.setBounds(10, 200, 140, 380); // 위치 설정
		friendListPane.setBackground(Color.WHITE);
		friendListPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // 왼쪽 정렬
		friendPane.add(friendListPane);
		repaint();

		// 채팅방 생성 버튼 //
		btnChatMake = new JButton("채팅방");
		btnChatMake.setBackground(Color.WHITE);
		btnChatMake.setFont(new Font("한컴 고딕", Font.PLAIN, 12));
		ChatMakeAction chatAction = new ChatMakeAction();
		btnChatMake.addActionListener(chatAction);
		btnChatMake.setBounds(7, 520, 85, 23);
		menuPane.add(btnChatMake);
		repaint();

		// 로그아웃 버튼 //
		btnLogout = new JButton("로그아웃");
		btnLogout.setBackground(Color.WHITE);
		btnLogout.setFont(new Font("한컴 고딕", Font.PLAIN, 12));
		// 로그아웃 버튼 클릭 이벤트
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "410", "LOGOUT");
				msg.outuser = UserName; // 누른 사람 이름 전달
				mainview.SendObject(msg); // mainview 통해 서버로 보내기
				System.exit(0);
			}
		});
		btnLogout.setBounds(7, 550, 85, 23);
		menuPane.add(btnLogout);
		repaint();

		UserName = username;
		setVisible(true);
	}

	// 마우스 이벤트 //
	// 채팅 목록 메뉴 클릭 시 //
	class ClickMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) { // 마우스 클릭 시
			JavaObjClientChat chat = new JavaObjClientChat(UserName, mainview); // 버튼 누르면 Chat.java 실행
			setVisible(false);
		}
	}

	// 새로 들어온 사람 체크박스 생성
	public void AddUser(String username) {
		String newuser = username; // 전달받은 새로운 접속자 이름

		// 체크박스 생성 //
		checkBox = new JCheckBox("    " + newuser); // 새로 들어온 사용자 이름으로 체크박스 생성
		checkBox.setFont(new Font("한컴 고딕", Font.BOLD, 13));
		checkBox.setSize(170, 20);
		checkBox.setBackground(Color.WHITE);
		managecheck.addCheck(checkBox); // 벡터에 추가

		checkBox.addActionListener(new ActionListener() { // 체크 박스 선택 시 선택한 이름 추가
			public void actionPerformed(ActionEvent e) {
				managecheck.AllCheck(checkBox); // 체크박스 체크됐는지 확인
			}
		});

		friendListPane.add(checkBox);
		repaint();

		setVisible(true);
		no_user++; // 접속자 + 1
		repaint();
	}

	// 체크박스 관리
	public class ManageCheck {
		// 체크박스 초기화
		public void resetAll() {
			for (JCheckBox ch : checkVec) // checkVec에 있는 checkBox 초기화
				ch.setSelected(false);
		}

		// 채팅방 추가
		public void addCheck(JCheckBox checkBox) {
			checkVec.add(checkBox);
		}

		// 체크박스 체크됐는지 확인
		public Object AllCheck(JCheckBox checkBox) {
			for (JCheckBox ch : checkVec) { // 모든 체크박스
				if (ch.isSelected()) // 체크박스 체크됐으면
					userlist += (ch.getText().trim()); // userlist에 이름 추가
			}
			return null;
		}

		// 로그아웃 한 사용자 체크박스 색 바꾸기
		public void OutUser(String outuser) {
			for (JCheckBox ch : checkVec) {
				if (ch.getText().equals(outuser))
					checkBox.setForeground(Color.RED);
			}
		}
	}

	// 채팅방 버튼으로 채팅방 생성 action
	class ChatMakeAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnChatMake) {
				// 효과음 재생
				SoundPlay soundplay = new SoundPlay(); // 효과음 재생
				soundplay.test();

				// 서버에 510 채팅방 생성 코드 보내기
				ChatMsg cm = new ChatMsg(UserName, "510", "MULTI CHAT MAKE");
				// cm.chatwith = userlist; // 자신의 이름 제외한 채팅 상대들 저장해주기
				cm.userlist = UserName + " " + userlist; // 자신 이름 + 선택한 이름들(서버로 보낼 것)
				mainview.SendObject(cm); // 메인뷰로 보내기

				// 채팅방 생성 코드 보내고 userlist 초기화
				userlist = "";
				// 체크박스 초기화
				managecheck.resetAll();
			}
		}

	}

	// 채팅방 생성 버튼 누르면 사운드 재생
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

	// 마우스 이벤트
	class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // 더블 클릭 시
				String chatUser = lblFriendName.getText(); // 더블 클릭한 친구 이름

				// 서버에 510 채팅방 생성 코드 보내기
				ChatMsg cm = new ChatMsg(UserName, "510", "NEW CHAT MAKE" + chatUser + "와 채팅할 것");
				cm.userlist = UserName + " " + chatUser; // 자신 이름도 포함해서
				mainview.SendObject(cm); // 메인뷰로 보내기

				// 채팅방 생성 코드 보내고 userlist 초기화
				userlist = "";
			}
		}
	}

	// 프로필 사진 관리
	public void ManageProfile() {
		frame = new Frame("프로필 사진 변경");
		fd = new FileDialog(frame, "프로필 사진 선택", FileDialog.LOAD);
		fd.setVisible(true);

		ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
		Image changeProfile = img.getImage(); // 이미지 아이콘 이미지로 변환
		Image changeProfileImg = changeProfile.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
		ImageIcon newProfileIcon = new ImageIcon(changeProfileImg);

		BufferedImage newProfile = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		Graphics g = newProfile.getGraphics();
		g.drawImage(changeProfile, 0, 0, null);
		g.dispose();

		// 새로 바꾼 이미지 저장
		try {
			String filename = "src/" + UserName + ".jpg"; // 프로필 사진 바꾼 사람의 이름으로 사진 저장
			ImageIO.write(newProfile, "jpg", new File(filename));

		} catch (IOException e) {
			e.printStackTrace();
		}
		btnProfile.setIcon(newProfileIcon); // 프로필 사진 선택한 사진으로 변경
		repaint();

		ChatMsg obcm = new ChatMsg(UserName, "500", "PROFILE IMG"); // 프로필 관리 코드
		obcm.setProfile(newProfileIcon); // 바꾼 이미지로 설정해서 서버로 보내기
		mainview.SendObject(obcm); // 500 코드 보내기
	}

	// 프로필 사진 변경 action
	class ProfileImgAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnProfile) { // 프로필 관리 버튼 누르면
				ManageProfile(); // 프로필 사진 관리 실행
			}
		}
	}
}