package main;

import javax.swing.*;

import admin.AdminMainFrame;
import system.*;
import user.*;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class BankSystemMainFrame extends JFrame {
	
    private static AccountStorage accountStorage = new AccountStorage();
    private int mouseX, mouseY;
    private JPanel backgroundPanel;
    private JPanel accountManagementPanel;
    private JPanel depositWithdrawalPanel;
    private int count = 0; //관리자 화면 진입용 숫자 세는 변수
    
    public BankSystemMainFrame() {
    	
    	
    	
        this.setSize(1440, 810); // 창 크기
        this.setAlwaysOnTop(true); // 항상 최상단 레이어로 배치
        this.setLayout(null);

        this.setResizable(false); // 크기 변경 불가능하도록 함
    	this.setUndecorated(true); // 프레임의 타이틀바를 없앰
        
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
        
        Font font = new Font("Malgun Gothic", Font.BOLD, 15);
       
        // 배경 패널 생성 및 설정
        JPanel fordeposit = new JPanel();
        fordeposit.setLayout(null);
        fordeposit.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        
        
        ImageIcon fordepositimage = new ImageIcon("이미지\\보이스피싱안내문구.PNG");
        JLabel fordepositbackground = new JLabel(fordepositimage);
        fordepositbackground.setBounds(0, 0, 1440, 810);
        fordeposit.add(fordepositbackground);
        fordeposit.setVisible(false);
        
        ImageIcon forsearchimage = new ImageIcon("이미지\\힣신용대출메인.PNG");
        JLabel forsearchimagebackground = new JLabel(forsearchimage);
        forsearchimagebackground.setBounds(0, 0, 1440, 810);
        fordeposit.add(forsearchimagebackground);
        fordeposit.setVisible(false);
        
        
        backgroundPanel = new JPanel();
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        
        
        //backgroundPanel에 관리자화면으로 들어갈 수 있는 숨겨진 버튼을 생성 및 추가
        JButton hideAdminButton = new JButton(); 
        hideAdminButton.setBounds(789, 626, 4, 3); // 버튼의 크기와 위치를 직접 설정
        hideAdminButton.setBorderPainted(false);
        hideAdminButton.setBackground(new Color(67, 38, 46));
        backgroundPanel.add(hideAdminButton);
        
        
        // backgroundPanel에 계좌 관리 버튼 생성 및 추가
        ImageIcon managementButtonImage = new ImageIcon("이미지\\버튼\\계좌관리.PNG"); // 절대경로
        JButton accountManagementButton = new JButton(managementButtonImage); // 절대경로
        accountManagementButton.setBounds(206, 104, 200, 100); // 버튼의 크기와 위치를 직접 설정
        backgroundPanel.add(accountManagementButton);
        
        // backgroundPanel에 입·출금 버튼 생성 및 추가
        ImageIcon depositWithdrawalButtonImage = new ImageIcon("이미지\\버튼\\입출금.PNG"); // 절대경로
        JButton depositWithdrawalButton = new JButton(depositWithdrawalButtonImage);
        depositWithdrawalButton.setBounds(206, 278, 200, 100); // 버튼의 크기와 위치를 직접 설정
        backgroundPanel.add(depositWithdrawalButton);
        
        // backgroundPanel에 계좌이체 버튼 생성 및 추가
        ImageIcon transferButtonImage = new ImageIcon("이미지\\버튼\\계좌이체.PNG"); // 절대경로
        JButton accountTransferButton = new JButton(transferButtonImage); 
        accountTransferButton.setBounds(206, 452, 200, 100);
        backgroundPanel.add(accountTransferButton);
        
        // backgroundPanel에 신용대출 버튼 생성 및 추가
        ImageIcon creditLoanButtonImage = new ImageIcon("이미지\\버튼\\신용대출.PNG"); // 절대경로
        JButton creditLoanButton = new JButton(creditLoanButtonImage);
        creditLoanButton.setBounds(206, 626, 200, 100);
        backgroundPanel.add(creditLoanButton);
        
        // backgroundPanel에 상품추천 버튼 생성 및 추가
        ImageIcon prButtonImage = new ImageIcon("이미지\\버튼\\상품추천.PNG"); // 절대경로
        JButton prButton = new JButton(prButtonImage); // pr은 product Recommendation를 의미
        prButton.setBounds(1034, 104, 200, 100);
        backgroundPanel.add(prButton);
        
        // backgroundPanel에 우대고객 여부 확인 버튼 생성 및 추가
        ImageIcon copcButtonImage = new ImageIcon("이미지\\버튼\\우대고객 여부 확인.PNG"); // 절대경로
        JButton copcButton = new JButton(copcButtonImage); // copc는 Confirmation of Preferred Customers를 의미
        copcButton.setBounds(1034, 365, 200, 100);
        backgroundPanel.add(copcButton);
        
        // backgroundPanel에 우대고객 혜택 버튼 생성 및 추가
        ImageIcon pcbButtonImage = new ImageIcon("이미지\\버튼\\우대고객 혜택.PNG"); // 절대경로
        JButton pcbButton = new JButton(pcbButtonImage); // pcb는 Preferred Customer Benefits를 의미
        pcbButton.setFont(font);
        pcbButton.setBounds(1034, 626, 200, 100);
        backgroundPanel.add(pcbButton);
        
        ImageIcon mainImageGIF = new ImageIcon("이미지\\메인.gif");
        // Image scaledImage = mainImageGIF.getImage().getScaledInstance(400, 150, Image.SCALE_DEFAULT);
        // mainImageGIF.setImage(scaledImage);
        JLabel mainImageGIFLabel = new JLabel(mainImageGIF);
        mainImageGIFLabel.setBounds(520, 570, 400, 150);
        backgroundPanel.add(mainImageGIFLabel);
        
        ImageIcon mainImage = new ImageIcon("이미지\\메인.PNG"); // 절대경로
        JLabel mainImageLabel = new JLabel(mainImage);
        mainImageLabel.setBounds(0, 0, 1440, 810);
        backgroundPanel.add(mainImageLabel);
        
        
        this.add(backgroundPanel);
        setVisible(true);
        
        // 계좌 관리 패널 생성 및 설정 
        accountManagementPanel = new JPanel();
        accountManagementPanel.setLayout(null);
        accountManagementPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountManagementPanel.setBackground (new Color (255, 255, 255)); 
        accountManagementPanel.setVisible(false);
        
        // accountManagementPanel에 생성 버튼 생성 및 추가
        ImageIcon creatingButtonImage = new ImageIcon("이미지\\버튼\\생성.PNG"); // 절대경로
        JButton creatingButton = new JButton(creatingButtonImage);
        creatingButton.setFont(font);
        creatingButton.setBounds(206, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(creatingButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 조회 버튼 생성 및 추가
        ImageIcon serchingButtonImage = new ImageIcon("이미지\\버튼\\조회.PNG"); // 절대경로
        JButton serchingButton = new JButton(serchingButtonImage);
        serchingButton.setFont(font);
        serchingButton.setBounds(650, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(serchingButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 해지 버튼 생성 및 추가
        ImageIcon closureButtonImage = new ImageIcon("이미지\\버튼\\해지.PNG"); // 절대경로
        JButton closureButton = new JButton(closureButtonImage);
        closureButton.setFont(font);
        closureButton.setBounds(950, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(closureButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 거래 취소 버튼 생성 및 추가
        ImageIcon tcButtonImage = new ImageIcon("이미지\\버튼\\취소.PNG"); // 절대경로
        JButton tcButton1 = new JButton(tcButtonImage); // tc는 transaction cancellation을 의미
        tcButton1.setBounds(0, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(tcButton1);
        this.add(accountManagementPanel);
        
        // 입·출금 패널 생성 및 설정 
        depositWithdrawalPanel = new JPanel();
        depositWithdrawalPanel.setLayout(null);
        depositWithdrawalPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        depositWithdrawalPanel.setVisible(false);
        
        // depositWithdrawalPanel에 입금 버튼 생성 및 추가
        ImageIcon depositButtonImage = new ImageIcon("이미지\\버튼\\입금.PNG"); // 절대경로
        JButton depositButton = new JButton(depositButtonImage);
        depositButton.setFont(font);
        depositButton.setBounds(206, 350, 200, 100); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(depositButton);
        
        // depositWithdrawalPanel에 입금 버튼 생성 및 추가
        ImageIcon withdrawalButtonImage = new ImageIcon("이미지\\버튼\\출금.PNG"); // 절대경로
        JButton withdrawalButton = new JButton(withdrawalButtonImage);
        withdrawalButton.setFont(font);
        withdrawalButton.setBounds(1034, 350, 200, 100); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(withdrawalButton);
        
        // accountManagementPanel에 거래 취소 버튼 생성 및 추가
        JButton tcButton2 = new JButton(tcButtonImage); // tc는 transaction cancellation을 의미
        tcButton2.setFont(font);
        tcButton2.setBounds(0, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(tcButton2);
        
        ImageIcon depositWithdrawalImage = new ImageIcon("이미지\\거래 유형.PNG"); // 절대경로
        JLabel depositWithdrawalImageLabel = new JLabel(depositWithdrawalImage);
        depositWithdrawalImageLabel.setBounds(0, 0, 1440, 810);
        depositWithdrawalPanel.add(depositWithdrawalImageLabel);
        this.add(depositWithdrawalPanel);
        
        hideAdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	count++;
            	if(count == 5) {
            		AdminMainFrame adminMainFrame = new AdminMainFrame(accountStorage);
            		adminMainFrame.setVisible(true);
            		setVisible(false);
            		count = 0;
            	}
            }
        });
        
        
        
        // accountManagementButton을 눌렀을 때 이벤트 처리
        accountManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundPanel.setVisible(false);
                accountManagementPanel.setVisible(true);
            }
        });
        
        // accountManagementButton을 눌렀을 때 이벤트 처리
        accountManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundPanel.setVisible(false);
                accountManagementPanel.setVisible(true);
            }
        });
        
        // creatingButton을 눌렀을 때 이벤트 처리
        creatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	AccountCreateDialog accountCreateDialog = new AccountCreateDialog(BankSystemMainFrame.this, accountStorage);
            	accountCreateDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        // serchingButton을 눌렀을 때 이벤트 처리
        serchingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {	
//	            ImageIcon searchbackgroundImage = new ImageIcon("이미지\\버튼\\힣신용대출메인.PNG");
//	            JLabel searchbackgroundImagelabel = new JLabel(searchbackgroundImage);
//	            searchbackgroundImagelabel.setBounds(0,0,1440,810);
            	AccountSearchDialog accountSearchDialog = new AccountSearchDialog(BankSystemMainFrame.this, accountStorage);
//            	accountSearchDialog.add(searchbackgroundImagelabel);
            	accountSearchDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        // closureButton을 눌렀을 때 이벤트 처리
        closureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	AccountCloseDialog accountCloseDialog = new AccountCloseDialog(BankSystemMainFrame.this, accountStorage);
            	accountCloseDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        // depositWithdrawalButton을 눌렀을 때 이벤트 처리
        depositWithdrawalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundPanel.setVisible(false);
                depositWithdrawalPanel.setVisible(true);
            }
        });
        
        // withdrawalButton을 눌렀을 때 이벤트 처리
        withdrawalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	AccountWithdrawalDialog accountWithdrawalDialog = new AccountWithdrawalDialog(BankSystemMainFrame.this, accountStorage);
            	accountWithdrawalDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        // depositButton을 눌렀을 때 이벤트 처리(입금)
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {	
            	ImageIcon depositbackgroundImage = new ImageIcon("이미지\\버튼\\힣신용대출메인.PNG");
        		JLabel depositbackgroundImagelabel = new JLabel(depositbackgroundImage);
        		depositbackgroundImagelabel.setBounds(0,0,1440,810);
            	AccountDepositDialog accountDepositDialog = new AccountDepositDialog(BankSystemMainFrame.this, accountStorage);
            	accountDepositDialog.add(depositbackgroundImagelabel);
            	fordeposit.setVisible(true);
            	accountDepositDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        // accountTransferButton을 눌렀을 때 이벤트 처리
        accountTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccountTransferDialog accountTransferDialog = new AccountTransferDialog(BankSystemMainFrame.this,accountStorage);
                accountTransferDialog.setVisible(true);
                setVisible(false);
            }
        });
        
        
        // creditLoanButton을 눌렀을 때 이벤트 처리
        creditLoanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccountCreditLoanDialog creditLoan = new AccountCreditLoanDialog(BankSystemMainFrame.this, accountStorage);
                creditLoan.setVisible(true);
                setVisible(false);
            }
        });
        
        prButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccountRecommendation Recommend = new AccountRecommendation(BankSystemMainFrame.this, accountStorage);
                Recommend.setVisible(true);
                setVisible(false);
            }
        });
        
        
        
        // tcButton1을 눌렀을 때 이벤트 처리
        tcButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundPanel.setVisible(true);
                accountManagementPanel.setVisible(false);
                depositWithdrawalPanel.setVisible(false);
            }
        });
        
        // tcButton2을 눌렀을 때 이벤트 처리
        tcButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundPanel.setVisible(true);
                accountManagementPanel.setVisible(false);
                depositWithdrawalPanel.setVisible(false);
            }
        });
    }
    
	    
	    
	    public JPanel getBackgroundPanel() {
        return backgroundPanel;
    }
    
    public JPanel getAccountManagementPanel() {
    	return accountManagementPanel;
    }
    
    public JPanel getDepositWithdrawalPanel() {
    	return depositWithdrawalPanel;
    }

    // 메인 패널 생성 후, 실행
    public static void main(String[] args) {
    	
        accountStorage = new AccountStorage();
        
        FileSystemFileReader fileSystemFileReader = new FileSystemFileReader();
		
		fileSystemFileReader.readAccountsDataFromExcel(accountStorage);
		 @SuppressWarnings("unused")
			SystemAccountStateCheck daliyCheck = new SystemAccountStateCheck(0, accountStorage); // 사용하진않지만 이체,출금제한이 하루가 지나면 확인하기 위해 선언    	
		
		
		
        BankSystemMainFrame bankSystemFrame = new BankSystemMainFrame();
        bankSystemFrame.setVisible(true);
    }
}