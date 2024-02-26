package user;


import javax.swing.*;

import org.apache.commons.collections4.map.MultiKeyMap;

import main.BankSystemMainFrame;
import system.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("serial")
public class AccountCloseDialog extends JDialog {
    
    private BankSystemMainFrame bankSystemFrame;
    private int mouseX, mouseY;
    public JTextField accountNumTextField;
    private SystemKeyPad virtualKeyPad;
    private String name;
    private String accountNum;
    private int balance;
    private String lth;
    private String accountType;
	private String CertficationbNumber = "";
    private String password;
    private FileSystemDataUpdater fd;
    private ArrayList<Account> list = null;
    private AccountCreditLoanDialog acld;
    
    public AccountCloseDialog(BankSystemMainFrame bankSystemFrame, AccountStorage as) {
    	
    	MultiKeyMap<String, Integer> multikeyMap = as.getMultikeyMap();
    	Map<String, Integer> map = as.getMap();
    	
        this.bankSystemFrame = bankSystemFrame;
        
        this.setSize(1440, 810);
        Font font = new Font("Malgun Gothic", Font.BOLD, 15);

        this.setUndecorated(true); // 다이얼로그의 타이틀바를 없앰
		setResizable(false); // 사용자가 창의 크기를 변경하지 못하게 설정
		setUndecorated(true); // 프레임의 타이틀바를 제거합니다.
//		setModal(true); //인증번호창을 모달로 설정하여 인증번호가 뜬상태에서 사용자가 부모창을 사용못하게합니다.
		setLocationRelativeTo(null); // 창이 가운대로 뜨게 설정합니다.	}
        
        // 창을 화면 중앙으로 띄우기
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int leftTopX = centerPoint.x - this.getWidth() / 2;
        int leftTopY = centerPoint.y - this.getHeight() / 2;
        this.setLocation(leftTopX, leftTopY);
        
        int number = 0;
		
		for (int i = 1; i <= 4; i++) {
			number = (int)(Math.random()*10);
			CertficationbNumber += String.valueOf(number);
		}
        
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
        JPanel accountClosePanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		  super.paintComponent(g);
                  ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                  g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
        	}
        };
        
        accountClosePanel.setLayout(null);
        accountClosePanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountClosePanel.setVisible(true); // 화면에 보이도록 설정
//        accountClosePanel.setBackground(Color.red);
                
        // accountSearchingPanel에 계좌 관리 버튼 생성 및 추가      
        JLabel accountCloseTitle = new JLabel("계좌 해지 ");
        accountCloseTitle.setBounds(650, 50, 200, 200); // (x, y, width, height)
        accountCloseTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 40)); // 폰트 크기를 20으로 설정
        accountClosePanel.add(accountCloseTitle);
        this.add(accountClosePanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountNumLabel = new JLabel("계좌번호: ");
        accountNumLabel.setBounds(300, 300, 120, 30); // (x, y, width, height)
        accountNumLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountClosePanel.add(accountNumLabel);
        this.add(accountClosePanel);

        accountNumTextField = new JTextField(5);
        accountNumTextField.setBounds(400, 300, 200, 30); // (x, y, width, height)
        accountNumTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountClosePanel.add(accountNumTextField);
        this.add(accountClosePanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel passwordLabel = new JLabel("비밀번호: ");
        passwordLabel.setBounds(300, 500, 120, 30); // (x, y, width, height)
        passwordLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountClosePanel.add(passwordLabel);
        this.add(accountClosePanel);

        JTextField passwordTextField = new JTextField(5);
        passwordTextField.setBounds(400, 500, 200, 30); // (x, y, width, height)
        passwordTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountClosePanel.add(passwordTextField);
        this.add(accountClosePanel);


        
        // accountSearchingResultPanel
        JPanel accountCloseResultPanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		  super.paintComponent(g);
                  ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                  g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
        	}
        };
        accountCloseResultPanel.setLayout(null);
        accountCloseResultPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        accountCloseResultPanel.setVisible(true); // 화면에 보이도록 설정
        this.add(accountCloseResultPanel);
