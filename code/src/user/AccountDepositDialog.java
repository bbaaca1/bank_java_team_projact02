package user;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import main.BankSystemMainFrame;
import system.*;

@SuppressWarnings("serial")
public class AccountDepositDialog extends JDialog {
	
	final static Logger logger = LogManager.getLogger(AccountDepositDialog.class);
	
	private int mouseX, mouseY;
	private String bank;
	private SystemKeyPad virtualKeyPad;
	private int index;
	private long totalAmount;
	private FileSystemDataUpdater fd;
	private SystemAccountStateCheck sc;

	public AccountDepositDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) 
	{
		// 이미지 아이콘들을 리스트화
				String folderPath = "이미지\\버튼";
				ArrayList<ImageIcon> iconlist = new ArrayList<>();
				
				File folder = new File(folderPath);
				File[] files = folder.listFiles();
				
				for (File file: files) 
				{
					if (file.isFile()) {
						ImageIcon icon = new ImageIcon(file.getPath());
						iconlist.add(icon);
					}
				}
				
				// 이미지들의 이름과 위치, 순서 확인
				for (int i = 0; i < files.length; i++) 
				{
					System.out.println(files[i] + ", " + i + " 번째");
				}
				
//				 String path = System.getProperty("user.dir");
//			     System.out.println("Working Directory = " + path);

		
		
		
		
		
		ArrayList<Account> depositAccountlist = as.getDepositAccountList();
		Map<String, Integer> map = as.getMap();

		this.setResizable(false);
		this.setUndecorated(true);
		// 배경 패널 설정
		
        
		ImageIcon depositbackgroundImage = new ImageIcon("이미지\\보이스피싱안내문구.PNG");
		JLabel backgroundimage = new JLabel(depositbackgroundImage);
		backgroundimage.setBounds(0,0,1440,810);
		this.add(backgroundimage);
		backgroundimage.setVisible(true);





		setVisible(false); // 화면에 보이도록 설정
		
		Font font = new Font("Malgun Gothic", Font.BOLD, 15); // 폰트 설정값을 저장

		this.setSize(1440, 810); // 창 크기

		// 창을 화면 중앙으로 띄우기
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int leftTopX = centerPoint.x - this.getWidth() / 2;
		int leftTopY = centerPoint.y - this.getHeight() / 2;
		this.setLocation(leftTopX, leftTopY);

		// JWindow 창 끄기
		JButton btnExit = new JButton("시스템 종료");
		btnExit.setBounds(0, 0, 100, 100);
		this.add(btnExit);

		// 마우스로 버튼을 클릭하면 이벤트 처리
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// 창 드래그 설정
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// 현재 마우스 위치 저장
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// 창 이동
				Point point = getLocation();
				int deltaX = e.getX() - mouseX;
				int deltaY = e.getY() - mouseY;
				setLocation(point.x + deltaX, point.y + deltaY);
			}
		});

		// 취소 버튼 생성 및 설정. 
		JButton cancleButton2 = new JButton(iconlist.get(59));
		cancleButton2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		cancleButton2.setBounds(0, 730, 150, 80);
		cancleButton2.setVisible(false);
		this.add(cancleButton2);
		//아래 가 보이스피싱 때 나올 취소
		JButton cancleButton1 = new JButton(iconlist.get(18));
		cancleButton1.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		cancleButton1.setBounds(0, 699, 716, 111);
		cancleButton1.setVisible(true);
		this.add(cancleButton1);
		cancleButton1.addMouseListener(new MouseAdapter() { // 마우스 이벤트
			@Override
			public void mousePressed(MouseEvent e) { // 클릭했을때
				setVisible(false);
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				return;
			}
		});

		// 버튼들을 패널에 추가 및 이벤트 처리
		this.setLayout(null);
		this.add(cancleButton2);
		cancleButton2.addMouseListener(new MouseAdapter() { // 마우스 이벤트
			@Override
			public void mousePressed(MouseEvent e) { // 클릭했을때
				setVisible(false);
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				return;
			}
		});

		
		
		// 확인 버튼 생성 및 설정
