package system;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import java.awt.*;
import java.awt.event.*;

public class SystemTextDialog extends JDialog {
	
	private JTextField inputTextField;
	private JButton okButton;
	private String returnText = "";
	private boolean hangulRestrict;
	
	// 기본 생성자 모든 문자를 자유롭게 입력가능하게 합니다.
	public SystemTextDialog() {
		runTextDialog();
	}
	
	// 생성자 true를 주면 최대 입력 자리수를 6자리로 제한하고 한글만 입력가능하게 합니다.
	public SystemTextDialog(boolean hangulRestrict) {
		this.hangulRestrict = hangulRestrict;
		runTextDialog();
	}
	
	
	private void runTextDialog() {
		setResizable(false); // 사용자가 창의 크기를 변경하지 못하게 설정
		setUndecorated(true); // 프레임의 타이틀바를 제거합니다.
		setModal(true); //키패드창을 모달로 설정하여 키패드가 뜬상태에서 사용자가 부모창을 사용못하게합니다.
		setLocationRelativeTo(null); // 창이 가운대로 뜨게 설정합니다.

		Font font = new Font("Malgun Gothic", Font.BOLD, 15); // 폰트:맑은 고딕 글씨체:강조 글자 크기:15px
		
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(true); // 배경색이 보이도록 설정
        textPanel.setBackground(Color.BLUE); // 원하는 배경색으로 설정
        
        //입력 텍스트 필드 부분
        inputTextField = new JTextField(20);
        inputTextField.setFont(font);
        inputTextField.setPreferredSize(new Dimension(450, 40)); // 크기는 높이 300px 넓이 80px
        inputTextField.addKeyListener(new KeyListener() {
			//엔터가 눌렀다 땟을때 확인버튼을 누르는 액션
			@Override
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					okButton.doClick();
				}
			}

			
			// 이 밑 두개의 메소드들은 사용하지 않습니다
			@Override
			public void keyTyped(KeyEvent e) {}
        	@Override
			public void keyPressed(KeyEvent e) {}
			

		});;
        textPanel.add(inputTextField, BorderLayout.CENTER);
        
        
        // 한글외 입력시 입력하지 못하게 하기위해 입력전 필터를 거는 필터
        /*
         * Document를 변경하는 데 사용할 수 있는 fb FilterBypass
			오프셋 문서 내 위치
			length 삭제할 텍스트의 길이
			text 삽입할 텍스트, null은 삽입할 텍스트가 없음을 나타냅니다.
			삽입할 텍스트의 속성을 나타내는 속성 집합, null은 합법적입니다.

         */
        if (hangulRestrict) {
        	((AbstractDocument) inputTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
	            @Override
	            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	                    throws BadLocationException {
	            	// 필터 hangulRestrict가 True일때 한글외 입력과 7자리이상 입력을 제한합니다.
	            	
	                    String filteredText = text.replaceAll("[^가-힣]", ""); // 한글외 입력시 공백("")으로 변환합니다.
	                    text = filteredText; // 변환한 입력을 TextField의 적용합니다
	                    
	                    int oldTextLength = fb.getDocument().getLength(); // 대체 이전의 텍스트 길이
	                    // 대체 텍스트 길이 = 대체 이전 텍스트길이 - 대체될 텍스트 길이 + 새로입력된 텍스트 길이
	                    int newTextLength = oldTextLength - length + filteredText.length(); // 대체 이후의 텍스트 길이
	                    
	                    if (newTextLength <= 6) {
	                    	// 현재 길이가 6자리 이하면 텍스트를 입력합니다.
	                        super.replace(fb, offset, length, text, attrs);
	                    } else {
	                        // 현재 길이가 6자리를 초과하는 입력은 무시합니다.
	                    }
	            }
        	});
        }
        
        //확인 버튼 부분
        okButton = new JButton("확인");
        okButton.setFont(font);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (inputTextField.getText().length() > 1) {
            		// 최소 두글자이상 입력되있어야 창이 꺼지게 합니다.
                	returnText = inputTextField.getText(); // 확인버튼을 누르면 현재 입력된 텍스트들을 저장합니다.
                    dispose();
				}
            }
        });
        textPanel.add(okButton);

        add(textPanel);
        pack();
		
	}
	
	// 입력된 텍스트들을 반환하는 메소드입니다.
	public String getText() {
		return returnText;
	}
	
}
