package user;


import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import main.BankSystemMainFrame;
import system.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class AccountCreateDialog extends JDialog {
	
    private SystemKeyPad keyPad;
    private SystemTextDialog textDialog;
    private BankSystemMainFrame bankSystemFrame;
    
    private int mouseX, mouseY;
    
    private String name;
    private String ssn;
    private String homeAddress;
    private String phoneNumber;
    private String certficationbNumber;
    private String accountType;
    private String accountPasswd;
    private String accountCheckPasswd;
    private String accountNumber;
    private LocalDate accountMaturity;
    private LocalDate accountDueDate;
    private static AccountStorage accountStorage;
    private FileSystemDataUpdater fileSystemDataUpdater;
    
    
    public AccountCreateDialog(BankSystemMainFrame bankSystemFrame,AccountStorage as) {
        
    	accountStorage = as;    	
    	
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
                // 창 이동 가능하게 설정
                Point point = getLocation();
                int deltaX = e.getX() - mouseX;
                int deltaY = e.getY() - mouseY;
                setLocation(point.x + deltaX, point.y + deltaY);
            }
        });
        
        // 배경 패널 생성 및 설정 부분
        JPanel accountCreatePanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountCreatePanel.setLayout(null);
        accountCreatePanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
//        accountCreatePanel.setBackground(Color.red);
        accountCreatePanel.setVisible(true); // 화면에 보이도록 설정
        
        // 계좌 생성 라벨 생성 및 추가 부분
        JLabel accountCreateLabel = new JLabel("계좌 생성");
        accountCreateLabel.setBounds(710, 120, 100, 40);
        accountCreateLabel.setFont(font);
        accountCreatePanel.add(accountCreateLabel);
        
        // 이름 입력 라벨과 이름 입력 텍스트 필드 생성 및 추가 부분
        JLabel accountNameLabel = new JLabel("이름: ");
        accountNameLabel.setBounds(50, 150, 150, 80);
        accountNameLabel.setFont(font);
        accountCreatePanel.add(accountNameLabel);
        
        JTextField nameTextField = new JTextField(0);
        nameTextField.setFont(font);
        nameTextField.setBounds(92, 172, 110, 40);
        
        // 이름 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        nameTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 이름 텍스트 필드 클릭시 다이얼로그 박스를 띄워서 이름을 입력하게 합니다
				textDialog = new SystemTextDialog(true);
				textDialog.setVisible(true);
				
				nameTextField.setText(textDialog.getText());
				name = nameTextField.getText();
			}
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        nameTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
         
        accountCreatePanel.add(nameTextField);
        
        // 주민번호 라벨과 주민번호 입력 텍스트 필드 생성 및 추가 부분
        JLabel accountSsnLabel = new JLabel("주민번호: ");
        accountSsnLabel.setBounds(10, 250, 100, 80);
        accountSsnLabel.setFont(font);
        accountCreatePanel.add(accountSsnLabel);
        
        JTextField ssnTextField = new JTextField(13);
        ssnTextField.setFont(font);
        ssnTextField.setBounds(92, 272, 300, 40);
        accountCreatePanel.add(ssnTextField);
        
        // 주민번호 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        ssnTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 주민번호 텍스트 필드 클릭시 키패드를 띄워서 숫자를 입력하게 합니다
				keyPad = new SystemKeyPad(13);
				keyPad.setInputNumberTextField(ssnTextField);
		        keyPad.setVisible(true);
		        
		        ssnTextField.setText(keyPad.getNumber());
		        ssn = ssnTextField.getText();

			}
			
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        ssnTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
        
        // 주소 라벨과 주소 입력 텍스트필드 추가 부분  
        JLabel accountHomeAddressLabel = new JLabel("주소: ");
        accountHomeAddressLabel.setBounds(45, 350, 100, 80);
        accountHomeAddressLabel.setFont(font);
        accountCreatePanel.add(accountHomeAddressLabel);
        
        JTextField homeAddressTextField = new JTextField();
        homeAddressTextField.setFont(font);
        homeAddressTextField.setBounds(92, 372, 300, 40);
        accountCreatePanel.add(homeAddressTextField);
        // 주소 텍스트 필드 마우스 이벤트
        homeAddressTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textDialog = new SystemTextDialog();
				textDialog.setVisible(true);
						
				String input = textDialog.getText();
				homeAddressTextField.setText(input);
		        homeAddress = homeAddressTextField.getText();

			}
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        homeAddressTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
        
        // 전화번호 라벨과 전화번호 입력 텍스트필드 추가 부분  
        JLabel accountPhoneNumberLabel = new JLabel("전화번호: ");
        accountPhoneNumberLabel.setBounds(10, 450, 100, 80);
        accountPhoneNumberLabel.setFont(font);
        accountCreatePanel.add(accountPhoneNumberLabel);
        
        JTextField phoneNumberTextField = new JTextField(11);
        phoneNumberTextField.setFont(font);
        phoneNumberTextField.setBounds(92, 472, 300, 40);
        accountCreatePanel.add(phoneNumberTextField);
        
        // 전화번호 텍스트 필드 마우스 이벤트
        phoneNumberTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keyPad = new SystemKeyPad(11);
				keyPad.setInputNumberTextField(phoneNumberTextField);
		        keyPad.setVisible(true);
		        
				phoneNumberTextField.setText(keyPad.getNumber());
		        phoneNumber = phoneNumberTextField.getText();

			}
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        // 텍스트 필드에 직접입력을 막는 액션
        phoneNumberTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }
            }
        });
        
        
        // 인증번호 부분
        JPanel accountCreateVerifyPanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountCreateVerifyPanel.setLayout(null);
