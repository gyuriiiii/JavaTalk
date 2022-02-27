
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
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
	// private JTextArea textArea;
	private JTextPane textArea;
	private JTextPane textAreaMenu; // ä�ù� ��� �޴�

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JButton emojiBtn; // �̸�Ƽ�� ��ư
	private JButton btnProfile; // ������ ���� ��ư

	private JavaObjClientView mainview;

	private String[] array = new String[10]; // ������ �̸� �迭
	public String[] chatwith; // ä���ϴ� ��� �̸�
	public String room_id;
	public String userlist; // ä�� ��� (�� ����)

	// �̸�Ƽ�� ��� pane
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

		// userlist���� �ڽ��̸� ���� �и�
		String[] chatchat = userlist.split(" "); // ���� �������� �и�
		String chatuserwith = "";

		for (int i = 0; i < chatchat.length; i++) {
			if (chatchat[i].equals(username)) { // �ڽ��� �̸��� ����
				continue;
			} else {
				chatuserwith += chatchat[i];
			}
		}

		setTitle("ä�ù�");
		setBounds(100, 100, 394, 630);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textAreaMenu = new JTextPane();
		textAreaMenu.setEditable(true); // ����� �̹��� ��
		textAreaMenu.setBackground(new Color(169, 189, 206));
		textAreaMenu.setBounds(0, 0, 382, 80);
		textAreaMenu.setFont(new Font("����ü", Font.PLAIN, 14));
		contentPane.add(textAreaMenu);
		repaint();

		lblPartnerName = new JLabel(chatuserwith);
		lblPartnerName.setBackground(Color.WHITE);
		lblPartnerName.setFont(new Font("���� ���", Font.BOLD, 14));
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
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // ��ư Ŭ���� ȣ��
				if (e.getSource() == txtInput) {
					String msg = null;
					msg = txtInput.getText();
					// �߰� //
					mainview.SendMessage(msg, room_id); // room_id�� �Բ� ������
					txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
					txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
					if (msg.contains("/exit")) // ���� ó��
						System.exit(0);
				}
			}
		});
		txtInput.setBounds(12, 489, 262, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("����");
		btnSend.setBackground(new Color(255, 236, 66));
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // ��ư Ŭ���� ȣ��
				// ���� ��ư ������
				if (e.getSource() == btnSend) {
					String msg = null;
					msg = txtInput.getText();

					mainview.SendMessage(msg, room_id); // room_id�� �Բ� ������
					txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
					txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
					if (msg.contains("/exit")) // ���� ó��
						System.exit(0);
				}
			}
		});
		btnSend.setBounds(295, 489, 69, 40);
		contentPane.add(btnSend);
		repaint();

		// ������ ����
		btnProfile = new JButton("");

		Image originalProfile = ImageIO.read(new File("src/profile.jpg")); // �⺻ ������ ����
		Image userprofile;
		ImageIcon profile;
		ImageIcon userprofileIcon;
		try {
			userprofile = ImageIO.read(new File("src/" + chatuserwith + ".jpg")); // ������ ���� �ҷ�����
			profile = new ImageIcon(userprofile);
			Image profileimg = profile.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image profilesize = profileimg.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
			userprofileIcon = new ImageIcon(profilesize);
		} catch (IOException e) {
			userprofileIcon = new ImageIcon(originalProfile);
		}

		// �̹��� ������ ũ�� ���� //
		btnProfile.setIcon(userprofileIcon);
		btnProfile.setBounds(12, 10, 55, 55);
		textAreaMenu.add(btnProfile);

		// ��� ������ Ŭ�� �� ���� ���� ���� //

		lblUserName = new JLabel("Name");
		lblUserName.setForeground(Color.BLACK);
		lblUserName.setFont(new Font("���� ���", Font.PLAIN, 12));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(300, 540, 60, 30);
		contentPane.add(lblUserName);
		repaint();
		setVisible(true);

		UserName = username;
		lblUserName.setText("[ " + username + " ]");

		// ���� ���� ��ư
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
					frame = new Frame("�̹���÷��");
					fd = new FileDialog(frame, "�̹��� ����", FileDialog.LOAD);
					// frame.setVisible(true);
					// fd.setDirectory(".\\");
					fd.setVisible(true);

					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");

					obcm.room_id = room_id; // ���ȣ ����
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.setImg(img);
					mainview.SendObject(obcm); // ���κ� ���� ������ ������
				}
			}
		});
		contentPane.add(imgBtn);
		repaint();

		// �̸�Ƽ�� ���� ��ư
		ImageIcon icon2 = new ImageIcon("src/emoji.png");
		Image img2 = icon2.getImage(); // �̹��� ������ �̹����� ��ȯ
		Image emojiImg = img2.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		ImageIcon emojiIcon = new ImageIcon(emojiImg);
		JButton emojiBtn = new JButton();
		emojiBtn.setIcon(emojiIcon);
		emojiBtn.setBackground(Color.WHITE);
		emojiBtn.setBounds(60, 540, 30, 30);

		// �̸�Ƽ�� ���� ��ư Ŭ�� �� �̺�Ʈ //
		emojiBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == emojiBtn) {
					new EmojiList();
				}
			}
		});

		contentPane.add(emojiBtn);
		repaint();

		JButton btnNewButton = new JButton("�� ��");
		btnNewButton.setFont(new Font("���� ���", Font.PLAIN, 14));
		btnNewButton.setBackground(SystemColor.controlHighlight);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				msg.room_id = room_id;
				mainview.SendObject(msg); // mainview ���� ������ ������
				System.exit(0);
			}
		});
		btnNewButton.setBounds(295, 20, 65, 35);
		textAreaMenu.add(btnNewButton);
		repaint();

		// �׸� ���� (ä�ù� ���� �� ����)
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

	// �̸�Ƽ�� ����Ŭ�� �̺�Ʈ
	class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // ���� Ŭ�� ��
				// ����Ŭ�� �� �̸�Ƽ�� ����
				JLabel lbl = (JLabel) e.getSource(); // Ŭ���� ��

				ChatMsg obcm = new ChatMsg(UserName, "600", "EMOJI");
				obcm.emoji = (ImageIcon) lbl.getIcon();
				obcm.room_id = room_id; // ���ȣ ����
				mainview.SendObject(obcm); // ���κ� ���� ������ ������
			}
		}
	}

	// ȭ�鿡 ���
	public void AppendText(String msg) {
		// ä�� ���� �˶� �︮����
		SoundPlay soundplay = new SoundPlay(); // ȿ���� ���
		soundplay.test();

		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();

		// ������ �̵�
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

	// ȭ�� ������ ���
	public void AppendTextR(String msg) {
		msg = msg.trim(); // �� �� blank�� \n ����
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
		// Image�� �ʹ� ũ�� �ִ� ���� �Ǵ� ���� 200 �������� ��ҽ�Ų��.
		if (width > 200 || height > 200) {
			if (width > height) { // ���� ����
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // ���� ����
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
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// īī���� ���� ���� ���
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

	// �̸�Ƽ�� ����Ʈ
	public class EmojiList extends JFrame {
		public EmojiList() {
			setTitle("�̸�Ƽ��");
			setBounds(180, 450, 250, 300);
			setResizable(false);

			JTextPane emojiPane = new JTextPane(); // �̸�Ƽ�� ��� pane ����
			emojiPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // ���̾ƿ� ����
			emojiPane.setBackground(Color.WHITE);
			emojiPane.setVisible(true);
			add(emojiPane); // �����ӿ� emojiPane ����

			// Ŭ�� ������
			MyMouseListener listener = new MyMouseListener();

			// ������ �����ؼ� emojiPane�� ����
			ImageIcon imgicon = new ImageIcon("src/emoji/1.png");
			Image img1 = imgicon.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg1 = img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji1 = new ImageIcon(emojiImg1); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji1 = new JLabel(emoji1); // ģ�� ������ ���� ����
			// ������ ���
			lblEmoji1.addMouseListener(listener);
			emojiPane.add(lblEmoji1);

			ImageIcon imgicon2 = new ImageIcon("src/emoji/2.png");
			Image img2 = imgicon2.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg2 = img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji2 = new ImageIcon(emojiImg2); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji2 = new JLabel(emoji2); // ģ�� ������ ���� ����
			lblEmoji2.addMouseListener(listener);
			emojiPane.add(lblEmoji2);

			ImageIcon imgicon3 = new ImageIcon("src/emoji/3.png");
			Image img3 = imgicon3.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg3 = img3.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji3 = new ImageIcon(emojiImg3); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji3 = new JLabel(emoji3); // ģ�� ������ ���� ����
			lblEmoji3.addMouseListener(listener);
			emojiPane.add(lblEmoji3);

			ImageIcon imgicon4 = new ImageIcon("src/emoji/4.png");
			Image img4 = imgicon4.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg4 = img4.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji4 = new ImageIcon(emojiImg4); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji4 = new JLabel(emoji4); // ģ�� ������ ���� ����
			lblEmoji4.addMouseListener(listener);
			emojiPane.add(lblEmoji4);

			ImageIcon imgicon5 = new ImageIcon("src/emoji/5.png");
			Image img5 = imgicon5.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg5 = img5.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji5 = new ImageIcon(emojiImg5); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji5 = new JLabel(emoji5); // ģ�� ������ ���� ����
			lblEmoji5.addMouseListener(listener);
			emojiPane.add(lblEmoji5);

			ImageIcon imgicon6 = new ImageIcon("src/emoji/6.png");
			Image img6 = imgicon6.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg6 = img6.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji6 = new ImageIcon(emojiImg6); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji6 = new JLabel(emoji6); // ģ�� ������ ���� ����
			lblEmoji6.addMouseListener(listener);
			emojiPane.add(lblEmoji6);

			ImageIcon imgicon7 = new ImageIcon("src/emoji/7.png");
			Image img7 = imgicon7.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg7 = img7.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji7 = new ImageIcon(emojiImg7); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji7 = new JLabel(emoji7); // ģ�� ������ ���� ����
			lblEmoji7.addMouseListener(listener);
			emojiPane.add(lblEmoji7);

			ImageIcon imgicon8 = new ImageIcon("src/emoji/8.png");
			Image img8 = imgicon8.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg8 = img8.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji8 = new ImageIcon(emojiImg8); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji8 = new JLabel(emoji8); // ģ�� ������ ���� ����
			lblEmoji8.addMouseListener(listener);
			emojiPane.add(lblEmoji8);

			ImageIcon imgicon9 = new ImageIcon("src/emoji/9.png");
			Image img9 = imgicon9.getImage(); // �̹��� ������ �̹����� ��ȯ
			Image emojiImg9 = img9.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon emoji9 = new ImageIcon(emojiImg9); // �̹��� �ٽ� �̹��� ���������� ��ȯ
			JLabel lblEmoji9 = new JLabel(emoji9); // ģ�� ������ ���� ����
			lblEmoji9.addMouseListener(listener);
			emojiPane.add(lblEmoji9);

			repaint();
			setVisible(true);
		}
	}
}
