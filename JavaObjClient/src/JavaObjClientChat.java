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

// 친구 목록 창
class JavaObjClientChat extends JFrame {
	private JPanel contentPane;
	public JPanel menuPane; // 메뉴 칸
	public JPanel friendPane; // 친구 목록 칸

	private JLabel lblFriend;

	public JLabel lblFriendMenu; // 친구 목록 메뉴
	public JLabel lblChatMenu; // 채팅 목록 메뉴

	private String Username;
	public JLabel lblChatRoom; // 채팅방 목록

	private JavaObjClientView mainview;
	public String userlist; // 채팅방 참가자 이름

	public JPanel ChatListPane; // 친구 목록 패널
	public JLabel lblChatUsers; // 새 친구 이름

	public JavaObjClientChat(String username, JavaObjClientView view) {
		mainview = view;

		setTitle("KaKaoTalk");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		lblFriend = new JLabel("채팅");
		lblFriend.setFont(new Font("한컴 고딕", Font.BOLD, 24));
		lblFriend.setBounds(30, 30, 50, 40);
		friendPane.add(lblFriend);
		repaint();
		ImageIcon addchat = new ImageIcon("src/addchat.png");
		repaint();

		// 친구 메뉴 //
		ImageIcon person = new ImageIcon("src/icon1.png");
		Image img = person.getImage(); // 이미지 아이콘 이미지로 변환
		Image personImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon personIcon = new ImageIcon(personImg); // 이미지 다시 이미지 아이콘으로 변환
		JLabel lblFriendMenu = new JLabel(personIcon);
		
		// 친구 목록 클릭 시
		// ClickMouseListener clickListener = new ClickMouseListener();
		// lblFriendMenu.addMouseListener(clickListener);
		lblFriendMenu.setBounds(30, 35, 40, 40);
		menuPane.add(lblFriendMenu);
		repaint();

		// 채팅 목록 //
		ImageIcon chat = new ImageIcon("src/icon2.png");
		Image img2 = chat.getImage(); // 이미지 아이콘 이미지로 변환
		Image chatImage = img2.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		ImageIcon chatIcon = new ImageIcon(chatImage); // 이미지 다시 이미지 아이콘으로 변환
		JLabel lblChatMenu = new JLabel(chatIcon);
		lblChatMenu.setBounds(25, 100, 50, 40);
		menuPane.add(lblChatMenu);
		repaint();
		setVisible(true);

		// 채팅 목록 출력 //
		lblChatRoom = new JLabel();
		// lblChatRoom.setBounds(30, 100, 230, 50);
		// friendPane.add(lblChatRoom);
		repaint();

		// 채팅 목록 Pane
		ChatListPane = new JPanel();
		ChatListPane.setBounds(18, 90, 170, 50); // 위치 설정
		ChatListPane.setBackground(Color.WHITE);
		ChatListPane.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); // 왼쪽 정렬
		friendPane.add(ChatListPane);
		repaint();

		setVisible(true);
	}

	// 마우스 이벤트 //
	// 친구 목록 메뉴 클릭 시 //
	class ClickMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) throws IOException { // 마우스 클릭 시
			JavaObjClientList list = new JavaObjClientList(Username, mainview); // 버튼 누르면 Chat.java 실행
			setVisible(false);
		}
	}

	// 채팅방 목록에 추가하기 //
	public void AddChatRoom(String userlist, String room_id) {
		ImageIcon profile2 = new ImageIcon("src/profile.jpg");
		Image imgProfile = profile2.getImage(); // 이미지 아이콘 이미지로 변환
		Image profileImg = imgProfile.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon profileIcon = new ImageIcon(profileImg); // 이미지 다시 이미지 아이콘으로 변환
		JLabel lblFriendProfile = new JLabel(profileIcon); // 친구 프로필 사진 생성
		ChatListPane.add(lblFriendProfile); // 친구 목록 pane에 추가
		repaint();

		this.userlist = userlist;
		lblChatUsers = new JLabel(userlist); // 접속자 이름으로 Label 생성
		lblChatUsers.setFont(new Font("한컴 고딕", Font.BOLD, 13));

		ChatListPane.add(lblChatUsers);
		repaint();
		setVisible(true);
	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(Username, "500", "MOUSE");
		mainview.SendObject(cm); // 자기 자신이 아니라 mainview에 있는 sendObject함 (입출력을 mainview에서 담당함)
	}

}