//        accountCreateVerifyPanel.setBackground(Color.blue);
        accountCreateVerifyPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
//        accountCreateVerifyPanel.add(accountCreateLabel);
        
        // 이전 입력했던 이름 라벨
        JLabel nameLabel = new JLabel();
        nameLabel.setBounds(50, 150, 200, 80);
        nameLabel.setFont(font);
        accountCreateVerifyPanel.add(nameLabel);
        
        // 이전 입력했던 주민번호 라벨 부부
        JLabel ssnLabel = new JLabel();
        ssnLabel.setBounds(10, 250, 500, 80);
        ssnLabel.setFont(font);
        accountCreateVerifyPanel.add(ssnLabel);
        
        // 이전 입력했던 주소 라벨 부분
        JLabel homeAddressLabel = new JLabel();
        homeAddressLabel.setBounds(45, 350, 900, 80);
        homeAddressLabel.setFont(font);
        accountCreateVerifyPanel.add(homeAddressLabel);
        
        // 이전 입력했던 전화번호 라벨 부분
        JLabel phoneNumberLabel = new JLabel();
        phoneNumberLabel.setBounds(10, 450, 500, 80);
        phoneNumberLabel.setFont(font);
        accountCreateVerifyPanel.add(phoneNumberLabel);
        
        // 인증번호 라벨 및 텍스트 필드 부분
        JLabel certficationbNumberLabel = new JLabel("인증번호: ");
        certficationbNumberLabel.setBounds(10, 550, 100, 80);
        certficationbNumberLabel.setFont(font);
        accountCreateVerifyPanel.add(certficationbNumberLabel);
        
        JTextField certficationbNumberTextField = new JTextField(11);
        certficationbNumberTextField.setFont(font);
        certficationbNumberTextField.setBounds(112, 572, 200, 40);
        accountCreateVerifyPanel.add(certficationbNumberTextField);
        
        certficationbNumberTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keyPad = new SystemKeyPad();
				keyPad.setInputNumberTextField(certficationbNumberTextField);
		        keyPad.setVisible(true);
		        
		        certficationbNumberTextField.setText(keyPad.getNumber());
			}
			
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			

		});
        
        certficationbNumberTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }
            }
        });
        
        
        // 세번째 패널 계좌 유형 입력 부분
        JPanel accountTypePanel = new JPanel(){
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountTypePanel.setLayout(null);
        accountTypePanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        
        // 창 메인 제목 라벨 부분
        JLabel typePanelMainLabel = new JLabel("계좌 유형 선택");
        typePanelMainLabel.setBounds(710, 120, 100, 40);
        typePanelMainLabel.setFont(font);
        accountTypePanel.add(typePanelMainLabel);
        
        // 예금 라벨 부분
        JLabel depositLabel = new JLabel("예금: ");
        depositLabel.setBounds(80, 300, 100, 80);
        depositLabel.setFont(font);
        accountTypePanel.add(depositLabel);
        
        // 일반 예금 버튼 부분
        JButton depositAccountButton = new JButton("일반 예금");
        depositAccountButton.setBounds(120, 300, 160, 80);
        depositAccountButton.setFont(font);
        accountTypePanel.add(depositAccountButton);
        
        //정기 예금 버튼 부분
        JButton TimeAccounttButton = new JButton("정기 예금");
        TimeAccounttButton.setBounds(320, 300, 160, 80);
        TimeAccounttButton.setFont(font);
        accountTypePanel.add(TimeAccounttButton);
        
        
        // 적금 라벨 부분
        JLabel savingLabel = new JLabel("적금: ");
        savingLabel.setBounds(80, 420, 100, 80);
        savingLabel.setFont(font);
        accountTypePanel.add(savingLabel);
        
        // 정기 적금 버튼 부분
        JButton savingAccountButton = new JButton("정기 적금");
        savingAccountButton.setBounds(120, 420, 160, 80);
        savingAccountButton.setFont(font);
        accountTypePanel.add(savingAccountButton);
        

        
        // 자유 적금 버튼 부분
        JButton freeAccounttButton = new JButton("자유 적금");
        freeAccounttButton.setBounds(320, 420, 160, 80);
        freeAccounttButton.setFont(font);
        accountTypePanel.add(freeAccounttButton);
        
        
        // 버튼클릭시 이벤트 처리 부분
        //일반 예금 버튼 이벤트 처리부분
        depositAccountButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				accountType = "일반 예금"; // 계좌 분류 입력
				accountNumber = "012068"; // 계좌 번호 앞 6자리 입력
				
				// 선택된 버튼의 색갈을 진하게 하고 나머지를 밝게합니다.
				depositAccountButton.setBackground(Color.LIGHT_GRAY);
				savingAccountButton.setBackground(null);
				freeAccounttButton.setBackground(null);
				TimeAccounttButton.setBackground(null);
			}
		});
        
        // 정기 예금 버튼 이벤트 처리부분
        TimeAccounttButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				accountType = "정기 예금"; // 계좌 분류 입력
				accountNumber = "011068"; // 계좌 번호 앞 6자리 입력
				
				// 선택된 버튼의 색갈을 진하게 하고 나머지를 밝게합니다.
				TimeAccounttButton.setBackground(Color.LIGHT_GRAY);
				depositAccountButton.setBackground(null);
				savingAccountButton.setBackground(null);
				freeAccounttButton.setBackground(null);
			}
		});
        
        // 정기 적금 계좌 버튼 이벤트 부분
        savingAccountButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				accountType = "정기 적금"; // 계좌 분류 입력
				accountNumber = "021068"; // 계좌 번호 앞 6자리 입력
				
				// 선택된 버튼의 색갈을 진하게 하고 나머지를 밝게합니다.
				savingAccountButton.setBackground(Color.LIGHT_GRAY);
				freeAccounttButton.setBackground(null);
				depositAccountButton.setBackground(null);
				TimeAccounttButton.setBackground(null);
			}
		});
        
        // 자유 적금 버튼 이벤트 부분
        freeAccounttButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				accountType = "자유 적금"; // 계좌 분류 입력
				accountNumber = "022068"; // 계좌 번호 앞 6자리 입력
				
				// 선택된 버튼의 색갈을 진하게 하고 나머지를 밝게합니다.
				freeAccounttButton.setBackground(Color.LIGHT_GRAY);
				depositAccountButton.setBackground(null);
				savingAccountButton.setBackground(null);
				TimeAccounttButton.setBackground(null);
			}
		});
        
        // 네번째 패널 계좌 비밀번호 입력 부분
        
        // 배경 패널 생성 및 설정 부분
        JPanel accountPasswdPanel = new JPanel(){
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        accountPasswdPanel.setLayout(null);
        accountPasswdPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        
        // 계좌 생성 라벨 생성 및 추가 부분
        JLabel accountPasswdLabel = new JLabel("비밀번호를 설정해주세요.");
        accountPasswdLabel.setBounds(710, 120, 400, 40);
        accountPasswdLabel.setFont(font);
        accountPasswdPanel.add(accountPasswdLabel);
        
        // 비밀번호 입력 라벨과 비밀번호 입력 텍스트 필드 생성 및 추가 부분
        JLabel accountFirstPasswdLabel = new JLabel("비밀번호: ");
        accountFirstPasswdLabel.setBounds(10, 250, 200, 80);
        accountFirstPasswdLabel.setFont(font);
        accountPasswdPanel.add(accountFirstPasswdLabel);
        
        JTextField accountFirstPasswdTextField = new JTextField(4);
        accountFirstPasswdTextField.setFont(font);
        accountFirstPasswdTextField.setBounds(77, 275, 200, 40);
        accountPasswdPanel.add(accountFirstPasswdTextField);
        
        // 비밀번호 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        accountFirstPasswdTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 비밀번호 확인 텍스트 필드 클릭시 다이얼로그 박스를 띄워서 이름을 입력하게 합니다
				keyPad = new SystemKeyPad();
				keyPad.setInputNumberTextField(new JTextField(4));
		        keyPad.setVisible(true);
				
		        accountFirstPasswdTextField.setText(keyPad.getNumber());
				accountPasswd = accountFirstPasswdTextField.getText();
			}
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        accountFirstPasswdTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
         
        // 비밀번호 확인 라벨과 비밀번호 확인 입력 텍스트 필드 생성 및 추가 부분
        JLabel accountSecondpasswdLabel = new JLabel("비밀번호 확인: ");
        accountSecondpasswdLabel.setBounds(10, 300, 350, 80);
        accountSecondpasswdLabel.setFont(font);
        accountPasswdPanel.add(accountSecondpasswdLabel);
        
        JTextField accountSecondpasswdTextField = new JTextField(4);
        accountSecondpasswdTextField.setFont(font);
        accountSecondpasswdTextField.setBounds(110, 322, 300, 40);
        
        accountPasswdPanel.add(accountSecondpasswdTextField);
        
        // 비밀번호 확인 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        accountSecondpasswdTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 주민번호 텍스트 필드 클릭시 키패드를 띄워서 숫자를 입력하게 합니다
				keyPad = new SystemKeyPad();
				keyPad.setInputNumberTextField(new JTextField(4));
		        keyPad.setVisible(true);
		        
		        accountSecondpasswdTextField.setText(keyPad.getNumber());
		        accountCheckPasswd = accountSecondpasswdTextField.getText();

			}
			
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        accountSecondpasswdTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
        
        // 적금 만기일 입력부분(일반 예금일경우 무시합니다.)
        JLabel accountMaturityLabel = new JLabel("만기일: ");
        accountMaturityLabel.setBounds(10, 350, 350, 80);
        accountMaturityLabel.setFont(font);
        accountPasswdPanel.add(accountMaturityLabel);
        
        JTextField accountMaturityTextField = new JTextField(4);
        accountMaturityTextField.setFont(font);
        accountMaturityTextField.setBounds(110, 372, 300, 40);
        
        accountPasswdPanel.add(accountMaturityTextField);
        
        // 만기일 확인 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        accountMaturityTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 만기일 텍스트 필드 클릭시 키패드를 띄워서 숫자를 입력하게 합니다
				accountMaturityTextField.setText("");
				keyPad = new SystemKeyPad(2);
				keyPad.setInputNumberTextField(accountMaturityTextField);
		        keyPad.setVisible(true);
		        
		        accountMaturityTextField.setText(keyPad.getNumber()+"개월");
		        int maturity = Integer.parseInt(keyPad.getNumber());
		        accountMaturity = LocalDate.now().plusMonths(maturity);
			}
        			
    		// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
        
        accountMaturityTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
        
        // 납기일 라벨 및 텍스트 필드 부분
        JLabel accountDuedateLabel = new JLabel("납기일: ");
        accountDuedateLabel.setBounds(10, 450, 350, 80);
        accountDuedateLabel.setFont(font);
        accountPasswdPanel.add(accountDuedateLabel);
        
        JTextField accountDuedateTextField = new JTextField(2);
        accountDuedateTextField.setFont(font);
        accountDuedateTextField.setBounds(120, 472, 300, 40);
        accountPasswdPanel.add(accountDuedateTextField);
        
        // 납기일 확인 텍스트 필드 마우스 이벤트(클릭시만 사용함)
        accountDuedateTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 납기일 텍스트 필드 클릭시 키패드를 띄워서 숫자를 입력하게 합니다
				accountDuedateTextField.setText("");
				keyPad = new SystemKeyPad(2);
				keyPad.setInputNumberTextField(accountDuedateTextField);
		        keyPad.setVisible(true);
		        
		        accountDuedateTextField.setText("매달 "+keyPad.getNumber()+"일");       		   
		        int dueDate = Integer.parseInt(keyPad.getNumber());
		        accountDueDate = LocalDate.now().plusMonths(1);
		        accountDueDate = LocalDate.of(accountDueDate.getYear(), accountDueDate.getMonth(), dueDate);

			}
    			
			// 나머지는 사용하지 않습니다.
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
	
        
        accountDuedateTextField.addKeyListener(new KeyAdapter() {
        	// 일반 입력 처리
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // 이벤트
            }
            // BACKSPACE 키 처리
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // BACKSPACE 키 이벤트
                }

            }
        });
		
        
        // 계좌 생성 완료 및 계좌 정보를 보여주는 부분
        
        JPanel finishPanel = new JPanel()
        {
        	protected void paintComponent(Graphics g) 
        	{
        		super.paintComponent(g);
                ImageIcon accountSearchingBackground = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
                g.drawImage(accountSearchingBackground.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
            
        };
        finishPanel.setLayout(null);
        finishPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
        
        JLabel accountNumberLabel = new JLabel();
        accountNumberLabel.setBounds(10, 550, 900, 80);
        accountNumberLabel.setFont(font);
        finishPanel.add(accountNumberLabel);
        
        // 확인 및 취소 버튼 부분
        // Panel에 계좌 관리 버튼 생성 및 추가
        ImageIcon cancel = new ImageIcon("이미지\\버튼\\취소.png");
        JButton tcButton = new JButton(cancel); // tc는 transaction cancellation을 의미
        tcButton.setFont(font);
        tcButton.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        
        //취소 버튼 추가 부분
        accountCreatePanel.add(tcButton);
        
        // backgroundPanel에 계좌 관리 버튼 생성 및 추가
        ImageIcon confirm = new ImageIcon("이미지\\버튼\\확인.png");
        JButton confirmButton = new JButton(confirm);
        confirmButton.setFont(font);
        confirmButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountCreatePanel.add(confirmButton);
        
        //  accountCreateVerifyPanel의 확인 버튼 생성 및 추가
        JButton verifyConfirmButton = new JButton(confirm);
        verifyConfirmButton.setFont(font);
        verifyConfirmButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountCreateVerifyPanel.add(verifyConfirmButton);
        
        // accountTypePanel의 확인 버튼 생성 및 추가
        JButton typeConfirmButton = new JButton(confirm);
        typeConfirmButton.setFont(font);
        typeConfirmButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountTypePanel.add(typeConfirmButton);
        
        // accountPasswdPanel의 확인버튼 생성 및 추가
        JButton passwdConfirmButton = new JButton(confirm);
        passwdConfirmButton.setFont(font);
        passwdConfirmButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        accountPasswdPanel.add(passwdConfirmButton);
        
        //
        JButton finsishConfirmButton = new JButton(confirm);
        finsishConfirmButton.setFont(font);
        finsishConfirmButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
        finishPanel.add(finsishConfirmButton);
        
        
        this.add(accountCreatePanel);
        this.add(accountCreateVerifyPanel);
        this.add(accountTypePanel);
        this.add(accountPasswdPanel);
        this.add(finishPanel);
        accountCreateVerifyPanel.setVisible(false);
        accountTypePanel.setVisible(false);
        accountPasswdPanel.setVisible(false);
        finishPanel.setVisible(false);
        // 취소 버튼 액션 이벤트 부분
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
        
        // accountCreatePanel 패널 확인 버튼 액션 이벤트 부분
        confirmButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				// 첫번째 화면에서 확인을 눌렀을때 실행되는 코드들
				accountCreatePanel.setVisible(false);
				accountCreateVerifyPanel.add(tcButton);
				try {
					if (name.isEmpty() || ssn.isEmpty() || homeAddress.isEmpty() || phoneNumber.isEmpty()) {
		                setVisible(false);
		                bankSystemFrame.setVisible(true);
		                bankSystemFrame.getBackgroundPanel().setVisible(true);
		                bankSystemFrame.getAccountManagementPanel().setVisible(false);
		                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
		                return;
					} 
				} catch (Exception error) {
	                setVisible(false);
	                bankSystemFrame.setVisible(true);
	                bankSystemFrame.getBackgroundPanel().setVisible(true);
	                bankSystemFrame.getAccountManagementPanel().setVisible(false);
	                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
	                return;
				}
				

				SystemCertificationNumber systemCertficationbNumber = new SystemCertificationNumber();
		        systemCertficationbNumber.setVisible(true);
		        certficationbNumber = systemCertficationbNumber.getCertficationbNumber();
		        nameLabel.setText("이름 : "+ name);
		        ssnLabel.setText("주민번호 : " + ssn);
		        homeAddressLabel.setText("주소 : "+ homeAddress);
		        phoneNumberLabel.setText("전화번호 : " + phoneNumber);
		        accountCreateVerifyPanel.setVisible(true);
		        
			}
		});
        
        // accountCreateVerifyPanel 패널 확인 버튼 부분
        verifyConfirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 두번째 화면에서 확인을 눌렀을때 실행되는 코드들
