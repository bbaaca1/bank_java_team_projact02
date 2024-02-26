package admin;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import system.*;

@SuppressWarnings("serial")
public class AdminCheckCashFlow  extends JDialog{

	 private int mouseX, mouseY;
	 private FileSystemDataUpdater fd;
	 private AdminCashFlowFinder depositCashFlow = new AdminCashFlowFinder(true); // 입금흐름
	 private AdminCashFlowFinder withdrawalCashFlow = new AdminCashFlowFinder(false); // 출금흐름
	 private SystemKeyPad virtualKeyPad;
	 private SystemTextDialog textDialog;
		public AdminCheckCashFlow (AdminMainFrame adminMainFrame, AccountStorage as) {

		
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
		        
		        JPanel cashFlowPanel = new JPanel();
		        cashFlowPanel.setLayout(null); // 레이아웃을 null로 지정해 원하는 곳에 컴포넌트를 배치할 수 있음
		        cashFlowPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
		        cashFlowPanel.setVisible(true);
		        
		        // 메인으로 돌아가는 버튼
		        JButton tcButton = new JButton("돌아가기"); // tc는 transaction cancellation을 의미
		        tcButton.setFont(font);
		        tcButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
		        cashFlowPanel.add(tcButton);
		        
		        JButton checkButton = new JButton("확인"); // tc는 transaction cancellation을 의미
		        checkButton.setFont(font);
		        checkButton.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
		        cashFlowPanel.add(checkButton);
		        
		        JLabel explanationLabel = new JLabel("이름과 보고싶은달을 현재달에서 뺀 값을 기입"); // 사용자의 총 입금량
		        explanationLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        explanationLabel.setBounds(200, 100, 1000,50);
		        cashFlowPanel.add(explanationLabel);
		        
		        JLabel explanationLabel2 = new JLabel("예) 현재달: 8월 보고싶은달: 5월 텍스트필드에 3기입"); // 사용자의 총 입금량
		        explanationLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        explanationLabel2.setBounds(200, 200, 1000,50);
		        cashFlowPanel.add(explanationLabel2);
		        
		        JLabel depositLabel = new JLabel(); // 사용자의 총 입금량
		        depositLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        depositLabel.setBounds(0, 500, 500,50);
		        cashFlowPanel.add(depositLabel);

		        JLabel withdrawalLabel = new JLabel(); // 사용자의 총 출금량
		        withdrawalLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        withdrawalLabel.setBounds(0, 600, 500,50);
		        cashFlowPanel.add(withdrawalLabel);
		        
		        JTextField nameTextField = new JTextField("이름 입력"); // 이름 입력 필드
		        nameTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        nameTextField.setBounds(300, 700, 200, 40);
		        cashFlowPanel.add(nameTextField);
		        
		        JTextField monthTextField = new JTextField("달 입력"); // 달 입력 필드
		        monthTextField.setFont(new Font("Malgun Gothic", Font.BOLD, 30)); // 맑은 고딕, 볼드, 글자 크기는 30
		        monthTextField.setBounds(600, 700, 200, 40);
		        cashFlowPanel.add(monthTextField);
		        
		        this.add(cashFlowPanel);
		        
		        tcButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                	
	                	adminMainFrame.setVisible(true);
	                	adminMainFrame.getBackgroundPanel().setVisible(true);
	                	adminMainFrame.getAccountManagementPanel().setVisible(false);
	                    adminMainFrame.getDepositWithdrawalPanel().setVisible(false);
	                	setVisible(false);
	                }
	            });
		        
		        checkButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                	String name = nameTextField.getText();
	                	String month = monthTextField.getText();
	                	if(!month.equals("달 입력") || month == null) {
	                		depositLabel.setText("총 입금량: " + depositCashFlow.getMonthlyTotalTransactions(name,Integer.parseInt(month)));
	                		withdrawalLabel.setText("총 출금량: " + withdrawalCashFlow.getMonthlyTotalTransactions(name,Integer.parseInt(month)));
	                	}
	                	else {
	                		depositLabel.setText("총 입금량: " + depositCashFlow.getMonthlyTotalTransactions(name));
	                		withdrawalLabel.setText("총 출금량: " + withdrawalCashFlow.getMonthlyTotalTransactions(name));
	                	}
	                	
	                }
	            });
		        
		        monthTextField.addMouseListener(new MouseListener() {
					
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
						virtualKeyPad = new SystemKeyPad(2);
		                virtualKeyPad.setVisible(true);
		                monthTextField.setText(virtualKeyPad.getNumber());
					}
				});
		        
		        nameTextField.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						textDialog = new SystemTextDialog();
						textDialog.setVisible(true);
								
						String input = textDialog.getText();
						nameTextField.setText(input);
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
		        
		}
}