//        accountCloseResultPanel.setBackground(Color.blue);
        
        // accountSearchingPanel에 계좌 관리 버튼 생성 및 추가      
        JLabel accountCloseResultTitle = new JLabel("계좌 해지 ");
        accountCloseResultTitle.setBounds(650, 50, 200, 200); // (x, y, width, height)
        accountCloseResultTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 40)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(accountCloseResultTitle);
        this.add(accountCloseResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel NameResultLabel = new JLabel("이름: ");
        NameResultLabel.setBounds(300, 300, 300, 30); // (x, y, width, height)
        NameResultLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(NameResultLabel);
        this.add(accountCloseResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountNumResultLabel = new JLabel("계좌번호: ");
        accountNumResultLabel.setBounds(300, 350, 300, 30); // (x, y, width, height)
        accountNumResultLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(accountNumResultLabel);
        this.add(accountCloseResultPanel);


        
        // 이름 라벨과 텍스트 필드
        JLabel balanceLabel = new JLabel("잔액: ");
        balanceLabel.setBounds(300, 400, 300, 30); // (x, y, width, height)
        balanceLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(balanceLabel);
        this.add(accountCloseResultPanel);


        // 이름 라벨과 텍스트 필드
        JLabel lthLabel = new JLabel("마지막 거래내역: "); //  lth == last transaction history
        lthLabel.setBounds(300, 450, 300, 30); // (x, y, width, height)
        lthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(lthLabel);
        this.add(accountCloseResultPanel);
        
        // 이름 라벨과 텍스트 필드
        JLabel accountTypeLabel= new JLabel("계좌 유형: ");
        accountTypeLabel.setBounds(300, 500, 300, 30); // (x, y, width, height)
        accountTypeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(accountTypeLabel);
        this.add(accountCloseResultPanel);
        

        JLabel accountCloseResultPhrases = new JLabel("위 계좌를 해지하시겠습니까?");
        accountCloseResultPhrases.setBounds(600, 600, 1000, 200); // (x, y, width, height)
        accountCloseResultPhrases.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        accountCloseResultPanel.add(accountCloseResultPhrases);
        this.add(accountCloseResultPanel);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton1Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton1 = new JButton(confirmButton1Image); // tc는 transaction cancellation을 의미
        confirmButton1.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton1.setBounds(0, 730, 150, 80);
        this.add(confirmButton1);
        confirmButton1.setVisible(true);
        
        // backgroundPanel에 계좌 관리 버튼 생성 및 추가
        ImageIcon tcButtonImage = new ImageIcon("이미지\\버튼\\취소.PNG"); // 절대경로
        JButton tcButton = new JButton(tcButtonImage); // tc는 transaction cancellation을 의미
        tcButton.setFont(font);
        tcButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        this.add(tcButton);
        tcButton.setVisible(true);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton2Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton2 = new JButton(confirmButton2Image); // tc는 transaction cancellation을 의미
        confirmButton2.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton2.setBounds(0, 730, 150, 80);
        this.add(confirmButton2);
        confirmButton2.setVisible(false);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton3Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton3 = new JButton(confirmButton3Image); // tc는 transaction cancellation을 의미
        confirmButton3.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton3.setBounds(0, 730, 150, 80);
        this.add(confirmButton3);
        confirmButton3.setVisible(false);
        
		JPanel CertficationbNumberPanel = new JPanel();
		CertficationbNumberPanel.setLayout(new BorderLayout());
		CertficationbNumberPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
		CertficationbNumberPanel.setVisible(false);
//		CertficationbNumberPanel.setBackground(Color.black);
		
        // 인증번호 출력 라벨부분
        JLabel CertficationbNumberLabel = new JLabel(CertficationbNumber);
        CertficationbNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        CertficationbNumberLabel.setFont(font);
        CertficationbNumberPanel.add(CertficationbNumberLabel,BorderLayout.CENTER);
        this.add(CertficationbNumberPanel);
        
        // ~초후 돌아갑니다 부분
        JLabel returnLabel = new JLabel();
		returnLabel.setFont(font);
        CertficationbNumberPanel.add(returnLabel,BorderLayout.SOUTH);
        this.add(CertficationbNumberPanel);
        
        
        
        JPanel CertficationbNumberResultPanel = new JPanel(){
        	protected void paintComponent(Graphics g) 
        	{
        		  super.paintComponent(g);
                  ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                  g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
        	}
        };
        CertficationbNumberResultPanel.setLayout(null);
        CertficationbNumberResultPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
		CertficationbNumberResultPanel.setVisible(false);
		
        JLabel CertficationbNumberResultLabel = new JLabel("인증번호: ");
        CertficationbNumberResultLabel.setBounds(400, 400, 120, 30); // (x, y, width, height)
        CertficationbNumberResultLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        CertficationbNumberResultPanel.add(CertficationbNumberResultLabel);
        this.add(CertficationbNumberResultPanel);
//        CertficationbNumberResultPanel.setBackground(Color.DARK_GRAY);

        JTextField CertficationbNumberResultTextField = new JTextField(5);
        CertficationbNumberResultTextField.setBounds(500, 400, 200, 30); // (x, y, width, height)
        CertficationbNumberResultTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        CertficationbNumberResultPanel.add(CertficationbNumberResultTextField);
        this.add(CertficationbNumberResultPanel);
        
        JPanel closeAmountOrganizePanel = new JPanel(){
        	protected void paintComponent(Graphics g) 
        	{
        		  super.paintComponent(g);
                  ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                  g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
        	}
        };
        closeAmountOrganizePanel.setLayout(null);
        closeAmountOrganizePanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        closeAmountOrganizePanel.setVisible(false);
        
        JPanel closeFinishPanel = new JPanel(){
        	protected void paintComponent(Graphics g) 
        	{
        		  super.paintComponent(g);
                  ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                  g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
        	}
        };
        closeFinishPanel.setLayout(null);
        closeFinishPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        closeFinishPanel.setVisible(false);
        
        JLabel closeAmountOrganizeResultTitle = new JLabel("계좌 해지 ");
        closeAmountOrganizeResultTitle.setBounds(650, 50, 200, 200); // (x, y, width, height)
        closeAmountOrganizeResultTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 35)); // 폰트 크기를 20으로 설정
        closeAmountOrganizePanel.add(closeAmountOrganizeResultTitle);
        this.add(closeAmountOrganizePanel);
        
        JLabel closeAmountOrganizeResultMent = new JLabel("잔액을 정리해주세요 ");
        closeAmountOrganizeResultMent.setBounds(570, 550, 500, 200); // (x, y, width, height)
        closeAmountOrganizeResultMent.setFont(new Font("Malgun Gothic", Font.BOLD, 35)); // 폰트 크기를 20으로 설정
        closeAmountOrganizePanel.add(closeAmountOrganizeResultMent);
        
        
        