//		        accountCreateVerifyPanel.setVisible(false);
//		        accountTypePanel.setVisible(true);
				
				accountTypePanel.add(tcButton);
				try {
					if (!certficationbNumberTextField.getText().equals(certficationbNumber)) {					
		                setVisible(false);
		                bankSystemFrame.setVisible(true);
		                bankSystemFrame.getBackgroundPanel().setVisible(true);
		                bankSystemFrame.getAccountManagementPanel().setVisible(false);
		                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
		                return;
					}
				} catch (NullPointerException n) {
	                setVisible(false);
	                bankSystemFrame.setVisible(true);
	                bankSystemFrame.getBackgroundPanel().setVisible(true);
	                bankSystemFrame.getAccountManagementPanel().setVisible(false);
	                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
	                return;
				}

				
		        accountCreateVerifyPanel.setVisible(false);
		        accountTypePanel.setVisible(true);
			}
		});
        
        // accountTypePanel 패널 확인 버튼 부분
        typeConfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 세번째 화면에서 확인을 눌렀을때 실행되는 코드들
				accountPasswdPanel.add(tcButton);
				try {
					// 만약 계좌유형을 선택안했다면 실행됩니다.
					if (accountType.isEmpty()) {
		                bankSystemFrame.setVisible(true);
		                bankSystemFrame.getBackgroundPanel().setVisible(true);
		                bankSystemFrame.getAccountManagementPanel().setVisible(false);
		                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
						return;
					}
				} catch (NullPointerException n) {
					// 계좌유형을 선택하지 않고 진행하면 실행됩니다.
	                bankSystemFrame.setVisible(true);
	                bankSystemFrame.getBackgroundPanel().setVisible(true);
	                bankSystemFrame.getAccountManagementPanel().setVisible(false);
	                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
					return;
				}

				accountTypePanel.setVisible(false);
				
				if (accountType.equals("일반 예금")) {
					// 일반 예금일 경우 만기일,납금일 라벨과 텍스트필드를 삭제 
					accountPasswdPanel.remove(accountMaturityLabel);
					accountPasswdPanel.remove(accountMaturityTextField);
					accountPasswdPanel.remove(accountDuedateLabel);
					accountPasswdPanel.remove(accountDuedateTextField);
					
				} else if(accountType.equals("자유 적금")) {
					// 자유 적금일때 납기일을 안받음
					accountPasswdPanel.remove(accountDuedateLabel);
					accountPasswdPanel.remove(accountDuedateTextField);
					accountDueDate= LocalDate.now();
			        accountDueDate = LocalDate.of(accountDueDate.getYear(), accountDueDate.getMonth(), 1);
				}
				
				accountPasswdPanel.setVisible(true);
				
			}
		});
        
        
        // accountPasswdPanel 패널 확인 버튼 부분 계좌번호를 설정함
        passwdConfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 네번째 화면에서 확인을 눌렀을때 실행되는 코드들
				try {
					// 만약 입력된 계좌 비밀번호와 확인한 계좌 비밀번호가 같다면 실행됩니다.
					if (!accountPasswd.equals(accountCheckPasswd)) {
		                bankSystemFrame.setVisible(true);
		                bankSystemFrame.getBackgroundPanel().setVisible(true);
		                bankSystemFrame.getAccountManagementPanel().setVisible(false);
		                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
		                return;
					}
					
				}catch (NullPointerException n) {
					// 만약 입력된 계좌 비밀번호나 확인한 계좌 비밀번호가 null일때 실행됩니다.
	                bankSystemFrame.setVisible(true);
	                bankSystemFrame.getBackgroundPanel().setVisible(true);
	                bankSystemFrame.getAccountManagementPanel().setVisible(false);
	                bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
	                return;
				}
				
				accountPasswdPanel.setVisible(false);
		        finishPanel.add(nameLabel);
		        finishPanel.add(ssnLabel);
		        finishPanel.add(homeAddressLabel);
		        finishPanel.add(phoneNumberLabel);
		        String serial = null;
		        ArrayList<Account> list = null;
		        
		        if (accountType.equals("일반 예금")) {
		        	// 계좌의 유형이 일반 예금일떄 list를 일반 예금(depositAccountList)로 설정함
					list = accountStorage.getDepositAccountList();
					
				} else if(accountType.equals("정기 적금")) {
					// 계좌의 유형이 정기 적금일떄 list를 정기 적금(savingAccountList)로 설정함
					list = accountStorage.getSavingAccountList();
					
				} else if(accountType.equals("정기 예금")) {
					// 계좌의 유형이 정기 예금일떄 list를 정기 예금(timeAccountList)로 설정함
					list = accountStorage.getTimeAccountList();
				} else {
					// 계좌의 유형이 자유 적금일떄 list를 일반 예금(freeAccountList)로 설정함
					list = accountStorage.getFreeAccountList();
				}
		        
		        // 계좌 번호의 일련번호 생성 부분
		        serial = accountNumber;
				for (int i = 0; i < 6; i++) {
					serial += String.valueOf(((int)(Math.random()*9))+1);
					if (i == 5) {
						for (Account account : list) {
							//account는 
							String listAccountNumber = account.getAccountNumber();
							//만약 리스트에 동일한 계좌번호가 있을때
							if (listAccountNumber.equals(serial)) {
								i = -1;
								serial = accountNumber;
								continue;
							}
						}
					}
				}
				
				accountNumber = serial;
	            String showAccountNumber = String.format("%s-%s-%s",
	            		accountNumber.substring(0,3),
	            		accountNumber.substring(3,6),
	            		accountNumber.substring(6));
	            
				accountNumberLabel.setText("계좌 번호: " + showAccountNumber);
		        finishPanel.setVisible(true);
			}
		});
    
    finsishConfirmButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 다섯번째 화면에서 확인을 눌렀을때 실행되는 코드들
			createAccount();
			finishPanel.setVisible(false);
            bankSystemFrame.getAccountManagementPanel().setVisible(false);
            bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
            setVisible(false);
            bankSystemFrame.setVisible(true);
            bankSystemFrame.getBackgroundPanel().setVisible(true);
            return;
		}
	});
}
    
    // 계좌를 생성하고 정보를 ArrayList,multikeyMap,map,엑셀에 저장하는 메소드
    public void createAccount() {
    	
    	Account account = null; // 생성한 계좌용 변수
    	int index = 0; // 계좌가 저장된 ArrayList의 인덱스 번호
    	
    	if (accountType.equals("일반 예금")) {
    		// 일반 계좌일경우 실행
    		account = new Account(name, accountNumber, ssn, homeAddress, phoneNumber, accountPasswd);
    		accountStorage.addDepositAccount(account);
    		
    		//리스트에 계좌 추가후 맵과 엑셀에 데이터 추가부분
    		index = accountStorage.getDepositAccountList().size() - 1;
    		accountStorage.putMultikeyMap(accountNumber, accountPasswd, index);
    		accountStorage.putMap(accountNumber, index);
    		fileSystemDataUpdater = new FileSystemDataUpdater(account, index);
    		fileSystemDataUpdater.updateExcelDate();
    		fileSystemDataUpdater.writeLimitsToExcel();
    		fileSystemDataUpdater.writeLoanToExcel();
    		
    		
		} else if(accountType.equals("정기 적금")) {
    		account = new Account(name, accountNumber, ssn, accountType, accountPasswd, accountMaturity, accountDueDate);
    		accountStorage.addSavingAccount(account);
    		
    		//리스트에 계좌 추가후 맵과 엑셀에 데이터 추가부분
    		index = accountStorage.getSavingAccountList().size() - 1;
    		accountStorage.putMultikeyMap(accountNumber, accountPasswd, index);
    		accountStorage.putMap(accountNumber, index);
    		fileSystemDataUpdater = new FileSystemDataUpdater(account, index);
    		fileSystemDataUpdater.updateExcelDate();
    		
		} else if(accountType.equals("정기 예금")) {
    		account = new Account(name, accountNumber, ssn, accountType, accountPasswd, accountMaturity, accountDueDate);
    		accountStorage.addTimeAccount(account);
    		
    		//리스트에 계좌 추가후 맵과 엑셀에 데이터 추가부분
    		index = accountStorage.getTimeAccountList().size() - 1;
    		accountStorage.putMultikeyMap(accountNumber, accountPasswd, index);
    		accountStorage.putMap(accountNumber, index);
    		fileSystemDataUpdater = new FileSystemDataUpdater(account, index);
    		fileSystemDataUpdater.updateExcelDate();
    		
		} else {
    		account = new Account(name, accountNumber, ssn, accountType, accountPasswd, accountMaturity, accountDueDate);
    		accountStorage.addFreeAccount(account);
    		
    		//리스트에 계좌 추가후 맵과 엑셀에 데이터 추가부분
    		index = accountStorage.getFreeAccountList().size() - 1;
    		accountStorage.putMultikeyMap(accountNumber, accountPasswd, index);
    		accountStorage.putMap(accountNumber, index);
    		fileSystemDataUpdater = new FileSystemDataUpdater(account, index);
    		fileSystemDataUpdater.updateExcelDate();
    		
    		
		}
    	
    }
    
}