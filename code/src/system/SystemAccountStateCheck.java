package system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.JOptionPane;


public class SystemAccountStateCheck  {
	
	
	final static Logger logger = LogManager.getLogger(SystemAccountStateCheck.class);
	
	private int index;
	private boolean flag;
	private AccountStorage accountStorage;
	private Account account;
	
	public SystemAccountStateCheck(int index, AccountStorage accountStorage) {
		this.index = index;
		this.accountStorage = accountStorage;
		checkDaliyState();
	}

	
	
	public void checkDaliyState() {
		ArrayList<Account> accountList = accountStorage.getDepositAccountList();
		LocalDate nowTime = LocalDate.now();
		
		for (Account account : accountList) {
			LocalDate accountTime = account.getLastTradingDate();
			
			if (nowTime.isAfter(accountTime)) {
				if (!account.isNonTradingAcc()) {
					account.setAccountStatus(0);
					account.setWithdrawalLimit(false);
					account.setTransferLimit(false);
				}
				
			}
			
		}
	}
	
	
	
	
	public void voicePhishing() {
		LocalDateTime nowTime = LocalDateTime.now(); //현재 시간을 선언
		String accountNumber = accountStorage.getDepositAccount(index).getAccountNumber();
		Account account = accountStorage.getDepositAccount(index);
		
		if(account.isFreez()) { //만약 동결이 되어있다면
			
			//이 구문이 동결시에 단 한번만 실행 되어야 한다.
			if(accountStorage.findAccountFreezTimeFromFreezMap(accountNumber) == null) { //freezMap에 계좌번호를 넣었을 때 값이 없다면
//				logger.info(accountNumber+"has been freez");
				LocalDateTime freezTime = LocalDateTime.now().plusMinutes(5); // 5분후로 설정
				account.setFreezTime(freezTime);
				accountStorage.putFreezMap(accountNumber, freezTime); //freezMap에 (계좌번호, 동결이 시작된 시간)을 넣어준다.
				FileSystemDataUpdater fileSystemDataUpdater = new FileSystemDataUpdater(account,index);
				fileSystemDataUpdater.writeLimitsToExcel();
			}
			
			LocalDateTime time = accountStorage.findAccountFreezTimeFromFreezMap(accountNumber); //계좌번호에 대한 동결이 시작된 시간을 담은 변수
			
			if(nowTime.isAfter(time)) { //동결시간이 하루이상 지났을 경우
//				logger.info(accountNumber+"has been unfrozen");
				accountStorage.getFreezMap().remove(accountNumber); //계좌번호에 대한 동결 시작시간 지우기
				account.setFreez(false); //isFreez의 값을 true에서 false로 바꾸기
				
			}
			
		}
	}
	
	public void CheckWithdrawalLimit(Long withdrawalMoney) {
		account = accountStorage.getDepositAccount(index);
		Long withdrawalLimits = account.getWithdrawalLimit();
		withdrawalLimits += withdrawalMoney;
		
		if (withdrawalLimits > 6000000) {
			account.setWithdrawalLimit(true);
			if (account.getState() == 4) {
				account.setState(5);
			} else {
				account.setState(3);
			}
		}
	}
	
	public void checkTransferLimit(Long transferMoney) {
		account = accountStorage.getDepositAccount(index);
		Long transferLimits = account.getTransferLimit();
		transferLimits += transferMoney;
		
		if (transferLimits > 30000000) {
			account.setTransferLimit(true);
			if (account.getState() == 3) {
				account.setState(5);
			} else {
				account.setState(4);
			}
		}
	}
	
	/*
	 * 메소드명: checkState
	 * 파라미터: int index, int num
	 * index: 인덱스값(사용자의 리스트위치)으로 계좌 상태를 보기 위해 사용
	 * num: num이 1이면 이체 제한만 해당, num이 0이라면 출금 제한 나머지 값은 입금에 해당함
	 * 반환값: boolean(계좌의 상태들 중 해당하는 상태가 있다면 true를 반환 아니라면 false를 반환)
	 * 기능설명:
	 * 사용자의 리스트에 들어가 있는 계좌상태 변수들을 확인하여 
	 * 각 상태에 따라 해당하는 문구를 출력 후 false를 반환
	 * 해당하지 않는다면 true를 반환 
	 */
	
	public boolean checkState(boolean flag) {
		// 계좌 상태를 확인하는 코드
		
		Account account = accountStorage.getDepositAccount(index); // 현재 사용중인 계좌를 담아두는 변수
		
		if (account.isNonTradingAcc()) { // isNonTradingAcc가 true라면 실행
			System.out.println("휴면 계좌입니다.");
			JOptionPane.showMessageDialog(null, "휴면 계좌입니다.");
			return false;
		}
		
		voicePhishing(); //isFreez를 확인하기 전 동결시간 확인 메소드
		if (account.isFreez()) { // isFreez가 true라면 실행
			System.out.println("계좌가 동결중입니다.");
			JOptionPane.showMessageDialog(null, "동결 계좌입니다.");
			return false;
		}
		
		if (account.isTransferLimit() && flag) { // isTransferLimit가 true라면 실행
			System.out.println("계좌가 이체 제한 상태입니다.");
			JOptionPane.showMessageDialog(null, "이체가 제한된 계좌입니다.");
			return false;
		}
		
		if (account.isWithLimit() && !flag) { // isWithLimit가 true라면 실행
			System.out.println("계좌가 출금 제한 상태입니다.");
			JOptionPane.showMessageDialog(null, "출금이 제한된 계좌입니다.");
			return false;
		}
		
		return true; 
		
	}
	
	public boolean checkState() {
		// 계좌 상태를 확인하는 코드
		
		Account account = accountStorage.getDepositAccount(index); // 현재 사용중인 계좌를 담아두는 변수
		
		if (account.isNonTradingAcc()) { // isNonTradingAcc가 true라면 실행
			System.out.println("휴면 계좌입니다.");
			JOptionPane.showMessageDialog(null, "휴면 계좌입니다.");
			return false;
		}
		
		voicePhishing(); //isFreez를 확인하기 전 동결시간 확인 메소드
		if (account.isFreez()) { // isFreez가 true라면 실행
			System.out.println("계좌가 동결중입니다.");
			JOptionPane.showMessageDialog(null, "동결 계좌입니다.");
			return false;
		}
		
		return true; 
		
	}
	
}
