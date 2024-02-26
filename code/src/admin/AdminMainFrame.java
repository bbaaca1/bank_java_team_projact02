package admin;

import javax.swing.*;

import system.*;
import user.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.beancontext.BeanContextServicesSupport;

@SuppressWarnings("serial")
public class AdminMainFrame extends JFrame {

    private int mouseX, mouseY;
    private JPanel backgroundPanel;
    private JPanel accountManagementPanel;
    private JPanel depositWithdrawalPanel;

    public AdminMainFrame(AccountStorage accountStorage) {
    	
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
        
        JLabel mainLabel = new JLabel("메인화면");
        mainLabel.setBounds(655, 150, 250, 100);
        mainLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 40));
        this.add(mainLabel);
        
        // JWindow 창 끄기
        JButton btnExit = new JButton("시스템 종료");
        btnExit.setBounds(600, 600, 250, 100);
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
        backgroundPanel = new JPanel();
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        setVisible(true); // 화면에 보이도록 설정
        
        // backgroundPanel에 계좌 관리 버튼 생성 및 추가
        JButton accountManagementButton = new JButton("고객 확인");
        accountManagementButton.setFont(font);
        accountManagementButton.setBounds(300, 150, 150, 80); // 버튼의 크기와 위치를 직접 설정
        backgroundPanel.add(accountManagementButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 입·출금 버튼 생성 및 추가
        JButton depositWithdrawalButton = new JButton("우대고객 확인");
        depositWithdrawalButton.setFont(font);
        depositWithdrawalButton.setBounds(300, 300, 150, 80); // 버튼의 크기와 위치를 직접 설정
        backgroundPanel.add(depositWithdrawalButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 계좌이체 버튼 생성 및 추가
        JButton accountTransferButton = new JButton("고객 정보 수정"); 
        accountTransferButton.setFont(font);
        accountTransferButton.setBounds(300, 450, 150, 80);
        backgroundPanel.add(accountTransferButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 신용대출 버튼 생성 및 추가
        JButton creditLoanButton = new JButton("고객 추가"); 
        creditLoanButton.setFont(font);
        creditLoanButton.setBounds(300, 600, 150, 80);
        backgroundPanel.add(creditLoanButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 상품 추천 버튼 생성 및 추가
        JButton prButton = new JButton("보유 현금 현황"); // pr은 product Recommendation를 의미
        prButton.setFont(font);
        prButton.setBounds(1000, 150, 150, 80);
        backgroundPanel.add(prButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 우대고객 계산기 버튼 생성 및 추가
        JButton copcButton = new JButton("현금 흐름 확인"); // copc는 Confirmation of Preferred Customers를 의미
        copcButton.setFont(font);
        copcButton.setBounds(1000, 300, 150, 80);
        backgroundPanel.add(copcButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 우대고객 혜택 버튼 생성 및 추가
        JButton pcbButton = new JButton("에러 로그 확인"); // pcb는 Preferred Customer Benefits를 의미
        pcbButton.setFont(font);
        pcbButton.setBounds(1000, 450, 150, 80);
        backgroundPanel.add(pcbButton);
        this.add(backgroundPanel);
        
        // backgroundPanel에 우대고객 혜택 버튼 생성 및 추가
        JButton userLogButton = new JButton("사용자 로그 확인"); // pcb는 Preferred Customer Benefits를 의미
        userLogButton.setFont(font);
        userLogButton.setBounds(1000, 600, 150, 80);
        backgroundPanel.add(userLogButton);
        this.add(backgroundPanel);
        
        
        // 계좌 관리 패널 생성 및 설정 
        accountManagementPanel = new JPanel();
        accountManagementPanel.setLayout(null);
        accountManagementPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountManagementPanel.setVisible(false);
        
        // accountManagementPanel에 생성 버튼 생성 및 추가
        JButton creatingButton = new JButton("생성");
        creatingButton.setFont(font);
        creatingButton.setBounds(350, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(creatingButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 조회 버튼 생성 및 추가
        JButton serchingButton = new JButton("조회");
        serchingButton.setFont(font);
        serchingButton.setBounds(650, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(serchingButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 해지 버튼 생성 및 추가
        JButton closureButton = new JButton("해지");
        closureButton.setFont(font);
        closureButton.setBounds(950, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(closureButton);
        this.add(accountManagementPanel);
        
        // accountManagementPanel에 거래 취소 버튼 생성 및 추가
        JButton tcButton1 = new JButton("거래 취소"); // tc는 transaction cancellation을 의미
        tcButton1.setFont(font);
        tcButton1.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountManagementPanel.add(tcButton1);
        this.add(accountManagementPanel);
        
        // 입·출금 패널 생성 및 설정 
        depositWithdrawalPanel = new JPanel();
        depositWithdrawalPanel.setLayout(null);
        depositWithdrawalPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        depositWithdrawalPanel.setVisible(false);
        
        // depositWithdrawalPanel에 입금 버튼 생성 및 추가
        JButton depositButton = new JButton("입금");
        depositButton.setFont(font);
        depositButton.setBounds(450, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(depositButton);
        this.add(depositWithdrawalPanel);
        
        // depositWithdrawalPanel에 입금 버튼 생성 및 추가
        JButton withdrawalButton = new JButton("출금");
        withdrawalButton.setFont(font);
        withdrawalButton.setBounds(800, 350, 150, 80); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(withdrawalButton);
        this.add(depositWithdrawalPanel);
        
        // accountManagementPanel에 거래 취소 버튼 생성 및 추가
        JButton tcButton2 = new JButton("거래 취소"); // tc는 transaction cancellation을 의미
        tcButton2.setFont(font);
        tcButton2.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        depositWithdrawalPanel.add(tcButton2);
        this.add(depositWithdrawalPanel);
        
        // accountManagementButton을 눌렀을 때 이벤트 처리
        accountManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminCustomerConfirmation customerConfirmation = new AdminCustomerConfirmation(AdminMainFrame.this, accountStorage);
                customerConfirmation.setVisible(true);
                setVisible(false);
            }
        });
        
        // creatingButton을 눌렀을 때 이벤트 처리
        creatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        // serchingButton을 눌렀을 때 이벤트 처리
        serchingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        // closureButton을 눌렀을 때 이벤트 처리
        closureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                setVisible(false);
            }
        });
        
        // depositButton을 눌렀을 때 이벤트 처리(입금)
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        // accountTransferButton을 눌렀을 때 이벤트 처리
        accountTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	AdminModifyCustomerInformation modifycustomerInformation = new AdminModifyCustomerInformation(AdminMainFrame.this, accountStorage);
            	modifycustomerInformation.setVisible(true);
                setVisible(false);
            }
        });
        
        //creditLoanButton을 눌렀을 때 이벤트 처리
        creditLoanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        
        // 현금흐름 버튼
        copcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	AdminCheckCashFlow checkCashFlow = new AdminCheckCashFlow(AdminMainFrame.this, accountStorage);
            	checkCashFlow.setVisible(true);
                setVisible(false);
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

//    // 메인 패널 생성 후, 실행
//    public static void main(String[] args) {
//    	
//        AdminMainFrame adminMainFrame = new AdminMainFrame();
//        adminMainFrame.setVisible(true);
//    }
}