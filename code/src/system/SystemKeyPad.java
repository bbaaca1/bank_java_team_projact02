package system;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class SystemKeyPad extends JDialog implements ActionListener {
	private JLabel userNum; // 사용자에게 입력한 값을 출력하기위한 변수 
	private JTextField inputNumberTextField; // 입력된값을 저장해 리스트로 전달하기 위한 텍스트 필드
	private String returnNumber = "";
	private int maxLength;
	private boolean securityOutput;
	
	//기본생성자 매개변수를 아무것도 안줄시 최대입력을 4자리로 제한
	public SystemKeyPad() {
		maxLength = 4;
		inputNumberTextField = new JTextField(4);
		runKeyPad();
	}
	
	//생성자 매개변수를 숫자(int)만 줄시 최대 입력을 매개변수 숫자로 제한
	public SystemKeyPad(int maxLength) {
		this.maxLength = maxLength;
		inputNumberTextField = new JTextField(maxLength);
		runKeyPad();
	}
	
	//생성자 매개변수를 True만 줄때 최대입력을 4자리로 제한하고 사용자가 보이는 부분이 *로 출력됨
	public SystemKeyPad(boolean securityOutput) {
		maxLength = 4;
		inputNumberTextField = new JTextField(4);
		this.securityOutput = securityOutput;
		runKeyPad();
	}
	
	//생성자 매개변수를 숫자(int)를 주고 True를 줄때 최대입력을 매개변수 숫자로 제한하고 사용자가 보이는 부분이 *로 출력됨
	public SystemKeyPad(int maxLength,boolean securityOutput) {
		this.maxLength = maxLength;
		inputNumberTextField = new JTextField(maxLength);
		this.securityOutput = securityOutput;
		runKeyPad();
	}
	
	
	
	public void runKeyPad() {		
		setResizable(false); // 사용자가 창의 크기를 변경하지 못하게 설정
		setLocationRelativeTo(null); // 창이 가운데 뜨게 설정
		setUndecorated(true); // 프레임의 타이틀바를 제거함
		setModal(true); //키패드창을 모달로 설정하여 키패드가 뜬상태에서 사용자가 부모창을 사용못하게함
		
		JPanel keyPanel = new JPanel(new BorderLayout());
		keyPanel.setOpaque(true); // 배경색이 보이도록 설정
		keyPanel.setBackground(Color.WHITE); // 원하는 배경색으로 설정
		
		// 키패드 입력 출력 부분
		userNum = new JLabel();
		userNum.setPreferredSize(new Dimension(300,80)); // 크기는 높이 300px 넓이 80px
		userNum.setHorizontalAlignment(JLabel.CENTER);// 키패드 클릭시 나오는 숫자를 가운대로 설정함
        userNum.setFont(new Font("Malgun Gothic", Font.BOLD, 15)); // 폰트:맑은 고딕 글씨체:강조 글자 크기:15px
        keyPanel.add(userNum, BorderLayout.NORTH); // 입력 부분의 위치는 상단에 위치함


        // 키패드 버튼 부분
        JPanel numberButtonPanel = new JPanel(new GridLayout(4,3)); // 4행 3열짜리 키패드 생성
        for (int i = 1; i <= 9; i++) {
        	JButton numberKeybutton = new JButton(String.valueOf(i)); // 문자열(String) 형식으로 1부터 9까지 숫자를 할당함
        	numberKeybutton.addActionListener(this); // 자기자신(numberKeybutton)을 반환 버튼이 눌렸을때 1~9의 수가 들어있는 객체를 반환하여 그 값이 나오게함
        	numberButtonPanel.add(numberKeybutton); //패널에 버튼 추가
        	
		}
        
        JButton deletButton = new JButton("삭제"); // 클릭시 글자를 하나씩 지우는 삭제 버튼
        deletButton.addActionListener(this);
        numberButtonPanel.add(deletButton);
        
        JButton zeroButton = new JButton("0"); // 클릭시 0이나오는 버튼을 추가
        zeroButton.addActionListener(this);
        numberButtonPanel.add(zeroButton);
        
        JButton confrimButton = new JButton("확인"); // 클릭시 입력된 숫자를 다음으로 전달하는 변수
        confrimButton.addActionListener(this);
        numberButtonPanel.add(confrimButton);
                
        keyPanel.add(numberButtonPanel, BorderLayout.CENTER); // 숫자 입력버튼을 중간에 위치시킴
        
        // 종료 버튼 부분
        JButton cancelButtonPanel = new JButton("취소");
        cancelButtonPanel.addActionListener(this);
        keyPanel.add(cancelButtonPanel, BorderLayout.SOUTH); // 종료버튼을 하단에 위치시킴
                
        add(keyPanel);
        pack();
       }

	// 
	@Override
	public void actionPerformed(ActionEvent e) {
	
    if (inputNumberTextField != null) {
    	
        JButton button = (JButton) e.getSource();
        String buttonNumber = button.getText();
        
        if (buttonNumber.equals("삭제")) {
            // 가장 마지막 글자를 삭제
            String oldText = userNum.getText(); // 변경전 현재 유저 출력용 변수를 담아둘 변수
            
            if (!oldText.isEmpty()) {
            	// 유저 출력용 저장변수(userNum)이 비어있지 않을때 실행함
                String newText = oldText.substring(0, oldText.length() - 1); // oldText의 마지막 숫자만 뺀 숫자나 숫자들을(처음부터 최대길이의 -1까지) 담을 변수
                userNum.setText(newText); //userNum을 newText의 값으로 설정함
                
                String oldInputNumber = inputNumberTextField.getText(); // 변경전 저장된 숫자(문자열)를 담아둘 변수
                
                if (!oldInputNumber.isEmpty()) {
                    String newInputNumber = oldInputNumber.substring(0, oldInputNumber.length() - 1); // oldInputNumber의 마지막 숫자만 뺀 숫자나 숫자들을(처음부터 최대길이의 -1까지) 담을 변수
                    inputNumberTextField.setText(newInputNumber);//userNum을 newInputNumber의 값으로 설정함
                }
            }
            
        } else if (buttonNumber.equals("확인")) {
            	returnNumber = inputNumberTextField.getText();
                this.dispose(); // 현재창 종료

            
            
        } else if(buttonNumber.equals("취소")){
        	this.dispose(); // 현재창 종료
        	
        } else {
            if(inputNumberTextField.getText().length() != maxLength) {
            	//지금까지 입력된 숫자가 4자리가 아닐때 실행됨
            	//만약 자리수를 4자리 보다 작거나 큰수로 설정하고 싶다면 위 조건식 != 4부분에서 4의 숫자를 변경하세요(ex: != 6 일경우 6자리까지 입력됨)
            	if (!securityOutput) {
    	        	userNum.setText(userNum.getText() + buttonNumber); // 유저에게 현재까지 입력된수 + 지금 누른 버튼의 할당된수를 출력함
				} else {
					userNum.setText(userNum.getText() + "*"); // 유저에게 입련된수+ 지금 누른 버튼의 수 만큼 *을 출력
				}
            	
	            inputNumberTextField.setText(inputNumberTextField.getText() + buttonNumber); // 현재까지 입력된수 + 지금 누른 버튼의 할당된수를 저장함
            }
        }
    }
            
	}
	
    public void setInputNumberTextField(JTextField inputNumberTextField) {
		this.inputNumberTextField = inputNumberTextField;
	}

	public String getNumber() {
        return returnNumber;
    }
	
}
