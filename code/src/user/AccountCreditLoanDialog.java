package user;


import javax.swing.*;

import org.apache.commons.collections4.Get;
import org.apache.commons.collections4.map.MultiKeyMap;

import main.BankSystemMainFrame;
import system.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class AccountCreditLoanDialog extends JDialog 
{
	private SystemKeyPad virtualKeyPad; // 키패드
	private SystemTextDialog textDialog; // 텍스트 다이어로그
	private BankSystemMainFrame bankSystemFrame; // 메인 시스템
	private int mouseX, mouseY;    // 마우스 좌표
	private String userinputname = null; // 고객이 입력한 이름
	private String userinputAccount = null; // 고객이 입력한 계좌번호
	private long totalAmount = 0; // 계좌들 잔액의 총합
	private int totalAccountnumber = 0; // 계좌들 갯수 
	private long maxLoan = 0; // 고객의 총 잔액을 이용하여 계산한 대출 한도= (고객의 이름과 주민번호가 일치하는 예·적금 계좌의 잔액 합)*(해당 고객의 신용도에 해당하는 배율)
	private long userinputLoan = 0; // 고객이 입력한 대출 원금
	private int userinputrepaymentperiod = 1; // 고객이 입력한 상환기간. x개월
	private int userinputpaymentdate = 0; // 고객이 입력한 상환일. y일
	private String userinputpassword = null; // 고객이 입력한 비밀번호. xxxx
	private long loanperperiod = 0; // 월별 대출 원금
	private long interestperperiod = 0; // 월 별 이자
	private int subinterper = 0; // 이자율
	private long totalprinandinter = 0;//고객이 입력한 원금과 설정된 이자율에 대한 원리금: userinputLoan*(100 + 개월 수에 의한 이자율 - 신용도에 의한 이자율 - 우대고객에 의한 이자율)/100
	private long principalandinterest = totalprinandinter/userinputrepaymentperiod; //월별 원리금
	private Map<String, Integer> vipDcinterest = new HashMap<String, Integer>(); // 우대고객의 등급에 대한 이자율 감면
	private Map<Integer, Integer> creditscoretomaxLoan = new HashMap<Integer, Integer>(); // 신용점수에 대한 대출 한도 비율
	private Map<Integer, Integer> creditscoretointerestper = new HashMap<Integer, Integer>(); // 신용점수에 대한 이자율 감면
	private Map<Integer, Integer> periodtointerestper = new HashMap<Integer, Integer>(); // 신용점수에 대한 이자율 감면
	private int calculatedcreditinterper = 0; //계산된 신용점수에 의한 이자율
	private int calculatedvipinterper = 0; // 계산된 우대고객에 의한 이자율
	private int calculatedperiodinterper = 0; // 상환기간에 의한 이자율 
	private Account acc; // 입력한 계좌번호와 이름에 의한 대표 계좌 객체
	private int index = 0; // 파일리스트내의 인덱스 번호
//	private FileSystemDataUpdater FSDU = new FileSystemDataUpdater(acc, index);
	
	
	
	
	public AccountCreditLoanDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) 
	{
		MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap(); // 멀티키맵
    	Map<String, Integer> map = as.getMap(); // 키맵
    	ArrayList<Account> depositAccountlist = as.getDepositAccountList(); // 계좌 객체를 저장한 리스트
//    	Account account = null;
		
		this.bankSystemFrame = bankSystemFrame; // 뱅크시스템
		vipDcinterest.put("일반 고객", 0); // 우대고객 등급에 의한 감면될 이자율
		vipDcinterest.put("2급 우대고객", 1); // 우대고객 등급에 의한 감면될 이자율
		vipDcinterest.put("1급 우대고객", 2);	// 우대고객 등급에 의한 감면될 이자율
		for (int i = 0; i < 101; i++) // 신용점수에 의한 이자율과 대출한도 비율 
		{
			if (i <= 20) 
			{
				creditscoretomaxLoan.put(i, 120);
				creditscoretointerestper.put(i, 10);
			}
			else if (21 <= i && i <= 40  ) {
				creditscoretomaxLoan.put(i, 140);
				creditscoretointerestper.put(i, 9);
			}
			else if (41 <= i && i <= 60  ) {
				creditscoretomaxLoan.put(i, 160);
				creditscoretointerestper.put(i, 7);
			}
			else if (61 <= i && i <= 80  ) {
				creditscoretomaxLoan.put(i, 180);
				creditscoretointerestper.put(i, 6);
			}
			else {
				creditscoretomaxLoan.put(i, 200);
				creditscoretointerestper.put(i, 5);
			}			
		}	
		
		for ( int j = 1 ; j < 100 ; j++ ) // 입력한 상환기간에 따른 이자율
		{
			if (j < 13)
			{
				periodtointerestper.put(j, 5);
			}
			else if (j < 25) 
			{
				periodtointerestper.put(j, 4);
			}
			else if (j < 37)
			{
				periodtointerestper.put(j, 3);
			}
			else if (j < 49) 
			{
				periodtointerestper.put(j, 2);
			}
			else
				periodtointerestper.put(j, 1);
		}
		
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
		
//		 String path = System.getProperty("user.dir");
//	     System.out.println("Working Directory = " + path);
		
		
		this.setSize(1440, 810);
		Font font = new Font("Malgun Gothic", Font.BOLD, 15);
//		Font fontforkeypad = new Font("Malgun Gothic", Font.BOLD, 60);
		this.setUndecorated(true);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int leftTopX = centerPoint.x - this.getWidth() / 2;
        int leftTopY = centerPoint.y - this.getHeight() / 2;
        this.setLocation(leftTopX, leftTopY);
        
        
        // 창 드래그 설정
        addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mousePressed(MouseEvent e) 
            {
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
        
        // 배경 패널 생성 및 설정 
        JPanel AccountCreditLoanPanel = new JPanel();
        AccountCreditLoanPanel.setLayout(null);
        AccountCreditLoanPanel.setBounds(0,0,1440,810);
        AccountCreditLoanPanel.setVisible(true);
        
        // 보이스 피싱용 배경이미지
        JLabel imagelabel = new JLabel(iconlist.get(34));
        imagelabel.setBounds(0, 0, 1440, 810);
        AccountCreditLoanPanel.add(imagelabel);
        imagelabel.setVisible(false);
        
        // 신용대출 화면 이용 간 배경화면
        ImageIcon AccountCreditLoanBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
        JLabel AccountCreditLoanBackgroundLabel = new JLabel(AccountCreditLoanBackground);
        AccountCreditLoanBackgroundLabel.setBounds(0,0,1440,810);
        AccountCreditLoanPanel.add(AccountCreditLoanBackgroundLabel);
        this.add(AccountCreditLoanPanel);
        
        // 정상적인 거래완료 후에 노출되는 5초 대기 화면 
        JPanel transactionFinishPanel = new JPanel();
        transactionFinishPanel.setLayout(null);
        transactionFinishPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        transactionFinishPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // transactionFinishPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel transactionFinishLabel = new JLabel();
        transactionFinishLabel.setBounds(560, 507, 1000, 200); // (x, y, width, height)
        transactionFinishLabel.setFont(font); // 폰트 크기를 20으로 설정
        transactionFinishLabel.setForeground(Color.red);
        transactionFinishPanel.add(transactionFinishLabel);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon transactionFinishPanelImageGIF = new ImageIcon("이미지\\거래완료.gif");
        JLabel transactionFinishTitleGIF = new JLabel(transactionFinishPanelImageGIF);
        transactionFinishTitleGIF.setBounds(650, 350, 229, 200); // (x, y, width, height)
        transactionFinishPanel.add(transactionFinishTitleGIF);
        
        ImageIcon transactionFinishPanelImage = new ImageIcon("이미지\\거래완료.png");
        JLabel transactionFinishTitle = new JLabel(transactionFinishPanelImage);
        transactionFinishTitle.setBounds(0, 0, 1440, 810); // (x, y, width, height)
        transactionFinishPanel.add(transactionFinishTitle);
        this.add(transactionFinishPanel);
        
        //라벨들 담을 컨테이너
        Container creditcontainer = getContentPane();
		creditcontainer.setLayout(null);
		
		//대출 시작1
		JLabel firstString = new JLabel("대출금 계산을 위해 고객님의 정보를 입력해주세요.");
		firstString.setBounds(70, 100, 700, 60);
		firstString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(firstString);
		firstString.setVisible(false);
		
		//계좌번호 입력 
		JLabel accountString = new JLabel("계좌번호: ");
		accountString.setBounds(70, 200, 700, 30);
		accountString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(accountString);
		accountString.setVisible(false);

		JTextField accountTextField = new JTextField(20);
		accountTextField.setBounds(170, 200, 500, 30);
		accountTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(accountTextField);
		accountTextField.setVisible(false);
		
		//이름 입력
		JLabel nameString = new JLabel("이름: ");
		nameString.setBounds(70, 300, 700, 30);
		nameString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(nameString);
		nameString.setVisible(false);

		JTextField nameTextField = new JTextField(5);
		nameTextField.setBounds(170, 300, 300, 30);
		nameTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(nameTextField);
		nameTextField.setVisible(false);
		
		//한도 계산 시작
		JLabel secondString = new JLabel("대출 한도를 계산합니다.");
		secondString.setBounds(570, 105, 250, 200);
		secondString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(secondString);
		secondString.setVisible(false);
		
		//대출한도 출력
		JLabel ThirdString = new JLabel("고객님의 대출한도는 " + maxLoan + " 원 입니다.");
		ThirdString.setBounds(70, 50, 700, 60);
		ThirdString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(ThirdString);
		ThirdString.setVisible(false);
		
		//대출금 입력
		JLabel ThirdbyoneString = new JLabel("희망하시는 대출금을 입력해주세요.");
		ThirdbyoneString.setBounds(70, 100, 700, 60);
		ThirdbyoneString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(ThirdbyoneString);
		ThirdbyoneString.setVisible(false);
		
		JLabel inputLoan = new JLabel("대출금: ");
		inputLoan.setBounds(450, 650, 400, 30);//200, 600, 150, 80
		inputLoan.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputLoan);
		inputLoan.setVisible(false);
		
		JTextField inputLoanTextField = new JTextField(15);
		inputLoanTextField.setBounds(550, 650, 400, 30);//200, 600, 150, 80
		inputLoanTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputLoanTextField);
		inputLoanTextField.setVisible(false);
		
		// 기타 금액 입력시 대출금 입력
		JTextField inputLoanetcetraTextField = new JTextField(15);
		inputLoanetcetraTextField.setBounds(550, 650, 400, 30);//200, 600, 150, 80
		inputLoanetcetraTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputLoanetcetraTextField);
		inputLoanetcetraTextField.setVisible(false);
		
		//납입정보 입력
		JLabel fourthString = new JLabel("납입정보를 입력해주세요.");
		fourthString.setBounds(70, 50, 700, 60);
		fourthString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(fourthString);
		fourthString.setVisible(false);
		
		//사용자가 입력한 정보들 출력
		JLabel typedLoan = new JLabel("입력하신 대출금: " + userinputLoan + " 만원");
		typedLoan.setBounds(70, 300, 700, 60);
		typedLoan.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(typedLoan);
		typedLoan.setVisible(false);
		
