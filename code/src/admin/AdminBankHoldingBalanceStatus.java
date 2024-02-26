package admin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.BankSystemMainFrame;
import system.*;

public class AdminBankHoldingBalanceStatus extends JDialog {

@SuppressWarnings("serial")

	private int mouseX, mouseY;
	private String bank;
	private SystemKeyPad virtualKeyPad;
	private int index;
	private long totalAmount;
	private FileSystemDataUpdater fd;
	private SystemAccountStateCheck sc;
	long sum = 0;
	public AdminBankHoldingBalanceStatus(BankSystemMainFrame bankSystemFrame, AccountStorage as) {
		ArrayList<Account> depositAccountlist = as.getDepositAccountList();
		//엑셀에 있는 사용자 금액을 가져옴
		for (int i = 0; i < depositAccountlist.size(); i++) {
			sum += depositAccountlist.get(i).getBalance();
		}
		Map<String, Integer> map = as.getMap();

		this.setResizable(false);
		this.setUndecorated(true);
		// 배경 패널 설정
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new BorderLayout());
		backgroundPanel.setOpaque(false);
		setVisible(true); // 화면에 보이도록 설정

		this.setSize(1440, 810); // 창 크기

		// 창을 화면 중앙으로 띄우기
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int leftTopX = centerPoint.x - this.getWidth() / 2;
		int leftTopY = centerPoint.y - this.getHeight() / 2;
		this.setLocation(leftTopX, leftTopY);

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

		// 취소 버튼 생성 및 설정
		JButton cancleButton = new JButton("취소");
		cancleButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		cancleButton.setBounds(0, 730, 150, 80);
		this.add(cancleButton);

		// 버튼들을 패널에 추가 및 이벤트 처리
		this.setLayout(null);
		this.add(cancleButton);
		cancleButton.addMouseListener(new MouseAdapter() { // 마우스 이벤트
			@Override
			public void mousePressed(MouseEvent e) { // 클릭했을때 메인화면
			}
		});		
	
		
		// 확인 버튼 생성 및 설정
		JButton confirmButton = new JButton("확인");
		confirmButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(1290, 730, 150, 80); 
		this.add(confirmButton);
		
		JLabel balanceStatus= new JLabel("현재 보유하고 있는 현금: ");
		balanceStatus.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		balanceStatus.setBounds(300, 300, 600, 100);
		this.add(balanceStatus);
		
		JLabel balance= new JLabel();
		balance.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		balance.setBounds(300, 300, 600, 100);
		this.add(balance);
		
		///////////////////// 첫 화면 //////////////////////
		confirmButton.setVisible(true); //확인 버튼
		balanceStatus.setVisible(true); //"현재 보유하고 있는 현금:"
		balance.setText("현재 보유하고 있는 현금: "+String.valueOf(sum)+"원");
		
		
		confirmButton.addMouseListener(new MouseAdapter() { //confirmButton(확인버튼) 누를 시 관리자 메인화면
			@Override
			public void mousePressed(MouseEvent e) {
				//confirmButton(확인버튼) 누를 시 관리자 메인화면 < 관리자 메인화면 아직x, 우선 사용자 메인화면으로 해놓음
				setVisible(false);
				bankSystemFrame.setVisible(true);
				bankSystemFrame.getBackgroundPanel().setVisible(true);
				bankSystemFrame.getAccountManagementPanel().setVisible(false);
				bankSystemFrame.getDepositWithdrawalPanel().setVisible(false);
			
			}
		});
		
	}
}
