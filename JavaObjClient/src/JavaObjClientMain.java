// JavaObjClient.java
// ObjecStream ����ϴ� ä�� Client
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaObjClientMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUserName;
	// private JTextField txtIpAddress;
	// private JTextField txtPortNumber;
	private JLabel lblLogo; // īī���� �ΰ�

	public JButton btnConnect; // �α��� ��ư

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjClientMain frame = new JavaObjClientMain();
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
	public JavaObjClientMain() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 630); // ũ�� ����
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(255, 235, 51));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ����� �̸� �Է�
		JLabel lblNewLabel = new JLabel(" �̸�");
		lblNewLabel.setFont(new Font("���� ���", Font.PLAIN, 14));
		lblNewLabel.setBounds(70, 345, 50, 40);
		contentPane.add(lblNewLabel);

		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(120, 345, 210, 40);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);

		// īī���� ������
		ImageIcon logo = new ImageIcon("src/logo3.png");
		logo = imageSetSize(logo, 100, 100);
		lblLogo = new JLabel(logo); // īī���� �ΰ� ����
		lblLogo.setBounds(130, 134, 139, 100);
		contentPane.add(lblLogo);

		// �α��� ��ư
		JButton btnConnect = new JButton("�α���");
		btnConnect.setForeground(Color.WHITE);
		btnConnect.setBackground(new Color(66, 54, 48));
		btnConnect.setBounds(70, 395, 260, 45);
		btnConnect.setFont(new Font("���� ���", Font.PLAIN, 16));
		contentPane.add(btnConnect);
		Myaction action = new Myaction();
		btnConnect.addActionListener(action);
		txtUserName.addActionListener(action);
	}

	ImageIcon imageSetSize(ImageIcon icon, int i, int j) { // image Size Setting
		Image ximg = icon.getImage(); // ImageIcon�� Image�� ��ȯ.
		Image yimg = ximg.getScaledInstance(i, j, java.awt.Image.SCALE_SMOOTH);
		ImageIcon xyimg = new ImageIcon(yimg);
		return xyimg;
	}

	class Myaction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnConnect || e.getSource() == txtUserName) { // �α��� ��ư �����ų� ����Ű ������
				SoundPlay soundplay = new SoundPlay(); // ȿ���� ���
				soundplay.test();
			}

			String username = txtUserName.getText().trim();
			String ip_addr = "127.0.0.1";
			String port_no = "30000";

			JavaObjClientView view = new JavaObjClientView(username, ip_addr, port_no);

			setVisible(false);

		}
	}

	// ���� ����
	class SoundPlay {
		public void test() {
			File bgm;
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;

			bgm = new File("src/sound/enter.wav");

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
}