//		JTextField typedLoanTextField = new JTextField(20);
//		typedLoanTextField.setBounds(240, 315, 400, 30);//200, 600, 150, 80
//		typedLoanTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
//		creditcontainer.add(typedLoanTextField);
//		typedLoanTextField.setVisible(false);
		
		JLabel repaymentperiod = new JLabel("상환기간: ");
		repaymentperiod.setBounds(70, 350, 700, 60);
		repaymentperiod.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(repaymentperiod);
		repaymentperiod.setVisible(false);
		
		JTextField repaymentperiodTextField = new JTextField(3);
		repaymentperiodTextField.setBounds(170, 365, 400, 30);//200, 600, 150, 80
		repaymentperiodTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(repaymentperiodTextField);
		repaymentperiodTextField.setVisible(false);
		
		JLabel paymentdate = new JLabel("납입일: ");
		paymentdate.setBounds(70, 400, 700, 60);
		paymentdate.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(paymentdate);
		paymentdate.setVisible(false);
		
		JTextField paymentdateTextField = new JTextField(2);
		paymentdateTextField.setBounds(150, 415, 400, 30);//200, 600, 150, 80
		paymentdateTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(paymentdateTextField);
		paymentdateTextField.setVisible(false);
		
		// 거래정보확인
		JLabel fifthString = new JLabel("거래 정보를 확인해주세요.");
		fifthString.setBounds(570, 105, 250, 200);
		fifthString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(fifthString);
		fifthString.setVisible(false);
		
		JLabel printLoan = new JLabel("입력하신 대출금: " + userinputLoan + " 만원");
		printLoan.setBounds(570, 155, 950, 200);
		printLoan.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(printLoan);
		printLoan.setVisible(false);
		
		JLabel printpaymentperiod = new JLabel("상환 기간: " + userinputrepaymentperiod + " 개월");
		printpaymentperiod.setBounds(570, 185, 250, 200);
		printpaymentperiod.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(printpaymentperiod);
		printpaymentperiod.setVisible(false);
		
		JLabel printpaymentday = new JLabel("납입일: " + userinputpaymentdate + " 일");
		printpaymentday.setBounds(570, 215, 250, 200);
		printpaymentday.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(printpaymentday);
		printpaymentday.setVisible(false);		
		
		JLabel interestandprincipal = new JLabel("예상 납입액: " + principalandinterest + "원");
		interestandprincipal.setBounds(570, 245, 250, 200);
		interestandprincipal.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(interestandprincipal);
		interestandprincipal.setVisible(false);
		
		//비밀번호 입력		
		JLabel passwordString = new JLabel("비밀번호를 입력하세요.");
		passwordString.setBounds(70, 100, 700, 60);
		passwordString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(passwordString);
		passwordString.setVisible(false);
		
		JLabel inputpassword = new JLabel("비밀번호: ");
		inputpassword.setBounds(70, 200, 700, 30);
		inputpassword.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputpassword);
		inputpassword.setVisible(false);
		
		JTextField passwordTextField = new JTextField(4);
		passwordTextField.setBounds(220, 200, 300, 30);
		passwordTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(passwordTextField);
		passwordTextField.setVisible(false);
		
		// 거래 완료
		JLabel lastDance = new JLabel("현금을 확인하세요.");
		lastDance.setBounds(600, 100, 700, 60);
		lastDance.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(lastDance);
		lastDance.setVisible(false);
		
		// 만약을 위한 에러
		JLabel iferrorToadmin = new JLabel("수량에 문제가 있다면 관리자에게 문의하세요.");
		iferrorToadmin.setBounds(220, 520, 700, 60);
		iferrorToadmin.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(iferrorToadmin);
		iferrorToadmin.setVisible(false);
		
		JLabel fastCall = new JLabel("문의하시면 최대한 빨리 연락을 드리겠습니다.");
		fastCall.setBounds(220, 560, 700, 60);
		fastCall.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(fastCall);
		fastCall.setVisible(false);
		
		JLabel soSorry = new JLabel("불편을 드려죄송합니다.");
		soSorry.setBounds(220, 600, 700, 60);
		soSorry.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(soSorry);
		soSorry.setVisible(false);
        
		// 대출 계속하기 버튼들
        JButton continueLoanButton = new JButton(iconlist.get(17)); //1번 버튼
		continueLoanButton.setFont(font);
		continueLoanButton.setBounds(724,699,716,111);
		AccountCreditLoanPanel.add(continueLoanButton);
		continueLoanButton.setVisible(false);
		
		// 2번버튼
		JButton continueLoanButton1 = new JButton(iconlist.get(64)); 
		continueLoanButton1.setFont(font);
		continueLoanButton1.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continueLoanButton1);
		continueLoanButton1.setVisible(false);
		
		// 한도계산화면 버튼
		JButton calculatingLoan = new JButton(iconlist.get(64)); 
		calculatingLoan.setFont(font);
		calculatingLoan.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(calculatingLoan);
		calculatingLoan.setVisible(false);
        
        // backgroundPanel에 거래 취소 버튼 생성 및 추가
        JButton tcButton = new JButton(iconlist.get(59)); // 공통 취소 버튼
        tcButton.setFont(font);
        tcButton.setBounds(0, 730, 150, 80);
        AccountCreditLoanPanel.add(tcButton);
        this.add(AccountCreditLoanPanel);
        
        tcButton.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) {
				// 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, 
				// AccountTransferFrame을 숨김
				setVisible(false);
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				return;
			}
		});
        
        // 보이스피싱 화면 대출받기 버튼 생성
        JButton docreditloanButton = new JButton(iconlist.get(28));
        docreditloanButton.setFont(font);
        docreditloanButton.setBounds(300, 300, 150, 80);
        AccountCreditLoanPanel.add(docreditloanButton);
        
        // 보이스피싱 화면 취소 버튼 생성
        JButton canceloanButton = new JButton(iconlist.get(18));
        canceloanButton.setBounds(0, 699, 716, 111);
        AccountCreditLoanPanel.add(canceloanButton);
        this.add(AccountCreditLoanPanel);
        canceloanButton.setVisible(false);
        
        // 대출금 선택 버튼 생성
        JButton fiftyButton = new JButton(iconlist.get(13));
        fiftyButton.setFont(font);
        fiftyButton.setBounds(200, 200, 150, 80);
        AccountCreditLoanPanel.add(fiftyButton);
        fiftyButton.setVisible(false);
        
        JButton hundredButton = new JButton(iconlist.get(1));
        hundredButton.setFont(font);
        hundredButton.setBounds(200, 300, 150, 80);
        AccountCreditLoanPanel.add(hundredButton);
        hundredButton.setVisible(false);
        
        JButton onefivezeroButton = new JButton(iconlist.get(3));
        onefivezeroButton.setFont(font);
        onefivezeroButton.setBounds(200, 400, 150, 80);
        AccountCreditLoanPanel.add(onefivezeroButton);
        onefivezeroButton.setVisible(false);
        
        JButton twohundredButton = new JButton(iconlist.get(5));
        twohundredButton.setFont(font);
        twohundredButton.setBounds(200, 500, 150, 80);
        AccountCreditLoanPanel.add(twohundredButton);
        twohundredButton.setVisible(false);
        
        JButton threehundredButton = new JButton(iconlist.get(8));
        threehundredButton.setFont(font);
        threehundredButton.setBounds(200, 600, 150, 80);
        AccountCreditLoanPanel.add(threehundredButton);
        threehundredButton.setVisible(false);
        
        JButton fivehundredButton = new JButton(iconlist.get(12));
        fivehundredButton.setFont(font);
        fivehundredButton.setBounds(1140, 200, 150, 80);
        AccountCreditLoanPanel.add(fivehundredButton);
        fivehundredButton.setVisible(false);
        
        JButton thousandButton = new JButton(iconlist.get(0));
        thousandButton.setFont(font);
        thousandButton.setBounds(1140, 300, 150, 80);
        AccountCreditLoanPanel.add(thousandButton);
        thousandButton.setVisible(false);
        
        JButton threethousandButton = new JButton(iconlist.get(7));
        threethousandButton.setFont(font);
        threethousandButton.setBounds(1140, 400, 150, 80);
        AccountCreditLoanPanel.add(threethousandButton);
        threethousandButton.setVisible(false);
        
        JButton fivethousandButton = new JButton(iconlist.get(11));
        fivethousandButton.setFont(font);
        fivethousandButton.setBounds(1140, 500, 150, 80);
        AccountCreditLoanPanel.add(fivethousandButton);
        fivethousandButton.setVisible(false);
        
        //기타버튼
        JButton etcetraButton = new JButton(iconlist.get(25));
        etcetraButton.setFont(font);
        etcetraButton.setBounds(1140, 600, 150, 80);
        AccountCreditLoanPanel.add(etcetraButton);   
        etcetraButton.setVisible(false);
        
        //3번버튼
        JButton continueLoanButton2 = new JButton(iconlist.get(64)); 
		continueLoanButton2.setFont(font);
		continueLoanButton2.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continueLoanButton2);
		continueLoanButton2.setVisible(false);
		
		// 4번버튼
		JButton continueLoanButton3 = new JButton(iconlist.get(64)); 
		continueLoanButton3.setFont(font);
		continueLoanButton3.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continueLoanButton3);
		continueLoanButton3.setVisible(false);
		
		// 5번버튼
		JButton continueLoanButton4 = new JButton(iconlist.get(64)); 
		continueLoanButton4.setFont(font);
		continueLoanButton4.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continueLoanButton4);
		continueLoanButton4.setVisible(false);
		
		// 6번버튼
		JButton continueLoanButton5 = new JButton(iconlist.get(64)); 
		continueLoanButton5.setFont(font);
		continueLoanButton5.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continueLoanButton5);
		continueLoanButton5.setVisible(false);
		
		// 관리자 문의하기버튼
		JButton iferror = new JButton(iconlist.get(31)); 
		iferror.setFont(font);
		iferror.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(iferror);
		iferror.setVisible(false);
		// 정상 거래 완료 버튼
		JButton confirm = new JButton(iconlist.get(64));
		confirm.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(confirm);
		confirm.setVisible(false);
		
		// 납입받기 버튼 생성
        JButton repaymentButton = new JButton(iconlist.get(27));
        repaymentButton.setFont(font);
        repaymentButton.setBounds(1000, 300, 150, 80);
        AccountCreditLoanPanel.add(repaymentButton);
        
        // 1번버튼
        JButton continuerepaymentButton1 = new JButton(iconlist.get(64)); 
        continuerepaymentButton1.setFont(font);
        continuerepaymentButton1.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continuerepaymentButton1);
		continuerepaymentButton1.setVisible(false);
		//2번버튼
		JButton continuerepaymentButton2 = new JButton(iconlist.get(64)); 
        continuerepaymentButton2.setFont(font);
        continuerepaymentButton2.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continuerepaymentButton2);
		continuerepaymentButton2.setVisible(false);
		//3번버튼
		JButton continuerepaymentButton3 = new JButton(iconlist.get(64)); 
        continuerepaymentButton3.setFont(font);
        continuerepaymentButton3.setBounds(1290,730,150,80);
		AccountCreditLoanPanel.add(continuerepaymentButton3);
		continuerepaymentButton3.setVisible(false);
		
		// 납입금 버튼 생성 이하 reapyment는 == RP
		JButton basicrepaymentButton = new JButton(iconlist.get(65)); 
		basicrepaymentButton.setFont(font);
		basicrepaymentButton.setBounds(200, 200, 150, 80);
        AccountCreditLoanPanel.add(basicrepaymentButton);
        basicrepaymentButton.setVisible(false);
        
        JButton fiveRPbutton = new JButton(iconlist.get(14));
        fiveRPbutton.setFont(font);
        fiveRPbutton.setBounds(200, 300, 150, 80);
        AccountCreditLoanPanel.add(fiveRPbutton);
        fiveRPbutton.setVisible(false);
        
        JButton tenRPButton = new JButton(iconlist.get(2));
        tenRPButton.setFont(font);
        tenRPButton.setBounds(200, 400, 150, 80);
        AccountCreditLoanPanel.add(tenRPButton);
        tenRPButton.setVisible(false);
        
        JButton twentyRPButton = new JButton(iconlist.get(6));
        twentyRPButton.setFont(font);
        twentyRPButton.setBounds(200, 500, 150, 80);
        AccountCreditLoanPanel.add(twentyRPButton);
        twentyRPButton.setVisible(false);
        
        JButton fiftyRPButton = new JButton(iconlist.get(13));
        fiftyRPButton.setFont(font);
        fiftyRPButton.setBounds(200, 600, 150, 80);
        AccountCreditLoanPanel.add(fiftyRPButton);
        fiftyRPButton.setVisible(false);
        
        JButton hundredRPButton = new JButton(iconlist.get(1));
        hundredRPButton.setFont(font);
        hundredRPButton.setBounds(1140, 200, 150, 80);
        AccountCreditLoanPanel.add(hundredRPButton);
        hundredRPButton.setVisible(false);
        
        JButton threehundredRPButton = new JButton(iconlist.get(8));
        threehundredRPButton.setFont(font);
        threehundredRPButton.setBounds(1140, 300, 150, 80);
        AccountCreditLoanPanel.add(threehundredRPButton);
        threehundredRPButton.setVisible(false);
        
        JButton fivehundredRPButton = new JButton(iconlist.get(12));
        fivehundredRPButton.setFont(font);
        fivehundredRPButton.setBounds(1140, 400, 150, 80);
        AccountCreditLoanPanel.add(fivehundredRPButton);
        fivehundredRPButton.setVisible(false);
        
        JButton thousandRPButton = new JButton(iconlist.get(0));
        thousandRPButton.setFont(font);
        thousandRPButton.setBounds(1140, 500, 150, 80);
        AccountCreditLoanPanel.add(thousandRPButton);
        thousandRPButton.setVisible(false);
        
        // 기타 금액 입력시 버튼
        JButton etcetraRPButton = new JButton(iconlist.get(25));
        etcetraRPButton.setFont(font);
        etcetraRPButton.setBounds(1140, 600, 150, 80);
        AccountCreditLoanPanel.add(etcetraRPButton);   
        etcetraRPButton.setVisible(false);
        
        //금액을 선택하여 입력시 필드
        JTextField inputRPTextField = new JTextField(15);
		inputRPTextField.setBounds(550, 650, 400, 30);//200, 600, 150, 80
		inputRPTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputRPTextField);
		inputRPTextField.setVisible(false);

        // 기타 금앱 입력시 필드
        JTextField inputRPetcetraTextField = new JTextField(15);
		inputRPetcetraTextField.setBounds(550, 650, 400, 30);//200, 600, 150, 80
		inputRPetcetraTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		creditcontainer.add(inputRPetcetraTextField);
		inputRPetcetraTextField.setVisible(false);

        
        // 대출받기 실행
        docreditloanButton.addActionListener(new ActionListener() 
        {
        	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//이전 버튼 끄기
				docreditloanButton.setVisible(false);
				repaymentButton.setVisible(false);
				tcButton.setVisible(false);
				// 보이스피싱 안내 화면
//				tcButton.set;
//				tcButton.setVisible(false);
				canceloanButton.setVisible(true);
				continueLoanButton.setVisible(true);
				imagelabel.setVisible(true);
				
				
			}
		});       
        // 보이스피싱 화면의 취소 버튼
        canceloanButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// 이전 버튼들 끄기
				canceloanButton.setVisible(false);
				imagelabel.setVisible(false);
				continueLoanButton.setVisible(false);
				
				fastCall.setVisible(false);
				soSorry.setVisible(false);
				confirm.setVisible(false);
				iferror.setVisible(false);
				setVisible(false);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);				
				return;
			}
		});
        
        // 누르면! 계좌번호 및 이름 입력 화면
        continueLoanButton.addActionListener(new ActionListener() 
        {
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{
        		// 이전 버튼 숨기기
        		canceloanButton.setVisible(false);
        		continueLoanButton.setVisible(false);
        		imagelabel.setVisible(false);
        		// 입력필드 띄우기
        		tcButton.setVisible(true);
				firstString.setVisible(true);
				nameString.setVisible(true);
				nameTextField.setVisible(true);
				accountString.setVisible(true);
				accountTextField.setVisible(true);
				
				
				// 숫자패드와 한글패드 띄우기
				
				
				if (virtualKeyPad != null)
				{	// 이미 생성된 virtualKeyPad가 있다면 숨김
	                virtualKeyPad.setVisible(false);
	            }
				SystemKeyPad virtualKeyPad = new SystemKeyPad(20); // 선언하고
				virtualKeyPad.setBounds(1400, 450, 400, 320);
				virtualKeyPad.setFont(font);
				virtualKeyPad.setInputNumberTextField(accountTextField); // 넣을게~ 
				accountTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
				virtualKeyPad.setVisible(true); // 켤게~
				userinputAccount = accountTextField.getText(); // 입력받은거 넣을게~
//				account = as.getDepositAccount(as.findAccountNumberFromMap(userinputAccount));
				
				if (textDialog != null) {
					textDialog.setVisible(false);					
				}
				SystemTextDialog textDialog = new SystemTextDialog(true);
				textDialog.setVisible(true);
				userinputname = textDialog.getText();
				nameTextField.setText(userinputname);
				nameTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
//				System.out.println(userinputname);
				
				// 새로운 계속거래 버튼 띄우기
				System.out.println(userinputname + ":" + userinputAccount);
				try 
				{
					System.out.println(depositAccountlist.get(map.get(userinputAccount)).getName());
				} catch (Exception e2) 
				{
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				
				continueLoanButton1.setVisible(true);        		
        	}
        });
        
        // 누르면! 대출한도 계산할게? 하는 화면 출력
        continueLoanButton1.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
//				조건확인
				if (map.get(userinputAccount) == null || !depositAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname) ) 
					// 이 후에 입력받은 값을 데이터파일에서 불러와서 비교해야해요
					// 만약 그 중에 하나라도 해당하면 메인으로 돌아가요
				{
					continueLoanButton1.setVisible(false);
					firstString.setVisible(false);
					nameString.setVisible(false);
					nameTextField.setVisible(false);
					accountString.setVisible(false);
					accountTextField.setVisible(false);
					
				
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				
//				//이전 버튼 숨기기
				continueLoanButton1.setVisible(false);
				firstString.setVisible(false);
				nameString.setVisible(false);
				nameTextField.setVisible(false);
				accountString.setVisible(false);
				accountTextField.setVisible(false);
				//대출금 계산 문구 출력하기
				calculatingLoan.setVisible(true);
				secondString.setVisible(true);

        		
        		
			}
		});
        // 누르면! 대출한도를 계산해서 출력하고, 대출금을 선택하는 화면
        calculatingLoan.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				calculatingLoan.setVisible(false);
				secondString.setVisible(false);
				
				//대출 문구 출력하기
				int i = 0;
				System.out.println(depositAccountlist.size());
				index = as.findAccountNumberFromMap(userinputAccount); // 인덱스 번호 찾기
				acc = as.getDepositAccount(index); // 인덱스 번호에 의한 계좌 한개의 정보 - 이름 주민번호 등등
				
				String A = acc.getSsn();
				
