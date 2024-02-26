package user;


import javax.swing.*;

import org.apache.commons.collections4.map.MultiKeyMap;

import admin.AdminCashFlowFinder;
import main.BankSystemMainFrame;
import system.Account;
import system.AccountStorage;
import system.SystemKeyPad;
import system.SystemTextDialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class AccountRecommendation extends JDialog 
{
	private SystemKeyPad virtualKeyPad; // 키패드
	private SystemTextDialog textDialog;
	private BankSystemMainFrame bankSystemFrame; // 메인 시스템
	private int mouseX, mouseY;    // 마우스 좌표
	private String userinputname = null; // 고객이 입력한 이름
	private String userinputAccount = null; // 고객이 입력한 계좌번호
	private long totalbalance = 0; // 고객명의 모든 계좌의 잔액들의 합
	private Map<String, Integer> vipDcinterest = new HashMap<String, Integer>(); // 우대고객의 등급에 대한 이자율 감면
	private Map<Integer, Integer> creditscoretomaxLoan = new HashMap<Integer, Integer>(); // 신용점수에 대한 대출 한도 비율
	private Map<Integer, Integer> creditscoretointerestper = new HashMap<Integer, Integer>(); // 신용점수에 대한 이자율 감면
	private AdminCashFlowFinder admincashflowfinder;
	private Account acc; // 입력한 계좌번호와 이름에 의한 대표 계좌 객체
	private int index = 0; // 파일리스트내의 인덱스 번호
	private long depositlog1 = 0;
	private long withdrawllog1 = 0;
	private long depositlog2 = 0;
	private long withdrawllog2 = 0;
	private long depositlog3 = 0;
	private long withdrawllog3 = 0;
	private long loggap1 = 0;
	private long loggap2 = 0;
	private long loggap3 = 0;
	private int selectgoods = 5;
	
	
	
	
	public AccountRecommendation(BankSystemMainFrame bankSystemFrame, AccountStorage as) 
	{
		this.bankSystemFrame = bankSystemFrame;
		MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap(); // 멀티키맵
    	Map<String, Integer> map = as.getMap(); // 키맵
    	ArrayList<Account> depositAccountlist = as.getDepositAccountList(); // 보통예금계좌 객체를 저장한 리스트
    	ArrayList<Account> TimeAccountlist = as.getTimeAccountList();// 정기예금계좌 객체를 저장한 리스트
		ArrayList<Account> savingAccountlist = as.getSavingAccountList();// 정기적금계좌 객체를 저장한 리스트
		ArrayList<Account> freeAccountlist = as.getFreeAccountList();// 자유적금계좌 객체를 저장한 리스트
		
		vipDcinterest.put("일반 고객", 0);
		vipDcinterest.put("2급 우대고객", 1);
		vipDcinterest.put("1급 우대고객", 2);		
		for (int i = 0; i < 101; i++) {
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
		AdminCashFlowFinder depositfinder = new AdminCashFlowFinder(true); // 입급로그 찾는애
		AdminCashFlowFinder withdrawlfinder = new AdminCashFlowFinder(); // 출금로그 찾는애
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
		
		for (int i = 0; i < files.length; i++) 
		{
			System.out.println(files[i] + ", " + i + " 번째");
		}
//		
		this.setSize(1440, 810);
		Font font = new Font("Malgun Gothic", Font.BOLD, 15);
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
        //배경 패널 생성 및 설정
        JPanel recommendationPanel = new JPanel();
        recommendationPanel.setLayout(null);
        recommendationPanel.setBounds(0,0,1440,810);
        recommendationPanel.setVisible(true);
        
        ImageIcon recommendationBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
        JLabel recommendationBackgroundLabel = new JLabel(recommendationBackground);
        recommendationBackgroundLabel.setBounds(0,0,1440,810);
        recommendationPanel.add(recommendationBackgroundLabel);
        this.add(recommendationPanel);
        
        
        
        Container recommendationcontainer = getContentPane();
		recommendationcontainer.setLayout(null);
		
		JLabel recommendationlistLabel = new JLabel("추천 상품 목록");
        recommendationlistLabel.setBounds(670, 100, 700, 60);
        recommendationlistLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        recommendationcontainer.add(recommendationlistLabel);
		recommendationlistLabel.setVisible(false);
		
		
		JLabel selectLabel = new JLabel("이 상품으로 변경하시겠습니까?");
		selectLabel.setBounds(56, 80, 560, 48);
		selectLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(selectLabel);
		selectLabel.setVisible(false);
		
		JLabel selectgoodsLabel = new JLabel("해당 번호와 상품 이름 및 설명");
		selectgoodsLabel.setBounds(56, 200, 560, 48);
		selectgoodsLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(selectgoodsLabel);
		selectgoodsLabel.setVisible(false);
		
		JLabel whyrecommendLabel = new JLabel("추천하는 이유!");
		whyrecommendLabel.setBounds(56, 400, 1000, 48);
		whyrecommendLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(whyrecommendLabel);
		whyrecommendLabel.setVisible(false);
		
		JLabel whyrecommendLabel1 = new JLabel("추천하는 이유!");
		whyrecommendLabel1.setBounds(56, 500, 1000, 48);
		whyrecommendLabel1.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(whyrecommendLabel1);
		whyrecommendLabel1.setVisible(false);
		
		JLabel selectfinishLabel = new JLabel(userinputAccount + "의 상품 정보가 아래와 같이 변경되었습니다.");
		selectfinishLabel.setBounds(56, 80, 560, 48);
		selectfinishLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(selectfinishLabel);
		selectfinishLabel.setVisible(false);
		
		JLabel selectfinishinfoLabel = new JLabel(userinputname + "이자율, 예치기간, 예상금액 의 변동사항을 보여줘요");
		selectfinishinfoLabel.setBounds(56, 120, 560, 48);
		selectfinishinfoLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
		recommendationcontainer.add(selectfinishinfoLabel);
		selectfinishinfoLabel.setVisible(false);
		
		JLabel firstString = new JLabel("상품 추천을 위해 고객님의 정보를 입력해주세요.");
		firstString.setBounds(70, 100, 700, 60);
		firstString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		recommendationcontainer.add(firstString);
		firstString.setVisible(false);

		JLabel accountString = new JLabel("계좌번호: ");
		accountString.setBounds(70, 200, 700, 30);
		accountString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		recommendationcontainer.add(accountString);
		accountString.setVisible(false);

		JTextField accountTextField = new JTextField(20);
		accountTextField.setBounds(170, 200, 500, 30);
		accountTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		recommendationcontainer.add(accountTextField);
		accountTextField.setVisible(false);
		
		JLabel nameString = new JLabel("이름: ");
		nameString.setBounds(70, 300, 700, 30);
		nameString.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		recommendationcontainer.add(nameString);
		nameString.setVisible(false);

		JTextField nameTextField = new JTextField(5);
		nameTextField.setBounds(170, 300, 300, 30);
		nameTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		recommendationcontainer.add(nameTextField);
		nameTextField.setVisible(false);
        
		JButton tcButton = new JButton(iconlist.get(59));
        tcButton.setFont(font);
        tcButton.setBounds(0, 730, 150, 80);
        recommendationPanel.add(tcButton);
        this.add(recommendationPanel);
		
		JButton firstrecommend = new JButton("1.  ");
		firstrecommend.setBounds(270, 300, 1000, 30);
		firstrecommend.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		firstrecommend.setHorizontalAlignment(SwingConstants.LEFT);
		recommendationcontainer.add(firstrecommend);
		firstrecommend.setVisible(false);
		
		JButton secondrecommend = new JButton("2.  ");
		secondrecommend.setBounds(270, 400, 1000, 30);
		secondrecommend.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		secondrecommend.setHorizontalAlignment(SwingConstants.LEFT);
		recommendationcontainer.add(secondrecommend);
		secondrecommend.setVisible(false);
		
		JButton thirdrecommend = new JButton("3.  ");
		thirdrecommend.setBounds(270, 500, 1000, 30);
		thirdrecommend.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		thirdrecommend.setHorizontalAlignment(SwingConstants.LEFT);
		recommendationcontainer.add(thirdrecommend);
		thirdrecommend.setVisible(false);
		
		JButton fourthrecommend = new JButton("4.  ");
		fourthrecommend.setBounds(270, 600, 1000, 30);
		fourthrecommend.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		fourthrecommend.setHorizontalAlignment(SwingConstants.LEFT);
		recommendationcontainer.add(fourthrecommend);
		fourthrecommend.setVisible(false);
        
        JButton continuerecommendationButton = new JButton(iconlist.get(64)); //계속 진행 버튼
        continuerecommendationButton.setFont(font);
        continuerecommendationButton.setBounds(1290,730,150,80);
		recommendationPanel.add(continuerecommendationButton);
		continuerecommendationButton.setVisible(false);
		
		JButton continueselectButton = new JButton(iconlist.get(64)); //계속 진행 버튼
		continueselectButton.setFont(font);
		continueselectButton.setBounds(1290,730,150,80);
		recommendationPanel.add(continueselectButton);
		continueselectButton.setVisible(false);
		
		JButton finishselectButton = new JButton(iconlist.get(64)); //계속 진행 버튼
		finishselectButton.setFont(font);
		finishselectButton.setBounds(1290,730,150,80);
		recommendationPanel.add(finishselectButton);
		finishselectButton.setVisible(false);
        
        JButton depositrecommendButton = new JButton(iconlist.get(44));
        depositrecommendButton.setFont(font);
        depositrecommendButton.setBounds(300, 300, 150, 80);
        recommendationPanel.add(depositrecommendButton);
        
        JButton savingrecommendButton = new JButton(iconlist.get(52));
        savingrecommendButton.setFont(font);
        savingrecommendButton.setBounds(1000, 300, 150, 80);
        recommendationPanel.add(savingrecommendButton);
        
        // 예금 및 적금 추천 5초 기다림 패널
        JPanel transactionFinishPanel = new JPanel();
        transactionFinishPanel.setLayout(null);
        transactionFinishPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        transactionFinishPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // transactionFinishPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel transactionFinishLabel = new JLabel();
        transactionFinishLabel.setBounds(centerPoint.x - this.getWidth() / 2, 420, 500, 30); // (x, y, width, height)
        transactionFinishLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        transactionFinishPanel.add(transactionFinishLabel);
        this.add(transactionFinishPanel);
        
        JLabel transactionFinishLabel2 = new JLabel("상품을 선별 중 입니다.");
        transactionFinishLabel2.setBounds(centerPoint.x - this.getWidth() / 2, 210, 500, 30); // (x, y, width, height)
        transactionFinishLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        transactionFinishPanel.add(transactionFinishLabel2);
        this.add(transactionFinishPanel);
        
        
        
        tcButton.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) {
				// 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountTransferFrame을 숨김
				setVisible(false);
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				return;
			}
		});
        
        depositrecommendButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//이전버튼 끄기 
				depositrecommendButton.setVisible(false);
				savingrecommendButton.setVisible(false);
				
				// TODO Auto-generated method stub
				firstString.setVisible(true);
				accountString.setVisible(true);
				accountTextField.setVisible(true);
				nameString.setVisible(true);
				nameTextField.setVisible(true);
				continuerecommendationButton.setVisible(true);
				tcButton.setVisible(true);
				
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
				System.out.println(userinputname + ": " + userinputAccount);
				continuerecommendationButton.setVisible(true);      		

				
			}
		});
        
        savingrecommendButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				depositrecommendButton.setVisible(false);
				savingrecommendButton.setVisible(false);
				
				// TODO Auto-generated method stub
				firstString.setVisible(true);
				accountString.setVisible(true);
				accountTextField.setVisible(true);
				nameString.setVisible(true);
				nameTextField.setVisible(true);
				tcButton.setVisible(true);
				
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
				System.out.println(userinputname + ": " + userinputAccount);
				continuerecommendationButton.setVisible(true);        		

			}
		});
        
        continuerecommendationButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					if (depositAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname)
							|| freeAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname)
							|| savingAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname)
							|| TimeAccountlist.get(map.get(userinputAccount)).getName().equals(userinputname)) 
						{	
//							System.out.println(depositAccountlist.size());
							index = as.findAccountNumberFromMap(userinputAccount); // 인덱스 번호 찾기
							acc = as.getDepositAccount(index); // 인덱스 번호에 의한 계좌 한개의 정보 - 이름 주민번호 등등
							
							String A = acc.getSsn();
							
							
							continuerecommendationButton.setVisible(false);
							firstString.setVisible(false);
							accountString.setVisible(false);
							accountTextField.setVisible(false);
							nameString.setVisible(false);
							nameTextField.setVisible(false);
							tcButton.setVisible(false);
							
							setVisible(true);
							transactionFinishPanel.setVisible(true);
					       		int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
					       		
					       		// 값들이 잘 들어왔는지 확인해요.
					        		System.out.println(userinputname + ":" + userinputAccount);
					        		// 만약 대출하기 일 경우, 이름, 계좌번호, 대출한도, 입력한대출금, 상환기간, 상환일, 비밀번호, 월 별 원금, 월 별 이자, 이자율, 원리금, 월 별 원리금.
					        		// 만약 대출금 상환하기 일 경우, 이름, 계좌번호, 0, 입력한 상환금, 1, 0, 비밀번호, 0, 0, 이자율, 0, 0
					        		// 대출금 상환하기를 통해 월별 원금, 월 별 이자, 총 원리금, 월 별 원리금은 계산해서 데이터파일에 새로 넣어줘야합니다.
					        		
					        		depositlog1 = depositfinder.getMonthlyTotalTransactions(userinputname);// userinputname 의 한달 입금 내역의 총 합을 가지고와요.
		    		 				withdrawllog1 = withdrawlfinder.getMonthlyTotalTransactions(userinputname);// userinputname 의 한달 출금 내역의 총 합을 가지고와요.
		    		 				loggap1 = depositlog1 - withdrawllog1; // 이 달에 남은 잔액
		    		 				
		    		 				depositlog2 = depositfinder.getMonthlyTotalTransactions(userinputname, 1); // 이번달 기준 한달 전 입금 내역의 총 합
		    		 				withdrawllog2 = withdrawlfinder.getMonthlyTotalTransactions(userinputname, 1); // 두달 전 (2) 까지만 가능
		    		 				loggap2 = depositlog2 - withdrawllog2; // 이 달에 남은 잔액
		    		 				
		    		 				depositlog3 = depositfinder.getMonthlyTotalTransactions(userinputname, 2); // 이번달 기준 한달 전 입금 내역의 총 합
		    		 				withdrawllog3 = withdrawlfinder.getMonthlyTotalTransactions(userinputname, 2); // 두달 전 (2) 까지만 가능
		    		 				loggap3 = depositlog3 - withdrawllog3; // 이 달에 남은 잔액
		    		 				
					        		Timer timer = new Timer(1000, new ActionListener() 
							{
								@Override
					      		 	public void actionPerformed(ActionEvent e) 
								{
					        		 		timeLeft[0]--; // 남은 시간 감소
						        		 	transactionFinishLabel.setText("상품을 선별중입니다. 잠시만 기다려주세요.  " + timeLeft[0]); // 레이블 텍스트 업데이트

					        		 		if (timeLeft[0] == 0)
									{ // 남은 시간이 0이면
					        		 				
					        		 				
					        		 				
					        		 				
					        		 				if (loggap1 <= 0 || loggap2 <= 0 || loggap3 <= 0) 
					        		 				{
					        		 					transactionFinishPanel.setVisible(false);
						        			           	setVisible(true);
						        			           	firstrecommend.setText("1. 보통예금, 이자율 0.1%로 변경");
						        			           	firstrecommend.setVisible(true);
						        			           	recommendationlistLabel.setVisible(true);
						        			           	tcButton.setVisible(true);
						        			           	recommendationPanel.setVisible(true);
													}
					        		 				else 
					        		 				{	
					        		 					if (withdrawllog1 == 0 && withdrawllog2 == 0 && withdrawllog3 == 0) 
					        		 					{
					        		 						if (depositlog1 >= 500000 || depositlog2 >= 500000 || depositlog3 >= 500000) 
					        		 						{
					        		 							transactionFinishPanel.setVisible(false);
								        			           	setVisible(true);
								        			           	secondrecommend.setText("1. 정기예금, 이자율 2.4%로 변경, 출금 불가");
								        			        	thirdrecommend.setText("2. 정기적금, 이자율 3.1%로 변경, 출금 불가, 납입일: 22일, 납입액: " + (long) ((loggap1+loggap2+loggap3)/10) );
								        			        	fourthrecommend.setText("3. 자유적금, 이자율 3.4%로 변경, 출금 불가, 납입일: 22일");
								        			           	secondrecommend.setVisible(true);
								        			        	thirdrecommend.setVisible(true);
								        			        	fourthrecommend.setVisible(true);
								        			        	recommendationlistLabel.setVisible(true);
								        			           	tcButton.setVisible(true);
								        			           	recommendationPanel.setVisible(true);
															}
					        		 						else 
					        		 						{
					        		 							transactionFinishPanel.setVisible(false);
								        			           	setVisible(true);
								        			           	secondrecommend.setText("1. 정기예금, 이자율 2.4%로 변경, 출금 불가");
								        			           	secondrecommend.setVisible(true);
								        			           	recommendationlistLabel.setVisible(true);
								        			           	tcButton.setVisible(true);
								        			           	recommendationPanel.setVisible(true);
															}
					        		 						transactionFinishPanel.setVisible(false);
							        			           	setVisible(true);
							        			        	thirdrecommend.setText("2. 정기적금, 이자율 3.1%로 변경, 출금 불가, 납입일: 22일, 납입액: " + (long) ((loggap1+loggap2+loggap3)/10) );
							        			           	fourthrecommend.setText("3. 자유적금, 이자율 3.4%로 변경, 출금 불가, 납입일: 22일");
							        			           	secondrecommend.setText("1. 정기예금, 이자율 2.4%로 변경, 출금 불가");
							        			           	secondrecommend.setVisible(true);
							        			           	thirdrecommend.setVisible(true);
							        			        	fourthrecommend.setVisible(true);
							        			           	recommendationlistLabel.setVisible(true);					        			           	
							        			           	tcButton.setVisible(true);
							        			           	recommendationPanel.setVisible(true);
														}
					        		 					else 
					        		 					{
					        		 						transactionFinishPanel.setVisible(false);
							        			           	setVisible(true);
							        			        	firstrecommend.setText("1. 보통예금, 이자율 0.1%로 변경");
							        			        	secondrecommend.setText("2. 정기예금, 이자율 2.4%로 변경, 출금 불가");
							        			        	thirdrecommend.setText("3. 정기적금, 이자율 3.1%로 변경, 출금 불가, 납입일: 22일, 납입액: " + (long) ((loggap1+loggap2+loggap3)/10) );
							        			        	fourthrecommend.setText("4. 자유적금, 이자율 3.4%로 변경, 출금 불가, 납입일: 22일");
							        			           	firstrecommend.setVisible(true);
							        			           	secondrecommend.setVisible(true);
							        			           	thirdrecommend.setVisible(true);
							        			        	fourthrecommend.setVisible(true);
							        			        	recommendationlistLabel.setText("전체 상품 목록");
							        			           	recommendationlistLabel.setVisible(true);
							        			           	tcButton.setVisible(true);
							        			           	recommendationPanel.setVisible(true);
														}
					        		 				}
//						        		        	transactionFinishPanel.setVisible(false);
//					        			           	setVisible(true);
//					        			           	firstrecommend.setText("1. 보통예금");
//					        			           	secondrecommend.setText("2. 정기예금");
//					        			           	thirdrecommend.setText("3. 정기적금");
//					        			           	fourthrecommend.setText("4. 자유적금");
//					        			           	firstrecommend.setVisible(true);
//					        			           	secondrecommend.setVisible(true);
//					        			        	thirdrecommend.setVisible(true);
//					        			        	fourthrecommend.setVisible(true);
//					        			        	recommendationlistLabel.setVisible(true);
//					        			           	recommendationPanel.setVisible(true);
//					        			           	tcButton.setVisible(true);
					        			           	
									((Timer)e.getSource()).stop(); // 타이머 중지
					        		       		 }
						        		}
					        		});
					        		timer.start();

						}
						else 
						{
							// 먼저 공백인지 확인해요.
							// 이 후에 입력받은 값을 데이터파일에서 불러와서 비교해야해요
							// 만약 그 중에 하나라도 해당하면 메인으로 돌아가요
							continuerecommendationButton.setVisible(false);
							firstString.setVisible(false);
							accountString.setVisible(false);
							accountTextField.setVisible(false);
							nameString.setVisible(false);
							nameTextField.setVisible(false);
							tcButton.setVisible(false);
							
							setVisible(false);
							bankSystemFrame.getAccountManagementPanel().setVisible(false);
							bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
							
							bankSystemFrame.setVisible(true);
							bankSystemFrame.getBackgroundPanel().setVisible(true);
							return;
							
							}

				} 
				catch (Exception e2) 
				{
				// TODO: handle exception
					continuerecommendationButton.setVisible(false);
					firstString.setVisible(false);
					accountString.setVisible(false);
					accountTextField.setVisible(false);
					nameString.setVisible(false);
					nameTextField.setVisible(false);
					tcButton.setVisible(false);
					
					setVisible(false);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					return;
					
				}
			}
		});
        
        firstrecommend.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// TODO Auto-generated method stub 일반예금
				firstrecommend.setVisible(false);
	           	secondrecommend.setVisible(false);
	        	thirdrecommend.setVisible(false);
	        	fourthrecommend.setVisible(false);
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	
	        	
				// 이 상품의 정보를 변수에 대입해주세요: 이자율 설정, 계좌 상태 설정, 
	        	selectLabel.setVisible(true);
	        	whyrecommendLabel.setText("입금과 출금의 패턴이 뚜렷하지 않아요.     " + "이번달 잔액: " + loggap1 + " / " + "1달전 잔액: " + loggap2 + " / "  + "2달전 잔액: " + loggap3);
	        	whyrecommendLabel1.setText("이번달 출금액: " + withdrawllog1 + " / " + "1달전 출금액: " + withdrawllog2 + " / "  + "2달전 출금액: " + withdrawllog3);
	        	selectgoods = 1;
	        	whyrecommendLabel1.setVisible(true);
	        	whyrecommendLabel.setVisible(true);
	        	selectgoodsLabel.setText(firstrecommend.getText());	
	        	selectgoodsLabel.setVisible(true);
	        	continueselectButton.setVisible(true);
			}
		});
        
        secondrecommend.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub 정기예금
				firstrecommend.setVisible(false);
	           	secondrecommend.setVisible(false);
	        	thirdrecommend.setVisible(false);
	        	fourthrecommend.setVisible(false);
	        	
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	
	        	
	        	
				// 이 상품의 정보를 변수에 대입해주세요: 이자율 설정, 계좌 상태 설정, 
	        	selectLabel.setVisible(true);
	        	whyrecommendLabel.setText("지난 3개월 동안 거래 내역이 거의 없고 잔액이 일정해요.     " + "이번달 잔액: " + loggap1 + " / " + "1달전 잔액: " + loggap2 + " / "  + "2달전 잔액: " +" / " + loggap3);
	        	selectgoods = 2;
	        	whyrecommendLabel.setVisible(true);
	        	selectgoodsLabel.setText(secondrecommend.getText());
	        	selectgoodsLabel.setVisible(true);
	        	continueselectButton.setVisible(true);
			}
		});
        
        thirdrecommend.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub 정기적금
				firstrecommend.setVisible(false);
	           	secondrecommend.setVisible(false);
	        	thirdrecommend.setVisible(false);
	        	fourthrecommend.setVisible(false);
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	
	        	
	        	
				// 이 상품의 정보를 변수에 대입해주세요: 이자율 설정, 계좌 상태 설정, 
	        	selectLabel.setVisible(true);
	        	whyrecommendLabel.setText("지난 3개월동안 출금 기록이 거의 없고 입금량이 어느정도 일정해요.     " + "이번달 잔액: " + loggap1 + " / " + "1달전 잔액: " +loggap2 + " / "  + "2달전 잔액: " + " / " + loggap3);
	        	whyrecommendLabel1.setText("이번달 입금액: " + depositlog1 + " / " + "1달전 입금액: " + depositlog2 + " / "  + "2달전 입금액: " + depositlog3);
	        	selectgoods = 3;
	        	whyrecommendLabel1.setVisible(true);
	        	whyrecommendLabel.setVisible(true);
	        	selectgoodsLabel.setText(thirdrecommend.getText());
	        	selectgoodsLabel.setVisible(true);
	        	continueselectButton.setVisible(true);
			}
		});
        
        fourthrecommend.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// TODO Auto-generated method stub 자유적금
				firstrecommend.setVisible(false);
	           	secondrecommend.setVisible(false);
	        	thirdrecommend.setVisible(false);
	        	fourthrecommend.setVisible(false);
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	
	        	
	        	
				// 이 상품의 정보를 변수에 대입해주세요: 이자율 설정, 계좌 상태 설정, 
	        	selectLabel.setVisible(true);
	        	whyrecommendLabel.setText("지난 3개월동안 출금 기록이 거의 없고 입금량이 일정하지 않아요.     " + "이번달 잔액: " + loggap1 + " / " + "1달 전 잔액: " + loggap2 + " / " + "2달 전 잔액: " +" / " + loggap3);
	        	whyrecommendLabel1.setText("이번달 입금액: " + depositlog1 + " / " + "1달전 입금액: " + depositlog2 + " / "  + "2달전 입금액: " + depositlog3);
	        	selectgoods = 4;
	        	whyrecommendLabel1.setVisible(true);
	        	whyrecommendLabel.setVisible(true);
	        	selectgoodsLabel.setText(fourthrecommend.getText());
	        	selectgoodsLabel.setVisible(true);
	        	continueselectButton.setVisible(true);
			}
		});
        
        continueselectButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// 이전 버튼 지우기
				tcButton.setVisible(false); // tcButton 끄고싶어요 ..
				selectLabel.setVisible(false);
				selectgoodsLabel.setVisible(false);
				whyrecommendLabel.setVisible(false);
	        	continueselectButton.setVisible(false);
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	switch (selectgoods) 
	        	{
				case 2:// 정기예금
					acc.setTimeInterestRate(2.4);
					acc.setState(5);
					selectfinishLabel.setText(userinputAccount + " 의 상품 정보가 아래와 같이 변경되었습니다.");
		        	selectfinishinfoLabel.setText(secondrecommend.getText());
					break;
				case 3:// 정기적금
					acc.setSavingInterestRate(3.1);
					acc.setState(5);
					selectfinishLabel.setText(userinputAccount + " 의 상품 정보가 아래와 같이 변경되었습니다.");
		        	selectfinishinfoLabel.setText(thirdrecommend.getText());
					break;
				case 4:// 자유적금
					acc.setFreeInterestRate(3.3);
					acc.setState(5);
					selectfinishLabel.setText(userinputAccount + " 의 상품 정보가 아래와 같이 변경되었습니다.");
		        	selectfinishinfoLabel.setText(fourthrecommend.getText());
					break;
				default://일반예금
					acc.setDepositInterestRate(0.1);
					selectfinishLabel.setText(userinputAccount + " 의 상품 정보가 아래와 같이 변경되었습니다.");
		        	selectfinishinfoLabel.setText(firstrecommend.getText());
					break;
				}
	        	
	        	// 필요버튼 켜기
	        	
	        	selectfinishLabel.setVisible(true);
	        	selectfinishinfoLabel.setVisible(true);
	        	finishselectButton.setVisible(true);
	        	
			}
		});
        
        finishselectButton.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// 이전버튼 및 필요없는 버튼 지우기
				selectfinishLabel.setVisible(false);
	        	selectfinishinfoLabel.setVisible(false);
	        	whyrecommendLabel1.setVisible(false);
	        	whyrecommendLabel1.setVisible(false);
	        	finishselectButton.setVisible(false);
	        	
	        	System.out.println(userinputname + ":" + userinputAccount);
	        	
	        	
	        	setVisible(false);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
				
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				return;
			}
		});
	}
}
