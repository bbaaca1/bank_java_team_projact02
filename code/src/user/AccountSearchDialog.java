package user;


import javax.swing.*;

import org.apache.commons.collections4.map.MultiKeyMap;

import main.BankSystemMainFrame;
import system.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class AccountSearchDialog extends JDialog {
    
    private BankSystemMainFrame bankSystemFrame;
    private int mouseX, mouseY;
    public JTextField accountNumTextField;
    private SystemKeyPad virtualKeyPad;
    private String name;
    private String accountNum;
    private int balance;
    private String lth;
    private String accountType;
    private ArrayList<Account> list = null;
    private String password;
    
    public AccountSearchDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) {
    	
    	MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap();
    	
    	
    	this.bankSystemFrame = bankSystemFrame;
        
        this.setSize(1440, 810);
        Font font = new Font("Malgun Gothic", Font.BOLD, 15);

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

        // 배경 패널 생성 및 설정
        JPanel accountSearchingPanel = new JPanel() 
        {
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountSearchingPanel.setLayout(null);
        accountSearchingPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountSearchingPanel.setVisible(true); // 화면에 보이도록 설정
                
        // accountSearchingPanel에 계좌 관리 버튼 생성 및 추가   
        JLabel accountSearchTitle = new JLabel("계좌 조회 ");
        accountSearchTitle.setBounds(650, 50, 200, 200); // (x, y, width, height)
        accountSearchTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 40)); // 폰트 크기를 20으로 설정
        accountSearchingPanel.add(accountSearchTitle);
        
        
        this.add(accountSearchingPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountNumLabel = new JLabel("계좌번호: ");
        accountNumLabel.setBounds(300, 300, 120, 30); // (x, y, width, height)
        accountNumLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingPanel.add(accountNumLabel);
        this.add(accountSearchingPanel);

        accountNumTextField = new JTextField(5);
        accountNumTextField.setBounds(400, 300, 200, 30); // (x, y, width, height)
        accountNumTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingPanel.add(accountNumTextField);
        this.add(accountSearchingPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel passwordLabel = new JLabel("비밀번호: ");
        passwordLabel.setBounds(300, 500, 120, 30); // (x, y, width, height)
        passwordLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingPanel.add(passwordLabel);
        this.add(accountSearchingPanel);

        JTextField passwordTextField = new JTextField(5);
        passwordTextField.setBounds(400, 500, 200, 30); // (x, y, width, height)
        passwordTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingPanel.add(passwordTextField);
        this.add(accountSearchingPanel);


        
        // accountSearchingResultPanel
        JPanel accountSearchingResultPanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountSearchingResultPanel.setLayout(null);
        accountSearchingResultPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountSearchingResultPanel.setVisible(true); // 화면에 보이도록 설정
        this.add(accountSearchingResultPanel);
        
        // accountSearchingPanel에 계좌 관리 버튼 생성 및 추가      
        JLabel accountSearchResultTitle = new JLabel("계좌 조회 ");
        accountSearchResultTitle.setBounds(650, 50, 200, 200); // (x, y, width, height)
        accountSearchResultTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 40)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(accountSearchResultTitle);
        this.add(accountSearchingResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel NameResultLabel = new JLabel("이름: ");
        NameResultLabel.setBounds(300, 300, 300, 30); // (x, y, width, height)
        NameResultLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(NameResultLabel);
        this.add(accountSearchingResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountNumResultLabel = new JLabel("계좌번호: ");
        accountNumResultLabel.setBounds(300, 350, 300, 30); // (x, y, width, height)
        accountNumResultLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(accountNumResultLabel);
        this.add(accountSearchingResultPanel);


        
        // 이름 라벨과 텍스트 필드
        JLabel balanceLabel = new JLabel("잔액: ");
        balanceLabel.setBounds(300, 400, 300, 30); // (x, y, width, height)
        balanceLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(balanceLabel);
        this.add(accountSearchingResultPanel);


        // 이름 라벨과 텍스트 필드
        JLabel lthLabel = new JLabel("마지막 거래내역: "); //  lth == last transaction history
        lthLabel.setBounds(300, 450, 300, 30); // (x, y, width, height)
        lthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(lthLabel);
        this.add(accountSearchingResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountTypeLabel= new JLabel("계좌 유형: ");
        accountTypeLabel.setBounds(300, 500, 300, 30); // (x, y, width, height)
        accountTypeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountSearchingResultPanel.add(accountTypeLabel);
        this.add(accountSearchingResultPanel);


        
        
        JPanel cancelCheckPanel = new JPanel();
        cancelCheckPanel.setLayout(null);
        cancelCheckPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        cancelCheckPanel.setVisible(true);
        this.add(cancelCheckPanel);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton1Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton1 = new JButton(confirmButton1Image); // tc는 transaction cancellation을 의미
        confirmButton1.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton1.setBounds(1290, 730, 150, 80);
        cancelCheckPanel.add(confirmButton1);
        this.add(cancelCheckPanel);
        
        // backgroundPanel에 계좌 관리 버튼 생성 및 추가
        ImageIcon tcButtonImage = new ImageIcon("이미지\\버튼\\취소.PNG"); // 절대경로
        JButton tcButton = new JButton(tcButtonImage); // tc는 transaction cancellation을 의미
        tcButton.setFont(font);
        tcButton.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        cancelCheckPanel.add(tcButton);
        this.add(cancelCheckPanel);
        accountSearchingResultPanel.setVisible(false);
        
        
        JPanel cancelCheckPanel2 = new JPanel();
        cancelCheckPanel2.setLayout(null);
        cancelCheckPanel2.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        cancelCheckPanel2.setVisible(true);
        this.add(cancelCheckPanel2);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton2Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton2 = new JButton(confirmButton2Image); // tc는 transaction cancellation을 의미
        confirmButton2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton2.setBounds(0, 730, 150, 80);
        cancelCheckPanel2.add(confirmButton2);
        this.add(cancelCheckPanel2);
//        this.add(accountSearchingResultPanel);

        
        tcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountTransferFrame을 숨김
                setVisible(false);
                bankSystemFrame.setVisible(true);
                bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            }
        }); 
        
        // confirmButton1을 눌렀을 때 이벤트 처리
        confirmButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String accountNum = accountNumTextField.getText(); // 계좌 번호를 입력받아 계좌유형을 검사하기 위해 계좌 번호를 저장해놓을 변수
            	
            	if (multikeyMap.get(accountNum, password) == null ) {
					System.out.println(accountNum);
					System.out.println(password);
					setVisible(false);
                    bankSystemFrame.setVisible(true);
                    bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    return;
				} else {
//					System.out.println(123123123);
	            	accountSearchingResultPanel.setVisible(true);
	            	cancelCheckPanel2.setVisible(true);
	            	accountSearchingPanel.setVisible(false);
	            	cancelCheckPanel.setVisible(false);
	            	
	            	if (accountNum.length() >= 3) { // 계좌 유형 구분하는 조건문
						String threeDigits = accountNum.substring(0,3);
						if (threeDigits.equals("012")) {
							list = as.getDepositAccountList();
							accountTypeLabel.setText("계좌 유형: 보통 예금");
						} else if (threeDigits.equals("021")) {
							list = as.getSavingAccountList();
							accountTypeLabel.setText("계좌 유형: 정기 적금");
						} else if (threeDigits.equals("011")) {
							list = as.getTimeAccountList();
							accountTypeLabel.setText("계좌 유형: 정기 예금");
						} else if (threeDigits.equals("022")) {
							list = as.getFreeAccountList();
							accountTypeLabel.setText("계좌 유형: 자유 적금");

						}
						
		            	NameResultLabel.setText("이름: " + list.get(multikeyMap.get(accountNumTextField.getText(), password)).getName());
		            	accountNumResultLabel.setText("계좌번호: "+list.get(multikeyMap.get(accountNumTextField.getText(), password)).getAccountNumber());
		            	balanceLabel.setText("금액: "+list.get(multikeyMap.get(accountNumTextField.getText(), password)).getBalance());
		            	lthLabel.setText("마지막 거래내역: "+list.get(multikeyMap.get(accountNumTextField.getText(), password)).getLastTradingDate());
				}
            	

            	}
      	
            }
        });
        
        	accountNumTextField.addMouseListener(new MouseListener() {
			
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
				accountNumTextField.setText("");
				virtualKeyPad = new SystemKeyPad(12);
                virtualKeyPad.setInputNumberTextField(accountNumTextField);
                virtualKeyPad.setVisible(true);
                
				if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }

                
                // 키보드로 입력 못받게 하는 이벤트 처리
                accountNumTextField.addKeyListener(new KeyAdapter() {
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
				

             
			}
		});
        
        
        passwordTextField.addMouseListener(new MouseListener() {
			
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
				
			  passwordTextField.setText("");
  			  virtualKeyPad = new SystemKeyPad(true);
  			  passwordTextField.setText(null);
  			  virtualKeyPad.setVisible(true);  			  
  			  passwordTextField.setText("****");
  			  password = virtualKeyPad.getNumber();

                
				if (virtualKeyPad != null) { // 이미 생성된 virtualKeyPad가 있다면 숨김
                    virtualKeyPad.setVisible(false);
                }
				

                
                // 키보드로 입력 못받게 하는 이벤트 처리
				passwordTextField.addKeyListener(new KeyAdapter() {
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
				

             
			}
		});
        
        confirmButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 클릭했을 때 BankSystemFrame 객체의 backgroundPanel을 보이게 하고, AccountTransferFrame을 숨김
                setVisible(false);
                bankSystemFrame.setVisible(true);
                bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            }
        }); 
        
    }    
}