//				for ( i = 0 ; i < depositAccountlist.size() ; i ++)
//				{	
//					if (depositAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname))
//					totalAmount += depositAccountlist.get(map.get(userinputAccount)).getBalance();
//					
//				}
				for ( i = 0 ; i < depositAccountlist.size() ; i ++)
				{	
					if (A.equals(as.getDepositAccount(i).getSsn()))
					{
						totalAmount += as.getDepositAccount(i).getBalance();
					}
				}
				
				System.out.println(totalAmount + ": 총 잔액");
				System.out.println(depositAccountlist.get(map.get(userinputAccount)).getPrincipal() + ":입력한 계좌의 대출 원금");
				maxLoan = (long) (totalAmount * creditscoretomaxLoan.get(depositAccountlist.get(map.get(userinputAccount)).getCreditScore())/100);
				System.out.println(maxLoan + ": 계산 전 대출한도");
//				System.out.println();
				for (int j = 0; j < depositAccountlist.size(); j++) 
				{
					if (depositAccountlist.get(j).getSsn().equals(depositAccountlist.get(map.get(userinputAccount)).getSsn())) 
					{
						System.out.println(j);
						System.out.println(depositAccountlist.get(j).getPrincipal());
						 maxLoan -= (long) depositAccountlist.get(j).getPrincipal();
					}
					
				}
				
