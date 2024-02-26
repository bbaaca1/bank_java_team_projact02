package system;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class SystemCertificationNumber extends JDialog {
	
	private String CertficationbNumber = "";
	private int timeLeft = 6;
	
	public SystemCertificationNumber() {
		setResizable(false); // 사용자가 창의 크기를 변경하지 못하게 설정
		setUndecorated(true); // 프레임의 타이틀바를 제거합니다.
		setModal(true); //인증번호창을 모달로 설정하여 인증번호가 뜬상태에서 사용자가 부모창을 사용못하게합니다.
		setLocationRelativeTo(null); // 창이 가운대로 뜨게 설정합니다.	}
		
        this.setSize(1440, 810);
        Font font = new Font("Malgun Gothic", Font.BOLD, 32);
        
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
		
		JPanel CertficationbNumberPanel = new JPanel();
		CertficationbNumberPanel.setLayout(null);
		CertficationbNumberPanel.setBounds(0, 0, 1440, 810); // 크기를 직접 설정
		
        // 인증번호 출력 라벨부분
        JLabel CertficationbNumberLabel = new JLabel("인증번호 : "+CertficationbNumber);
        CertficationbNumberLabel.setBounds(500, 207, 1000, 200);
        CertficationbNumberLabel.setFont(font);
        CertficationbNumberPanel.add(CertficationbNumberLabel);
        
        // ~초후 돌아갑니다 부분
        JLabel  CertficationbNumberFinishCountLabel = new JLabel();
        CertficationbNumberFinishCountLabel.setBounds(496, 407, 1000, 200); // (x, y, width, height)
        CertficationbNumberFinishCountLabel.setFont(font);
        CertficationbNumberPanel.add( CertficationbNumberFinishCountLabel);
        
        ImageIcon CertficationbNumberFinisPanelImage = new ImageIcon("이미지\\버튼\\힣신용대출메인.png");
        JLabel CertficationbNumberFinisTitle = new JLabel(CertficationbNumberFinisPanelImage);
        CertficationbNumberFinisTitle.setBounds(0, 0, 1440, 810); // (x, y, width, height)
        CertficationbNumberPanel.add(CertficationbNumberFinisTitle);
        
        
        this.add(CertficationbNumberPanel);
        
//		int[] timeLeft = {6}; // 남은 시간을 저장하는 배열
		Timer timer = new Timer(1000, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	timeLeft--; // 남은 시간 감소
		    	CertficationbNumberFinishCountLabel.setText(timeLeft + "초후 돌아갑니다.");; // 레이블 텍스트 업데이트
		        
		        if (timeLeft == 0) { // 남은 시간이 0이면
		            setVisible(false);
		            ((Timer)e.getSource()).stop(); // 타이머 중지
		        }
		    }
		});
		timer.start();
		
	}
	
	public String getCertficationbNumber() {
		return CertficationbNumber;
	}

//	public static void main(String[] args) {
//		SystemCertificationNumber systemCertficationbNumber = new SystemCertificationNumber();
//		systemCertficationbNumber.setVisible(true);
//		
//	}
}
