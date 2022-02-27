
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
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
import javax.swing.text.StyledEditorKit.ForegroundAction;

public class JavaObjClientChatRoom extends JFrame {

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
	// private JTextArea textArea;
	private JTextPane textArea;
	private JTextPane textAreaMenu; // 채팅방 상단 메뉴

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JButton emojiBtn; // 이모티콘 버튼
	private JButton btnProfile; // 프로필 관리 버튼

	private JavaObjClientView mainview;

	private String[] array = new String[10]; // 접속자 이름 배열
	public String[] chatwith; // 채팅하는 상대 이름
	public String room_id;
	public String userlist; // 채팅 상대 (나 포함)

	// 이모티콘 목록 pane
	public JTextPane emojiPane;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public JavaObjClientChatRoom(String username, JavaObjClientView view, String room_id, String userlist)
			throws IOException {
		mainview = view;
		this.room_id = room_id;

		// userlist에서 자신이름 빼고 분리
		String[] chatchat = userlist.split(" "); // 공백 기준으로 분리
		String chatuserwith = "";

		for (int i = 0; i < chatchat.length; i++) {
			if (chatchat[i].equals(username)) { // 자신의 이름은 제외
				continue;
			} else {
				chatuserwith += chatchat[i];
			}
		}

		setTitle("채팅방");
		setBounds(100, 100, 394, 630);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textAreaMenu = new JTextPane();
		textAreaMenu.setEditable(true); // 해줘야 이미지 들어감
		textAreaMenu.setBackground(new Color(169, 189, 206));
		textAreaMenu.setBounds(0, 0, 382, 80);
		textAreaMenu.setFont(new Font("굴림체", Font.PLAIN, 14));
		contentPane.add(textAreaMenu);
		repaint();

		lblPartnerName = new JLabel(chatuserwith);
		lblPartnerName.setBackground(Color.WHITE);
		lblPartnerName.setFont(new Font("한컴 고딕", Font.BOLD, 14));
		lblPartnerName.setHorizontalAlignment(SwingConstants.LEFT);
		lblPartnerName.setBounds(75, 18, 200, 40);
		textAreaMenu.add(lblPartnerName);
		setVisible(true);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 79, 382, 401);
		contentPane.add(scrollPane);
		repaint();