//				for (Account list : depositAccountlist) {
//					if (acc.getSsn().equals(list.getSsn())) {
//						System.out.println(list.getPrincipal());
//						
//					}
//				}

//						
////						maxLoan = (long) (totalAmount * creditscoretomaxLoan.get(depositAccountlist.get(map.get(userinputAccount)).getCreditScore())/100) - depositAccountlist.get(j).getPrincipal();
//					}
//					else

				
//				if () {
//					대출한도 계산
//				만약 대출로그에 / 입력한 계좌번호와 주민번호를 공유하는 계좌번호를 찾고, 그 계좌번호에 대출원금이 있다면 /
//				그 내역의 대출한도를 계산한 대출한도에서 빼서 대입해라.
//				}

				
				
				
				System.out.println(maxLoan + ": 대출한도");
				ThirdString.setText("고객님의 대출한도는 " + maxLoan + " 원 입니다.");
				ThirdString.setVisible(true);
				ThirdbyoneString.setVisible(true);
				fiftyButton.setVisible(true);
				hundredButton.setVisible(true);
				onefivezeroButton.setVisible(true);
				twohundredButton.setVisible(true);
				threehundredButton.setVisible(true);
				fivehundredButton.setVisible(true);
				thousandButton.setVisible(true);
				threethousandButton.setVisible(true);
				fivethousandButton.setVisible(true);
				etcetraButton.setVisible(true);
				
				inputLoan.setVisible(true);
				inputLoanTextField.setVisible(true);
				inputLoanTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
				continueLoanButton2.setVisible(true);			   
			}
			
        });
        // 여기서부터 누르면! 해당 금액이 입력되는 버튼
        fiftyButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 500000;
        		inputLoanTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        hundredButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 1000000;      
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        onefivezeroButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 1500000;        		
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        twohundredButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 2000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        threehundredButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 3000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        fivehundredButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 5000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        thousandButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 10000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        threethousandButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 30000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        //여기까지! 누르면 해당 금액이 입력되는 화면
        fivethousandButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 50000000;
				inputLoanTextField.setText(String.valueOf(userinputLoan));
				System.out.println(userinputLoan);
			}
		});
        
        //누르면! 기타 대출금을 입력하는 화면 출력
        etcetraButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				continueLoanButton2.setVisible(false);
				fiftyButton.setVisible(false);
				hundredButton.setVisible(false);
				onefivezeroButton.setVisible(false);
				twohundredButton.setVisible(false);
				threehundredButton.setVisible(false);
				fivehundredButton.setVisible(false);
				thousandButton.setVisible(false);
				threethousandButton.setVisible(false);
				fivethousandButton.setVisible(false);
				etcetraButton.setVisible(false);
				inputLoanTextField.setVisible(false);