//		ImageIcon confirmButtonImage1 = new ImageIcon("이미지\\버튼\\확인.png");
		JButton continueTrading = new JButton(iconlist.get(17));
		continueTrading.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		continueTrading.setBounds(724, 699, 716, 111);
		this.add(continueTrading);

		JButton continueTrading2 = new JButton(iconlist.get(64));
		continueTrading2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		continueTrading2.setBounds(1290, 730, 150, 80);
		this.add(continueTrading2);

//		JButton bankButton1 = new JButton(iconlist.get(26)); // ct는 continue transaction을 의미
//		bankButton1.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
//		bankButton1.setBounds(300, 200, 150, 80); // 버튼의 크기와 위치를 직접 설정
//		this.add(bankButton1);
//
//		JButton bankButton2 = new JButton(iconlist.get(42)); // ct는 continue transaction을 의미
//		bankButton2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
//		bankButton2.setBounds(450, 200, 150, 80); // 버튼의 크기와 위치를 직접 설정
//		this.add(bankButton2);
//
//		JButton bankButton3 = new JButton(iconlist.get(60)); // ct는 continue transaction을 의미
//		bankButton3.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
//		bankButton3.setBounds(600, 200, 150, 80); // 버튼의 크기와 위치를 직접 설정
//		this.add(bankButton3);
//
//		JButton bankButton4 = new JButton(iconlist.get(48)); // ct는 continue transaction을 의미
//		bankButton4.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
//		bankButton4.setBounds(750, 200, 150, 80); // 버튼의 크기와 위치를 직접 설정
//		this.add(bankButton4);
//
//		JButton bankButton5 = new JButton(iconlist.get(39)); // ct는 continue transaction을 의미
//		bankButton5.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
//		bankButton5.setBounds(900, 200, 150, 80); // 버튼의 크기와 위치를 직접 설정
//		this.add(bankButton5);
		ImageIcon[] bankImage = new ImageIcon[5];
		bankImage[0] = new ImageIcon(folderPath + "\\단풍은행.png");
        bankImage[1] = new ImageIcon(folderPath + "\\신한은행.png");
        bankImage[2] = new ImageIcon(folderPath + "\\카카오은행.png");
        bankImage[3] = new ImageIcon(folderPath + "\\우리은행.png");
        bankImage[4] = new ImageIcon(folderPath + "\\새마을금고.png");
        
		
		JButton[] AccountBankButton = new JButton[5];
		int xValue = 0; // 버튼의 x위치를 변화시킬 값
        for(int i = 0; i < 5; i++) 
        {
        	AccountBankButton[i] = new JButton(bankImage[i]);
        	
        	
        	AccountBankButton[i].setFont(font); // 정해진 폰트로 설정

        	if(i == 0) {
        		AccountBankButton[i].setBounds(206 + xValue, 300, 200, 100); // 버튼의 크기와 위치를 직접 설정

        	}
        	else {
        		AccountBankButton[i].setBounds(206 + xValue, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정

            	xValue += 276;
        	}
        	this.add(AccountBankButton[i]);
        }
        	
 

		JButton confirmButton = new JButton(iconlist.get(64));
		confirmButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(1290, 730, 150, 80); // (x축, y축, 넓이, 높이)
		this.add(confirmButton);

		JButton confirmButton2 = new JButton(iconlist.get(64));
		confirmButton2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton2.setBounds(1290, 730, 150, 80); // (x축, y축, 넓이, 높이)
		this.add(confirmButton2);

		JButton confirmButton3 = new JButton(iconlist.get(64));
		confirmButton3.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton3.setBounds(1290, 730, 150, 80); // (x축, y축, 넓이, 높이)
		this.add(confirmButton3);

		JLabel guideLabel = new JLabel("입금할 계좌번호를 입력하세요.");
		guideLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		guideLabel.setBounds(300, 240, 600, 100);
		this.add(guideLabel);

		JLabel guideLabel4 = new JLabel("입금할 계좌번호의 은행을 선택하세요.");
		guideLabel4.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		guideLabel4.setBounds(300, 100, 600, 100);
		this.add(guideLabel4);

		// 계좌 입력하는 필드 생성 및 설정
		JLabel accountNumberLabel = new JLabel("계좌번호:");
		accountNumberLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		accountNumberLabel.setBounds(300, 320, 100, 30);
		this.add(accountNumberLabel);

		// 계좌번호 입력하는 텍스트필드
		JTextField accountNumberTextField = new JTextField();
		accountNumberTextField.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
		accountNumberTextField.setBounds(400, 320, 200, 30);
		this.add(accountNumberTextField);

		JLabel daguideLabel = new JLabel("얼마를 입금하시겠습니까?");
		daguideLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		daguideLabel.setBounds(300, 120, 600, 100);
		this.add(daguideLabel);

		JLabel daguideLabel2 = new JLabel("얼마를 입금하시겠습니까?");
		daguideLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		daguideLabel2.setBounds(300, 120, 600, 100);
		this.add(daguideLabel2);

		// 맨 위 확인문구
		JLabel guideLabel2 = new JLabel("입금액을 보내는 것이 맞나요?");
		guideLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		guideLabel2.setBounds(300, 100, 600, 100);
		this.add(guideLabel2);

		JLabel sendBankLabel = new JLabel("돈을 받는 은행:");
		sendBankLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		sendBankLabel.setBounds(300, 260, 600, 100);
		this.add(sendBankLabel);

		JLabel sendBankaccountLabel = new JLabel("돈을 받는 계좌번호: ");
		sendBankaccountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		sendBankaccountLabel.setBounds(300, 240, 600, 100);
		this.add(sendBankaccountLabel);

		JLabel receiverNameLabel = new JLabel("돈을 받는 사람의 이름:");
		receiverNameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		receiverNameLabel.setBounds(300, 220, 600, 100);
		this.add(receiverNameLabel);

		JLabel chargeLabel = new JLabel("수수료: 500원");
		chargeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		chargeLabel.setBounds(300, 500, 600, 100);
		this.add(chargeLabel);

		JLabel guideLabel3 = new JLabel("거래가 완료되었습니다.");
		guideLabel3.setFont(new Font("Malgun Gothic", Font.BOLD, 70));
		guideLabel3.setBounds(320, 350, 840, 110);
		this.add(guideLabel3);

		JLabel depositAmount1 = new JLabel("입금액:");// 처음 나오는 입금액 : (배열버튼 화면)
		depositAmount1.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		depositAmount1.setBounds(300, 700, 600, 100);
		this.add(depositAmount1);

		JLabel depositAmount2 = new JLabel("입금액:");
		depositAmount2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		depositAmount2.setBounds(300, 400, 600, 100);
		this.add(depositAmount2);

		JLabel depositAmount3 = new JLabel("입금액:");
		depositAmount3.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		depositAmount3.setBounds(300, 400, 600, 100);
		this.add(depositAmount3);

		// 입금금액 입력하는 텍스트필드
		JTextField keypadTextField = new JTextField();
		keypadTextField.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
		keypadTextField.setBounds(370, 435, 200, 30);
		this.add(keypadTextField);
		
		JButton confirmButton4 = new JButton(iconlist.get(64));
		confirmButton4.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton4.setBounds(1290, 730, 150, 80); // (x축, y축, 넓이, 높이)
		this.add(confirmButton4);
		
		
		// 계좌번호 입력하는 키패드
		accountNumberTextField.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				virtualKeyPad = new SystemKeyPad(12);
				virtualKeyPad.setInputNumberTextField(accountNumberTextField);
				virtualKeyPad.setVisible(true);
				accountNumberTextField.setText(virtualKeyPad.getNumber());
			}
		});

		// 금액 입력하는 키패드
		keypadTextField.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				virtualKeyPad = new SystemKeyPad(100000);
				virtualKeyPad.setInputNumberTextField(keypadTextField);
				virtualKeyPad.setVisible(true);
				keypadTextField.setText(virtualKeyPad.getNumber());
				totalAmount = Long.parseLong(virtualKeyPad.getNumber());
			}
		});
		// 은행목록을 보여주기 전 "계속거래" 버튼
		continueTrading.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) 
			{
				cancleButton1.setVisible(false);
				cancleButton2.setVisible(true);
				AccountBankButton[0].setVisible(true);
				AccountBankButton[1].setVisible(true);
				AccountBankButton[2].setVisible(true);
				AccountBankButton[3].setVisible(true);
				AccountBankButton[4].setVisible(true);
				backgroundimage.setVisible(false);
				guideLabel4.setVisible(false);
				continueTrading.setVisible(false);
				confirmButton.setVisible(false);
				guideLabel4.setVisible(true);
				continueTrading2.setVisible(false);

			}
		});

		AccountBankButton[0].addMouseListener(new MouseAdapter() { // 대우 은행(bankButton1)
			@Override

			public void mousePressed(MouseEvent e) {
				bank = " 대우 은행";
				guideLabel.setVisible(true);
				accountNumberLabel.setVisible(true);
				accountNumberTextField.setVisible(true);
				guideLabel4.setVisible(false);
				continueTrading2.setVisible(true);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				continueTrading.setVisible(false);

			}
		});

		AccountBankButton[1].addMouseListener(new MouseAdapter() { // 신한 은행(bankButton2)
			@Override
			public void mousePressed(MouseEvent e) {
				bank = " 신한 은행";
				guideLabel.setVisible(true);
				accountNumberLabel.setVisible(true);
				accountNumberTextField.setVisible(true);
				continueTrading2.setVisible(true);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				guideLabel4.setVisible(false);
				continueTrading.setVisible(false);
			}
		});
		AccountBankButton[2].addMouseListener(new MouseAdapter() { // 카카오 은행(bankButton3)
			@Override
			public void mousePressed(MouseEvent e) {
				bank = "카카오 은행";
				guideLabel.setVisible(true);
				accountNumberLabel.setVisible(true);
				accountNumberTextField.setVisible(true);
				continueTrading2.setVisible(true);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				guideLabel4.setVisible(false);
			}
		});
		AccountBankButton[3].addMouseListener(new MouseAdapter() { // 우리 은행(bankButton4)
			@Override
			public void mousePressed(MouseEvent e) {
				bank = " 우리 은행";
				guideLabel.setVisible(true);
				accountNumberLabel.setVisible(true);
				accountNumberTextField.setVisible(true);
				continueTrading2.setVisible(true);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				guideLabel4.setVisible(false);
			}
		});
		AccountBankButton[4].addMouseListener(new MouseAdapter() { // 새마을 금고 (bankButton5)
			@Override
			public void mousePressed(MouseEvent e) {
				bank = " 새마을 금고";
				guideLabel.setVisible(true);
				accountNumberLabel.setVisible(true);
				accountNumberTextField.setVisible(true);
				continueTrading2.setVisible(true);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				guideLabel4.setVisible(false);
			}
		});
		// 금액 입력 첫번째 화면 // 금액 버튼배열을 10의 크기로 생성
		JButton[] amountButton = new JButton[10];
		ImageIcon[] amountImage = new ImageIcon[10];
		int yValue = 0; // 버튼의 y위치를 변화시킬 값
		amountImage[0] = new ImageIcon(folderPath + "\\1만.png");
        amountImage[1] = new ImageIcon(folderPath + "\\3만.png");
        amountImage[2] = new ImageIcon(folderPath + "\\5만.png");
        amountImage[3] = new ImageIcon(folderPath + "\\10만.png");
        amountImage[4] = new ImageIcon(folderPath + "\\50만.png");
        amountImage[5] = new ImageIcon(folderPath + "\\100만.png");
        amountImage[6] = new ImageIcon(folderPath + "\\300만.png");
        amountImage[7] = new ImageIcon(folderPath + "\\500만.png");
        amountImage[8] = new ImageIcon(folderPath + "\\1000만.png");
        amountImage[9] = new ImageIcon(folderPath + "\\기타.png");
		for (int i = 0; i < 10; i++) {
			amountButton[i] = new JButton(amountImage[i]);
			amountButton[i].setFont(new Font("Malgun Gothic", Font.BOLD, 20));
			if (i < 5) // 만약 5보다 작다면
				amountButton[i].setBounds(200, 200 + yValue, 150, 80); // 왼쪽에 차례대로 정렬되는 버튼
			else
				amountButton[i].setBounds(1140, yValue - 300, 150, 80); // 오른쪽에 차례대로 정렬되는 버튼

			yValue += 100; // y위치를 100씩 이동
			this.add(amountButton[i]); // Button을 추가
		}

		amountButton[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 10000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 3만 원 버튼
		amountButton[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 30000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 5만 원 버튼
		amountButton[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 50000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 10만 원 버튼
		amountButton[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 100000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 50만 원 버튼
		amountButton[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 500000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 100만 원 버튼
		amountButton[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 1000000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 200만 원 버튼
		amountButton[6].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 3000000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 300만 원 버튼
		amountButton[7].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 5000000;
				totalAmount += amount;

				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 600만 원 버튼
		amountButton[8].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int amount = 10000000;
				totalAmount += amount;
				depositAmount1.setText("입금액:" + String.valueOf(totalAmount) + "원");
			}
		});

		// 계속거래 (continueTrading2) : "입금할 계좌번호를 입력하세요" 계좌번호: 입력칸 => 화면에 있는 계속거래 버튼 누르면
		// 아래 목록을 보여줌
		continueTrading2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String account = accountNumberTextField.getText();
				
				if (Pattern.matches("\\d{12}", account) && map.get(account) != null) {
					index = map.get(account);
					sc = new SystemAccountStateCheck(index, as);
					if (!sc.checkState()) {
						bankSystemFrame.setVisible(true);
						bankSystemFrame.getBackgroundPanel().setVisible(true);
						bankSystemFrame.getAccountManagementPanel().setVisible(false);
						bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
						setVisible(false);
						return;
					}
					amountButton[0].setVisible(true);
					amountButton[1].setVisible(true);
					amountButton[2].setVisible(true);
					amountButton[3].setVisible(true);
					amountButton[4].setVisible(true);
					amountButton[5].setVisible(true);
					amountButton[6].setVisible(true);
					amountButton[7].setVisible(true);
					amountButton[8].setVisible(true);
					amountButton[9].setVisible(true);
					daguideLabel.setVisible(true);
					confirmButton.setVisible(true); // 입금액으로 가야하는 버튼
					depositAmount1.setVisible(true); // 첫번째로 나오는 "입금액:"
					guideLabel.setVisible(false);
					accountNumberLabel.setVisible(false);
					accountNumberTextField.setVisible(false);
					AccountBankButton[0].setVisible(false);
					AccountBankButton[1].setVisible(false);
					AccountBankButton[2].setVisible(false);
					AccountBankButton[3].setVisible(false);
					AccountBankButton[4].setVisible(false);
					continueTrading2.setVisible(false);
					guideLabel4.setVisible(false);
					keypadTextField.setVisible(false);

				} else {
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					setVisible(false);
					return;
				}
			}

		});
		// "얼마를 입력하시겠습니까?", 기타버튼, 확인버튼이 있는 화면에서 [기타버튼]을 누를 시 아래 목록을 보여줌
		amountButton[9].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				confirmButton3.setVisible(true);
				daguideLabel.setVisible(true); // "얼마를 입금하시겠습니까?" 안내문구
				keypadTextField.setVisible(true);
				depositAmount2.setVisible(true); // 두번째로 나오는 "입금액:"
				amountButton[0].setVisible(false);
				amountButton[1].setVisible(false);
				amountButton[2].setVisible(false);
				amountButton[3].setVisible(false);
				amountButton[4].setVisible(false);
				amountButton[5].setVisible(false);
				amountButton[6].setVisible(false);
				amountButton[7].setVisible(false);
				amountButton[8].setVisible(false);
				amountButton[9].setVisible(false);
				guideLabel.setVisible(false);
				accountNumberLabel.setVisible(false);
				accountNumberTextField.setVisible(false);
				AccountBankButton[0].setVisible(false);
				AccountBankButton[1].setVisible(false);
				AccountBankButton[2].setVisible(false);
				AccountBankButton[3].setVisible(false);
				AccountBankButton[4].setVisible(false);
				continueTrading2.setVisible(false);
				guideLabel4.setVisible(false);
				depositAmount1.setVisible(false); // 첫번째로 나오는 "입금액:"
				
			}
		});

		// =================처음 실행할 때 화면======================
		AccountBankButton[0].setVisible(false);//단
		AccountBankButton[1].setVisible(false);//신
		AccountBankButton[2].setVisible(false);//카
		AccountBankButton[3].setVisible(false);//우
		AccountBankButton[4].setVisible(false);// 새마을금고
		guideLabel4.setVisible(false); // 입금할 계좌번호의 은행을 선택하세요.
		guideLabel.setVisible(false); // 입금할 계좌번호를 입력하세요.
		accountNumberTextField.setVisible(false);// 계좌번호 입력하는 텍스트필드
		accountNumberLabel.setVisible(false); // "계좌번호:"
		guideLabel2.setVisible(false); // 입금액을 보내는 것이 맞나요?
		confirmButton2.setVisible(false);
		guideLabel3.setVisible(false); // 거래가 완료되었습니다.
		receiverNameLabel.setVisible(false); // "돈을 받는 사람의 이름:"
		chargeLabel.setVisible(false); // "수수료:"
		sendBankLabel.setVisible(false); // "돈을 받는 은행:"
		sendBankaccountLabel.setVisible(false);
		daguideLabel.setVisible(false); // "얼마를 입금하시겠습니까?"-1번째
		daguideLabel2.setVisible(false); // "얼마를 입금하시겠습니까?"-2번째
		confirmButton3.setVisible(false);
		depositAmount1.setVisible(false); // 첫번째로 나오는 "입금액:"
		depositAmount2.setVisible(false); // 두번째로 나오는 "입금액:"
		keypadTextField.setVisible(false);
		amountButton[0].setVisible(false);
		amountButton[1].setVisible(false);
		amountButton[2].setVisible(false);
		amountButton[3].setVisible(false);
		amountButton[4].setVisible(false);
		amountButton[5].setVisible(false);
		amountButton[6].setVisible(false);
		amountButton[7].setVisible(false);
		amountButton[8].setVisible(false);
		amountButton[9].setVisible(false);// 기타 버튼
		depositAmount3.setVisible(false);
		confirmButton4.setVisible(false);
		// 버튼들을 패널에 추가 및 이벤트 처리
		this.setLayout(null);
		
		this.add(confirmButton);

		confirmButton.addMouseListener(new MouseAdapter() 
		{ // 얼마를 입금하시겠습니까에 들어갈 첫번째 확인
			@Override
			public void mousePressed(MouseEvent e) {
				sendBankaccountLabel.setVisible(true);
				receiverNameLabel.setVisible(true); // "돈을 받는 사람의 이름:"
				chargeLabel.setVisible(true); // 수수료:
				sendBankLabel.setVisible(true); // 보내는 사람은행:
				confirmButton2.setVisible(true);
				depositAmount2.setVisible(true); // 두번째로 나오는 "입금액:"
				guideLabel2.setVisible(true); // "입금액을 보내는 것이 맞나요?" 안내문구
				amountButton[0].setVisible(false);
				amountButton[1].setVisible(false);
				amountButton[2].setVisible(false);
				amountButton[3].setVisible(false);
				amountButton[4].setVisible(false);
				amountButton[5].setVisible(false);
				amountButton[6].setVisible(false);
				amountButton[7].setVisible(false);
				amountButton[8].setVisible(false);
				amountButton[9].setVisible(false);
				guideLabel.setVisible(false);
				accountNumberLabel.setVisible(false);
				accountNumberTextField.setVisible(false);
				confirmButton.setVisible(false);
				daguideLabel.setVisible(false);
				daguideLabel2.setVisible(false);
				depositAmount1.setVisible(false); // 첫번째로 나오는 "입금액:"
				keypadTextField.setVisible(false);
				receiverNameLabel.setText("돈을 받는 사람: " + depositAccountlist.get(index).getName());// 사용자 이름을 가져옴
				depositAmount2.setText("입금액: " + String.valueOf(totalAmount) + " 원");
				sendBankLabel.setText(sendBankLabel.getText() + bank);// 위에서 선택한 은행을 가져와서 보여줌
				sendBankaccountLabel.setText("돈을 받는 계좌번호: " + accountNumberTextField.getText()); // 위에서 입력한 계좌 가져와서 보여줌
			}
		});
		confirmButton2.addMouseListener(new MouseAdapter() { // confirmButton2 : "입금액을 보내는 것이 맞나요?" 안내글 있는 화면의 확인 버튼 >
			@Override
			public void mousePressed(MouseEvent e) {
				depositAccountlist.get(index).setBalance(depositAccountlist.get(index).getBalance() + (totalAmount - 500)); // 입금액
				depositAccountlist.get(index).setLastTradingDate(LocalDate.now());// 현재시간 넣어줌
				fd = new FileSystemDataUpdater(depositAccountlist.get(index), index);// 엑셀에 저장하기 위해 사용자의 위치 값을 넣어줌(?)
				fd.ModifyAccountDateToExcel();// 엘셀에 저장
				
            	// 입금정보 로깅 부분
            	ThreadContext.put("ACCOUNT", depositAccountlist.get(index).getName()+"-입금");
            	
            	String logFolderPath = "data/" + ThreadContext.get("ACCOUNT"); // 사용자 명으로 폴더생성

                // 폴더 생성
                File folder = new File(logFolderPath);
                if (!folder.exists()) {
                	// 경로에 폴더가 없다면 폴더를 생성
                    folder.mkdirs();
                }
                
                logger.info("deposit: " + totalAmount);

        		ThreadContext.remove("ACCOUNT");
				
				guideLabel3.setVisible(true);
				confirmButton4.setVisible(true);
				amountButton[0].setVisible(false);
				amountButton[1].setVisible(false);
				amountButton[2].setVisible(false);
				amountButton[3].setVisible(false);
				amountButton[4].setVisible(false);
				amountButton[5].setVisible(false);
				amountButton[6].setVisible(false);
				amountButton[7].setVisible(false);
				amountButton[8].setVisible(false);
				amountButton[9].setVisible(false);
				guideLabel2.setVisible(false);
				confirmButton2.setVisible(false);
				sendBankaccountLabel.setVisible(false);
				receiverNameLabel.setVisible(false);
				chargeLabel.setVisible(false);
				sendBankLabel.setVisible(false);
				depositAmount1.setVisible(false); // 첫번째로 나오는 "입금액:"
				depositAmount2.setVisible(false); // 두번째로 나오는 "입금액:"
				keypadTextField.setVisible(false);
			}
		});
		confirmButton3.addMouseListener(new MouseAdapter() { // 기타 > 확인 누르면나오는 화면
			@Override
			public void mousePressed(MouseEvent e) {
				depositAmount2.setVisible(true); // 두번째로 나오는 "입금액:"
				guideLabel2.setVisible(true); // "입금액을 보내는 것이 맞나요?" 안내문구
				chargeLabel.setVisible(true); // 수수료:
				sendBankLabel.setVisible(true); // 보내는 사람은행:
				confirmButton2.setVisible(true);
				sendBankaccountLabel.setVisible(true);
				receiverNameLabel.setVisible(true); // "돈을 받는 사람의 이름:"
				amountButton[0].setVisible(false);
				amountButton[1].setVisible(false);
				amountButton[2].setVisible(false);
				amountButton[3].setVisible(false);
				amountButton[4].setVisible(false);
				amountButton[5].setVisible(false);
				amountButton[6].setVisible(false);
				amountButton[7].setVisible(false);
				amountButton[8].setVisible(false);
				amountButton[9].setVisible(false);
				guideLabel.setVisible(false);
				accountNumberLabel.setVisible(false);
				accountNumberTextField.setVisible(false);
				daguideLabel.setVisible(false);
				confirmButton3.setVisible(false);
				daguideLabel2.setVisible(false);
				depositAmount1.setVisible(false); // 첫번째로 나오는 "입금액:"
				keypadTextField.setVisible(false);
				receiverNameLabel.setText("돈을 받는 사람: " + depositAccountlist.get(index).getName());// 사용자 이름을 가져옴
				depositAmount2.setText("입금액: " + String.valueOf(totalAmount));
				sendBankLabel.setText(sendBankLabel.getText() + bank);// 위에서 선택한 은행을 가져와서 보여줌
				sendBankaccountLabel.setText("돈을 받는 계좌번호: " + accountNumberTextField.getText()); // 위에서 입력한 계좌 가져와서 보여줌
			
			}
		});
		confirmButton4.addMouseListener(new MouseAdapter() { // 거래가 완료되었습니다 화면에 있는 확인버튼 누르면 메인으로감
				@Override
				public void mousePressed(MouseEvent e) {
					setVisible(false);
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					return;
				}
		});
	}
}