		textArea = new JTextPane();
		Color c = new Color(178, 199, 217);
		textArea.setBackground(c);
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // 버튼 클릭시 호출
				if (e.getSource() == txtInput) {
					String msg = null;
					msg = txtInput.getText();
					// 추가 //
					mainview.SendMessage(msg, room_id); // room_id도 함께 보내기
					txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
					txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
					if (msg.contains("/exit")) // 종료 처리
						System.exit(0);
				}
			}
		});
		txtInput.setBounds(12, 489, 262, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("전송");
		btnSend.setBackground(new Color(255, 236, 66));
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // 버튼 클릭시 호출
				// 전송 버튼 누르면
				if (e.getSource() == btnSend) {
					String msg = null;
					msg = txtInput.getText();

					mainview.SendMessage(msg, room_id); // room_id도 함께 보내기
					txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
					txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
					if (msg.contains("/exit")) // 종료 처리
						System.exit(0);
				}
			}
		});
		btnSend.setBounds(295, 489, 69, 40);
		contentPane.add(btnSend);
		repaint();

		// 프로필 사진
		btnProfile = new JButton("");

		Image originalProfile = ImageIO.read(new File("src/profile.jpg")); // 기본 프로필 사진
		Image userprofile;
		ImageIcon profile;
		ImageIcon userprofileIcon;
		try {
			userprofile = ImageIO.read(new File("src/" + chatuserwith + ".jpg")); // 프로필 사진 불러오기
			profile = new ImageIcon(userprofile);
			Image profileimg = profile.getImage(); // 이미지 아이콘 이미지로 변환
			Image profilesize = profileimg.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
			userprofileIcon = new ImageIcon(profilesize);
		} catch (IOException e) {
			userprofileIcon = new ImageIcon(originalProfile);
		}

		// 이미지 아이콘 크기 변경 //
		btnProfile.setIcon(userprofileIcon);
		btnProfile.setBounds(12, 10, 55, 55);
		textAreaMenu.add(btnProfile);

		// 상대 프로필 클릭 시 원본 사진 보기 //

		lblUserName = new JLabel("Name");
		lblUserName.setForeground(Color.BLACK);
		lblUserName.setFont(new Font("한컴 고딕", Font.PLAIN, 12));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(300, 540, 60, 30);
		contentPane.add(lblUserName);
		repaint();
		setVisible(true);

		UserName = username;
		lblUserName.setText("[ " + username + " ]");

		// 사진 전송 버튼
		ImageIcon icon = new ImageIcon("src/image.png");
		Image img = icon.getImage();
		Image imgImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(imgImg);
		JButton imgBtn = new JButton();
		imgBtn.setIcon(imgIcon);
		imgBtn.setBackground(Color.WHITE);
		imgBtn.setBounds(12, 540, 30, 30);

		imgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == imgBtn) {
					frame = new Frame("이미지첨부");
					fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
					// frame.setVisible(true);
					// fd.setDirectory(".\\");
					fd.setVisible(true);

					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");

					obcm.room_id = room_id; // 방번호 설정
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.setImg(img);
					mainview.SendObject(obcm); // 메인뷰 통해 서버로 보내기
				}
			}
		});
		contentPane.add(imgBtn);
		repaint();

		// 이모티콘 전송 버튼
		ImageIcon icon2 = new ImageIcon("src/emoji.png");
		Image img2 = icon2.getImage(); // 이미지 아이콘 이미지로 변환
		Image emojiImg = img2.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		ImageIcon emojiIcon = new ImageIcon(emojiImg);
		JButton emojiBtn = new JButton();
		emojiBtn.setIcon(emojiIcon);
		emojiBtn.setBackground(Color.WHITE);
		emojiBtn.setBounds(60, 540, 30, 30);

		// 이모티콘 전송 버튼 클릭 시 이벤트 //
		emojiBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == emojiBtn) {
					new EmojiList();
				}
			}
		});

		contentPane.add(emojiBtn);
		repaint();

		JButton btnNewButton = new JButton("종 료");
		btnNewButton.setFont(new Font("한컴 고딕", Font.PLAIN, 14));
		btnNewButton.setBackground(SystemColor.controlHighlight);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				msg.room_id = room_id;
				mainview.SendObject(msg); // mainview 통해 서버로 보내기
				System.exit(0);
			}
		});
		btnNewButton.setBounds(295, 20, 65, 35);
		textAreaMenu.add(btnNewButton);
		repaint();

		// 테마 변경 (채팅방 배경색 등 변경)
		JButton change = new JButton();
		change.setBackground(Color.WHITE);
		change.setBounds(108, 540, 30, 30);
		ImageIcon icon3 = new ImageIcon("src/theme.png");
		Image img3 = icon3.getImage();
		Image imgImg3 = img3.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon imgIcon3 = new ImageIcon(imgImg3);
		change.setIcon(imgIcon3);

		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaMenu.setBackground(new Color(79, 78, 78));
				textArea.setBackground(new Color(83, 83, 82));
				lblPartnerName.setForeground(Color.WHITE);
			}
		});
		contentPane.add(change);
		repaint();
	}

	// 이모티콘 더블클릭 이벤트
	class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // 더블 클릭 시
				// 더블클릭 한 이모티콘 전송
				JLabel lbl = (JLabel) e.getSource(); // 클릭한 라벨

				ChatMsg obcm = new ChatMsg(UserName, "600", "EMOJI");
				obcm.emoji = (ImageIcon) lbl.getIcon();
				obcm.room_id = room_id; // 방번호 설정
				mainview.SendObject(obcm); // 메인뷰 통해 서버로 보내기
			}
		}
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// 채팅 오면 알람 울리도록
		SoundPlay soundplay = new SoundPlay(); // 효과음 재생
		soundplay.test();

		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();

		// 끝으로 이동
		// textArea.setCaretPosition(len);
		// textArea.replaceSelection(msg + "\n");

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.WHITE);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 화면 우측에 출력
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞 뒤 blank와 \n 제거
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", right);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);

		} else
			textArea.insertIcon(ori_icon);
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
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
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// 카카오톡 오면 사운드 재생
	class SoundPlay {
		public void test() {
			File bgm;
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;

			bgm = new File("src/sound/kakaotalk.wav");

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

	// 이모티콘 리스트
	public class EmojiList extends JFrame {
		public EmojiList() {
			setTitle("이모티콘");
			setBounds(180, 450, 250, 300);
			setResizable(false);

			JTextPane emojiPane = new JTextPane(); // 이모티콘 목록 pane 생성
			emojiPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // 레이아웃 설정
			emojiPane.setBackground(Color.WHITE);
			emojiPane.setVisible(true);
			add(emojiPane); // 프레임에 emojiPane 부착

			// 클릭 리스너
			MyMouseListener listener = new MyMouseListener();

			// 사이즈 조절해서 emojiPane에 부착
			ImageIcon imgicon = new ImageIcon("src/emoji/1.png");
			Image img1 = imgicon.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg1 = img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji1 = new ImageIcon(emojiImg1); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji1 = new JLabel(emoji1); // 친구 프로필 사진 생성
			// 리스너 등록
			lblEmoji1.addMouseListener(listener);
			emojiPane.add(lblEmoji1);

			ImageIcon imgicon2 = new ImageIcon("src/emoji/2.png");
			Image img2 = imgicon2.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg2 = img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji2 = new ImageIcon(emojiImg2); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji2 = new JLabel(emoji2); // 친구 프로필 사진 생성
			lblEmoji2.addMouseListener(listener);
			emojiPane.add(lblEmoji2);

			ImageIcon imgicon3 = new ImageIcon("src/emoji/3.png");
			Image img3 = imgicon3.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg3 = img3.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji3 = new ImageIcon(emojiImg3); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji3 = new JLabel(emoji3); // 친구 프로필 사진 생성
			lblEmoji3.addMouseListener(listener);
			emojiPane.add(lblEmoji3);

			ImageIcon imgicon4 = new ImageIcon("src/emoji/4.png");
			Image img4 = imgicon4.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg4 = img4.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji4 = new ImageIcon(emojiImg4); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji4 = new JLabel(emoji4); // 친구 프로필 사진 생성
			lblEmoji4.addMouseListener(listener);
			emojiPane.add(lblEmoji4);

			ImageIcon imgicon5 = new ImageIcon("src/emoji/5.png");
			Image img5 = imgicon5.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg5 = img5.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji5 = new ImageIcon(emojiImg5); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji5 = new JLabel(emoji5); // 친구 프로필 사진 생성
			lblEmoji5.addMouseListener(listener);
			emojiPane.add(lblEmoji5);

			ImageIcon imgicon6 = new ImageIcon("src/emoji/6.png");
			Image img6 = imgicon6.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg6 = img6.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji6 = new ImageIcon(emojiImg6); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji6 = new JLabel(emoji6); // 친구 프로필 사진 생성
			lblEmoji6.addMouseListener(listener);
			emojiPane.add(lblEmoji6);

			ImageIcon imgicon7 = new ImageIcon("src/emoji/7.png");
			Image img7 = imgicon7.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg7 = img7.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji7 = new ImageIcon(emojiImg7); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji7 = new JLabel(emoji7); // 친구 프로필 사진 생성
			lblEmoji7.addMouseListener(listener);
			emojiPane.add(lblEmoji7);

			ImageIcon imgicon8 = new ImageIcon("src/emoji/8.png");
			Image img8 = imgicon8.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg8 = img8.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji8 = new ImageIcon(emojiImg8); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji8 = new JLabel(emoji8); // 친구 프로필 사진 생성
			lblEmoji8.addMouseListener(listener);
			emojiPane.add(lblEmoji8);

			ImageIcon imgicon9 = new ImageIcon("src/emoji/9.png");
			Image img9 = imgicon9.getImage(); // 이미지 아이콘 이미지로 변환
			Image emojiImg9 = img9.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji9 = new ImageIcon(emojiImg9); // 이미지 다시 이미지 아이콘으로 변환
			JLabel lblEmoji9 = new JLabel(emoji9); // 친구 프로필 사진 생성
			lblEmoji9.addMouseListener(listener);
			emojiPane.add(lblEmoji9);

			repaint();
			setVisible(true);
		}
	}
}