//				//필요라벨 및 버튼켜기
//				
//				
				ThirdString.setVisible(true);
				ThirdbyoneString.setVisible(true);
				inputLoan.setVisible(true);
				inputLoanetcetraTextField.setVisible(true);
				continueLoanButton2.setVisible(true);
//				
//				//키패드 출력하기
				if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
				
				SystemKeyPad virtualKeyPad = new SystemKeyPad(20);
				virtualKeyPad.setBounds(1400, 450, 400, 320);
				virtualKeyPad.setFont(font);
			    virtualKeyPad.setInputNumberTextField(inputLoanetcetraTextField);
			    virtualKeyPad.setVisible(true);
			    inputLoanetcetraTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
			    System.out.println(userinputLoan);
			    try 
			    {
			    	userinputLoan = Long.parseLong(inputLoanetcetraTextField.getText());	
				} catch (NumberFormatException e2) 
			    {
					
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
			    catch (Exception e2) {
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
			    System.out.println(userinputLoan);
			    
//				
				
        		
        		
			}
		});
        
        //누르면! 상환 정보(계획)을 입력하는 화면 출력.
        continueLoanButton2.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				if (userinputLoan != 0 && userinputLoan%10000 == 0 && userinputLoan <= maxLoan)
					//대출금이 0원이 아니고, 만원단위라면 진행하고 아니면 메인으로 돌아가요
				{
					System.out.println(userinputLoan);
					
					//이전 버튼 숨기기
					ThirdString.setVisible(false);
					ThirdbyoneString.setVisible(false);
					inputLoan.setVisible(false);
					inputLoanTextField.setVisible(false);
					inputLoanetcetraTextField.setVisible(false);
					fiftyButton.setVisible(false);
					hundredButton.setVisible(false);
					onefivezeroButton.setVisible(false);
					twohundredButton.setVisible(false);
					threehundredButton.setVisible(false);
					fivehundredButton.setVisible(false);
					thousandButton.setVisible(false);
					threethousandButton.setVisible(false);
					fivethousandButton.setVisible(false);
					etcetraButton.setVisible(false);
					continueLoanButton2.setVisible(false);
					
					//대출금 계산 문구 출력하기
					typedLoan.setText("입력하신 대출금: " + userinputLoan + " 원");
					typedLoan.setVisible(true);
//					typedLoanTextField.setVisible(true);
					repaymentperiod.setVisible(true);
					repaymentperiodTextField.setVisible(true);
					paymentdate.setVisible(true);
					paymentdateTextField.setVisible(true);
					continueLoanButton3.setVisible(true);
					
					
					//계산기 출력하기
					if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
	                    virtualKeyPad.setVisible(false);
	                }
					SystemKeyPad virtualKeyPad = new SystemKeyPad(2);
					
					virtualKeyPad.setInputNumberTextField(repaymentperiodTextField);
					virtualKeyPad.setBounds(1400, 450, 400, 320);
					virtualKeyPad.setFont(font);
					virtualKeyPad.setVisible(true);
					repaymentperiodTextField.addKeyListener(new KeyAdapter() 
	                {
	                    @Override
	                    public void keyTyped(KeyEvent e) {
	                        e.consume(); // 이벤트 소비
	                    }
	                    
	                    @Override
	                    public void keyPressed(KeyEvent e) {
	                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	                            e.consume(); // BACKSPACE 키 이벤트 소비
	                        }
	                    }
	                });
					userinputrepaymentperiod = Integer.parseInt(repaymentperiodTextField.getText());
					
					
					if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
	                    virtualKeyPad.setVisible(false);
	                }
					SystemKeyPad virtualdateKeyPad = new SystemKeyPad(2);
					virtualKeyPad.setBounds(1400, 450, 400, 320);
					virtualKeyPad.setFont(font);	
					virtualdateKeyPad.setInputNumberTextField(paymentdateTextField);
					virtualdateKeyPad.setVisible(true);
					paymentdateTextField.addKeyListener(new KeyAdapter() 
	                {
	                    @Override
	                    public void keyTyped(KeyEvent e) {
	                        e.consume(); // 이벤트 소비
	                    }
	                    
	                    @Override
	                    public void keyPressed(KeyEvent e) {
	                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	                            e.consume(); // BACKSPACE 키 이벤트 소비
	                        }
	                    }
	                });
					userinputpaymentdate = Integer.parseInt(paymentdateTextField.getText());
				}
				else //0원이면 메인으로 돌아가요
				{
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				
				
			}
		});
        
        //누르면! 대출정보 및 상환정보(계획)을 출력해주는 화면출력
        continueLoanButton3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				if (userinputrepaymentperiod < 1 ||userinputpaymentdate < 1 || userinputpaymentdate > 30)
				{
					typedLoan.setVisible(false);
					repaymentperiod.setVisible(false);
					repaymentperiodTextField.setVisible(false);
					paymentdate.setVisible(false);
					paymentdateTextField.setVisible(false);
					continueLoanButton3.setVisible(false);
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				// 이전 버튼 끄기
				typedLoan.setVisible(false);
				repaymentperiod.setVisible(false);
				repaymentperiodTextField.setVisible(false);
				paymentdate.setVisible(false);
				paymentdateTextField.setVisible(false);
				continueLoanButton3.setVisible(false);
				//필요 내용 및 버튼 출력하기
				printLoan.setText("입력하신 대출금: " + userinputLoan + " 원");
				printpaymentperiod.setText("상환기간: " + userinputrepaymentperiod + " 개월");
				printpaymentday.setText("납입일: " + userinputpaymentdate + " 일");
				try 
				{
					loanperperiod = (long) userinputLoan/userinputrepaymentperiod;
				} catch (Exception e2)
				{
					typedLoan.setVisible(false);
					repaymentperiod.setVisible(false);
					repaymentperiodTextField.setVisible(false);
					paymentdate.setVisible(false);
					paymentdateTextField.setVisible(false);
					continueLoanButton3.setVisible(false);
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				if (vipDcinterest.containsKey(depositAccountlist.get(map.get(userinputAccount)).getVip()))
				{
					calculatedvipinterper = vipDcinterest.get(depositAccountlist.get(map.get(userinputAccount)).getVip());
				}
				calculatedcreditinterper = creditscoretointerestper.get(depositAccountlist.get(map.get(userinputAccount)).getCreditScore());
				calculatedperiodinterper = periodtointerestper.get(userinputrepaymentperiod);
				subinterper = calculatedcreditinterper - calculatedvipinterper; 
				interestperperiod = (long) (userinputLoan*subinterper/100)/userinputrepaymentperiod;
				totalprinandinter = userinputLoan + ((long) Math.round(userinputLoan*subinterper/100));
				principalandinterest = (long) totalprinandinter/userinputrepaymentperiod;
				interestandprincipal.setText("예상 납입액: " + principalandinterest + "원");
				printLoan.setVisible(true);
				printpaymentperiod.setVisible(true);
				printpaymentday.setVisible(true);
				interestandprincipal.setVisible(true);				
				continueLoanButton4.setVisible(true);
				
				
				
			}
		});
        
        // 누르면! 모두 확인하고 동의하면 비밀번호를 입력하는 화면 출력
        continueLoanButton4.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// 이전 버튼 끄기
				fifthString.setVisible(false);
				printLoan.setVisible(false);
				printpaymentperiod.setVisible(false);
				printpaymentday.setVisible(false);
				interestandprincipal.setVisible(false);
				continueLoanButton4.setVisible(false);
				// 필요 라벨 및 버튼 켜기
			    
				passwordString.setVisible(true);
				inputpassword.setVisible(true);
				passwordTextField.setVisible(true);
				continueLoanButton5.setVisible(true);
				
				// 키패드 켜기
				if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
				
				SystemKeyPad virtualKeyPad = new SystemKeyPad(4);
			    virtualKeyPad.setInputNumberTextField(passwordTextField);
			    virtualKeyPad.setBounds(1400, 450, 400, 320);
				virtualKeyPad.setFont(font);
			    virtualKeyPad.setVisible(true);
			    passwordTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
			    userinputpassword = passwordTextField.getText();
			    System.out.println(userinputpassword);
			    System.out.println(acc.getAccountPasswd());
				
			}
		});
        
        // 누르면! 비밀번호가 일치하는 지 확인하고 현금을 수거하라는 메시지를 출력하는 화면을 출력
        continueLoanButton5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (!userinputpassword.equals(acc.getAccountPasswd())) // 상숫값을 변경해주세요 입력받은 값을 데이터파일에서 불러와서 비교해야해요
				{
					passwordString.setVisible(false);
					inputpassword.setVisible(false);
					passwordTextField.setVisible(false);
					continueLoanButton5.setVisible(false);
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				if (acc.getPrincipal() != 0) // 만약 이미 대출을 받은 상태라면 
				{
					acc.setLoanLimit(maxLoan - userinputLoan); // 대출한도에서 대출량 빼고
					acc.setPrincipal(acc.getPrincipal() + userinputLoan); // 그전에 대출받은 양에 이번에 받은 대출량을 더한다.
					acc.setRepaymentdate(userinputpaymentdate);
					acc.setRepaymentperiod(acc.getRepaymentperiod() + userinputrepaymentperiod);
					acc.setInterestRate((int) ((acc.getInterestRate() +subinterper)/2));
					acc.setInterestPerMonth((long) (acc.getPrincipal()*subinterper/100)/acc.getRepaymentperiod());
					acc.setPrincipalPerMonth((long) acc.getPrincipal()/acc.getRepaymentperiod());
//					acc.setPrincipalAndInterest(acc.getPrincipal() + acc.getInterest());
					acc.setInterest((int) (long) ((acc.getPrincipal()*subinterper)/100));
					acc.setPrincipalAndInterest(acc.getPrincipal() + acc.getInterest());
					acc.setPrincipalAndInterestPerMonth((long) acc.getPrincipalAndInterest()/acc.getRepaymentperiod());
					FileSystemDataUpdater FSDU = new FileSystemDataUpdater(acc, index);
					FSDU.writeLoanToExcel();
				}
				else 
				{
					acc.setLoanLimit(maxLoan - userinputLoan);
					acc.setPrincipal(userinputLoan);
					acc.setRepaymentdate(userinputpaymentdate);
					acc.setRepaymentperiod(userinputrepaymentperiod);
					acc.setInterestRate(subinterper);
					acc.setInterestPerMonth(interestperperiod);
					acc.setPrincipalPerMonth(loanperperiod);
					acc.setPrincipalAndInterest(totalprinandinter);
					acc.setPrincipalAndInterestPerMonth(principalandinterest);
					acc.setInterest((int) (totalprinandinter - userinputLoan));
					FileSystemDataUpdater FSDU = new FileSystemDataUpdater(acc, index);
					FSDU.writeLoanToExcel();
					
				}
				// 이전 버튼 끄기
				passwordString.setVisible(false);
				inputpassword.setVisible(false);
				passwordTextField.setVisible(false);
				continueLoanButton5.setVisible(false);
				//불필요 버튼 끄기
				tcButton.setVisible(false);
				// 필요 라벨 및 버튼 출력하기
				lastDance.setVisible(true);
				iferrorToadmin.setVisible(true);
				fastCall.setVisible(true);
				soSorry.setVisible(true);
				confirm.setBounds(0, 730, 150, 80);
				confirm.setVisible(true);
				iferror.setVisible(true);
				
			}
		});
        
        // 현금에 수량이 이상하다면 누르는 버튼
        iferror.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountTransferFrame을 숨김
				// 관리자에게 로그를 보내야해요
				fastCall.setVisible(false);
				soSorry.setVisible(false);
				confirm.setVisible(false);
				iferror.setVisible(false);
				setVisible(false);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				return;
				
				
			}
		});
        
        //현금의 수량이 정상적일 때 누르는 버튼
        confirm.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountTransferFrame을 숨김
				// 여기서 모든 연산처리를 완료해야해요.
				fastCall.setVisible(false);
				soSorry.setVisible(false);
				confirm.setVisible(false);
				iferror.setVisible(false);
				continuerepaymentButton3.setVisible(false);
				fourthString.setVisible(false);
				inputLoan.setVisible(false);
				inputpassword.setVisible(false);
				passwordString.setVisible(false);
				passwordTextField.setVisible(false);
				tcButton.setVisible(false);
				interestandprincipal.setVisible(false);
				ThirdbyoneString.setVisible(false);
				printpaymentday.setVisible(false);
				printpaymentperiod.setVisible(false);
				printLoan.setVisible(false);
				inputRPTextField.setVisible(false);
				ThirdString.setVisible(false);
				setVisible(true);
				transactionFinishPanel.setVisible(true);
        		int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
        		
        		// 값들이 잘 들어왔는지 확인해요.
        		System.out.println(userinputname + ":" + userinputAccount + ":" + maxLoan + ":" + userinputLoan + ":" + userinputrepaymentperiod + ":" + userinputpaymentdate + ":" + userinputpassword + ":" + loanperperiod + ":" + interestperperiod + ":" + subinterper + ":" + totalprinandinter + ":" + principalandinterest);
        		// 만약 대출하기 일 경우, 이름, 계좌번호, 대출한도, 입력한대출금, 상환기간, 상환일, 비밀번호, 월 별 원금, 월 별 이자, 이자율, 원리금, 월 별 원리금.
        		// 만약 대출금 상환하기 일 경우, 이름, 계좌번호, 0, 입력한 상환금, 1, 0, 비밀번호, 0, 0, 이자율, 0, 0
        		// 대출금 상환하기를 통해 월별 원금, 월 별 이자, 총 원리금, 월 별 원리금은 계산해서 데이터파일에 새로 넣어줘야합니다.
        		
        		Timer timer = new Timer(1000, new ActionListener() {
        		    @Override
        		    public void actionPerformed(ActionEvent e) {
        		        timeLeft[0]--; // 남은 시간 감소
        		        transactionFinishLabel.setText(timeLeft[0] + ""); // 레이블 텍스트 업데이트

        		        if (timeLeft[0] == 0) { // 남은 시간이 0이면
        		        	transactionFinishPanel.setVisible(false);
        		            setVisible(false);
        		            bankSystemFrame.setVisible(true);
        		            bankSystemFrame.getBackgroundPanel().setVisible(true);
        		            bankSystemFrame.getAccountManagementPanel().setVisible(false);
        		            bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
        		            ((Timer)e.getSource()).stop(); // 타이머 중지
        		        }
        		    }
        		});
        		timer.start();
			}
		});
        
        //대출금 납입하기 누르면! 계좌번호와 이름을 입력하는 화면 출력
        repaymentButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				//사용한 버튼 끄기
				docreditloanButton.setVisible(false);
				repaymentButton.setVisible(false);
				//필요한 정보 켜기
				firstString.setText("대출을 상환하려는 고객님의 정보를 입력해주세요.");
				firstString.setVisible(true);
				accountString.setVisible(true);	
				accountTextField.setVisible(true);				
				nameString.setVisible(true);				
				nameTextField.setVisible(true);
				
				if (virtualKeyPad != null)
				{	// 이미 생성된 virtualKeyPad가 있다면 숨김
	                virtualKeyPad.setVisible(false);
	            }
				SystemKeyPad virtualKeyPad = new SystemKeyPad(20); // 선언하고
				virtualKeyPad.setBounds(1400, 450, 400, 320);
				virtualKeyPad.setFont(font);
				virtualKeyPad.setInputNumberTextField(accountTextField); // 넣을게~ 
				accountTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
				virtualKeyPad.setVisible(true); // 켤게~
				userinputAccount = accountTextField.getText(); // 입력받은거 넣을게~

				
				if (textDialog != null) {
					textDialog.setVisible(false);					
				}
				SystemTextDialog textDialog = new SystemTextDialog(true);
				textDialog.setVisible(true);
				userinputname = textDialog.getText();
				nameTextField.setText(userinputname);
				nameTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });

				//다음 거래 버튼 켜기
				continuerepaymentButton1.setVisible(true);
				
			}
		}); 
        
        // 누르면! 납입금을 선택하는 화면을 출력
        continuerepaymentButton1.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					index = as.findAccountNumberFromMap(userinputAccount); // 인덱스 번호 찾기

				} catch (NullPointerException nullError) {
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				System.out.println(index);
				acc = as.getDepositAccount(index); // 인덱스 번호에 의한 계좌 한개의 정보 - 이름 주민번호 등등
//				String A = acc.getSsn();
				System.out.println(userinputname + ":" + userinputAccount);
				System.out.println(depositAccountlist.get(map.get(userinputAccount)).getName());
				System.out.println(acc.getPrincipal());
				System.out.println(depositAccountlist.get(map.get(userinputAccount)).getPrincipal());
				
				// 조건확인
				if (map.get(userinputAccount) == null || !depositAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname) || depositAccountlist.get(map.get(userinputAccount)).getPrincipal() == 0) 
