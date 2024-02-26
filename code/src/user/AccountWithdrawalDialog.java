package user;

import javax.swing.*;

import org.apache.commons.collections4.map.MultiKeyMap;

import com.sun.jdi.Value;

import main.BankSystemMainFrame;
import system.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.collections4.map.MultiKeyMap;

@SuppressWarnings("serial")
public class AccountWithdrawalDialog extends JDialog {
	
	private SystemAccountStateCheck withdrawalSecurity;
    private FileSystemDataUpdater fd;
    private SystemKeyPad virtualKeyPad;
    private BankSystemMainFrame bankSystemFrame;
    private int mouseX, mouseY;
    private long totalAmount = 0; // 출금액 담아둘 변수(100만원 이하만 가능)
    private long inputAmount = 0; // 출금액 담아둘 변수(100만원 이하만 가능)
    private String password;
    private LocalDate date; // 마지막 거래 일자 담아둘 변수
    private String inputAccount;
    private Account withdrawalAccount;
    private int withdrawalAccountIndex = 0;
    
    public AccountWithdrawalDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) {
    	MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap();
    	Map<String, Integer> map = as.getMap();
    	ArrayList<Account> withdrawalAccountList = as.getDepositAccountList();
    	
        this.bankSystemFrame = bankSystemFrame;
        
        this.setSize(1440, 810);
        Font font = new Font("Malgun Gothic", Font.BOLD, 30);
        Color color = new Color(203, 218, 75);

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
        
//        // JWindow 창 끄기
//        JButton btnExit = new JButton("시스템 종료");
//        btnExit.setBounds(0, 0, 100, 100);
//        this.add(btnExit);
//
//        // 마우스로 버튼을 클릭하면 이벤트 처리
//        btnExit.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.exit(0);
//            }
//        });
        // ----------------------------------------------------
        // panel, button, label, textfield 생성 및 설정, 추가 구간
        // ---------------- 출금 패널 생성 및 설정 ------------------
        JPanel accountWithdrawalPanel = new JPanel();
        accountWithdrawalPanel.setLayout(null);
        accountWithdrawalPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountWithdrawalPanel.setVisible(true); // 화면에 보이도록 설정

        // accountWithdrawalPanel에 취소 버튼 생성 및 추가
        ImageIcon tcImage1 = new ImageIcon("이미지\\버튼\\X.png");
        JButton tcButton1 = new JButton(tcImage1); // tc는 transaction cancellation을 의미
        tcButton1.setBounds(0, 699, 716, 111); // 버튼의 크기와 위치를 직접 설정
        accountWithdrawalPanel.add(tcButton1);
        
        // accountWithdrawalPanel에 확인 버튼 생성 및 추가
        ImageIcon continueWithdrawalImage1 = new ImageIcon("이미지\\버튼\\O.png");
        JButton continueWithdrawalButton1 = new JButton(continueWithdrawalImage1); 
        continueWithdrawalButton1.setBounds(724, 699, 716, 111); // 버튼의 크기와 위치를 직접 설정
        accountWithdrawalPanel.add(continueWithdrawalButton1);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon voicePishingWarningImage = new ImageIcon("이미지\\보이스피싱안내문구.png");
        JLabel voicePishingWarningImageLabel = new JLabel(voicePishingWarningImage);
        voicePishingWarningImageLabel.setBounds(0, 0, 1440, 810);
        accountWithdrawalPanel.add(voicePishingWarningImageLabel);
        this.add(accountWithdrawalPanel);
        // -----------------------------------------------------
        // 취소 버튼 생성 및 설정, 이벤트처리
        ImageIcon tcImage = new ImageIcon("이미지\\버튼\\취소.png");
        JButton tcButton2 = new JButton(tcImage); // tc는 transaction cancellation을 의미
        tcButton2.setBounds(0, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        this.add(tcButton2);
        
        tcButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountWithdrawalDialog를 숨김
                setVisible(false);
                bankSystemFrame.setVisible(true);
                bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                return;
            }
        });
        // -----------------------------------------------------
        // ------------- 출금 은행 선택 패널 생성 및 설정 -------------
        JPanel wabsPanel = new JPanel(); // wabs는 Withdrawal account bank select을 의미
        wabsPanel.setLayout(null);
        wabsPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        wabsPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // wabsPanel에 단풍은행 버튼 생성 및 추가
        ImageIcon mpBankButtonImage = new ImageIcon("이미지\\버튼\\단풍은행.png");
        JButton mpBankButton = new JButton(mpBankButtonImage); 
        mpBankButton.setFont(font);
        mpBankButton.setBounds(206, 300, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wabsPanel.add(mpBankButton);
        
        // wabsPanel에 신한은행 버튼 생성 및 추가
        ImageIcon shBankButtonImage = new ImageIcon("이미지\\버튼\\신한은행.png");
        JButton shBankButton = new JButton(shBankButtonImage); 
        shBankButton.setFont(font);
        shBankButton.setBounds(206, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wabsPanel.add(shBankButton);
        
        // wabsPanel에 카카오은행 버튼 생성 및 추가
        ImageIcon kakaoBankButtonImage = new ImageIcon("이미지\\버튼\\카카오은행.png");
        JButton kakaoBankButton = new JButton(kakaoBankButtonImage); 
        kakaoBankButton.setFont(font);
        kakaoBankButton.setBounds(482, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wabsPanel.add(kakaoBankButton);
        
        // wabsPanel에 우리은행 버튼 생성 및 추가
        ImageIcon weBankButtonImage = new ImageIcon("이미지\\버튼\\우리은행.png");
        JButton weBankButton = new JButton(weBankButtonImage); 
        weBankButton.setBounds(758, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wabsPanel.add(weBankButton);
        
        // wabsPanel에 새마을 금고 버튼 생성 및 추가
        ImageIcon mgBankButtonImage = new ImageIcon("이미지\\버튼\\새마을금고.png");
        JButton mgBankButton = new JButton(mgBankButtonImage); 
        mgBankButton.setBounds(1034, 500, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wabsPanel.add(mgBankButton);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon wabsPanelImage = new ImageIcon("이미지\\출금 은행 유형.png");
        JLabel wabsPanelImageLabel = new JLabel(wabsPanelImage);
        wabsPanelImageLabel.setBounds(0, 0, 1440, 810);
        wabsPanel.add(wabsPanelImageLabel);
        this.add(wabsPanel);
        // ------------------------------------------------------------
        // -------------- 출금 계좌 번호 입력 패널 생성 및 설정 ---------------
        JPanel waniPanel = new JPanel(); // wani는 Withdrawal account number input을 의미
        waniPanel.setLayout(null);
        waniPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        waniPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // waniPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel accountNumLabel = new JLabel("계좌번호:");
        accountNumLabel.setBounds(206, 300, 200, 30); // (x, y, width, height)
        accountNumLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 폰트 크기를 20으로 설정
        waniPanel.add(accountNumLabel);
        
        JTextField textField = new JTextField(12);
        textField.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        textField.setBounds(346, 295, 300, 40);
        waniPanel.add(textField);
        
        // waniPanel에 확인 버튼 생성 및 추가
        ImageIcon continueWithdrawalButton = new ImageIcon("이미지\\버튼\\확인.png");
        JButton continueWithdrawalButton2 = new JButton(continueWithdrawalButton);
        continueWithdrawalButton2.setBounds(1240, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        waniPanel.add(continueWithdrawalButton2);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon waniPanelImage = new ImageIcon("이미지\\출금 계좌번호 입력.png");
        JLabel waniTitle = new JLabel(waniPanelImage);
        waniTitle.setBounds(0, 0, 1440, 810); // (x, y, width, height)
        waniPanel.add(waniTitle);
        this.add(waniPanel);
        // -------------------------------------------------------------
        // 입력된 계좌번호가 일반 저축 통장의 계좌번호와 일치하지 않을 경우 패널 생성 및 설정
        JPanel untradabelPanel = new JPanel();
        untradabelPanel.setLayout(null);
        untradabelPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        untradabelPanel.setVisible(false); // 화면에 안 보이도록 설정
        
		JLabel returnLabel = new JLabel();
		returnLabel.setBounds(560, 507, 1000, 200);
		returnLabel.setFont(font);
		returnLabel.setForeground(Color.red);
		untradabelPanel.add(returnLabel);
		
		// 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon untradablePanelImageGIF = new ImageIcon("이미지\\거래 불가능.gif");
		JLabel untradableTitleGIF = new JLabel(untradablePanelImageGIF);
		untradableTitleGIF.setBounds(650, 350, 200, 200);
		untradabelPanel.add(untradableTitleGIF);
	
        ImageIcon untradablePanelImage = new ImageIcon("이미지\\거래 불가능.png");
		JLabel untradableTitle = new JLabel(untradablePanelImage);
		untradableTitle.setBounds(0, 0, 1440, 810);
		untradabelPanel.add(untradableTitle);
		this.add(untradabelPanel);
		// ----------------------------------------------------
        // ------------- 출금액 선택하는 패널 생성 및 설정 -------------
        JPanel wasPanel = new JPanel(); // was는 Withdrawal amount select를 의미
        wasPanel.setLayout(null);
        wasPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        wasPanel.setVisible(false); // 화면에 안 보이도록 설정
		
		JLabel withdrawalAmountLabel = new JLabel("출금액:                 원");
		withdrawalAmountLabel.setBounds(540, 650, 400, 30);
		withdrawalAmountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		wasPanel.add(withdrawalAmountLabel);
		
		JLabel balanceBeforeTransactionLabel1 = new JLabel();
		balanceBeforeTransactionLabel1.setBounds(540, 110, 400, 30);
		balanceBeforeTransactionLabel1.setFont(font);
		balanceBeforeTransactionLabel1.setForeground(color.red);
		wasPanel.add(balanceBeforeTransactionLabel1);
		
		JTextField withdrawalAmountTextField = new JTextField(7);
		withdrawalAmountTextField.setBounds(620, 650, 100, 30);
		withdrawalAmountTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		wasPanel.add(withdrawalAmountTextField);
        
        // wasPanel(Withdrawal amount select)에 출금액 선택 버튼 생성 및 추가
		ImageIcon oneButtonImage = new ImageIcon("이미지\\버튼\\1만.png");
        JButton oneButton = new JButton(oneButtonImage);
        oneButton.setBounds(200, 80, 200, 100);
        wasPanel.add(oneButton);

        ImageIcon threeButtonImage = new ImageIcon("이미지\\버튼\\3만.png");
        JButton threeButton = new JButton(threeButtonImage);
        threeButton.setBounds(200, 210, 200, 100);
        wasPanel.add(threeButton);
        
        ImageIcon fiveButtonImage = new ImageIcon("이미지\\버튼\\5만.png");
        JButton fiveButton = new JButton(fiveButtonImage);
        fiveButton.setBounds(200, 340, 200, 100);
        wasPanel.add(fiveButton);
        
        ImageIcon tenButtonImage = new ImageIcon("이미지\\버튼\\10만.png");
        JButton tenButton = new JButton(tenButtonImage);
        tenButton.setBounds(200, 470, 200, 100);
        wasPanel.add(tenButton);
        
        ImageIcon twentyButtonImage = new ImageIcon("이미지\\버튼\\20만.png");
        JButton twentyButton = new JButton(twentyButtonImage);
        twentyButton.setBounds(200, 600, 200, 100);
        wasPanel.add(twentyButton);
        
        ImageIcon thirtyButtonImage = new ImageIcon("이미지\\버튼\\30만.png");
        JButton thirtyButton = new JButton(thirtyButtonImage);
        thirtyButton.setBounds(1040, 80, 200, 100);
        wasPanel.add(thirtyButton);
        
        ImageIcon fiftyButtonImage = new ImageIcon("이미지\\버튼\\50만.png");
        JButton fiftyButton = new JButton(fiftyButtonImage);
        fiftyButton.setBounds(1040, 210, 200, 100);
        wasPanel.add(fiftyButton);
        
        ImageIcon seventyButtonImage = new ImageIcon("이미지\\버튼\\70만.png");
        JButton seventyButton = new JButton(seventyButtonImage);
        seventyButton.setBounds(1040, 340, 200, 100);
        wasPanel.add(seventyButton);
        
        ImageIcon hundredButtonImage = new ImageIcon("이미지\\버튼\\100만.png");
        JButton hundredButton = new JButton(hundredButtonImage);
        hundredButton.setBounds(1040, 470, 200, 100);
        wasPanel.add(hundredButton);
        
        ImageIcon etceteraButtonImage = new ImageIcon("이미지\\버튼\\기타.png");
        JButton etceteraButton = new JButton(etceteraButtonImage);
        etceteraButton.setBounds(1040, 600, 200, 100);
        wasPanel.add(etceteraButton);   
        
        // wasPanel에 확인 버튼 생성 및 추가
		JButton continueWithdrawalButton3 = new JButton(continueWithdrawalButton);
		continueWithdrawalButton3.setBounds(1240, 710, 200, 100);
		wasPanel.add(continueWithdrawalButton3);
		
		// 이미지 아이콘과 라벨로 배경 넣기
		ImageIcon wasPanelImage = new ImageIcon("이미지\\출금 금액 선택.png");
		JLabel wasPanelTitle = new JLabel(wasPanelImage);
		wasPanelTitle.setBounds(0, 0, 1440, 810);
		wasPanel.add(wasPanelTitle);
		this.add(wasPanel);
		// -------------------------------------------------
        // ------------- 출금액 입력 패널 생성 및 설정 -------------
        JPanel waaiPanel = new JPanel(); // waai는 Withdrawal account amount input을 의미
        waaiPanel.setLayout(null);
        waaiPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        waaiPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // 이름 라벨과 텍스트 필드
		JLabel withdrawalAmountLabel2 = new JLabel("출금액:                 원");
		withdrawalAmountLabel2.setBounds(540, 650, 400, 30);
		withdrawalAmountLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		waaiPanel.add(withdrawalAmountLabel2);
		
		JLabel balanceBeforeTransactionLabel2 = new JLabel();
		balanceBeforeTransactionLabel2.setBounds(206, 370, 400, 30);
		balanceBeforeTransactionLabel2.setFont(font);
		balanceBeforeTransactionLabel2.setForeground(color.red);
		waaiPanel.add(balanceBeforeTransactionLabel2);
		
		JTextField withdrawalAmountTextField2 = new JTextField(7);
		withdrawalAmountTextField2.setBounds(620, 650, 100, 30);
		withdrawalAmountTextField2.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		waaiPanel.add(withdrawalAmountTextField2);
		
        // waniPanel에 계속 버튼 생성 및 추가
        JButton continueWithdrawalButton4 = new JButton(continueWithdrawalButton);
        continueWithdrawalButton4.setBounds(1240, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        waaiPanel.add(continueWithdrawalButton4);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon waaiPanelImage = new ImageIcon("이미지\\출금 금액 입력.png");
        JLabel waaiTitle = new JLabel(waaiPanelImage);
        waaiTitle.setBounds(0, 0, 1440, 810);
        waaiPanel.add(waaiTitle);
        this.add(waaiPanel);
        // -------------------------------------------------------------
        // -------------- 출금 계좌 비밀번호 입력 패널 생성 및 설정 ---------------
        JPanel wapiPanel = new JPanel(); // wapi는 Withdrawal account password input을 의미
        wapiPanel.setLayout(null);
        wapiPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        wapiPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // wapiPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(206, 350, 120, 30); // (x, y, width, height)
        passwordLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        wapiPanel.add(passwordLabel);
        
		JTextField passwordTextField = new JTextField(0);
		passwordTextField.setBounds(320, 350, 400, 30);
		passwordTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		wapiPanel.add(passwordTextField);
        
        // waniPanel에 확인 버튼 생성 및 추가
        JButton continueWithdrawalButton5 = new JButton(continueWithdrawalButton);
        continueWithdrawalButton5.setBounds(1240, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        wapiPanel.add(continueWithdrawalButton5);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon wapiPanelImage = new ImageIcon("이미지\\비밀번호.png");
        JLabel wapiTitle = new JLabel(wapiPanelImage);
        wapiTitle.setBounds(0, 0, 1440, 810); // (x, y, width, height)
        wapiPanel.add(wapiTitle);
        this.add(wapiPanel);
        // ------------------------------------------------------
        // -------------- 출금 금액 확인 패널 생성 및 설정 ---------------
        JPanel waaicPanel = new JPanel(); // waaic는 Withdrawal account amount input confirm을 의미
        waaicPanel.setLayout(null);
        waaicPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        waaicPanel.setVisible(false); // 화면에 안 보이도록 설정
  
        // waaicPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel withdrawalAmountLabel3 = new JLabel();
        withdrawalAmountLabel3.setBounds(206, 300, 400, 30); // (x, y, width, height)
        withdrawalAmountLabel3.setFont(font); // 폰트 크기를 30으로 설정
        waaicPanel.add(withdrawalAmountLabel3);
        
        JLabel withdrawalChargeLabel = new JLabel("수수료: 500원");
        withdrawalChargeLabel.setBounds(206, 400, 400, 30); // (x, y, width, height)
        withdrawalChargeLabel.setFont(font); // 폰트 크기를 30으로 설정
        waaicPanel.add(withdrawalChargeLabel);
        
        JLabel balanceAfterTransactionLabel = new JLabel();
        balanceAfterTransactionLabel.setBounds(206, 500, 500, 30);
        balanceAfterTransactionLabel.setFont(font); // 폰트 크기를 30으로 설정
        waaicPanel.add(balanceAfterTransactionLabel);

        // waaicPanel에 확인 버튼 생성 및 추가
        JButton continueWithdrawalButton6 = new JButton(continueWithdrawalButton);
        continueWithdrawalButton6.setBounds(1240, 710, 200, 100); // 버튼의 크기와 위치를 직접 설정
        waaicPanel.add(continueWithdrawalButton6);
        
        // 이미지 아이콘과 라벨로 배경 넣기
        ImageIcon waaicPanelImage = new ImageIcon("이미지\\출금 금액 확인.png");
        JLabel waaicTitle = new JLabel(waaicPanelImage);
        waaicTitle.setBounds(0, 0, 1440, 810); // (x, y, width, height)
        waaicPanel.add(waaicTitle);
        this.add(waaicPanel);
        // ---------------------------------------------------
        // -------------- 거래 완료 패널 생성 및 설정 ---------------
        JPanel transactionFinishPanel = new JPanel();
        transactionFinishPanel.setLayout(null);
        transactionFinishPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        transactionFinishPanel.setVisible(false); // 화면에 안 보이도록 설정
        
        // transactionFinishPanel에 이름 라벨과 텍스트 필드 생성 및 추가
        JLabel transactionFinishLabel = new JLabel();
        transactionFinishLabel.setBounds(560, 507, 1000, 200); // (x, y, width, height)
        transactionFinishLabel.setFont(font); // 폰트 크기를 30으로 설정
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
        // ----------------------------------------------
		//--------------- 이벤트 처리 구간 -------------------
        // accountWithdrawalPanel에 있는 버튼 이벤트 처리
        // tcButton1를 눌렀을 때 이벤트 처리
        tcButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountWithdrawalDialog를 숨김
            	setVisible(false);
                bankSystemFrame.setVisible(true);
                bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                return;
            }
        }); 
        // continueWithdrawalButton1을 눌렀을 때 이벤트 처리
        continueWithdrawalButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	accountWithdrawalPanel.setVisible(false);
            	wabsPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 은행을 선택하세요");
            }
        });
        // -------------------------------------------------
        // wabsPanel에 있는 버튼 이벤트 처리
        /*
         * 예금 구분은 금융결제원 '참가기관별 CMS 계좌번호 체계'를 기준으로 구분
         * 참고문헌
         * CMS 참가기관 계좌번호체계 및 공동코드(2023.8.1. 기준). (2023년 7월 14일). 금융결제원. https://www.cmsedi.or.kr/cms/board/workdata/view/989
         */
        // mpBankButton를 눌렀을 때 이벤트 처리
        mpBankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wabsPanel.setVisible(false);
            	waniPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 계좌번호를 입력하세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(12);
                virtualKeyPad.setVisible(true);
                textField.setText(virtualKeyPad.getNumber());
                inputAccount = virtualKeyPad.getNumber();
                // 텍스트 필드 눌렀을 때 이벤트 처리
                textField.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(12);
        				textField.setText(null);
                        virtualKeyPad.setVisible(true);
                        textField.setText(virtualKeyPad.getNumber());
                        inputAccount = virtualKeyPad.getNumber();
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                
                // 키보드로 입력 못받게 하는 이벤트 처리
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        
        // shBankButton를 눌렀을 때 이벤트 처리
        shBankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wabsPanel.setVisible(false);
            	waniPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 계좌번호를 입력하세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(12);
                virtualKeyPad.setVisible(true);
                textField.setText(virtualKeyPad.getNumber());
                inputAccount = virtualKeyPad.getNumber();
                // 텍스트 필드 눌렀을 때 이벤트 처리
                textField.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(12);
        				textField.setText(null);
                        virtualKeyPad.setVisible(true);
                        textField.setText(virtualKeyPad.getNumber());
                        inputAccount = virtualKeyPad.getNumber();
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                // 키보드로 입력 못받게 하는 이벤트 처리
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        // kakaoBankButton를 눌렀을 때 이벤트 처리
        kakaoBankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wabsPanel.setVisible(false);
            	waniPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 계좌번호를 입력하세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(13);
                virtualKeyPad.setVisible(true);
                textField.setText(virtualKeyPad.getNumber());                        
                inputAccount = virtualKeyPad.getNumber();
                // 텍스트 필드 눌렀을 때 이벤트 처리
                textField.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(13);
        				textField.setText(null);
                        virtualKeyPad.setVisible(true);
                        textField.setText(virtualKeyPad.getNumber());
                        inputAccount = virtualKeyPad.getNumber();
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                // 키보드로 입력 못받게 하는 이벤트 처리
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        
        // weBankButton를 눌렀을 때 이벤트 처리
        weBankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wabsPanel.setVisible(false);
            	waniPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 계좌번호를 입력하세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(14);
                virtualKeyPad.setVisible(true);
                textField.setText(virtualKeyPad.getNumber());
                inputAccount = virtualKeyPad.getNumber();
            	// 텍스트 필드 눌렀을 때 이벤트 처리
                textField.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(14);
        				textField.setText(null);
                        virtualKeyPad.setVisible(true);
                        textField.setText(virtualKeyPad.getNumber());
                        inputAccount = virtualKeyPad.getNumber();
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                // 키보드로 입력 못받게 하는 이벤트 처리
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        
        // mgBankButton를 눌렀을 때 이벤트 처리
        mgBankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wabsPanel.setVisible(false);
            	waniPanel.setVisible(true);
            	// System.out.println("출금할 계좌의 계좌번호를 입력하세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(13);
                virtualKeyPad.setVisible(true);
                textField.setText(virtualKeyPad.getNumber());
                inputAccount = virtualKeyPad.getNumber();
                // 텍스트 필드 눌렀을 때 이벤트 처리
                textField.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(13);
        				textField.setText(null);
                        virtualKeyPad.setVisible(true);
                        textField.setText(virtualKeyPad.getNumber());
                        inputAccount = virtualKeyPad.getNumber();
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                // 키보드로 입력 못받게 하는 이벤트 처리
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        // -------------------------------------------------
        // waniPanel에 있는 버튼 이벤트 처리       
        // continueWithdrawalButton2을 눌렀을 때 이벤트 처리
        continueWithdrawalButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	virtualKeyPad.setVisible(false);
            	waniPanel.setVisible(false);
            	if (map.get(inputAccount) == null) {
            		setVisible(false);
		            bankSystemFrame.setVisible(true);
		            bankSystemFrame.getBackgroundPanel().setVisible(true);
		            bankSystemFrame.getAccountManagementPanel().setVisible(false);
		            bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
		            return;
				} else if (inputAccount.substring(0,3).equals("011") || inputAccount.substring(0,3).equals("021") || inputAccount.substring(0,3).equals("022")) {
                	wasPanel.setVisible(false);
                	untradabelPanel.setVisible(true);
            		int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
            		Timer timer = new Timer(1000, new ActionListener() {
            		    @Override
            		    public void actionPerformed(ActionEvent e) {
            		        timeLeft[0]--; // 남은 시간 감소
            		        returnLabel.setText(timeLeft[0] + ""); // 레이블 텍스트 업데이트

            		        if (timeLeft[0] == 0) { // 남은 시간이 0이면
            		        	untradabelPanel.setVisible(false);
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
				} else {
					withdrawalAccountIndex = map.get(inputAccount);
        			withdrawalAccount = withdrawalAccountList.get(withdrawalAccountIndex);
        			date = withdrawalAccount.getLastTradingDate(); // 마지막 거래날짜
        			withdrawalSecurity = new SystemAccountStateCheck(withdrawalAccountIndex, as); // security 생성자에 사용자의 arraylist index(account)와 accountstorage의 as를 넣는다.
        			
        			if(!date.isEqual(LocalDate.now())) { // 마지막 거래날짜로부터 하루이상 지났다면
        				withdrawalAccount.setWithdrawalLimit(0);
        				withdrawalAccount.setWithdrawalLimit(false);
						if(withdrawalAccount.getState() == 3) {
							withdrawalAccount.setState(0);
						}
						else if(withdrawalAccount.getState() == 5) {
							withdrawalAccount.setState(4);
						}
						else if(withdrawalAccount.getState() == 6) {
							withdrawalAccount.setState(1);
						}
						else if(withdrawalAccount.getState() == 8) {
							withdrawalAccount.setState(7);
						}
        			}
        			
        			if(!withdrawalSecurity.checkState(false)) { 
        				bankSystemFrame.setVisible(true);
                    	bankSystemFrame.getBackgroundPanel().setVisible(true);
                        bankSystemFrame.getAccountManagementPanel().setVisible(false);
                        bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    	setVisible(false);
                    	return;
        			}
        			
	            	wasPanel.setVisible(true);
	            	balanceBeforeTransactionLabel1.setText("* 거래 전 잔액: " + withdrawalAccount.getBalance());
        			
				}
            }
        });
        // -------------------------------------------------
        // wasPanel에 있는 버튼 이벤트 처리
        // oneButton을 눌렀을 때 이벤트 처리
        oneButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 10000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });

        // threeButton을 눌렀을 때 이벤트 처리
        threeButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 30000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
        // fiveButton을 눌렀을 때 이벤트 처리 
        fiveButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 50000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });

        // tenButton을 눌렀을 때 이벤트 처리
        tenButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 100000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
        // twentyButton을 눌렀을 때 이벤트 처리
        twentyButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 200000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
        // thirtyButton을 눌렀을 때 이벤트 처리
        thirtyButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 300000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
        // fiftyButton을 눌렀을 때 이벤트 처리
        fiftyButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 500000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
        // seventyButton을 눌렀을 때 이벤트 처리
        seventyButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int amount = 700000;
        		totalAmount += amount;
        		if (totalAmount > 1000000) {
        			totalAmount = 1000000;
        		}
        		withdrawalAmountTextField.setText(String.valueOf(totalAmount));
        	}
        });
     
     	// hundredButton을 눌렀을 때 이벤트 처리 hundredButton
     	hundredButton.addActionListener(new ActionListener() {
     		@Override
     		public void actionPerformed(ActionEvent e) {
     			int amount = 1000000;
     			totalAmount += amount;
     			if (totalAmount > 1000000) {
     				totalAmount = 1000000;
     			}
     			withdrawalAmountTextField.setText(String.valueOf(totalAmount));
     		}
     	});
        
        // etceteraButton을 눌렀을 때 이벤트 처리
        etceteraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	balanceBeforeTransactionLabel2.setText("* 거래 전 잔액: " + withdrawalAccount.getBalance());
            	totalAmount = 0;
            	wasPanel.setVisible(false);
            	waaiPanel.setVisible(true);
            	//System.out.println("출금액을 입력해 주세요");
                if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
            	virtualKeyPad = new SystemKeyPad(7);
                virtualKeyPad.setVisible(true);
                // System.out.println(totalAmount);
                withdrawalAmountTextField2.setText(virtualKeyPad.getNumber());
                System.out.println(withdrawalAmountTextField2.getText());
                
	            try {
	                	inputAmount = Integer.parseInt(withdrawalAmountTextField2.getText());
				} catch (NumberFormatException e1) {
						// TODO: handle exception
						inputAmount = 0;
				}
				
                
                withdrawalAmountTextField2.addMouseListener(new MouseListener() {
        			@Override
        			public void mouseClicked(MouseEvent e) {
        				virtualKeyPad = new SystemKeyPad(7);
        				withdrawalAmountTextField2.setText(null);
                        virtualKeyPad.setVisible(true);
                        withdrawalAmountTextField2.setText(virtualKeyPad.getNumber());
                        
        	            try {
    	                	inputAmount = Integer.parseInt(withdrawalAmountTextField2.getText());
        	            } catch (NumberFormatException e1) {
    						// TODO: handle exception
    						inputAmount = 0;
        	            }
        			}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {
					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
                });
                
                if (inputAmount > 1000000) {
                    inputAmount = 1000000;
                	withdrawalAmountTextField2.setText(String.valueOf(inputAmount));
				}
                
                // 키보드로 입력 못받게 하는 이벤트 처리
                withdrawalAmountTextField2.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume(); // 이벤트
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // BACKSPACE 키 이벤트
                        }
                    }
                });
            }
        });
        
        // continueWithdrawalButton3을 눌렀을 때 이벤트 처리
        continueWithdrawalButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	wasPanel.setVisible(false);
            	virtualKeyPad.setVisible(false);
		
            	if (withdrawalAccount.getBalance() - totalAmount - 500 < 0 || totalAmount <= 0) {
            		JOptionPane.showMessageDialog(null, "잔액이 부족합니다.");
            		setVisible(false);
                    bankSystemFrame.setVisible(true);
                    bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    return;
				}  else {
					if (withdrawalAccount.getWithdrawalLimit() + totalAmount > 6000000) {
						withdrawalAccount.setWithdrawalLimit(true);
	        			if (withdrawalAccount.getState() == 0) {
	        				withdrawalAccount.setState(3);
	        			}
	        			else if (withdrawalAccount.getState() == 4) {
	        				withdrawalAccount.setState(5);
	        			} 
					}
					
					wapiPanel.setVisible(true);
	            	  if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
	            		  virtualKeyPad.setVisible(false);
	            	  }
	            	  virtualKeyPad = new SystemKeyPad(true);
	            	  virtualKeyPad.setVisible(true);
	            	  if (virtualKeyPad.getNumber().length() < 4) {
						if (virtualKeyPad.getNumber().length() < 1) {
							 passwordTextField.setText("");
						} else if (virtualKeyPad.getNumber().length() < 2) {
							passwordTextField.setText("*");
						} else if (virtualKeyPad.getNumber().length() < 3) {
							passwordTextField.setText("**");
						} else {
							passwordTextField.setText("***");
						} 
					} else {
						passwordTextField.setText("****");
					}
	            	  System.out.println(virtualKeyPad.getNumber());
	            	  password = virtualKeyPad.getNumber();
	            	  passwordTextField.addMouseListener(new MouseListener() {
	            		  @Override
	            		  public void mouseClicked(MouseEvent e) {
	            			  virtualKeyPad = new SystemKeyPad(true);
	            			  passwordTextField.setText(null);
	            			  virtualKeyPad.setVisible(true);
	            			  password = virtualKeyPad.getNumber();
	            			  if (virtualKeyPad.getNumber().length() < 4) {
	      						if (virtualKeyPad.getNumber().length() < 1) {
	      							 passwordTextField.setText("");
	      						} else if (virtualKeyPad.getNumber().length() < 2) {
	      							passwordTextField.setText("*");
	      						} else if (virtualKeyPad.getNumber().length() < 3) {
	      							passwordTextField.setText("**");
	      						} else {
	      							passwordTextField.setText("***");
	      						}
	      					} else {
								passwordTextField.setText("****");
							}
	            		  } 
	            		  public void mousePressed(MouseEvent e) {
	            		  }
	            		  public void mouseReleased(MouseEvent e) {
	            		  }
	            		  public void mouseEntered(MouseEvent e) {
	            		  }
	            		  public void mouseExited(MouseEvent e) {
	            		  }
	            	  });
	                
	            	  // 키보드로 입력 못받게 하는 이벤트 처리
	            	  passwordTextField.addKeyListener(new KeyAdapter() {
	            		  @Override
	            		  public void keyTyped(KeyEvent e) {
	            			  e.consume(); // 이벤트
	            		  }
	            		  @Override
	            		  public void keyPressed(KeyEvent e) {
	            			  if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	            				  e.consume(); // BACKSPACE 키 이벤트
	            			  }
	            		  }
	            	  });					
					}
            	}
        });
        
        // -------------------------------------------------
        // waaiPanel에 있는 버튼 이벤트 처리        
        // continueWithdrawalButton4을 눌렀을 때 이벤트 처리
        continueWithdrawalButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	virtualKeyPad.setVisible(false);
            	waaiPanel.setVisible(false);
                if (withdrawalAmountTextField2.getText().substring(0, 1) == "0" || inputAmount % 10000 != 0 || withdrawalAccount.getBalance() - inputAmount - 500 < 0) {
                    setVisible(false);
                    bankSystemFrame.setVisible(true);
                    bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    return;
				} else {
					if (withdrawalAccount.getWithdrawalLimit() + inputAmount > 6000000) {
						withdrawalAccount.setWithdrawalLimit(true);
	        			if (withdrawalAccount.getState() == 0) {
	        				withdrawalAccount.setState(3);
	        			}
	        			else if (withdrawalAccount.getState() == 4) {
	        				withdrawalAccount.setState(5);
	        			} 
					}
					wapiPanel.setVisible(true);
	            	  if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
	            		  virtualKeyPad.setVisible(false);
	            	  }
	            	  virtualKeyPad = new SystemKeyPad(true);
	            	  virtualKeyPad.setVisible(true);
	            	  password = virtualKeyPad.getNumber();
	            	  if (virtualKeyPad.getNumber().length() < 4) {
							if (virtualKeyPad.getNumber().length() < 1) {
								 passwordTextField.setText("");
							} else if (virtualKeyPad.getNumber().length() < 2) {
								passwordTextField.setText("*");
							} else if (virtualKeyPad.getNumber().length() < 3) {
								passwordTextField.setText("**");
							} else {
								passwordTextField.setText("***");
							}
						} else {
							passwordTextField.setText("****");
						}
	                
	            	  passwordTextField.addMouseListener(new MouseListener() {
	            		  @Override
	            		  public void mouseClicked(MouseEvent e) {
	            			  virtualKeyPad = new SystemKeyPad(true);
	            			  passwordTextField.setText(null);
	            			  virtualKeyPad.setVisible(true);
	            			  password = virtualKeyPad.getNumber();
	            			  if (virtualKeyPad.getNumber().length() < 4) {
	      						if (virtualKeyPad.getNumber().length() < 1) {
	      							 passwordTextField.setText("");
	      						} else if (virtualKeyPad.getNumber().length() < 2) {
	      							passwordTextField.setText("*");
	      						} else if (virtualKeyPad.getNumber().length() < 3) {
	      							passwordTextField.setText("**");
	      						} else {
	      							passwordTextField.setText("***");
	      						}
	      					} else {
	      						passwordTextField.setText("****");
	      					}
	      	            	  
	            		  }
	            		  public void mousePressed(MouseEvent e) {
	            		  }
	            		  public void mouseReleased(MouseEvent e) {
	            		  }
	            		  public void mouseEntered(MouseEvent e) {
	            		  }
	            		  public void mouseExited(MouseEvent e) {
	            		  }
	            	  });
	            	  // 키보드로 입력 못받게 하는 이벤트 처리
	            	  passwordTextField.addKeyListener(new KeyAdapter() {
	            		  @Override
	            		  public void keyTyped(KeyEvent e) {
	            			  e.consume(); // 이벤트
	            		  }
	            		  @Override
	            		  public void keyPressed(KeyEvent e) {
	            			  if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	            				  e.consume(); // BACKSPACE 키 이벤트
	            			  }
	            		  }
	            	  });
				}
            }
        });
        // -------------------------------------------------
        // wapiPanel에 있는 버튼 이벤트 처리     
        // continueWithdrawalButton5을 눌렀을 때 이벤트 처리
        continueWithdrawalButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (multikeyMap.get(inputAccount, password) != null ) {
                	if (inputAmount == 0) {
                		withdrawalAmountLabel3.setText("출금액: " +  totalAmount + "원");
                		withdrawalAccount.setBalance(withdrawalAccount.getBalance() - totalAmount - 500);
                		withdrawalAccount.setWithdrawalLimit(totalAmount);
                		withdrawalSecurity.CheckWithdrawalLimit(totalAmount);
                		
    				} else {
    					withdrawalAmountLabel3.setText("출금액: " +  inputAmount + "원");
    					withdrawalAccount.setBalance(withdrawalAccount.getBalance() - inputAmount - 500);
    	            	withdrawalAccount.setWithdrawalLimit(inputAmount);
    	            	withdrawalSecurity.CheckWithdrawalLimit(inputAmount);
    				} 

                	balanceAfterTransactionLabel.setText("거래 후 잔액: " + withdrawalAccount.getBalance());
                	
                	withdrawalAccount.setLastTradingDate(LocalDate.now()); // 마지막 거래일자
                	
                	fd = new FileSystemDataUpdater(withdrawalAccount, withdrawalAccountIndex);
                	fd.ModifyAccountDateToExcel();
                	
                	virtualKeyPad.setVisible(false);
                	wapiPanel.setVisible(false);
                	waaicPanel.setVisible(true);
            	} else {
            		setVisible(false);
		            bankSystemFrame.setVisible(true);
		            bankSystemFrame.getBackgroundPanel().setVisible(true);
		            bankSystemFrame.getAccountManagementPanel().setVisible(false);
		            bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
		            return;
				}

            }
        });
        
        // -------------------------------------------------
        // waaicPanel에 있는 버튼 이벤트 처리     
        // continueWithdrawalButton6을 눌렀을 때 이벤트 처리
        continueWithdrawalButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	tcButton2.setVisible(false);
            	waaicPanel.setVisible(false);
            	transactionFinishPanel.setVisible(true);
            	int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
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
        
    }
 
}