//        closeFinishPanel.setBackground(Color.green);
        
        JLabel closeFinishMentLabel= new JLabel("계좌가 해지되었습니다. ");
        closeFinishMentLabel.setBounds(600, 300, 1000, 200); // (x, y, width, height)
        closeFinishMentLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20)); // 폰트 크기를 20으로 설정
        closeFinishPanel.add(closeFinishMentLabel);
        this.add(closeFinishPanel);
        
        // 확인 버튼 생성 및 설정
        ImageIcon confirmButton4Image = new ImageIcon("이미지\\버튼\\확인.PNG"); // 절대경로
        JButton confirmButton4 = new JButton(confirmButton4Image); // tc는 transaction cancellation을 의미
        confirmButton4.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton4.setBounds(0, 730, 150, 80);
        closeFinishPanel.add(confirmButton4);
        this.add(closeFinishPanel);
        confirmButton4.setVisible(false);
        

        
        tcButton.addActionListener(new ActionListener() {
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
        

        
        // accountClosePanel 에서 확인 버튼을 눌렀을때
        confirmButton1.addActionListener(new ActionListener() {
            
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
        		String accountNum = accountNumTextField.getText(); // 계좌 번호를 입력받아 계좌유형을 검사하기 위해 계좌 번호를 저장해놓을 변수
        		
            	if (multikeyMap.get(accountNum, password) == null ) {
					setVisible(false);
                    bankSystemFrame.setVisible(true);
                    bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    return;
				} else {
					accountCloseResultPanel.setVisible(true);
            		confirmButton2.setVisible(true);
            		accountClosePanel.setVisible(false);
            		confirmButton1.setVisible(false);
            		
            		if (accountNum.length() >= 3) {
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
        
        confirmButton3.addActionListener(new ActionListener() {
            
        	@Override
            public void actionPerformed(ActionEvent e) {
					setVisible(false);
                    bankSystemFrame.setVisible(true);
                    bankSystemFrame.getBackgroundPanel().setVisible(true);
                    bankSystemFrame.getAccountManagementPanel().setVisible(false);
                    bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                    return;
            }
        });
        
   
       
     // accountCloseResultPanel에서 확인 버튼을 눌렀을때
        confirmButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tcButton.setVisible(false);
                if (virtualKeyPad != null)
                    virtualKeyPad.setVisible(false);
                confirmButton2.setVisible(false);
                accountCloseResultPanel.setVisible(false);
                CertficationbNumberPanel.setVisible(true); // CertficationbNumberPanel을 보이도록 설정

//                System.out.println("tlqkf");
                int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        timeLeft[0]--; // 남은 시간 감소
                        returnLabel.setText(timeLeft[0] + "초 후에 이 창이 종료됩니다."); // 레이블 텍스트 업데이트

                        if (timeLeft[0] == 0) { // 남은 시간이 0이면
                        	CertficationbNumberResultPanel.setVisible(true); // CertficationbNumberResultPanel을 보이도록 설정
                        	CertficationbNumberPanel.setVisible(false);
                        	confirmButton3.setVisible(true);
                        	tcButton.setVisible(true);

                            ((Timer) e.getSource()).stop(); // 타이머 중지
                        }
                        
                    }
                });
                timer.start();
            }
        });
        confirmButton4.addActionListener(new ActionListener() {
            
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
        		
                setVisible(false);
                bankSystemFrame.setVisible(true);
                bankSystemFrame.getBackgroundPanel().setVisible(true);
                bankSystemFrame.getAccountManagementPanel().setVisible(false);
                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
                return;
        		
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
        
        CertficationbNumberResultTextField.addMouseListener(new MouseListener() {
			
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
				CertficationbNumberResultTextField.setText("");
				virtualKeyPad = new SystemKeyPad(4);
                virtualKeyPad.setInputNumberTextField(CertficationbNumberResultTextField);
                virtualKeyPad.setVisible(true);
                
 
                
				if (CertficationbNumber.equals(CertficationbNumberResultTextField.getText())) {
	                if (list.get(multikeyMap.get(accountNumTextField.getText(), password)).getBalance() == 0 ) {
						closeFinishPanel.setVisible(true);
						closeAmountOrganizePanel.setVisible(false);
						CertficationbNumberResultPanel.setVisible(false);
						
						fd = new FileSystemDataUpdater(list.get(multikeyMap.get(accountNumTextField.getText(), password)), multikeyMap.get(accountNumTextField.getText(), password)); 
						String accountNumber = accountNumTextField.getText();
						list.remove(multikeyMap.get(accountNumber, password));
						multikeyMap.clear();
						map.clear();
						
						fd.removeAccount();
						int index = 0;
						for (Account account : list) {
							multikeyMap.put(accountNumber,password, index);
							map.put(accountNumber, index);
							index++;
						}
						
						
					} else if (list.get(multikeyMap.get(accountNumTextField.getText(), password)).getBalance() > 0 ||list.get(multikeyMap.get(accountNumTextField.getText(), password)).getPrincipal() > 0 ) {
						closeAmountOrganizePanel.setVisible(true);
						CertficationbNumberResultPanel.setVisible(false);
					
					}
					

				} else  {
					setVisible(false);
					bankSystemFrame.setVisible(true);
					bankSystemFrame.getBackgroundPanel().setVisible(true);
					bankSystemFrame.getAccountManagementPanel().setVisible(false);
					bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					return;
				}
				
                
                // 키보드로 입력 못받게 하는 이벤트 처리
				CertficationbNumberResultTextField.addKeyListener(new KeyAdapter() {
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
        


        
    }    
}