//					|| 
					// 먼저 공백인지 확인해요.
					// 이 후에 입력받은 값을 데이터파일에서 불러와서 비교해야해요. 
					// 여기는 입력받은 계좌번호의 주민번호를 확인해서 그 주민번호로 대출이 있는지 확인해요. 
					// 없으면 메인으로 돌아가요
					// 만약 그 중에 하나라도 해당하면 메인으로 돌아가요
				{
					continueLoanButton1.setVisible(false);
					firstString.setVisible(false);
					nameString.setVisible(false);
					nameTextField.setVisible(false);
					accountString.setVisible(false);
					accountTextField.setVisible(false);
					
				
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				

				// 사용한 버튼 끄기
				firstString.setVisible(false);
				accountString.setVisible(false);	
				accountTextField.setVisible(false);				
				nameString.setVisible(false);				
				nameTextField.setVisible(false);
				continuerepaymentButton1.setVisible(false);
				
				//필요한 정보 켜기
				ThirdbyoneString.setText("얼마를 상환하시겠습니까?");
				ThirdbyoneString.setVisible(true);
				basicrepaymentButton.setVisible(true);
				fiveRPbutton.setVisible(true);
				tenRPButton.setVisible(true);
				twentyRPButton.setVisible(true);
				fiftyRPButton.setVisible(true);
				hundredRPButton.setVisible(true);
				threehundredRPButton.setVisible(true);
				fivehundredRPButton.setVisible(true);
				thousandRPButton.setVisible(true);
				etcetraRPButton.setVisible(true);
				inputLoan.setText("납입금: ");
				inputLoan.setVisible(true);
				inputRPTextField.setVisible(true);
				System.out.println(acc.getPrincipalAndInterestPerMonth());
				inputRPTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
				
				
				// 다음 거래 버튼 켜기
				continuerepaymentButton2.setVisible(true);
			}
		});
        // 기존 원리금을 넣어주는 버튼
        basicrepaymentButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += (long) (acc.getPrincipalPerMonth() + acc.getInterestPerMonth());
     			if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        fiveRPbutton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 50000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        tenRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 100000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        twentyRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 200000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        fiftyRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 500000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        hundredRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 1000000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        threehundredRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 3000000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        fivehundredRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 5000000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        
        thousandRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				userinputLoan += 10000000;
				if (userinputLoan > acc.getPrincipalAndInterest()) 
     			{
     				userinputLoan = acc.getPrincipalAndInterest();
     			}
				inputRPTextField.setText(String.valueOf(userinputLoan));
        		System.out.println(userinputLoan);
			}
		});
        // 기타금액으로 입력할 때 누르는 버튼
        etcetraRPButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	//이전 버튼 숨기기
				basicrepaymentButton.setVisible(false);
				fiveRPbutton.setVisible(false);
				tenRPButton.setVisible(false);
				twentyRPButton.setVisible(false);
				fiftyRPButton.setVisible(false);
				hundredRPButton.setVisible(false);
				threehundredRPButton.setVisible(false);
				fivehundredRPButton.setVisible(false);
				thousandRPButton.setVisible(false);
				etcetraRPButton.setVisible(false);
				
				//필요 문구 켜기
				inputRPetcetraTextField.setVisible(true);
				continuerepaymentButton2.setVisible(true);
				
				if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
				
				SystemKeyPad virtualKeyPad = new SystemKeyPad(20);
				virtualKeyPad.setBounds(1400, 450, 400, 320);
				virtualKeyPad.setFont(font);
			    virtualKeyPad.setInputNumberTextField(inputRPetcetraTextField);
			    virtualKeyPad.setVisible(true);
			    inputRPetcetraTextField.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트 소비
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트 소비
                        }
                    }
                });
			    try 
			    {
			    	userinputLoan = Long.parseLong(inputRPetcetraTextField.getText());	
			    	if (userinputLoan > acc.getPrincipalAndInterest()) 
	     			{
	     				userinputLoan = acc.getPrincipalAndInterest();
	     			}
				} catch (NumberFormatException e2) 
			    {
					
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
			    catch (Exception e2) {
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
		
			}
		});
        
        //누르면! 입력한 상환금을 확인하고 비밀번호를 입력하는 화면을 출력하는 버튼
        continuerepaymentButton2.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (userinputLoan != 0)
				{

					// 이전 버튼 숨기기
					inputRPTextField.setVisible(false);
					inputRPetcetraTextField.setVisible(false);
					continuerepaymentButton2.setVisible(false);
					ThirdbyoneString.setVisible(false);
					inputLoan.setVisible(false);
					basicrepaymentButton.setVisible(false);
					fiveRPbutton.setVisible(false);
					tenRPButton.setVisible(false);
					twentyRPButton.setVisible(false);
					fiftyRPButton.setVisible(false);
					hundredRPButton.setVisible(false);
					threehundredRPButton.setVisible(false);
					fivehundredRPButton.setVisible(false);
					thousandRPButton.setVisible(false);
					etcetraRPButton.setVisible(false);
					//필요 문구 켜기
					fourthString.setText("납입정보를 확인하고 비밀번호를 입력해주세요.");
					fourthString.setVisible(true);
					inputLoan.setText("납입금: " + userinputLoan);
					inputLoan.setBounds(70, 200, 700, 30);
					inputLoan.setVisible(true);
					passwordString.setBounds(70, 350, 700, 60);
					passwordString.setVisible(true);
					inputpassword.setBounds(70, 400, 700, 60);
					inputpassword.setVisible(true);
					passwordTextField.setBounds(170, 415, 300, 30);
					passwordTextField.setVisible(true);
					continuerepaymentButton3.setVisible(true);
					
					if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
	                    virtualKeyPad.setVisible(false);
	                }
					
					SystemKeyPad virtualKeyPad = new SystemKeyPad(4);
					virtualKeyPad.setBounds(1400, 450, 400, 320);
					virtualKeyPad.setFont(font);
				    virtualKeyPad.setInputNumberTextField(passwordTextField);
				    virtualKeyPad.setVisible(true);
				    passwordTextField.addKeyListener(new KeyAdapter() 
	                {
	                    @Override
	                    public void keyTyped(KeyEvent e) {
	                        e.consume(); // 이벤트 소비
	                    }
	                    
	                    @Override
	                    public void keyPressed(KeyEvent e) {
	                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	                            e.consume(); // BACKSPACE 키 이벤트 소비
	                        }
	                    }
	                });
				    userinputpassword = passwordTextField.getText();
				}
				else 
				{
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);	
					return;
				}
			}
		});
        
        //누르면! 비밀번호가 일치하다면 상환을 시켜주는 버튼
        continuerepaymentButton3.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// 비밀번호가 맞아야만 상환이됩니다. 상숫값을 바꿔주세요
				if (!userinputpassword.equals(acc.getAccountPasswd()))
				{
					passwordString.setVisible(false);
					inputpassword.setVisible(false);
					passwordTextField.setVisible(false);
					continueLoanButton5.setVisible(false);
					fastCall.setVisible(false);
					soSorry.setVisible(false);
					confirm.setVisible(false);
					iferror.setVisible(false);
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
				}
				else 
				{

					long userlimit = acc.getLoanLimit();//한도
					long principal = acc.getPrincipal();//대출원금
					acc.getRepaymentdate();//상환일
					int period = acc.getRepaymentperiod();//상환기간 (개월)
					acc.getInterestRate();//이자율
//					double interestpermonth = acc.getInterestPerMonth();//월 별 이자
//					long principalpermonth = acc.getPrincipalPerMonth();// 월 별 원금
					long principalandinterest = acc.getPrincipalAndInterest();//원리금
//					long paipermonth = acc.getPrincipalAndInterestPerMonth();//월 별 원리금
					int interest = acc.getInterest();
					long A = 0;
					long B = 0;
					A = (long) ((long) userinputLoan/(100+acc.getInterestRate())*100); // 납입금 중 납입원금
					B = userinputLoan - A;//납입금 중 납입이자
					acc.setLoanLimit(userlimit + A); // 납입원금 차감하여 한도 재 계산
					
					acc.setPrincipal(principal - A); // 납입원금 차감하여 대출원금 재 계산
					
					acc.setInterest((int) (interest - B)); // 납입이자 차감하여 총 이자 재 계산
					
					acc.setRepaymentperiod(period-1); //납입하여 개월 수 재계산
					
					acc.setPrincipalAndInterest(principalandinterest - userinputLoan); // 납입금 차감하여 원리금 재 계산
					
//					acc.setPrincipalPerMonth((long) acc.getPrincipal()/acc.getRepaymentperiod());// 월별 원금 재계산
//					
//					acc.setInterestPerMonth((int) acc.getInterest()/acc.getRepaymentperiod());// 월별 이자 재계산
//					
//					acc.setPrincipalAndInterestPerMonth((long) acc.getPrincipalAndInterest()/acc.getRepaymentperiod());
					
					System.out.println(acc.getLoanLimit() + " :대출한도 재계산");
					System.out.println(acc.getPrincipal() + " :대출원금 재계산");
					System.out.println(acc.getInterest() + " :이자 재계산");
					System.out.println(acc.getRepaymentperiod() + " :상환기간 재계산");
					System.out.println(acc.getPrincipalAndInterest() + " :원리금 재계산");
//					System.out.println(acc.getPrincipalPerMonth() + " :월별원금 재계산");
//					System.out.println(acc.getInterestPerMonth() + " :월별이자 재계산");
//					System.out.println(acc.getPrincipalAndInterestPerMonth() + ":월별 원리금 재계산");
					
					FileSystemDataUpdater FSDU = new FileSystemDataUpdater(acc, index); // 여기서 터져요1
					FSDU.writeLoanToExcel();// 여기서 터져요2
//					
//					acc.setLoanLimit(acc.getLoanLimit()-A);
//					acc.setPrincipal(acc.getPrincipal()-A);
//					acc.setRepaymentperiod(acc.getRepaymentperiod()-1);
					
					/* 대출원금과 이자를 계산하여 상환시켜요
					 * 데이터파일을 불러와서 계산한다.
					 * 
					 * 
					 * 
					 * */ 
					
				}
				if (acc.getPrincipal() != 0) 
				{
					// 이전 버튼 숨기기
					continuerepaymentButton3.setVisible(false);
					fourthString.setVisible(false);
					inputLoan.setVisible(false);
					inputpassword.setVisible(false);
					passwordString.setVisible(false);
					passwordTextField.setVisible(false);
					tcButton.setVisible(false);
					//필요 문구 켜기
					ThirdString.setText("상환이 완료되었습니다. 잔여 상환 정보를 확인해주세요.");
					ThirdString.setVisible(true);
					ThirdbyoneString.setText("이자율은 자동으로 계산됩니다.");
					ThirdbyoneString.setVisible(true);
					/* 여기서 납입을 연산해야해요
					 * 대출 관련 데이터파일에서 불러온다.
					 * 입력받은 납입금을 계산하여, 납입이자와 납입원금을 분리한다.
					 * 대출원금에서 납입원금을 빼고 이자에서 납입이자를 빼고, 둘을 합산하여 납입후의 원리금을 계산한다.
					 * 상환기간의 개월 수를 1 줄이고 상환정보를 재출력한다.
					 * 상숫값을 수정해주세요
					 */
					printLoan.setText("남은 대출원금: " + acc.getPrincipal() + " 원" );
					printLoan.setVisible(true);
					printpaymentperiod.setText("남은 상환기간: " + acc.getRepaymentperiod() + " 개월");
					printpaymentperiod.setVisible(true);
					printpaymentday.setText("납입일: " + acc.getRepaymentdate() + " 일");
					printpaymentday.setVisible(true);
					interestandprincipal.setText("예상 납입액: " + (long) ((acc.getPrincipalPerMonth() + acc.getInterestPerMonth())) + " 원");
					interestandprincipal.setVisible(true);
					confirm.setVisible(true);
				}
				else
				{
					continuerepaymentButton3.setVisible(false);
					fourthString.setVisible(false);
					inputLoan.setVisible(false);
					inputpassword.setVisible(false);
					passwordString.setVisible(false);
					passwordTextField.setVisible(false);
					tcButton.setVisible(false);
					ThirdString.setText("상환이 완료되었습니다");
					ThirdString.setVisible(true);
					
					confirm.setVisible(true);
				}
				
			}
		});
	}
}