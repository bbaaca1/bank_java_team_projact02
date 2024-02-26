package user;


import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
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

import org.apache.commons.collections4.map.MultiKeyMap;

import main.BankSystemMainFrame;
import system.*;

@SuppressWarnings("serial")
public class AccountTransferDialog extends JDialog {
    
	private SystemAccountStateCheck senderSecurity;
	private SystemAccountStateCheck collectorSecurity;
    private int mouseX, mouseY;
    private SystemKeyPad virtualKeyPad;
    private long totalAmount; // 600만원을 초과해서 보관할 수 없는 변수
    private int senderIndex;
    private int collectorIndex;
    private FileSystemDataUpdater fd;
	private LocalDate date; // 마지막거래일자를 담아둘 변수
	private Account senderAccount;
	private Account collectorAccount;
    private ArrayList<Account> list = null;

    public AccountTransferDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) {
    	
    	MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap();
    	Map<String, Integer> map = as.getMap();
    	ArrayList<Account> depositAccountlist = as.getDepositAccountList();
    	ArrayList<Account> savingAccountlist = as.getSavingAccountList();
    	ArrayList<Account> timeAccountlist = as.getTimeAccountList();
    	ArrayList<Account> freeAccountlist = as.getFreeAccountList();
    	
        this.setSize(1440, 810);
        Font font = new Font("Malgun Gothic", Font.BOLD, 15); // 폰트 설정값을 저장

        this.setUndecorated(true); // 다이얼로그의 타이틀바를 없앰
        
        // 창을 화면 중앙으로 띄우기
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int leftTopX = centerPoint.x - this.getWidth() / 2;
        int leftTopY = centerPoint.y - this.getHeight() / 2;
        this.setLocation(leftTopX, leftTopY);
        
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
        
        String imagePath = "이미지\\"; // 이미지 경로
        
        //취소버튼 이미지
        ImageIcon voicePhshingTcImage = new ImageIcon(imagePath + "버튼\\X.PNG");
        ImageIcon tcImage = new ImageIcon(imagePath + "버튼\\취소.PNG");
        
        // 거래를 취소합니다 버튼
        JButton voicePhshingTcButton = new JButton(voicePhshingTcImage); // tc는 transaction cancellation을 의미
        voicePhshingTcButton.setBounds(0, 699, 716, 111); // 버튼의 크기와 위치를 직접 설정
        this.add(voicePhshingTcButton);
        
        // 취소 버튼
        JButton tcButton = new JButton(tcImage); // tc는 transaction cancellation을 의미
        tcButton.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        this.add(tcButton);
        tcButton.setVisible(false);;
            
        //확인버튼 이미지
        ImageIcon voicePhshingCheckImage = new ImageIcon(imagePath + "버튼\\O.PNG");
        ImageIcon checkImage = new ImageIcon(imagePath + "버튼\\확인.PNG");
        
        // 계속 거래버튼을 생성하는 반복문        
        JButton[] ctButton = new JButton[3]; // ct는 continue transaction을 의미
        for(int i = 0; i < 3; i++) {
        	if(i == 0) {
        		ctButton[i] = new JButton(voicePhshingCheckImage);
        		ctButton[i].setBounds(724, 699, 716, 111);
        	}
        	else {
        		ctButton[i] = new JButton(checkImage);
        		ctButton[i].setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        	}
            this.add(ctButton[i]); // AccountTransferDialog에 버튼추가
            ctButton[i].setVisible(false);
        }
        
        // 확인 버튼을 생성하는 반복문
        JButton[] checkButton = new JButton[4];
        for(int i = 0; i < 4; i++) {
        	checkButton[i] = new JButton(checkImage);
        	checkButton[i].setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
            this.add(checkButton[i]); 
            checkButton[i].setVisible(false);
        }
        
        
        /*
         * 첫 번째 패널: 송금 계좌의 은행 선택화면의 패널
         * 두 번재 패널: 송금자 계좌번호 입력화면의 패널
         * 세 번째 패널: 송금액 입력 화면의 패널
         * 네 번째 패널: 기타를 눌렀을 때 보이는 화면의 패널
         * 다섯 번째 패널: 수금 계좌의 은행 선택화면의 패널
         * 여섯 번째 패널: 수금자 계좌번호 입력화면의 패널
         * 일곱 번째 패널: 송금자의 계좌 비밀번호 입력 화면의 패널
         * 여덟 번째 패널: 이체 정보를 확인하는 화면의 패널
         * 아홉 번째 패널: 거래 완료 화면의 패널
         * 열 번째 패널: 보이스 피싱화면의 패널
         * */
        
        JPanel[] accountTransferPanel = new JPanel[10]; // 패널이 10개로 구성되어있음
        JLabel[] accountTransferLabel = new JLabel[10];
        ImageIcon accountTransferImage = new ImageIcon(imagePath + "버튼\\힣신용대출메인.PNG");
        for(int i = 0; i < 10; i++) {
        	accountTransferPanel[i] = new JPanel();
        	accountTransferPanel[i].setLayout(null); // 레이아웃을 null로 지정해 원하는 곳에 컴포넌트를 배치할 수 있음
        	accountTransferPanel[i].setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        	
        	if(i != 9) {
        		accountTransferLabel[i] = new JLabel(accountTransferImage);
        		accountTransferLabel[i].setBounds(0, 0, 1440, 810);
        		accountTransferPanel[i].add(accountTransferLabel[i]);
        		accountTransferLabel[i].setVisible(true);
        	}
        	accountTransferPanel[i].setVisible(false);
        	
        }
        // 보이스 피싱 화면
        ImageIcon voicePhshingImage = new ImageIcon(imagePath + "보이스피싱안내문구.png");
        JLabel voicePhshingLabel = new JLabel(voicePhshingImage);
        voicePhshingLabel.setBounds(0, 0, 1440, 810);
        accountTransferPanel[9].add(voicePhshingLabel); // 두 번째 패널에 Label을 추가
 
        //은행 이미지 배열
        ImageIcon[] bankImage = new ImageIcon[5];
        
        // 은행 이미지
        bankImage[0] = new ImageIcon(imagePath + "버튼\\단풍은행.png");
        bankImage[1] = new ImageIcon(imagePath + "버튼\\신한은행.png");
        bankImage[2] = new ImageIcon(imagePath + "버튼\\카카오은행.png");
        bankImage[3] = new ImageIcon(imagePath + "버튼\\우리은행.png");
        bankImage[4] = new ImageIcon(imagePath + "버튼\\새마을금고.png");
        
        JButton[] senderAccountBankButton = new JButton[5]; // 송금계좌의 은행버튼
        JButton[] collectorAccountBankButton = new JButton[5]; // 수금계좌의 은행버튼

        int xValue = 0; // 버튼의 x위치를 변화시킬 값
        for(int i = 0; i < 5; i++) {
        	senderAccountBankButton[i] = new JButton(bankImage[i]);
        	collectorAccountBankButton[i] = new JButton(bankImage[i]);
        	
        	senderAccountBankButton[i].setFont(font); // 정해진 폰트로 설정
        	collectorAccountBankButton[i].setFont(font); // 정해진 폰트로 설정
        	if(i == 0) {
        		senderAccountBankButton[i].setBounds(206 + xValue, 300, 200, 100); // 버튼의 크기와 위치를 직접 설정
            	collectorAccountBankButton[i].setBounds(206 + xValue, 300, 200, 100); // 버튼의 크기와 위치를 직접 설정
        	}
        	else {
        		senderAccountBankButton[i].setBounds(206 + xValue, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
            	collectorAccountBankButton[i].setBounds(206 + xValue, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
            	xValue += 276;
        	}
        	
        	
        	accountTransferPanel[0].add(senderAccountBankButton[i]); // 첫 번째 패널에 Button을 추가
        	accountTransferPanel[0].setComponentZOrder(senderAccountBankButton[i], 0);
        	
        	accountTransferPanel[4].add(collectorAccountBankButton[i]); // 다섯 번째 패널에 Button을 추가
        	accountTransferPanel[4].setComponentZOrder(collectorAccountBankButton[i], 0);
        	
        }


      
        // 송금자 계좌번호 입력화면
        JLabel senderAccountEnterLabel = new JLabel("송금자의 계좌번호를 입력하세요");
        senderAccountEnterLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
        senderAccountEnterLabel.setBounds(0, 300, 500, 50);
        accountTransferPanel[1].add(senderAccountEnterLabel); // 두 번째 패널에 Label을 추가
    
        JLabel accountLabel = new JLabel("계좌번호: ");
        accountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
        accountLabel.setBounds(0, 500, 190,50);
        accountTransferPanel[1].add(accountLabel); // 두 번째 패널에 Label을 추가
        
        // 계좌번호 입력 TextField
        JTextField senderAccountEnterTextField = new JTextField(20);
        senderAccountEnterTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
        senderAccountEnterTextField.setBounds(200, 500, 300, 50);
        accountTransferPanel[1].add(senderAccountEnterTextField); // 두 번째 패널에 TextField를 추가
        
        
        accountTransferPanel[1].setComponentZOrder(senderAccountEnterLabel, 0);
        accountTransferPanel[1].setComponentZOrder(accountLabel, 0);
        //금액 및 기타 이미지 배열
        ImageIcon[] amountImage = new ImageIcon[10];
        
        //금액 및 기타 이미지
        amountImage[0] = new ImageIcon(imagePath + "버튼\\1만.png");
        amountImage[1] = new ImageIcon(imagePath + "버튼\\3만.png");
        amountImage[2] = new ImageIcon(imagePath + "버튼\\5만.png");
        amountImage[3] = new ImageIcon(imagePath + "버튼\\10만.png");
        amountImage[4] = new ImageIcon(imagePath + "버튼\\50만.png");
        amountImage[5] = new ImageIcon(imagePath + "버튼\\100만.png");
        amountImage[6] = new ImageIcon(imagePath + "버튼\\200만.png");
        amountImage[7] = new ImageIcon(imagePath + "버튼\\300만.png");
        amountImage[8] = new ImageIcon(imagePath + "버튼\\600만.png");
        amountImage[9] = new ImageIcon(imagePath + "버튼\\기타.png");
        		
        // 송금액 입력 화면
        JButton[] amountButton = new JButton[10];  // 금액 버튼배열을 10의 크기로 생성
        int yValue = 0; // 버튼의 y위치를 변화시킬 값
        
        for(int i = 0; i < 10; i++) {
        	amountButton[i] = new JButton(amountImage[i]);
        	amountButton[i].setFont(font);
        	if(i < 5) // 만약 5보다 작다면
        		amountButton[i].setBounds(200, 200 + yValue, 150, 80); // 왼쪽에 차례대로 정렬되는 버튼
        	else
        		amountButton[i].setBounds(1140, yValue - 300, 150, 80); // 오른쪽에 차례대로 정렬되는 버튼
        	
        	yValue += 100; // y위치를 100씩 이동
        	accountTransferPanel[2].add(amountButton[i]); // 세 번째 패널에 Button을 추가
        	accountTransferPanel[2].setComponentZOrder(amountButton[i], 0);
        }
        
        JLabel amountEnterLabel = new JLabel("얼마를 송금하시겠습니까?");
        amountEnterLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        amountEnterLabel.setBounds(200, 50, 1000, 200);
        accountTransferPanel[2].add(amountEnterLabel); // 세 번째 패널에 Label을 추가
        
		JLabel remittanceAmountLabel = new JLabel("송금액: ");
		remittanceAmountLabel.setBounds(450, 650, 400, 30);
		remittanceAmountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		accountTransferPanel[2].add(remittanceAmountLabel); // 세 번째 패널에 Label을 추가

		JLabel remittanceAmountEnterLabel = new JLabel();
		remittanceAmountEnterLabel.setBounds(550, 650, 400, 30);
		remittanceAmountEnterLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		accountTransferPanel[2].add(remittanceAmountEnterLabel); // 세 번째 패널에 TextField를 추가
		
		accountTransferPanel[2].setComponentZOrder(amountEnterLabel, 0);
		accountTransferPanel[2].setComponentZOrder(remittanceAmountLabel, 0);
		accountTransferPanel[2].setComponentZOrder(remittanceAmountEnterLabel, 0);
		
		
        
		// 기타를 눌렀을 때 보이는 화면
        JLabel amountEnterLabel2 = new JLabel("얼마를 송금하시겠습니까?");
        amountEnterLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        amountEnterLabel2.setBounds(200, 50, 1000, 200);
        accountTransferPanel[3].add(amountEnterLabel2); // 네 번째 패널에 Label을 추가

		JLabel remittanceAmountLabel2 = new JLabel("송금액: "); 	
		remittanceAmountLabel2.setBounds(450, 650, 400, 30);
		remittanceAmountLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		accountTransferPanel[3].add(remittanceAmountLabel2); // 네 번째 패널에 Label을 추가
		
		JTextField remittanceAmountTextField2 = new JTextField(0);
		remittanceAmountTextField2.setBounds(550, 650, 200, 30);
		remittanceAmountTextField2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		accountTransferPanel[3].add(remittanceAmountTextField2); // 네 번째 패널에 TextField를 추가
		
		accountTransferPanel[3].setComponentZOrder(amountEnterLabel2, 0);
		accountTransferPanel[3].setComponentZOrder(remittanceAmountLabel2, 0);
		accountTransferPanel[3].setComponentZOrder(remittanceAmountTextField2, 0);

		// 수금자 계좌번호 입력화면
        JLabel collectorAccountEnterLabel = new JLabel("수금자의 계좌번호를 입력하세요");
        collectorAccountEnterLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        collectorAccountEnterLabel.setBounds(0, 300, 500,50);
        accountTransferPanel[5].add(collectorAccountEnterLabel); // 여섯 번째 패널에 Label을 추가
    
        JLabel accountLabel2 = new JLabel("계좌번호: ");
        accountLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        accountLabel2.setBounds(0, 500, 190,50);
        accountTransferPanel[5].add(accountLabel2); // 여섯 번째 패널에 Label을 추가

        JTextField collectorAccountEnterTextField = new JTextField(20);
        collectorAccountEnterTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        collectorAccountEnterTextField.setBounds(200, 500, 300,50);
        accountTransferPanel[5].add(collectorAccountEnterTextField); // 여섯 번째 패널에 TextField를 추가
        
        accountTransferPanel[5].setComponentZOrder(collectorAccountEnterLabel, 0);
        accountTransferPanel[5].setComponentZOrder(accountLabel2, 0);
        
        // 송금자의 계좌 비밀번호 입력 화면
        JLabel senderPasswordEnterLabel = new JLabel("송금자의 계좌 비밀번호를 입력하세요");
        senderPasswordEnterLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        senderPasswordEnterLabel.setBounds(0, 300, 600,50);
        accountTransferPanel[6].add(senderPasswordEnterLabel); // 일곱 번째 패널에 Label을 추가
    
        JLabel passwordLabel = new JLabel("비밀번호: ");
        passwordLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        passwordLabel.setBounds(0, 500, 190,50);
        accountTransferPanel[6].add(passwordLabel); // 일곱 번째 패널에 Label을 추가

        JTextField senderPasswordEnterTextField = new JTextField(20);
        senderPasswordEnterTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        senderPasswordEnterTextField.setBounds(200, 500, 300,50);
        accountTransferPanel[6].add(senderPasswordEnterTextField); // 일곱 번째 패널에 TextField를 추가
        
        accountTransferPanel[6].setComponentZOrder(senderPasswordEnterLabel, 0);
        accountTransferPanel[6].setComponentZOrder(passwordLabel, 0);
        accountTransferPanel[6].setComponentZOrder(senderPasswordEnterTextField, 0);

        // 이체 정보를 확인하는 화면
        JLabel transferinfoLabel = new JLabel("이체 정보를 확인하세요");
        transferinfoLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        transferinfoLabel.setBounds(0, 100, 400,50);
        accountTransferPanel[7].add(transferinfoLabel); // 여덟 번째 패널에 Label을 추가
        
        JLabel senderAccountLabel = new JLabel("송금자 계좌번호: ");
        senderAccountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        senderAccountLabel.setBounds(0, 300, 800,50);
        accountTransferPanel[7].add(senderAccountLabel); // 여덟 번째 패널에 Label을 추가
        
        JLabel senderAmountLabel = new JLabel("송금액: ");
        senderAmountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        senderAmountLabel.setBounds(0, 400, 600,50);
        accountTransferPanel[7].add(senderAmountLabel); // 여덟 번째 패널에 Label을 추가
        
        JLabel collectorAccountLabel = new JLabel("수금자 계좌번호: ");
        collectorAccountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        collectorAccountLabel.setBounds(0, 500, 800,50);
        accountTransferPanel[7].add(collectorAccountLabel); // 여덟 번째 패널에 Label을 추가
        
        JLabel feeLabel = new JLabel("수수료: 500원");
        feeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        feeLabel.setBounds(0, 600, 400,50);
        accountTransferPanel[7].add(feeLabel); // 여덟 번째 패널에 Label을 추가
        
        accountTransferPanel[7].setComponentZOrder(transferinfoLabel, 0);
        accountTransferPanel[7].setComponentZOrder(senderAccountLabel, 0);
        accountTransferPanel[7].setComponentZOrder(senderAmountLabel, 0);
        accountTransferPanel[7].setComponentZOrder(collectorAccountLabel, 0);
        accountTransferPanel[7].setComponentZOrder(feeLabel, 0);
        
        // 거래 완료 화면
        JLabel transactionCompletedLabel = new JLabel("거래가 완료되었습니다.");
        transactionCompletedLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 40));
        transactionCompletedLabel.setBounds(450, 150, 500,50);
        accountTransferPanel[8].add(transactionCompletedLabel); // 아홉 번째 패널에 Label을 추가
        
        accountTransferPanel[8].setComponentZOrder(transactionCompletedLabel, 0);
        
        for(int i = 0; i < 10; i++) 
        	this.add(accountTransferPanel[i]); // AccountTransferDialog에 열 개의 패널들을 추가
        
        this.setVisible(true); // AccountTransferDialog 보여주기
        voicePhshingTcButton.setVisible(true); // 취소 버튼 보여주기
        ctButton[0].setVisible(true); // 첫 번째 거래 계속 버튼 보여주기
        accountTransferPanel[9].setVisible(true); // 보이스피싱화면 보여주기

        // 거래를 취소합니다 버튼
        voicePhshingTcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	bankSystemFrame.setVisible(true);
            	bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            	setVisible(false);
            	return;
            }
        });
        
        // 취소 버튼
        tcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	bankSystemFrame.setVisible(true);
            	bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            	setVisible(false);
            	return;
            }
        }); 
        
        // 보이스피싱 화면의 "계속 거래"버튼
        ctButton[0].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[0].setVisible(true);
        		tcButton.setVisible(true);
        		
        		voicePhshingTcButton.setVisible(false);
        		accountTransferPanel[9].setVisible(false);
        		ctButton[0].setVisible(false);
        	}
        });
        
        // 송금자의 계좌번호를 입력하는 화면의 "계속 거래"버튼
        ctButton[1].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String account = senderAccountEnterTextField.getText();

        		if (Pattern.matches("\\d{12}", account) && map.get(account) != null) { // 계좌번호가 12자리 이고 map에 계좌번호가 존재할 떄
        			senderIndex = map.get(account); // 사용자의 ArrayList 인덱스 값 저장
        			senderAccount = depositAccountlist.get(senderIndex);
        			date = senderAccount.getLastTradingDate(); // 마지막 거래날짜
        			senderSecurity = new SystemAccountStateCheck(senderIndex, as); // security 생성자에 사용자의 arraylist index와 accountstorage의 as를 넣는다.
        			
        			if(!date.isEqual(LocalDate.now())) { // 마지막 거래날짜로부터 하루이상 지났다면
        				senderAccount.setTransferLimit(0);
        				senderAccount.setTransferLimit(false);
						if(senderAccount.getState() == 4) {
							senderAccount.setState(0);
						}
						else if(senderAccount.getState() == 5) {
							senderAccount.setState(3);
						}
						else if(senderAccount.getState() == 7) {
							senderAccount.setState(1);
						}
						else if(senderAccount.getState() == 8) {
							senderAccount.setState(6);
						}
        			}
        			
        			
        			if(!senderSecurity.checkState(true)) { 
        				bankSystemFrame.setVisible(true);
                    	bankSystemFrame.getBackgroundPanel().setVisible(true);
                        bankSystemFrame.getAccountManagementPanel().setVisible(false);
                        bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    	setVisible(false);
                    	return;
        			}
        			
        			if(account.substring(0,3).equals("021") || account.substring(0,3).equals("022") || account.substring(0,3).equals("011")) { 
        				//정기 적금, 자유 적금, 정기 예금 통장은 이체불가
        				bankSystemFrame.setVisible(true);
                    	bankSystemFrame.getBackgroundPanel().setVisible(true);
                        bankSystemFrame.getAccountManagementPanel().setVisible(false);
                        bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    	setVisible(false);
                    	return;
        			}
        			
        			accountTransferPanel[2].setVisible(true);
	        		checkButton[0].setVisible(true);
	        		
	        		accountTransferPanel[1].setVisible(false);
	        		ctButton[1].setVisible(false);
        		}
        		else {
        			bankSystemFrame.setVisible(true);
                	bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                	setVisible(false);
                	return;
        		}

        	}
        });
        
      // 송금액을 입력하는 화면의 "확인"버튼
        checkButton[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(totalAmount > 500 && senderAccount.getBalance() >= totalAmount) {
            		if(senderAccount.getTransferLimit() + totalAmount > 30000000) {
            			senderAccount.setTransferLimit(true);
            			if (senderAccount.getState() == 0) {
            				senderAccount.setState(4);
            			}
            			else if (senderAccount.getState() == 3) {
            				senderAccount.setState(5);
            			} 
            			bankSystemFrame.setVisible(true);
                    	bankSystemFrame.getBackgroundPanel().setVisible(true);
                        bankSystemFrame.getAccountManagementPanel().setVisible(false);
                        bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    	setVisible(false);
                    	return;
            		}
            		accountTransferPanel[4].setVisible(true);

                	accountTransferPanel[2].setVisible(false);
                	accountTransferPanel[3].setVisible(false);
                	checkButton[0].setVisible(false);
            	}
            	else {
            		bankSystemFrame.setVisible(true);
                	bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                	setVisible(false);
                	return;
            	}
            	
            }
        });
        
        // 수금자의 계좌번호를 입력하는 화면의 "계속 거래"버튼
        ctButton[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	String account = collectorAccountEnterTextField.getText();

        		if (Pattern.matches("\\d{12}", account)  && map.get(account) != null && senderAccount.getAccountNumber() != account) { 
        			// 계좌번호가 12자리 이고, map안에 account가 들어가 있고, 송금자의 계좌번호와 같지 않아야함
        			collectorIndex = map.get(account);
        			String threeDigits = account.substring(0,3);
        			if(threeDigits.equals("012")) {
        				list = depositAccountlist;
        			}
        			else if (threeDigits.equals("021")) {
        				list = savingAccountlist;
        			}
        			else if (threeDigits.equals("011")) {
        				list = timeAccountlist;
        			}
        			else if (threeDigits.equals("022")) {
        				list = freeAccountlist;
        			}
        			
        			collectorAccount = list.get(collectorIndex);
        			
        			if(totalAmount >= 3000000) { 
        				//3백만원 이상 이체를 받은 계좌는 계좌 동결 시작
        				collectorAccount.setFreez(true);

        				collectorSecurity = new SystemAccountStateCheck(collectorIndex, as);
        				collectorSecurity.voicePhishing();
        			}
	            	accountTransferPanel[6].setVisible(true);
	            	checkButton[1].setVisible(true);
	            	
	            	ctButton[2].setVisible(false);
	            	accountTransferPanel[5].setVisible(false);
        		}
        		else {
        			bankSystemFrame.setVisible(true);
                	bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                	setVisible(false);
                	return;
        		}
        		
            }
        });
        // 송금자의 계좌 비밀번호를 입력하는 화면의 "확인"버튼
        checkButton[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	String account = senderAccountEnterTextField.getText();
            	String password = senderPasswordEnterTextField.getText();
            	
            	if (Pattern.matches("\\d{4}", password)&& multikeyMap.get(account, password) != null) { // 비밀번호가 4자리일 때
	            	accountTransferPanel[7].setVisible(true);
	            	checkButton[2].setVisible(true);
	            	
	            	accountTransferPanel[6].setVisible(false);
	            	checkButton[1].setVisible(false);
	            	
	            	senderAccountLabel.setText("송금자 계좌번호: " + senderAccountEnterTextField.getText());
	            	senderAmountLabel.setText("송금액: " + String.valueOf(totalAmount) + "원");
	            	collectorAccountLabel.setText("수금자 계좌번호: " + collectorAccountEnterTextField.getText());
        		}
            	else {
            		bankSystemFrame.setVisible(true);
                	bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                	setVisible(false);
                	return;
            	}
            	
            	
            	

        		
            }
        });
        
        // 이체 정보를 확인하는 화면의 "확인"버튼
        checkButton[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	senderAccount.setBalance(senderAccount.getBalance() - totalAmount - 500);
            	collectorAccount.setBalance(collectorAccount.getBalance() + totalAmount);
            	
            	senderAccount.setTransferLimit(totalAmount);
            	
            	senderAccount.setLastTradingDate(LocalDate.now()); // 마지막 거래일자
            	collectorAccount.setLastTradingDate(LocalDate.now()); // 마지막 거래일자
            	
            		
            	fd = new FileSystemDataUpdater(senderAccount, senderIndex);
            	fd.ModifyAccountDateToExcel();
            	
            	fd = new FileSystemDataUpdater(collectorAccount, collectorIndex);
            	fd.ModifyAccountDateToExcel();
            	
            	accountTransferPanel[8].setVisible(true);
            	checkButton[3].setVisible(true);
            	
            	tcButton.setVisible(false);
            	accountTransferPanel[7].setVisible(false);
            	checkButton[2].setVisible(false);

            }
        });
        
     // 거래 완료가 된 화면의 "확인"버튼
        checkButton[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	bankSystemFrame.setVisible(true);
            	bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            	setVisible(false);
            	return;
            }
        });
        
        
        
        // 대우은행 버튼
        senderAccountBankButton[0].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[1].setVisible(true);
        		ctButton[1].setVisible(true);
        		
        		accountTransferPanel[0].setVisible(false);
        	}
        });
        
        // 신한은행 버튼
        senderAccountBankButton[1].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[1].setVisible(true);
        		ctButton[1].setVisible(true);
        		
        		accountTransferPanel[0].setVisible(false);
        	}
        });
        
        // 카카오은행 버튼
        senderAccountBankButton[2].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[1].setVisible(true);
        		ctButton[1].setVisible(true);
        		
        		accountTransferPanel[0].setVisible(false);
        	}
        });
        
        // 우리은행 버튼
        senderAccountBankButton[3].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[1].setVisible(true);
        		ctButton[1].setVisible(true);
        		
        		accountTransferPanel[0].setVisible(false);
        	}
        	
        });
        
        // 새마을금고 버튼
        senderAccountBankButton[4].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[1].setVisible(true);
        		ctButton[1].setVisible(true);
        		
        		accountTransferPanel[0].setVisible(false);
        	}
        });
        
        // 1만 원 버튼
        amountButton[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 10000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 3만 원 버튼
        amountButton[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 30000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 5만 원 버튼
        amountButton[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 50000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 10만 원 버튼
        amountButton[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 100000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 50만 원 버튼
        amountButton[4].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 500000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 100만 원 버튼
        amountButton[5].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 1000000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 200만 원 버튼
        amountButton[6].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 2000000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 300만 원 버튼
        amountButton[7].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 3000000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        // 600만 원 버튼
        amountButton[8].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int amount = 6000000;
                totalAmount += amount;
                if (totalAmount > 6000000) {
   				totalAmount = 6000000;
                }
                remittanceAmountEnterLabel.setText(String.valueOf(totalAmount) + "원");
            }
        });
        
        //기타 버튼
        amountButton[9].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	accountTransferPanel[2].setVisible(false);
            	accountTransferPanel[3].setVisible(true);
            }
        });
        
     
      //대우은행 버튼
        collectorAccountBankButton[0].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[5].setVisible(true);
        		ctButton[2].setVisible(true);
        		
        		accountTransferPanel[4].setVisible(false);
        	}
        });
        
      //신한은행 버튼
        collectorAccountBankButton[1].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[5].setVisible(true);
        		ctButton[2].setVisible(true);
        		
        		accountTransferPanel[4].setVisible(false);
        	}
        });
        
      //카카오은행 버튼
        collectorAccountBankButton[2].addActionListener(new ActionListener() {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    			accountTransferPanel[5].setVisible(true);
	    		ctButton[2].setVisible(true);
	    		
	    		accountTransferPanel[4].setVisible(false);
    		}
    	});
        
      //우리은행 버튼
        collectorAccountBankButton[3].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[5].setVisible(true);
        		ctButton[2].setVisible(true);
        		
        		accountTransferPanel[4].setVisible(false);             
        	}
        	
        });
        
      //새마을금고 버튼
        collectorAccountBankButton[4].addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		accountTransferPanel[5].setVisible(true);
        		ctButton[2].setVisible(true);
        		
        		accountTransferPanel[4].setVisible(false);
        	}
        });
        
        // 송금인 계좌번호 입력필드
        senderAccountEnterTextField.addMouseListener(new MouseListener() {
			
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
                virtualKeyPad.setVisible(true);
                senderAccountEnterTextField.setText(virtualKeyPad.getNumber());
			}
		});
        
        // 기타화면 금액 입력 필드
        remittanceAmountTextField2.addMouseListener(new MouseListener() {
			
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
				String amount = null;
				totalAmount = 0;
				virtualKeyPad = new SystemKeyPad(7);
                virtualKeyPad.setVisible(true);
                amount = virtualKeyPad.getNumber();
                if(amount.substring(0, 1) != "0") { // amount의 첫 글자가 0이 아니고
                	totalAmount = Long.parseLong(amount);
                	if(totalAmount > 6000000) { // totalAmount의 금액이 600만원을 초과한다면
                		remittanceAmountTextField2.setText("6000000"); // TextField에 600만원 보여주기
                		totalAmount = 6000000; // 입력된 금액을 totalAmount에 저장
                	}
                	else {
                		remittanceAmountTextField2.setText(amount); // TextField에 600만원 보여주기
                		totalAmount = Long.parseLong(amount); // 입력된 금액을 totalAmount에 저장
                	}
                }
			}
		});
        
        // 수금인 계좌번호 입력필드
        collectorAccountEnterTextField.addMouseListener(new MouseListener() {
			
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
                virtualKeyPad.setVisible(true);
                collectorAccountEnterTextField.setText(virtualKeyPad.getNumber());
			}
		});
        
        // 송금인 비밀번호 입력필드
        senderPasswordEnterTextField.addMouseListener(new MouseListener() {
			
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
				virtualKeyPad = new SystemKeyPad(4);
                virtualKeyPad.setVisible(true);
                senderPasswordEnterTextField.setText(virtualKeyPad.getNumber());
			}
		});
        
        
    }   
    
}