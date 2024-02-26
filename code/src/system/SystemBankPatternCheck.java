package system;

import java.util.regex.Pattern;

public class SystemBankPatternCheck {
	
	private String bankName;
	// 은행 이름 배열(String)
	private static final String[] BANK_NAMES = {"단풍 은행","신한 은행", "카카오 은행","우리 은행","새마을 금고"};
	
	//은행 번호 패턴들
	private static final Pattern MAPLE_BANK_PATTERN = Pattern.compile("^(012|021|011|022)068(\\d{6})$"); // 단풍은행 계좌번호 확인용 패턴
	private static final Pattern SHINHAN_BANK_PATTERN = Pattern.compile("^(100|10[1-9]|160|161|1[0-3][0-9])(\\d{3})(\\d{6})$"); // 신한은행 계좌번호 확인용 패턴
	private static final Pattern KAKAO_BANK_PATTERN = Pattern.compile("^(3)(333|388|355)(\\d{2})(\\d{7})$"); // 카카오 뱅크 계좌번호 확인용 패턴
	private static final Pattern WOORI_BANK_PATTERN = Pattern.compile("^(1)(006|007|002)(\\d{3})(\\d{6})$"); // 우리은행 계좌 번호 확인용 패턴
	// 새마을 금고 계좌 번호 확인용 패턴
	private static final Pattern MG_BANK_PATTERN = Pattern.compile("^(9)(00[2-4]|072|09[0-3]|200|202|205|207-210|212)(\\d{4})(\\d{4})(\\d{1})$"); 
	//패턴이 담긴 배열 은행 이름배열의 인덱스와 같은 인덱스에 지정
	private static final Pattern[] PATTERNS = {MAPLE_BANK_PATTERN,SHINHAN_BANK_PATTERN,KAKAO_BANK_PATTERN,WOORI_BANK_PATTERN,MG_BANK_PATTERN};
	
	// 은행계좌 만 입력하면 단풍은행의 계좌 유효성을 검사하여 true나 false를 반환하는 메소드
	public boolean checkBankPettern(String inputAccountNumber) {
		Pattern accountNumberPattern = null;
		
		for (int i = 0; i < BANK_NAMES.length; i++) {
			if (BANK_NAMES[i].equals(bankName)) {
				accountNumberPattern = PATTERNS[i];
				break;
			}
		}
		
		if (accountNumberPattern.matcher(inputAccountNumber).matches()) {
			return true;
		}else {
			return false;
		}
		
	}
	
	// 은행이름과 계좌 번호를 넣으면 지정한 은행의 계좌 유효성을 검사하여 true나 false를 반환하는 메소드
	public boolean checkBankPettern(String bankName,String inputAccountNumber) {
		
		Pattern accountNumberPattern = null;
		
		for (int i = 0; i < BANK_NAMES.length; i++) {
			if (BANK_NAMES[i].equals(bankName)) {
				accountNumberPattern = PATTERNS[i];
				break;
			}
		}
		
		
		
		if (accountNumberPattern.matcher(inputAccountNumber).matches()) {
			return true;
		}else {
			return false;
		}
		
		
		
	}
	
}
