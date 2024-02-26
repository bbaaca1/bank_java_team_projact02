package admin;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import system.Account;
import system.AccountStorage;
import system.FileSystemDataUpdater;

@SuppressWarnings("serial")
public class AdminModifyCustomerInformation extends JDialog {

	 private int mouseX, mouseY;
	 private int size;
	 private FileSystemDataUpdater fd;
	 
	 public AdminModifyCustomerInformation(AdminMainFrame adminMainFrame, AccountStorage as) {
		 	ArrayList<Account> depositAccountlist = as.getDepositAccountList();
	    	ArrayList<Account> savingAccountlist = as.getSavingAccountList();
	    	ArrayList<Account> timeAccountlist = as.getTimeAccountList();
	    	ArrayList<Account> freeAccountlist = as.getFreeAccountList();
	    	
	    	int index = 0;
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
	        
	        JPanel customerInfoPanel = new JPanel();
	        customerInfoPanel.setLayout(null); // 레이아웃을 null로 지정해 원하는 곳에 컴포넌트를 배치할 수 있음
	        customerInfoPanel.setBounds(0, 600, 1440, 210); // 크기를 직접 설정
	        customerInfoPanel.setVisible(true);
	        
	        JPanel customerInfoPanel2 = new JPanel();
	        customerInfoPanel2.setLayout(new BorderLayout()); // 레이아웃을 null로 지정해 원하는 곳에 컴포넌트를 배치할 수 있음
	        customerInfoPanel2.setBounds(0, 100, 1440, 600); // 크기를 직접 설정
	        customerInfoPanel2.setVisible(true);
	        
	        // 취소 버튼
	        JButton tcButton = new JButton("돌아가기"); // tc는 transaction cancellation을 의미
	        tcButton.setFont(font);
	        tcButton.setBounds(1290, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
	        customerInfoPanel.add(tcButton);
	        tcButton.setVisible(true);
	        
	        // 정보 수정 버튼
	        JButton modifyInfoButton = new JButton("정보 수정"); // tc는 transaction cancellation을 의미
	        modifyInfoButton.setFont(font);
	        modifyInfoButton.setBounds(0, 730, 150, 80); // 버튼의 크기와 위치를 직접 설정
	        customerInfoPanel.add(modifyInfoButton);
	        modifyInfoButton.setVisible(true);
	        
	        String[] columnNames = {"이름", "주민번호", "계좌번호", "비밀번호", "잔액", "마지막거래일자", 
	        		"주소", "전화번호", "계좌상태", "신용점수", "우대고객여부", "납기일", "만기일"};
	        String[][] data = new String[depositAccountlist.size() + savingAccountlist.size() + timeAccountlist.size() + freeAccountlist.size()][13];
	        for(int i = 0; i < depositAccountlist.size(); i++, index++) { // 2
	        	data[index][0] = depositAccountlist.get(i).getName();
	        	data[index][1] = depositAccountlist.get(i).getSsn();
	        	data[index][2] = depositAccountlist.get(i).getAccountNumber();
	        	data[index][3] = depositAccountlist.get(i).getAccountPasswd();
	        	data[index][4] = String.valueOf(depositAccountlist.get(i).getBalance());
	        	data[index][5] = String.valueOf(depositAccountlist.get(i).getLastTradingDate());
	        	data[index][6] = depositAccountlist.get(i).getHomeAddress();
	        	data[index][7] = depositAccountlist.get(i).getPhoneNumber();
	        	data[index][8] = String.valueOf(depositAccountlist.get(i).getState());
	        	data[index][9] = String.valueOf(depositAccountlist.get(i).getCreditScore());
	        	data[index][10] = String.valueOf(depositAccountlist.get(i).getVip());
	        	data[index][11] = null;
	        	data[index][12] = null;
	        }

	        for(int i = 0; i < savingAccountlist.size(); i++, index++) { // 3
	        	data[index][0] = savingAccountlist.get(i).getName();
	        	data[index][1] = savingAccountlist.get(i).getSsn();
	        	data[index][2] = savingAccountlist.get(i).getAccountNumber();
	        	data[index][3] = savingAccountlist.get(i).getAccountPasswd();
	        	data[index][4] = String.valueOf(savingAccountlist.get(i).getBalance());
	        	data[index][5] = String.valueOf(savingAccountlist.get(i).getLastTradingDate());
	        	data[index][6] = null;
	        	data[index][7] = null;
	        	data[index][8] = null;
	        	data[index][9] = null;
	        	data[index][10] = null;
	        	data[index][11] = String.valueOf(savingAccountlist.get(i).getAccountDueDate());
	        	data[index][12] = String.valueOf(savingAccountlist.get(i).getAccountMaturity());
	        }

	        for(int i = 0; i < timeAccountlist.size(); i++, index++) { // 4
	        	data[index][0] = timeAccountlist.get(i).getName();
	        	data[index][1] = timeAccountlist.get(i).getSsn();
	        	data[index][2] = timeAccountlist.get(i).getAccountNumber();
	        	data[index][3] = timeAccountlist.get(i).getAccountPasswd();
	        	data[index][4] = String.valueOf(timeAccountlist.get(i).getBalance());
	        	data[index][5] = String.valueOf(timeAccountlist.get(i).getLastTradingDate());
	        	data[index][6] = null;
	        	data[index][7] = null;
	        	data[index][8] = null;
	        	data[index][9] = null;
	        	data[index][10] = null;
	        	data[index][11] = String.valueOf(timeAccountlist.get(i).getAccountDueDate());
	        	data[index][12] = String.valueOf(timeAccountlist.get(i).getAccountMaturity());
	        }
	        
	        for(int i = 0; i < freeAccountlist.size(); i++, index++) { // 5
	        	data[index][0] = freeAccountlist.get(i).getName();
	        	data[index][1] = freeAccountlist.get(i).getSsn();
	        	data[index][2] = freeAccountlist.get(i).getAccountNumber();
	        	data[index][3] = freeAccountlist.get(i).getAccountPasswd();
	        	data[index][4] = String.valueOf(freeAccountlist.get(i).getBalance());
	        	data[index][5] = String.valueOf(freeAccountlist.get(i).getLastTradingDate());
	        	data[index][6] = null;
	        	data[index][7] = null;
	        	data[index][8] = null;
	        	data[index][9] = null;
	        	data[index][10] = null;
	        	data[index][11] = String.valueOf(freeAccountlist.get(i).getAccountDueDate());
	        	data[index][12] = String.valueOf(freeAccountlist.get(i).getAccountMaturity());
	        }

	        // JTable 생성 및 모델 설정
	        JTable table = new JTable(data,columnNames);
	        table.getTableHeader().setReorderingAllowed(false);  // 드래그 사용 불가
	        table.setPreferredScrollableViewportSize(new Dimension(1440, 600));
	        JScrollPane scrollPane = new JScrollPane(table);
	        customerInfoPanel2.add(scrollPane);
	        this.add(customerInfoPanel2);
            this.add(customerInfoPanel);
            
            // UI 배치 설정 및 보여주기
            this.setVisible(true);
 
            
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
            
            modifyInfoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	int row = 0;
                	for(int i = 0; i < depositAccountlist.size(); i++) {
                		fd = new FileSystemDataUpdater(depositAccountlist.get(i), i);
                		depositAccountlist.get(i).setName(String.valueOf(table.getValueAt(row, 0)));
        	        	depositAccountlist.get(i).setSsn(String.valueOf(table.getValueAt(row, 1)));
        	        	depositAccountlist.get(i).setAccountPasswd(String.valueOf(table.getValueAt(row, 3)));
        	        	depositAccountlist.get(i).setBalance(Long.parseLong(String.valueOf(table.getValueAt(row, 4))));
        	        	depositAccountlist.get(i).setLastTradingDate(LocalDate.parse(String.valueOf(table.getValueAt(0, 5))));
        	        	depositAccountlist.get(i).setHomeAddress(String.valueOf(table.getValueAt(row, 6)));
        	        	depositAccountlist.get(i).setPhoneNumber(String.valueOf(table.getValueAt(row, 7)));
        	        	depositAccountlist.get(i).setState(Integer.parseInt(String.valueOf(table.getValueAt(row, 8))));
        	        	depositAccountlist.get(i).setCreditScore(Integer.parseInt(String.valueOf(table.getValueAt(row, 9))));
        	        	row++;
        	        	fd.ModifyAccountDateToExcel();
                	}
                	for(int i = 0; i < savingAccountlist.size(); i++) {
                		fd = new FileSystemDataUpdater(savingAccountlist.get(i), i);
                		savingAccountlist.get(i).setName(String.valueOf(table.getValueAt(row, 0)));
                		savingAccountlist.get(i).setSsn(String.valueOf(table.getValueAt(row, 1)));
                		savingAccountlist.get(i).setAccountPasswd(String.valueOf(table.getValueAt(row, 3)));
                		savingAccountlist.get(i).setBalance(Long.parseLong(String.valueOf(table.getValueAt(row, 4))));
                		savingAccountlist.get(i).setLastTradingDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 5))));
                		savingAccountlist.get(i).setAccountDueDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 11))));
                		savingAccountlist.get(i).setAccountMaturity(LocalDate.parse(String.valueOf(table.getValueAt(row, 12))));
                		row++;
                		fd.ModifyAccountDateToExcel();
                	}
                	for(int i = 0; i < timeAccountlist.size(); i++) {
                		fd = new FileSystemDataUpdater(timeAccountlist.get(i), i);
                		timeAccountlist.get(i).setName(String.valueOf(table.getValueAt(row, 0)));
                		timeAccountlist.get(i).setSsn(String.valueOf(table.getValueAt(row, 1)));
                		timeAccountlist.get(i).setAccountPasswd(String.valueOf(table.getValueAt(row, 3)));
                		timeAccountlist.get(i).setBalance(Long.parseLong(String.valueOf(table.getValueAt(row, 4))));
                		timeAccountlist.get(i).setLastTradingDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 5))));
                		timeAccountlist.get(i).setAccountDueDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 11))));
                		timeAccountlist.get(i).setAccountMaturity(LocalDate.parse(String.valueOf(table.getValueAt(row, 12))));
                		row++;
                		fd.ModifyAccountDateToExcel();
                	}
                	for(int i = 0; i < freeAccountlist.size(); i++) {
                		fd = new FileSystemDataUpdater(freeAccountlist.get(i), i);
                		freeAccountlist.get(i).setName(String.valueOf(table.getValueAt(row, 0)));
                		freeAccountlist.get(i).setSsn(String.valueOf(table.getValueAt(row, 1)));
                		freeAccountlist.get(i).setAccountPasswd(String.valueOf(table.getValueAt(row, 3)));
                		freeAccountlist.get(i).setBalance(Long.parseLong(String.valueOf(table.getValueAt(row, 4))));
                		freeAccountlist.get(i).setLastTradingDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 5))));
                		freeAccountlist.get(i).setAccountDueDate(LocalDate.parse(String.valueOf(table.getValueAt(row, 11))));
                		freeAccountlist.get(i).setAccountMaturity(LocalDate.parse(String.valueOf(table.getValueAt(row, 12))));
                		row++;
                		fd.ModifyAccountDateToExcel();
                	}
                }
            });
            
            
	 }
	 
}